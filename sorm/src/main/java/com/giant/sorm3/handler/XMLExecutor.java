package com.giant.sorm3.handler;

import com.giant.commons.opeator.DBOperator;
import com.giant.sorm3.core.DBManger;
import com.giant.sorm3.exception.ParseParamException;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class XMLExecutor extends  SimpleExecutor{

    public XMLExecutor(Connection connection) {
        super(connection);
    }

    public XMLExecutor(ParameterHandler parameterHandler, StatementHandler statementHandler, ResultHandler resultHandler, Connection connection) {
        super(parameterHandler, statementHandler, resultHandler, connection);
    }

    public Object xmlExecute(Method method,Object[] args){
        ResultSet rs=null;
        try {
            XMLParameterHandler parameterHandler= (XMLParameterHandler) getParameterHandler();
            Object[] params=parameterHandler.parseParameter(method,args);
            if(method.getReturnType().equals(int.class)){
                return getStatementHandler().doDML(method,params);
            }else {
                rs=getStatementHandler().doSelect(method,params);
                return getResultHandler().parseResultSet(method,rs);
            }
        } catch (ParseParamException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManger.close(rs,getStatementHandler().getPreparedStatement(),null);
            getStatementHandler().setPreparedStatement(null);
        }
        return null;
    }
}
