package com.giant.sorm3.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.relation.Relation;

import com.giant.commons.opeator.ReflectOperator;
import com.giant.sorm3.annotation.ColumnResult;
import com.giant.sorm3.session.SqlSession;
import org.apache.log4j.Logger;

public class ColumnResultHandler extends SimpleResultHandler {
    private Logger logger = Logger.getLogger(ColumnResultHandler.class);
    private SqlSession sqlSession;

    public ColumnResultHandler() {
        super();
    }

    public ColumnResultHandler(SqlSession sqlSession) {
        super();
        this.sqlSession = sqlSession;
    }

    @Override
    public Object parseResultSet(Method method, ResultSet rs) throws Exception {
        // TODO Auto-generated method stub
        Object object = super.parseResultSet(method, rs);
        if (object == null)
            return null;
        doColumnResult(method, rs, object);
        return object;
    }

    public void doColumnResult(Method method, ResultSet rs, Object object) throws NoSuchMethodException, SecurityException, NoSuchFieldException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ColumnResult.class)) {
                ColumnResult columnResult = field.getAnnotation(ColumnResult.class);
                Class<?> resultClass = columnResult.mapper();
                String methodName = columnResult.method();
                Class<?>[] params = columnResult.params();
                String[] paramValues = columnResult.paramValues();
                if (resultClass == null || methodName == null || methodName.equals("")) {
                    continue;
                }
                Method resultMethod = resultClass.getDeclaredMethod(methodName, params);
                List<Object> list = parseParamValues(resultMethod, paramValues, object);
                Object result = sqlSession.getMapper(resultClass);
                Object value = null;
                if (list != null) {
                    value = ReflectOperator.doMethod(resultMethod, result, list.toArray());
                } else {
                    value = ReflectOperator.doMethod(resultMethod, result, null);
                }
                ReflectOperator.doFieldSet(field, object, value);
            }
        }
    }

    public List<Object> parseParamValues(Method method, String[] paramValues, Object object) throws NoSuchFieldException, SecurityException {
        Parameter[] parameters = method.getParameters();
        System.out.println(parameters.length + ":" + paramValues.length);
        if (parameters.length != paramValues.length) {
            logger.debug("参数有误,参数个数与方法不匹配:" + Arrays.toString(paramValues));
            return null;
        }
        List<Object> list = null;
        for (int i = 0; i < paramValues.length; i++) {
            String valueString = paramValues[i];
            if (valueString == null) {
                continue;
            }
            if (list == null) {
                list = new ArrayList<Object>();
            }

            if (valueString.contains("{") && valueString.contains("}") && valueString.contains("=")) {
                if (ReflectOperator.isMap(parameters[i].getType(), null)) {
                    valueString = valueString.substring(1);
                    valueString = valueString.substring(0, valueString.length() - 1);
                    Map<String, Object> map = null;
                    if (valueString.contains(",")) {
                        String[] temp = valueString.split(",");
                        map = new HashMap<String, Object>();
                        for (String str : temp) {
                            String[] strs = str.split("=");
                            Field field = object.getClass().getDeclaredField(strs[1]);
                            Object value = ReflectOperator.doFieldGet(field, object);
                            map.put(strs[0], value);
                        }
                    } else {
                        map = new HashMap<String, Object>();
                        String strs[] = valueString.split("=");
                        Field field = object.getClass().getDeclaredField(strs[1]);
                        Object value = ReflectOperator.doFieldGet(field, object);
                        map.put(strs[0], value);
                    }
                    list.add(map);
                } else {
                    list.add(object);
                }

            } else {
                Class<?> clazz = parameters[i].getType();
                Field field = object.getClass().getDeclaredField(valueString);
                Object value = ReflectOperator.doFieldGet(field, object);
                if (ReflectOperator.isNumberObject(clazz) || ReflectOperator.isNumber(clazz)) {
                    Number number = null;
                    if (value instanceof Number) {
                        number = (Number) value;
                    } else {
                        number = Double.parseDouble((String) value);
                    }
                    list.add(createNumber(number, clazz));
                } else if (String.class.equals(clazz)) {
                    list.add(valueString);
                } else {
                    list.add(null);
                }
            }
        }
        return list;

    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

}
