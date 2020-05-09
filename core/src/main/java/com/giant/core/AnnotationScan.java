package com.giant.core;

import com.giant.bean.BeanInformation;
import com.giant.config.ApplicationConfiguration;

import java.util.Map;

public interface AnnotationScan {
    Map<String, BeanInformation>  scan(ApplicationConfiguration configuration) throws ClassNotFoundException;
    void injectionProcess(Map<String,Object> beans,Map<String,BeanInformation> infos);
}
