package com.giant.sorm3.handler;

import java.lang.reflect.Method;

public interface Executor {
	Object doSelect(Method method, Object[] args);
	int doDML(Method method, Object[] args);
}
