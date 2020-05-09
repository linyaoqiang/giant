package com.giant.core;

import com.giant.aop.AopContext;
import com.giant.aop.AopProxyFactory;
import com.giant.bean.BeanInformation;
import com.giant.bean.Property;
import com.giant.commons.opeator.ReflectOperator;
import com.giant.commons.opeator.StringOperator;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Map;

public class DefinitionProperty {
    private Logger logger = Logger.getLogger(DefinitionProperty.class);
    private AopProxyFactory factory = AopProxyFactory.INSTANCE;
    private AopContext aop;

    /**
     * 为Bean的属性进行注入
     * @param beans
     * @param infos
     * @param beanFactory
     * @throws NoSuchFieldException
     */
    public void defineProperty(Map<String, Object> beans, Map<String, BeanInformation> infos, AbstractBeanFactory beanFactory) throws NoSuchFieldException {
        for (String key : infos.keySet()) {
            Object o = beans.get(key);
            Class<?> clazz = o.getClass();
            BeanInformation info = infos.get(key);
            if (info.getProperties() == null)
                continue;

            //遍历每一个Property
            for (Property property : info.getProperties()) {
                String name = property.getName();
                String value = property.getValue();
                Field field = clazz.getDeclaredField(name);
                Class<?> fieldClass = field.getType();
                if (value != null) {
                    //注入值
                    injectValue(fieldClass, field, o, value);
                    continue;
                }
                String ref = property.getRef();
                if (ref == null || ref.equals("")) {
                    ref = StringOperator.firstToLowerCase(fieldClass.getSimpleName());
                }
                //注入引用
                boolean flag = injectRef(beans, fieldClass, field, o, ref);
                if (!flag)
                    logger.error("Unable to inject " + name + " for " + info);
            }
        }
    }

    /**
     * 注入引用
     *
     * @param beans      Bean的实例容器
     * @param fieldClass 字段类型
     * @param field      字段
     * @param object     实例
     * @param ref        引用标识符
     * @return
     */
    public boolean injectRef(Map<String, Object> beans, Class<?> fieldClass, Field field, Object object, String ref) {
        for (String keyFRef : beans.keySet()) {
            Object o = beans.get(keyFRef);
            Class<?> clazz = o.getClass();
            if (aop != null && aop.isUseAop()) {
                //使用aop渲染后的Bean
                o = aop.getAopBeans().get(keyFRef);
            }
            //根据类型进行注入
            if (fieldClass.equals(clazz) || equalsInterfaces(fieldClass, clazz)) {
                ReflectOperator.doFieldSet(field, object, o);
                return true;
            }
            //根据名称进行诸如
            if (keyFRef.equals(ref)) {
                ReflectOperator.doFieldSet(field, object, o);
                return true;
            }
        }

        //注入失败
        return false;
    }

    /**
     * 注入值
     * @param fieldClass 字段类型
     * @param field      字段
     * @param object     实例
     * @param value      值
     */
    public void injectValue(Class<?> fieldClass, Field field, Object object, String value) {
        /**
         * 判断类型，并进行注入
         */
        if (value.equals("") || fieldClass.equals(String.class)) {
            ReflectOperator.doFieldSet(field, object, value);
        } else if (ReflectOperator.isInteger(fieldClass) || ReflectOperator.isInt(fieldClass)) {
            ReflectOperator.doFieldSet(field, object, Integer.parseInt(value));
        } else if (ReflectOperator.isLongObject(fieldClass) || ReflectOperator.isLong(fieldClass)) {
            ReflectOperator.doFieldSet(field, object, Long.parseLong(value));
        } else if (ReflectOperator.isDoubleObject(fieldClass) || ReflectOperator.isDouble(fieldClass)) {
            ReflectOperator.doFieldSet(field, object, Double.parseDouble(value));
        } else if (float.class.equals(fieldClass) || Float.class.equals(fieldClass)) {
            ReflectOperator.doFieldSet(field, object, Float.parseFloat(value));
        } else if (boolean.class.equals(fieldClass) || Boolean.class.equals(fieldClass)) {
            ReflectOperator.doFieldSet(field, object, Boolean.parseBoolean(value));
        }
    }

    /**
     * fieldClass是否是该clazz接口的一个类型
     *
     * @param fieldClass 字段类型
     * @param clazz
     * @return true|false
     */
    public boolean equalsInterfaces(Class<?> fieldClass, Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces == null)
            return false;
        boolean flag = false;
        /**
         * 是否匹配接口
         */
        for (Class<?> iC : interfaces) {
            if (iC.equals(fieldClass)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void setAop(AopContext aop) {
        this.aop = aop;
    }
}
