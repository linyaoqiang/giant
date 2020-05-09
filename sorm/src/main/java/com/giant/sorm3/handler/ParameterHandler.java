package com.giant.sorm3.handler;

import java.lang.reflect.Method;

import com.giant.sorm3.exception.ParseParamException;

public interface ParameterHandler {

	Object[] parseParameter(Method method, Object[] args) throws ParseParamException;
}
