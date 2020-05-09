package com.giant.sorm3.handler;

import com.giant.commons.opeator.FileOperator;

import java.io.InputStream;
import java.lang.reflect.Method;

public class XMLMapperHandler extends MapperHandler {

    public XMLMapperHandler(Executor executor) {
        super(executor);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = super.invoke(proxy, method, args);
        InputStream in=FileOperator.createInByClassPath( FileOperator.classToPath(method.getDeclaringClass()) +".xml");
        if (object==null&&in!= null) {
            in.close();
            if(method.getReturnType().equals(int.class)){
                synchronized (MapperHandler.class){
                    return ((XMLExecutor)getExecutor()).xmlExecute(method,args);
                }
            }else{
                return ((XMLExecutor)getExecutor()).xmlExecute(method,args);
            }

        }
        return object;
    }
}
