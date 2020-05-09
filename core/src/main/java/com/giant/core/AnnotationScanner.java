package com.giant.core;

import com.giant.annotation.*;
import com.giant.bean.ApplicationStandardSpace;
import com.giant.bean.BeanInformation;
import com.giant.bean.Property;
import com.giant.commons.opeator.FileOperator;
import com.giant.commons.opeator.StringOperator;
import com.giant.config.ApplicationConfiguration;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationScanner implements AnnotationScan {
    private Logger logger = Logger.getLogger(AnnotationScanner.class);

    @Override
    public Map<String, BeanInformation> scan(ApplicationConfiguration configuration) throws ClassNotFoundException {
        Element root = FileOperator.createRootElement(configuration.getConfig());
        if (root == null)
            return null;

        Element context = root.element(ApplicationStandardSpace.CONTEXT);
        //如果没有context标签或者context没有scan-packages这个属性则直接返回null
        if (context == null || context.attributeValue(ApplicationStandardSpace.CONTEXT_SCAN_PACKAGES) == null)
            return null;
        String[] packageNames = context.attributeValue(ApplicationStandardSpace.CONTEXT_SCAN_PACKAGES).split(",");
        Map<String, BeanInformation> infos = new HashMap<>();
        for (String packageName : packageNames) {
            String packagePath = packageName.replace(".", "/");
            URL url = FileOperator.createURLFromBuildPath(packagePath);
            File packageFile = new File(url.getFile());
            if (!packageFile.exists()) {
                logger.error("package not found :" + packageName);
                continue;
            }

            File[] classFiles = packageFile.listFiles();
            if (classFiles == null) {
                logger.error("There are no classes under the package");
                continue;
            }

            for (File classFile : classFiles) {
                if (classFile.isDirectory()) {
                    continue;
                }
                String className = packageName + "." + classFile.getName().substring(0, classFile.getName().lastIndexOf(".class"));
                Class<?> clazz = Class.forName(className);
                BeanInformation info = new BeanInformation();
                String id = annotationValue(clazz);
                /**
                 * 如果id为null。说明没有配置注解
                 */
                if (id == null)
                    continue;
                //如果id为空串, id为类名的首字母小写
                if (id.equals("")) {
                    id = StringOperator.firstToLowerCase(clazz.getSimpleName());
                }
                //设置配置信息
                info.setId(id);
                info.setClassName(className);
                //添加
                infos.put(id, info);
            }
        }
        return infos;
    }

    /**
     * 查看是否有配置注解，并返回注解中的值
     * @param clazz beanClass
     * @return  注解中的值 String
     */
    public String annotationValue(Class<?> clazz) {
        if (clazz.getDeclaredAnnotation(Repository.class) != null) {
            return clazz.getDeclaredAnnotation(Repository.class).value();
        } else if (clazz.getDeclaredAnnotation(Service.class) != null) {
            return clazz.getDeclaredAnnotation(Service.class).value();
        } else if (clazz.getDeclaredAnnotation(Controller.class) != null) {
            return clazz.getDeclaredAnnotation(Controller.class).value();
        } else if (clazz.getDeclaredAnnotation(Component.class) != null) {
            return clazz.getDeclaredAnnotation(Component.class).value();
        }
        return null;
    }

    /**
     * 将字段封装成Property并返还
     * @param field
     * @return  Property
     */
    public Property fieldValue(Field field) {
        Property property = null;
        //如果使用Autowired进行进行配置，则映射成一个引用Property
        if (field.getDeclaredAnnotation(Autowired.class) != null) {
            Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
            property = new Property();
            property.setName(field.getName());
            String ref = autowired.value();
            if (ref.equals("")) {
                ref = StringOperator.firstToLowerCase(field.getType().getSimpleName());
            }
            property.setRef(ref);
        } else if (field.getDeclaredAnnotation(Value.class) != null) {
            //如果使用Value进行配置，则解析成valueProperty
            Value value = field.getDeclaredAnnotation(Value.class);
            property = new Property();
            property.setName(field.getName());
            property.setValue(value.value());
        }
        return property;
    }
    @Override
    public void injectionProcess(Map<String, Object> beans, Map<String, BeanInformation> infos) {
        for (String key : beans.keySet()) {
            Object o = beans.get(key);
            Class<?> clazz = o.getClass();
            Field[] fields = clazz.getDeclaredFields();
            BeanInformation information = infos.get(key);
            for (Field field : fields) {
                Property property = fieldValue(field);
                if (property != null) {
                    List<Property> properties = information.getProperties();
                    if (properties == null) {
                        properties = new ArrayList<>();
                        information.setProperties(properties);
                    }
                    properties.add(property);
                }
            }
        }
    }
}
