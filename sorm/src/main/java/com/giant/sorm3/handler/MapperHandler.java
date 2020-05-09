package com.giant.sorm3.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.giant.commons.opeator.FileOperator;
import com.giant.sorm3.utils.SormUtils;

public class MapperHandler implements InvocationHandler {
    private Executor executor;

    public MapperHandler(Executor executor) {
        super();
        this.executor = executor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = null;
        if (SormUtils.isSelectMethod(method)) {
            object = executor.doSelect(method, args);
        } else if (SormUtils.isDMLMethod(method)) {
            synchronized (MapperHandler.class) {
                int index = executor.doDML(method, args);
                return index;
            }
        }
        return object;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

}
