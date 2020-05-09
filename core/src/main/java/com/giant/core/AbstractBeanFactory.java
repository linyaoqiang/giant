package com.giant.core;

import com.giant.aop.*;
import com.giant.bean.BeanInformation;
import com.giant.bean.ConstructorArg;
import com.giant.commons.opeator.ReflectOperator;
import com.giant.config.ApplicationConfiguration;
import com.giant.exception.CreateBeanException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractBeanFactory implements BeanFactory {
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, BeanInformation> infos = new HashMap<>();
    private AnnotationScan scanner = new AnnotationScanner();
    private DefinitionProperty definite = new DefinitionProperty();
    private AopContext aop;
    private AopProxyFactory aopProxyFactory = AopProxyFactory.INSTANCE;
    private Map<String, Object> aopBeans = null;
    private int count = 0;

    public AbstractBeanFactory(ApplicationConfiguration configuration, AopContext aop) {
        this.aop = aop;
        initBeans(configuration);
    }

    /**
     * 初始化Bean
     *
     * @param configuration
     */
    public void initBeans(ApplicationConfiguration configuration) {
        try {
            //获得一些advice(通知类)实例，并初始化aopBeans容器
            aopBeans = aop.getAopBeans();


            //获取所有配置bean配置信息，包括通过注解来映射的bean的配置
            Map<String,BeanInformation> is=scanner.scan(configuration);
            if(is!=null) {
                configuration.getInformationList().addAll(scanner.scan(configuration).values());
            }


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
            if (aop.isUseAop()) {
                beans = aopBeans;
                //不需要这个属性了，当然这一步不是必要的
                aopBeans = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化构造方法带有引用的bean
     *
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
            //如果已经创建过实例,则不再创建
            if (beans.get(id) != null) {
                return;
            }

            //创建refBean
            Object o = createRefBean(configuration, information, null);
            beans.put(id, o);
            //判断是否使用aop，其判断逻辑主要是有没有切面
            if (aop.isUseAop()) {
                // 获得经过aop渲染的实例
                AopProxy proxy = aopProxyFactory.createAopProxy(o, new DefaultAopHandler(aop.getChain(), o));
                //放入到aopBeans
                aopBeans.put(id, proxy.getProxy());
            }
            //所有构造方法有引用的bean的信息也放入到infos中，为了后面的初始化属性和注入属性
            infos.put(id, information);
        }
        //清空所有来自配置信息的内容，这一步不是必要的
        configuration.getInformationList().clear();
        //这个也不是必要的
        //configuration.setInformationList(null);
    }

    /**
     * 创建带有引用的Bean
     *
     * @param configuration
     * @param information
     * @param template
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws CreateBeanException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
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
                // 如果没有找到对应的引用，则初始化需要引用的bean，这是一个递归的过程
                for (BeanInformation info : configuration.getInformationList()) {
                    if (ref.equals(info.getId())) {
                        Object o = createRefBean(configuration, info, this);
                        if (aop.isUseAop()) {
                            //将创建好Bean进行Aop渲染
                            AopProxy proxy = aopProxyFactory.createAopProxy(o, new DefaultAopHandler(aop.getChain(), o));
                            //将获得渲染后的实例
                            o = proxy.getProxy();
                            //放入到aopBeans中
                            aopBeans.put(info.getId(), o);
                        }
                        //返回渲染后的实例或者原来的实例
                        return o;
                    }
                }
                throw new CreateBeanException("unable to inject, possibly missing bean :<ref>==>" + ref);
            }
        });
    }

    /**
     * 初始化构造方法没有引用的Bean
     *
     * @param configuration
     * @throws CreateBeanException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private void initNotRefBeans(ApplicationConfiguration configuration) throws CreateBeanException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<BeanInformation> notRefBeans = new ArrayList<>();
        for (BeanInformation information : configuration.getInformationList()) {
            String className = information.getClassName();
            if (className == null || "".equals(className)) {
                throw new CreateBeanException("Cannot create bean, class name is empty string or null :" + className);
            }
            boolean isNotRef = true;
            //循环遍历，查看其是否是一个非引用的构造方法
            if (information.getArgs() != null) {
                for (ConstructorArg arg : information.getArgs()) {
                    //ref和value不能同时存在
                    if (arg.getRef() != null && arg.getValue() != null) {
                        throw new CreateBeanException("Ref and value cannot exist at the same time");
                    }
                    if (arg.getRef() != null) {
                        isNotRef = false;
                        break;
                    }
                }
            }
            // 如果是一个非引用的构造方法，则实例该Bean
            if (isNotRef) {
                String id = information.getId();
                Object obj = createBean(configuration, information, null);
                //如果使用aop
                if (aop.isUseAop()) {
                    //创建对应的aop代理
                    AopProxy proxy = aopProxyFactory.createAopProxy(obj, new DefaultAopHandler(aop.getChain(), obj));
                    //将代理后对象放入到aopBeans容器中
                    aopBeans.put(id, proxy.getProxy());
                }
                //放入到bean容器中
                beans.put(id, obj);
                //该该实例过的bean的配置信息放入到一个集合中
                notRefBeans.add(information);
                //infos存放了所有的配置信息，将该配置信息添加到infos中
                infos.put(id, information);
            }
        }
        //应用程序上下文的配置信息中清空所有非引用构造方法实例的配置信息，用于下一次创建构造方法有引用的bean
        configuration.getInformationList().removeAll(notRefBeans);
    }

    /**
     * 创建Bean的核心代码
     *
     * @param configuration  应用程序配置上下文
     * @param information    Bean的配置信息
     * @param createTemplate 创建模板
     * @return Bean
     * @throws CreateBeanException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private Object createBean(ApplicationConfiguration configuration, BeanInformation information, CreateTemplate createTemplate) throws CreateBeanException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        String className = information.getClassName();
        Class<?> oClass = ReflectOperator.getClass(className);
        //如果该类不存在，直接返回null
        if (oClass == null) {
            return null;
        }
        //存放获取构造方法的Class信息的集合
        List<Class<?>> classes = new ArrayList<>();
        //存放获取构造方法参数的集合
        List<Object> params = new ArrayList<>();

        //如果有构造参数
        if (information.getArgs() != null) {
            //遍历所有的构造方法
            for (ConstructorArg arg : information.getArgs()) {
                //获得类型，是一个类的全限定路径
                String type = arg.getType();
                //获取value
                String value = arg.getValue();
                //是否可以解析为基本数据类型
                boolean flag = parseBasic(type, value, classes, params);
                if (flag) {
                    //如果可以，则直接进入下一次循环
                    continue;
                }

                Class<?> clazz = ReflectOperator.getClass(type);
                classes.add(clazz);
                //不是基本数据类型，则是否可以解析为包装类或者是String
                flag = parsePacking(clazz, value, params);
                //如果可以直接进入下一次循环
                if (flag)
                    continue;

                //如果创建模板不为null，则通过创建模板进行解析
                if (createTemplate != null) {
                    params.add(createTemplate.create(configuration, information, arg));
                } else {
                    //无法解析类型，可能是引用其是list，map，因为没有写该类型，懒!
                    throw new CreateBeanException("cannot create bean,because this arg could not parse :" + arg);
                }
            }
        }
        //执行构造方法，并将对应的参数传入，将创建好的bean返回
        return oClass.getConstructor(classes.toArray(new Class<?>[0])).newInstance(params.toArray());
    }

    /**
     * 为构造方法提供类型和参数
     * @param type    类型 可能是 int,float,long,double,boolean,
     * @param value   值
     * @param classes 用于获取构造方法的Class集合
     * @param params  用于执行构造方法的参数集合
     * @return 解析是否完成
     */
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

    /**
     * 为构造方法提供类型和参数
     * @param clazz  类型
     * @param value  值
     * @param params 用于执行构造方法的参数集合
     * @return 解析是否完成
     */
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
