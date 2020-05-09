package com.giant.core;

public interface BeanFactory {
    /**
     * 获取Bean
     * @param id    标识符
     * @param clazz Bean的Class
     * @param <T>   Bean的类型
     * @return Bean
     */
    <T> T getBean(String id,Class<T> clazz);
}
