package com.giant.core;

import com.giant.annotation.*;
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

        Element context = root.element("context");

        if (context == null || context.attributeValue("scan-packages") == null)
            return null;
        String[] packageNames = context.attributeValue("scan-packages").split(",");
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
                if (id == null)
                    continue;

                if (id.equals("")) {
                    id = StringOperator.firstToLowerCase(clazz.getSimpleName());
                }
                info.setId(id);
                info.setClassName(className);
                infos.put(id, info);
            }
        }
        return infos;
    }

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

    public Property fieldValue(Field field) {
        Property property = null;
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
