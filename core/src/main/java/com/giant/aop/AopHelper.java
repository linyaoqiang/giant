package com.giant.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

public class AopHelper {
    public static final AopHelper HELPER= new AopHelper();
    private AopHelper(){

    }
    public String getAopMethodName(Class<?> clazz, Method method) {
        String className=clazz.getCanonicalName();
        String methodName=method.getName();
        Parameter[] parameters=method.getParameters();
        StringBuffer paramName=new StringBuffer("(");
        for(Parameter parameter:parameters) {
            paramName.append(parameter.getType().getSimpleName()+" ");
            paramName.append(parameter.getName()+",");
        }
        if(paramName.lastIndexOf(",")!=-1)
            paramName.replace(paramName.length()-1, paramName.length(),")");
        else
            paramName.append(")");
        return className+"."+methodName+paramName.toString();
    }

    public String getAopMethodName(Method method){
        return getAopMethodName(method.getDeclaringClass(),method);
    }

    public String getPointName(String pointName) {
        pointName=pointName.replace("execution(","").trim();
        pointName=pointName.substring(0,pointName.lastIndexOf(")"));
        pointName=pointName.replace("*","\\w*");
        pointName=pointName.replace("...","[\\w\\W]*");
        pointName=pointName.replace(".","\\.");
        pointName=pointName.replace("(", "\\(");
        pointName=pointName.replace(")","\\)");
        return pointName;
    }

    public String getAopMethodName(Object o,Method method){
        return getAopMethodName(o.getClass(),method);
    }

    public boolean isAopPointcut(String pointName,Class<?> clazz,Method method){
        return Pattern.matches(getPointName(pointName),getAopMethodName(clazz,method));
    }

    public boolean isAopPointcut(String matchesPointName,Method method){
        return Pattern.matches(matchesPointName,getAopMethodName(method));
    }
}
