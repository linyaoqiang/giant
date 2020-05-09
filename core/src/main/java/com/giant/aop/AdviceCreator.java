package com.giant.aop;

import com.giant.annotation.Pointcut;
import com.giant.bean.ApplicationStandardSpace;
import com.giant.commons.opeator.FileOperator;
import com.giant.commons.opeator.StringOperator;
import org.dom4j.Element;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 创建通知类的实例
 */
public class AdviceCreator {
    private Map<String,Object> aopBeans=new HashMap<>();
    private Map<String,String> pointcuts= new HashMap<>();
    public static final AdviceCreator ADVICE_CREATOR = new AdviceCreator();
    private AdviceCreator(){}
    public void createAdvice(String config) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element root = FileOperator.createRootElement(config);
        Element aopE = root.element(ApplicationStandardSpace.AOP);
        //如果没有aop这个标签，或者属性proxy不为jdk则，则直接返回
        if (aopE == null || !"jdk".equals(aopE.attributeValue(ApplicationStandardSpace.AOP_PROXY))) {
            return;
        }

        Element advicesE = aopE.element(ApplicationStandardSpace.AOP_ADVICES);
        if (advicesE != null) {
            //创建advice实例通过包名,要其通知类必须配置@Pointcut
            createAdviceFromPackage(advicesE);
        }
        //变量每一个advice标签,创建advice实例
        for (Iterator<Element> iterator = aopE.elementIterator(ApplicationStandardSpace.AOP_ADVICE); iterator.hasNext(); ) {
            createAdvice(iterator.next());
        }
    }

    /**
     * 通过包名来扫描对应的advice，并创建实例
     * @param advicesE
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void createAdviceFromPackage(Element advicesE) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] packages = advicesE.attributeValue(ApplicationStandardSpace.AOP_ADVICES_SCAN_PACKAGES).split(",");
        for (String packageName : packages) {
            String packagePath = packageName.replace(".", "/");
            URL url = FileOperator.createURLFromBuildPath(packagePath);
            if (url == null)
                continue;

            File packageFile = new File(url.getFile());
            if (!packageFile.exists())
                continue;

            File[] classFiles = packageFile.listFiles();
            for (File classFile : classFiles) {
                if (classFile.isDirectory()) {
                    continue;
                }
                String className = packageName + "." + classFile.getName().substring(0, classFile.getName().lastIndexOf(".class"));
                Class<?> clazz = Class.forName(className);
                Pointcut pointcut = clazz.getAnnotation(Pointcut.class);
                //如果有pointcut注解，并且其切点不为空
                if (pointcut != null && !pointcut.value().equals("")) {
                    Object o = clazz.newInstance();
                    String id = StringOperator.firstToLowerCase(clazz.getSimpleName());
                    //放入到容器中
                    aopBeans.put(id, o);
                    pointcuts.put(id,pointcut.value());
                }
            }
        }
    }

    /**
     * 根据advice标签创建一个实例
     * @param adviceE
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void createAdvice(Element adviceE) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String className=adviceE.attributeValue(ApplicationStandardSpace.AOP_ADVICE_CLASS_NAME);
        String id =adviceE.attributeValue(ApplicationStandardSpace.AOP_ADVICE_ID);
        String pointcut = adviceE.attributeValue(ApplicationStandardSpace.AOP_ADVICE_POINTCUT);
        if(className==null||className.equals("")||"".equals(pointcut)||pointcut==null){
            return;
        }
        Class<?> clazz = Class.forName(className);
        if(id==null||id.equals("")){
            id = StringOperator.firstToLowerCase(clazz.getSimpleName());
        }

        aopBeans.put(id,clazz.newInstance());
        pointcuts.put(id,pointcut);
    }

    public Map<String, Object> getAopBeans() {
        return aopBeans;
    }

    public Map<String, String> getPointcuts() {
        return pointcuts;
    }
}
