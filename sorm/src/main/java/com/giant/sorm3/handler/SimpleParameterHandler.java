package com.giant.sorm3.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.giant.commons.opeator.ReflectOperator;
import com.giant.sorm3.annotation.Delete;
import com.giant.sorm3.annotation.Insert;
import com.giant.sorm3.annotation.Select;
import com.giant.sorm3.annotation.Update;
import com.giant.sorm3.exception.ParseParamException;
import com.giant.sorm3.utils.SormUtils;
import org.apache.log4j.Logger;


@SuppressWarnings("all")
public class SimpleParameterHandler implements ParameterHandler {
    private Logger logger = Logger.getLogger(SimpleParameterHandler.class);

    @Override
    public Object[] parseParameter(Method method, Object[] args) throws ParseParamException {
        // TODO Auto-generated method stub
        List<Object> list = null;
        List<String> paramNames = null;
        String sql = null;

        if (SormUtils.isInsertMethod(method)) {
            sql = method.getAnnotation(Insert.class).value();
        } else if (SormUtils.isDeleteMethod(method)) {
            sql = method.getAnnotation(Delete.class).value();
        } else if (SormUtils.isUpdateMethod(method)) {
            sql = method.getAnnotation(Update.class).value();
        } else if (SormUtils.isSelectMethod(method)) {
            sql = method.getAnnotation(Select.class).value();
        }


        if (sql == null || sql.equals("")) {
            return null;
        }
        paramNames = parseSqlParam(sql);
        list = parseSqlParamValue(args, paramNames);
        return list == null ? null : list.toArray();
    }

    public List<String> parseSqlParam(String sql) throws ParseParamException {
        if (sql == null || sql.equals("")) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        while (SormUtils.hasParam(sql)) {

            sql = sql.substring(sql.indexOf("#{") + 2);
            String paramName = sql.substring(0, sql.indexOf("}"));
            sql = sql.substring(sql.indexOf("}") + 1);
            list.add(paramName);
        }
        return list;
    }

    public List<Object> parseSqlParamValue(Object[] args, List<String> paramNames) throws ParseParamException {
        if (paramNames == null || paramNames.size() == 0) {
            return null;
        }
        List<Object> list = new ArrayList<Object>();
        for (String name : paramNames) {

            try {
                int number = Integer.parseInt(name);
                list.add(args[number]);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                logger.debug("参数为对象中的数据:" + name);
                Object object = parseSqlParamValueByName(args, name);
                list.add(object);
            } catch (Exception e) {
                logger.error("参数数组越界，请检查你的参数!!" + e.getMessage());
            }
        }
        return list;

    }


    public Object parseSqlParamValueByName(Object[] args, String name) throws ParseParamException {
        for (Object arg : args) {
            Class<?> clazz = arg.getClass();
            if (String.class.equals(clazz) || ReflectOperator.isNumberObject(clazz) || ReflectOperator.isNumber(clazz)) {
                continue;
            }
            if (ReflectOperator.isList(clazz, arg)) {
                throw new ParseParamException("目前不支持List集合的参数，因为没有意义");
            }
            if (ReflectOperator.isMap(clazz, arg)) {
                Map map = (Map) arg;
                Object value = map.get(name);
                System.out.println(value);
                if (value != null) {
                    return value;
                }
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.equals(name)) {

                    return ReflectOperator.doFieldGet(field, arg);

                }
            }
        }
        return null;
    }
}
