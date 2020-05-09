package com.giant.core;

import com.giant.bean.BeanInformation;
import com.giant.config.ApplicationConfiguration;

import java.util.Map;

public interface AnnotationScan {
    /**
     * 扫描注解来创建Bean的配置信息
     * @param configuration
     * @return
     * @throws ClassNotFoundException
     */
    Map<String, BeanInformation>  scan(ApplicationConfiguration configuration) throws ClassNotFoundException;

    /**
     * 扫描Bean中的属性中的注解并将其映射成Property，并存储在Bean的配置信息中
     * @param beans
     * @param infos
     */
    void injectionProcess(Map<String,Object> beans,Map<String,BeanInformation> infos);
}
