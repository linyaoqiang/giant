package com.giant.sorm3.test;

public class Test2 {
    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println(Test2.class.getMethod("m").getName());
        System.out.println(Test2.class.getMethod("m").getDeclaringClass().getName());
    }
    public void m(){

    }
}
