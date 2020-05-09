package com.giant.sorm3.handler;

import com.giant.commons.opeator.DBOperator;
import com.giant.commons.opeator.FileOperator;
import com.giant.sorm3.utils.SormUtils;
import org.dom4j.Element;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Iterator;

public class XMLStatementHandler extends SimpleStatementHandler {

    public XMLStatementHandler(Connection connection) {
        super(connection);
    }

    @Override
    public int doDML(Method method, Object[] paramValues) {
        if(SormUtils.isDMLMethod(method)){
            return super.doDML(method,paramValues);
        }
        String badSql = getBadSql(method);
        String sql = parseSql(badSql);
        setPreparedStatement(DBOperator.getPreparedStatement(getConnection(), sql, paramValues));
        return executeUpdate(sql, getPreparedStatement(), paramValues);
    }

    @Override
    public ResultSet doSelect(Method method, Object[] paramValues) {
        if(SormUtils.isSelectMethod(method)){
            return super.doSelect(method,paramValues);
        }

        String badSql = getBadSql(method);
        String sql = parseSql(badSql);
        setPreparedStatement(DBOperator.getPreparedStatement(getConnection(), sql, paramValues));
        return executeQuery(sql, getPreparedStatement(), paramValues);
    }

    public String getBadSql(Method method) {
        String methodName = method.getName();
        String badSql = null;
        Element root = FileOperator.createRootElement(FileOperator.classToPath(method.getDeclaringClass())+".xml");
        for (Iterator<Element> iterator = root.elementIterator();iterator.hasNext(); ) {
            Element element = iterator.next();
            if (element.attributeValue("method").equals(methodName)) {
                badSql = element.getText().trim();
                break;
            }
        }
        return badSql;
    }
}
