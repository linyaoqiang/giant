package com.giant.sorm3.handler;

import com.giant.commons.opeator.FileOperator;
import com.giant.sorm3.exception.ParseParamException;
import com.giant.sorm3.utils.SormUtils;
import org.dom4j.Element;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class XMLParameterHandler extends SimpleParameterHandler {
    @Override
    public Object[] parseParameter(Method method, Object[] args) throws ParseParamException {
        if(SormUtils.isDMLMethod(method)||SormUtils.isSelectMethod(method)){
            return super.parseParameter(method,args);
        }

        Element root = FileOperator.createRootElement(FileOperator.classToPath(method.getDeclaringClass()) + ".xml");
        String methodName = method.getName();
        String sql = null;
        List<Object> params = null;
        for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
            Element element = it.next();
            if (methodName.equals(element.attributeValue("method"))) {
                sql = element.getText().trim();
                break;
            }
        }
        
        if (sql != null) {
            List<String> paramNames = parseSqlParam(sql);
            params = parseSqlParamValue(args, paramNames);
        }else{
            throw new ParseParamException("未能找到对应的SQL语句");
        }
        if (params == null) {
            return null;
        }
        return params.toArray();
    }
}
