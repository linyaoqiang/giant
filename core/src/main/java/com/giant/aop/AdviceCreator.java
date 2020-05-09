package com.giant.aop;

import com.giant.annotation.Pointcut;
import com.giant.commons.opeator.FileOperator;
import com.giant.commons.opeator.StringOperator;
import org.dom4j.Element;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdviceCreator {
    private Map<String,Object> aopBeans=new HashMap<>();
    private Map<String,String> pointcuts= new HashMap<>();
    public static final AdviceCreator ADVICE_CREATOR = new AdviceCreator();
    private AdviceCreator(){}
    public void createAdvice(String config) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element root = FileOperator.createRootElement(config);
        Element aopE = root.element("aop");
        if (aopE == null || !"jdk".equals(aopE.attributeValue("proxy"))) {
            return;
        }

        Element advicesE = aopE.element("advices");
        if (advicesE != null) {
            createAdviceFromPackage(advicesE);
        }
        for (Iterator<Element> iterator = aopE.elementIterator("advice"); iterator.hasNext(); ) {
            createAdvice(iterator.next());
        }
    }

    public void createAdviceFromPackage(Element advicesE) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] packages = advicesE.attributeValue("scan-packages").split(",");
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
                if (pointcut != null && !pointcut.value().equals("")) {
                    Object o = clazz.newInstance();
                    String id = StringOperator.firstToLowerCase(clazz.getSimpleName());
                    aopBeans.put(id, o);
                    pointcuts.put(id,pointcut.value());
                }
            }
        }
    }

    public void createAdvice(Element adviceE) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String className=adviceE.attributeValue("class");
        String id =adviceE.attributeValue("id");
        String pointcut = adviceE.attributeValue("pointcut");
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
