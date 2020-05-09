package com.giant.advice.test;


import com.giant.annotation.BeforeAdvice;

public class BAdvice {
    @BeforeAdvice
    public void Before(Object[] args){
        System.out.println(this.getClass()+":before"+args);
    }
}
