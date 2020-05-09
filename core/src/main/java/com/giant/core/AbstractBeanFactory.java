package com.giant.core;

import com.giant.aop.*;
import com.giant.aop.interceptor.DefaultAopInterceptorChain;
import com.giant.bean.BeanInformation;
import com.giant.bean.ConstructorArg;
import com.giant.commons.opeator.ReflectOperator;
import com.giant.config.ApplicationConfiguration;
import com.giant.exception.CreateBeanException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.*;

public abstract class AbstractBeanFactory implements BeanFactory {
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, BeanInformation> infos = new HashMap<>();
    private AnnotationScan scanner = new AnnotationScanner();
    private DefinitionProperty definite = new DefinitionProperty();
    private AopContext aop;
    private AopProxyFactory aopProxyFactory = AopProxyFactory.INSTANCE;
    private Map<String, Object> aopBeans = null;
    private int count=0;

    public AbstractBeanFactory(ApplicationConfiguration configuration, AopContext aop) {
        this.aop = aop;
        initBeans(configuration);
    }

    public void initBeans(ApplicationConfiguration configuration) {
        try {
            //获得一些advice实例，并初始化容器
            aopBeans = aop.getAopBeans();
            //获取所有配置bean配置信息，包括通过注解来映射的bean的配置
            configuration.getInformationList().addAll(scanner.scan(configuration).values());
            //初始化所有空构造方法的实例，注解默认使用空构造
            initNotRefBeans(configuration);
            //初始化所有非空构造方法的实例
            initRefBeans(configuration);
            //初始化bean属性中的注解内容映射成Property对象
            scanner.injectionProcess(beans, infos);

            //注入属性的对象声明aop，为其注入做准备
            definite.setAop(aop);

            //为所有的bean的配置property属性注入对应的实例
            definite.defineProperty(beans, infos, this);

            //如果使用aop编程，则beans赋值为aopBeans
            if(aop.isUseAop()){
                beans = aopBeans;
                //不需要这个属性了，当然这一步不是必要的
                aopBeans = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *  初始化构造方法带有引用的bean
     * @param configuration 应用程序上下文配置信息
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws CreateBeanException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     */
    private void initRefBeans(ApplicationConfiguration configuration) throws NoSuchMethodException, IllegalAccessException, InstantiationException, CreateBeanException, InvocationTargetException, ClassNotFoundException {
        for (BeanInformation information : configuration.getInformationList()) {
            String id = information.getId();
            //创建refBean
            Object o = createRefBean(configuration, information, null);
            beans.put(id, o);
            //判断是否使用aop，其判断逻辑主要是有没有切面
            if (aop.isUseAop()) {
                // 获得经过aop渲染的实例
                AopProxy proxy = aopProxyFactory.createAopProxy(o,new DefaultAopHandler(aop.getChain(),o));
                //放入到aopBeans
                aopBeans.put(id,proxy.getProxy());
            }
            infos.put(id, information);
        }
        configuration.getInformationList().clear();
    }

    public Object createRefBean(ApplicationConfiguration configuration, BeanInformation information, CreateTemplate template) throws InvocationTargetException, NoSuchMethodException, InstantiationException, CreateBeanException, IllegalAccessException, ClassNotFoundException {
        // 通过调用创建没有ref(引用)的bean的进行创建，这里使用了模板方法
        return createBean(configuration, information, new CreateTemplate() {
            @Override
            public Object create(ApplicationConfiguration configuration, BeanInformation information, ConstructorArg arg) throws ClassNotFoundException, CreateBeanException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
                Class<?> clazz = Class.forName(arg.getType());
                String ref = arg.getRef();
                for (String key : beans.keySet()) {
                    if (key.equals(ref)) {
                        /*
                            如果匹配了beans的key，则说明了aopBeans中有对应的实例
                            则我们选择构造参数使用渲染过后的
                            如果不清楚为什么有aop渲染过后的实例，可以看initRefBeans或者initNotRefBeans方法
                        */
                        return aopBeans.get(key);
                    }
                }
                for (BeanInformation info : configuration.getInformationList()) {
                    if (ref.equals(info.getId())) {
                        Object o = createRefBean(configuration, info, this);
                        if (aop.isUseAop()) {
                            AopProxy proxy = aopProxyFactory.createAopProxy(o, new DefaultAopHandler(aop.getChain(), o));
                            o = proxy.getProxy();
                            aopBeans.put(info.getId(), o);
                            count++;
                        }
                        return o;
                    }
                }
                throw new CreateBeanException("unable to inject, possibly missing bean :<ref>==>" + ref);
            }
        });
    }

    private void initNotRefBeans(ApplicationConfiguration configuration) throws CreateBeanException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<BeanInformation> notRefBeans = new ArrayList<>();
        for (BeanInformation information : configuration.getInformationList()) {
            String className = information.getClassName();
            if (className == null || "".equals(className)) {
                throw new CreateBeanException("Cannot create bean, class name is empty string or null :" + className);
            }
            boolean isNotRef = true;
            if (information.getArgs() != null) {
                for (ConstructorArg arg : information.getArgs()) {
                    if (arg.getRef() != null && arg.getValue() != null) {
                        throw new CreateBeanException("Ref and value cannot exist at the same time");
                    }
                    if (arg.getRef() != null) {
                        isNotRef = false;
                        break;
                    }
                }
            }
            if (isNotRef) {
                String id = information.getId();
                Object obj = createBean(configuration, information, null);
                if (aop.isUseAop()) {
                    AopProxy proxy = aopProxyFactory.createAopProxy(obj, new DefaultAopHandler(aop.getChain(), obj));
                    aopBeans.put(id,proxy.getProxy());
                }
                beans.put(id, obj);
                notRefBeans.add(information);
                infos.put(id, information);
            }
        }
        configuration.getInformationList().removeAll(notRefBeans);
    }


    private Object createBean(ApplicationConfiguration configuration, BeanInformation information, CreateTemplate createTemplate) throws CreateBeanException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        String className = information.getClassName();
        List<Class<?>> classes = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        Class<?> oClass = ReflectOperator.getClass(className);
        if (oClass == null) {
            return null;
        }
        if (information.getArgs() != null) {
            for (ConstructorArg arg : information.getArgs()) {
                String type = arg.getType();
                String value = arg.getValue();
                boolean flag = parseBasic(type, value, classes, params);
                if (flag) {
                    continue;
                }
                Class<?> clazz = ReflectOperator.getClass(type);
                classes.add(clazz);
                flag = parsePacking(clazz, value, params);
                if (flag)
                    continue;
                if (createTemplate != null) {
                    params.add(createTemplate.create(configuration, information, arg));
                }
            }
        }

        return oClass.getConstructor(classes.toArray(new Class<?>[0])).newInstance(params.toArray());
    }

    public boolean parseBasic(String type, String value, List<Class<?>> classes, List<Object> params) {
        boolean flag = false;
        if (type.equals("int")) {
            classes.add(int.class);
            params.add(Integer.parseInt(value));
            flag = true;
        } else if (type.equals("float")) {
            classes.add(float.class);
            params.add(Float.parseFloat(value));
            flag = true;
        } else if (type.equals("double")) {
            classes.add(double.class);
            params.add(Double.parseDouble(value));
            flag = true;
        } else if (type.equals("boolean")) {
            classes.add(boolean.class);
            params.add(Boolean.parseBoolean(value));
            flag = true;
        } else if (type.equals("long")) {
            classes.add(long.class);
            params.add(Long.parseLong(value));
        }
        return flag;
    }

    public boolean parsePacking(Class<?> clazz, String value, List<Object> params) {
        boolean flag = false;
        if (String.class.equals(clazz)) {
            params.add(value);
            flag = true;
        } else if (ReflectOperator.isInteger(clazz)) {
            params.add(Integer.parseInt(value));
        } else if (ReflectOperator.isDoubleObject(clazz)) {
            params.add(Double.parseDouble(value));
            flag = true;
        } else if (Float.class.equals(clazz)) {
            params.add(Float.parseFloat(value));
            flag = true;
        } else if (Boolean.class.equals(clazz)) {
            params.add(Boolean.parseBoolean(value));
            flag = true;
        } else if (ReflectOperator.isLongObject(clazz)) {
            params.add(Long.parseLong(value));
            flag = true;
        }
        return flag;
    }


    public Map<String, Object> getBeans() {
        return beans;
    }

    public void setBeans(Map<String, Object> beans) {
        this.beans = beans;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        return (T) beans.get(id);
    }

    public Map<String, Object> getAopBeans() {
        return aopBeans;
    }
}
