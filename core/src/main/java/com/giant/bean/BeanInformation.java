package com.giant.bean;

import java.util.List;

public class BeanInformation {
    private String id;
    private String className;
    private String factoryMethod;
    private List<ConstructorArg> args;
    private List<Property> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }



    public List<ConstructorArg> getArgs() {
        return args;
    }

    public void setArgs(List<ConstructorArg> args) {
        this.args = args;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    @Override
    public String toString() {
        return "BeanInformation{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                ", factoryMethod='" + factoryMethod + '\'' +
                ", args=" + args +
                ", properties=" + properties +
                '}';
    }
}
