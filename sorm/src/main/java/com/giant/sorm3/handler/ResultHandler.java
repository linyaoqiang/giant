package com.giant.sorm3.handler;

import java.lang.reflect.Method;
import java.sql.ResultSet;

public interface ResultHandler {
	Object parseResultSet(Method method, ResultSet rs) throws Exception;
}
