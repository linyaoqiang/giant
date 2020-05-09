package com.giant.core;

public interface BeanFactory {
    <T> T getBean(String id,Class<T> clazz);
}
