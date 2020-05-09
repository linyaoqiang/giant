package com.giant.commons.opeator;


import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReflectOperator {
	public static Object doFieldGet(Field field, Object object) {
		field.setAccessible(true);
		try {
			return field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void doFieldSet(Field field, Object object, Object value) {
		field.setAccessible(true);
		try {
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object doMethod(Method method,Object object,Object[] params) {
		try {
			return method.invoke(object,params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object doInvoke(Method method, Object object, Object[] objectValue) {
		method.setAccessible(true);
		try {
			if (objectValue == null || objectValue.length == 0) {
				return method.invoke(object);
			} else {
				return method.invoke(object, objectValue);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Object doNewInstance(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getClass(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			return clazz;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?>[] getParameterizedType(Field f) {

		// 获取f字段的通用类型
		Type fc = f.getGenericType(); // 关键的地方得到其Generic的类型

		// 如果不为空并且是泛型参数的类型
		if (fc != null && fc instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fc;

			Type[] types = pt.getActualTypeArguments();

			if (types != null && types.length > 0) {
				Class<?>[] classes = new Class<?>[types.length];
				for (int i = 0; i < classes.length; i++) {
					classes[i] = (Class<?>) types[i];
				}
				return classes;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param obj       对象
	 * @param fieldName 字段名
	 * @return 返回obj使用get方法获得的数据
	 */
	public static Object doMethodGet(Object obj, String fieldName) {
		try {
			Method method = obj.getClass().getDeclaredMethod("get" + StringOperator.firstToUpperCase(fieldName));
			return method.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param obj         对象
	 * @param columnName  字段名
	 * @param columnValue 字段值 给对象的columnName赋值columnValue
	 */
	public static void doMethodSet(Object obj, String columnName, Object columnValue) {
		try {
			if (columnValue == null) {
				return;
			}
			Method method = obj.getClass().getDeclaredMethod("set" + StringOperator.firstToUpperCase(columnName),
					columnValue.getClass());
			method.invoke(obj, columnValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean isInteger(Class<?> parameterType) {
		return Integer.class.equals(parameterType);
	}

	public static boolean isLongObject(Class<?> clazz) {
		return Long.class.equals(clazz);
	}
	public static boolean isLong(Class<?> clazz) {
		return long.class.equals(clazz);
	}
	public static boolean isFloatObjects(Class<?> parameterType) {
		return Float.class.equals(parameterType) || Double.class.equals(parameterType);
	}

	public static boolean isInt(Class<?> parameterType) {
		return int.class.equals(parameterType);
	}

	public static boolean isFloats(Class<?> parameterType) {
		return float.class.equals(parameterType) || double.class.equals(parameterType);
	}

	public static boolean isDoubleObject(Class<?> parameterType) {
		return Double.class.equals(parameterType);
	}
	public static boolean isDouble(Class<?> parameterType) {
		return double.class.equals(parameterType);
	}
	
	public static boolean isDoubles(Class<?> parameterType) {
		return isDoubleObject(parameterType)||isDouble(parameterType);
	}
	
	public static boolean isNumberObject(Class<?> parameterType) {
		return isInteger(parameterType) || isFloatObjects(parameterType)||isLongObject(parameterType);
	}

	public static boolean isNumber(Class<?> parameterType) {
		return isInt(parameterType) || isFloats(parameterType)||isLong(parameterType);
	}
	
	public static boolean isList(Class<?> clazz,Object object) {
		return (object==null?false:(object instanceof List))||List.class.equals(clazz)
				||ArrayList.class.equals(clazz)||LinkedList.class.equals(clazz)
				||CopyOnWriteArrayList.class.equals(clazz);
	}
	public static boolean isSet(Class<?> clazz,Object object) {
		return (object==null?false:(object instanceof Set))||Set.class.equals(clazz)
				||HashSet.class.equals(clazz)||TreeSet.class.equals(clazz);
	}
	public static boolean isMap(Class<?> clazz,Object object) {
		
		return (object==null?false:(object instanceof Map))||Map.class.equals(clazz)
				||HashMap.class.equals(clazz)||TreeMap.class.equals(clazz);
	}
	public static String[] getGenericClassNameByField(Field field) {
		if(field==null) {
			return null;
		}
		Type type=field.getGenericType();
		return getGenericClassName4Map(type);
	}
	public static String[] getGenericReturnClassNameByMethod(Method method) {
		if(method==null) {
			return null;
		}
		Type type=method.getGenericReturnType();
		return getGenericClassName4Map(type);
	}
	public static String[] getGenericParameterClassNameByMethod(Method method) {
		if(method==null) {
			return null;
		}
		Type[] types=method.getGenericParameterTypes();
		List<String> names=new ArrayList<String>();
		for(Type type:types) {
			names.addAll(Arrays.asList(getGenericClassName4Map(type)));
		}
		return names.toArray(new String[] {});
	}
	
	public static String getGenericClassName4List(Type type) {
		String[] names=getGenericClassName4Map(type);
		return names==null?null:names[0];
	}
	public static String[] getGenericClassName4Map(Type type) {
		if(type==null) {
			return null;
		}
		String genericName=type.getTypeName();
		if(genericName==null||genericName.equals("")) {
			return null;
		}
		genericName=genericName.substring(genericName.indexOf("<")+1,genericName.indexOf(">"));
		if(genericName.contains(",")) {
			String[] nameArray=genericName.split(",");
			for(int i=0;i<nameArray.length;i++) {
				nameArray[i]=nameArray[i].trim();
			}
			return nameArray;
		}
		return new String[] {genericName.trim()};
	}

	public static Object createJdkProxy(Object target, InvocationHandler handler){
		Class<?>[] interfaces = target.getClass().getInterfaces();
		if(interfaces==null||interfaces.length<=0){
			return null;
		}
		ClassLoader classLoader=target.getClass().getClassLoader();
		return Proxy.newProxyInstance(classLoader,interfaces,handler);
	}
}
