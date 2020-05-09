package com.giant.sorm3.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.giant.commons.opeator.ReflectOperator;
import com.giant.sorm3.annotation.Column;
import com.giant.sorm3.exception.ParseResultException;
import org.apache.log4j.Logger;


public class SimpleResultHandler implements ResultHandler {
    private Logger logger = Logger.getLogger(SimpleResultHandler.class);

    @Override
    public Object parseResultSet(Method method, ResultSet rs) throws Exception {
        Class<?> returnClass = method.getReturnType();
        return doCreate(method, rs, returnClass);
    }

    public Object doCreate(Method method, ResultSet rs, Class<?> clazz) throws Exception {
        if (ReflectOperator.isNumberObject(clazz) || ReflectOperator.isNumber(clazz)) {
            Number number = createNumber(rs);
            if (number == null) {
                return null;
            }
            if (Number.class.equals(clazz)) {
                return number;
            }
            return createNumber(number, clazz);
        } else if (String.class.equals(clazz)) {
            return createString(rs);
        } else if (ReflectOperator.isList(clazz, null)) {
            return createList(method, rs);
        } else if (ReflectOperator.isSet(clazz, null)) {
            throw new ParseResultException("不支持返回值类型Set,因为没有意义,Set可以使用List代替");
        } else if (ReflectOperator.isMap(clazz, null)) {
            String[] typeNames = ReflectOperator.getGenericReturnClassNameByMethod(method);
            if (typeNames == null || !"java.lang.Integer".equals(typeNames[0]) && typeNames.length == 2) {
                throw new ParseResultException("返回值类型误Map时必须满足条件:1,必须有泛型\t"
                        + "2,第一个泛型必须是Integer\t3,第三个类型必须是基本数据的封装类或者String，简单的JavaBean");
            }
            return createMap(method, rs);
        } else {
            return createBean(clazz, rs);
        }
    }

    public Map<Integer, Object> createMap(Method method, ResultSet rs) throws Exception {
        // TODO Auto-generated method stub
        String[] typeNames = ReflectOperator.getGenericReturnClassNameByMethod(method);
        if (typeNames[1] == null)
            return null;
        Map<Integer, Object> map = null;
        Class<?> valueClass = ReflectOperator.getClass(typeNames[1]);
        if (valueClass == null) {
            return null;
        }
        if (ReflectOperator.isList(valueClass, null) || ReflectOperator.isMap(valueClass, null)) {
            throw new ParseResultException("不支持Map中泛型是List或者Map的情况,将在后续版本中思考需不需要更新这种情况");
        }
        Object value = null;
        int index = 0;
        while ((value = doCreate(null, rs, valueClass)) != null) {
            index++;
            if (map == null) {
                map = new HashMap<Integer, Object>();
            }
            map.put(index, value);
        }
        return map;

    }

    public List<Object> createList(Method method, ResultSet rs) throws Exception {
        // TODO Auto-generated method stub
        String typeName = ReflectOperator.getGenericClassName4List(method.getGenericReturnType());
        if (typeName == null)
            return null;


        Class<?> valueClass = ReflectOperator.getClass(typeName);
        if (ReflectOperator.isList(valueClass, null) || ReflectOperator.isMap(valueClass, null)) {
            throw new ParseResultException("不支持Map中泛型是List或者Map的情况,将在后续版本中思考需不需要更新这种情况");
        }

        List<Object> list = null;
        Object value = null;
        while ((value = doCreate(null, rs, valueClass)) != null) {
            if (list == null) {
                list = new ArrayList<Object>();
            }
            list.add(value);
        }
        return list;
    }

    public Number createNumber(ResultSet rs) throws Exception {
        Number number = null;
        if (rs.next()) {
            number = rs.getDouble(1);
        }
        return number;
    }

    public Number createNumber(Number number, Class<?> clazz) {
        if (ReflectOperator.isLongObject(clazz) || ReflectOperator.isLong(clazz)) {
            return number.longValue();
        } else if (ReflectOperator.isFloatObjects(clazz) || ReflectOperator.isFloats(clazz)) {
            if (ReflectOperator.isDoubles(clazz))
                return number.doubleValue();
            return number.floatValue();
        } else if (ReflectOperator.isInteger(clazz) || ReflectOperator.isInt(clazz)) {
            return number.intValue();
        }

        return number;

    }

    public String createString(ResultSet rs) throws Exception {
        String str = null;
        if (rs.next()) {
            str = rs.getString(1);
        }
        return str;
    }

    public Object createBean(Class<?> clazz, ResultSet rs) throws Exception {
        Object object = null;
        if (rs.next()) {
            Field[] fields = clazz.getDeclaredFields();
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                for (Field field : fields) {
                    Class<?> fieldClass = field.getType();
                    String fieldName = field.getName();
                    Column column = field.getAnnotation(Column.class);
                    if (column != null && column.value() != null) {
                        fieldName = column.value();
                    }
                    if (columnName.equals(fieldName)) {
                        Object value = rs.getObject(i);
                        if (!fieldClass.equals(value.getClass())) {
                            continue;
                        }
                        if (object == null) {
                            object = ReflectOperator.doNewInstance(clazz);
                        }
                        ReflectOperator.doFieldSet(field, object, value);
                        break;
                    }
                }

            }


        }
        return object;
    }

}