package com.lisijietech.generator.module.inject.service.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lisijietech.generator.module.inject.annotation.DataSource;
import com.lisijietech.generator.module.inject.annotation.DataValue;
import com.lisijietech.generator.module.inject.handler.DataValueHandler;
import com.lisijietech.generator.module.inject.handler.impl.PropSourceHandlerImpl;
import com.lisijietech.generator.module.inject.handler.impl.SimpleHandlerImpl;
import com.lisijietech.generator.module.inject.service.InjectService;

/**
 * properties文件注入service实现类
 * @author lisijie
 * @date 2020年6月15日
 */
public class PropInjectServiceImpl implements InjectService{
	
	private static final Logger logger = LoggerFactory.getLogger(PropInjectServiceImpl.class);
	
	@Override
	public void inject(Object obj) {
		Class<?> clazz = obj.getClass();
		
		//根据对象注解获取properties文件数据。
		Map<String,String> propsMap = new PropSourceHandlerImpl().getDataSource(clazz);
		
		if(propsMap == null || propsMap.isEmpty()) {
			logger.debug("{}方法propsMap变量为空",Thread.currentThread().getStackTrace()[2].getMethodName());
			return;
		}
		
		//获取prefixes，相当于指明成员属性注入能够使用的范围，或者匹配定位。
		String[] prefixes = null;
		DataSource ds = clazz.getDeclaredAnnotation(DataSource.class);
		if(ds != null) {
			prefixes = ds.prefix();
		}
		
		//对象成员变量注入值
		injectFields(obj, prefixes, propsMap);
	}
	
	@Override
	public void injectFields(Object target,String[] parentPrefixes,Map<String,String> props) {
		//缓存。缓存注入对象。当前名(field名)+当前类型(field类型)+父层前缀范围和当前注解前缀范围计算后的范围+最终处理类 作为key
		//这里主要防止field的循环依赖注入。 
		Map<String,Object> cache = new HashMap<>();
		
		Field[] fs = target.getClass().getDeclaredFields();
		for(Field f : fs) {
			//处理器。默认为SimpleHandlerImpl。
			DataValueHandler handler = new SimpleHandlerImpl();
			//如果成员属性有注解
			DataValue dv = f.getDeclaredAnnotation(DataValue.class);
			if(dv != null) {
				//如果注解设置了处理器，就设置给handler
				Class<? extends DataValueHandler> handlerClazz = dv.handler();
				if(handlerClazz != null) {
					try {
						handler = handlerClazz.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			//执行注入处理。
			handler.handle(target, f, parentPrefixes, props, cache);
		}
	}
	
//	public static void main(String[] args) {
//		String i = null;
//		System.out.println(i instanceof String);
//		
//		MPGConfigEntity entity = new MPGConfigEntity();
//		try {
//			Field f = entity.getClass().getDeclaredField("i");
//			Field[] fs = f.getType().getDeclaredFields();
//			Constructor<?>[] cs = f.getType().getDeclaredConstructors();
//			Method[] ms = f.getType().getDeclaredMethods();
//			System.out.println(fs.length);
//			System.out.println(cs.length);
//			System.out.println(ms.length);
//		} catch (NoSuchFieldException | SecurityException e) {
//			e.printStackTrace();
//		}
//		
//		try {
////			class.newInstance()方法不能创建私有无参构造方法的实例。
////			MPGConfigEntity entity = MPGConfigEntity.class.newInstance();
//			MPGConfigEntity entity = new MPGConfigEntity();
//			Field f = MPGConfigEntity.class.getDeclaredField("i");
//			f.setAccessible(true);
//			Integer i = 1;
//			f.set(entity, i );
//			System.out.println(entity.getI());
//			
//			Field f1 = MPGConfigEntity.class.getDeclaredField("c");
//			f1.setAccessible(true);
//			Character c = 100;
//			f1.set(entity, c );
//			System.out.println(entity.getC());
//			
//			Field f2 = MPGConfigEntity.class.getDeclaredField("ig");
//			f2.setAccessible(true);
//			int ig = 100;
//			f2.set(entity, ig );
//			System.out.println(entity.getIg());
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		
//		Object obj = 1;
//		System.out.println(obj.getClass().getName());
//		System.out.println(obj.getClass() == "".getClass());
//		
//		Object obj = new Object();
//		Object obj = "ss";
//		报错，实例对象类型是Object，不能强制转换为子类类型。除非对象就是子类型实例对象。
//		String s = (String)obj;
//		System.out.println(s.length());
//		
//		int[] i = {1,2,3};
//		List<?> list = Arrays.asList(i);
//		Arrays.asList(i)参数为基础类型数组时，由于T...可变长参数T泛型必须是Object类型，int不是，int[]是对象类型， 会把int[]当做一个对象。
//		System.out.println(Object.class.isAssignableFrom(int.class));
//		System.out.println(Object.class.isAssignableFrom(int[].class));
//		System.out.println(list.get(0));
//		会报错，Arrays.asList(i)的实例对象是Arrays的一个私有静态内部类。继承了{# java.util.AbstractList}，不能删除和添加元素。
//		数组转集合用Collections.addAll(c, elements)方法，如果数组需要添加或删除元素。
//		list.add(1, null);
//		
//		报错，https://blog.csdn.net/weixin_33775582/article/details/93467179
//		Object[] ia = {1};
//		Object ia = new int[]{1};
//		int[] arrays = (int[]) ia;
//		
//		Object obj = null;
//		System.out.println(obj instanceof Object);
//		System.out.println(null == null);
//		
//		System.out.println(new Integer(1).getClass().getName());
//		System.out.println(Integer.class.getName());
//		System.out.println(int.class.getName());
//		System.out.println(char.class.getName());
//		System.out.println(String[][].class.getName());
//		System.out.println(short[][].class.getName());
//		System.out.println(int[][].class.getComponentType().getName());
//		
//		int[][] ii = {{1,2},{3,4},{5,6}};
//		for(int i = 0;i < ii.length;i++) {
//			System.out.println(Arrays.toString(ii[i]));
//			System.out.println(ii[i][0]);
//		}
//		
//		System.out.println(Arrays.toString(int.class.getDeclaredFields()));
//		System.out.println(Arrays.toString(int.class.getDeclaredMethods()));
//		System.out.println(Arrays.toString(int.class.getDeclaredConstructors()));
//		try {
//				System.out.println(Integer.class.getDeclaredConstructor(String.class).newInstance("3"));
//				System.out.println(Integer.class.getDeclaredConstructors().length);
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		
//		java标号label。跳到指定域，或者指定循环。可以改成flag赋值判断，毕竟标号label语法不推荐使用。
//		int[] a = {0,1};
//		int[] b = {0,1};
//		int[] c = {0,1,2,3};
//		for(int i : a) {
//			System.out.println("a="+i);
//			out:
//			for(int j : b) {
//				System.out.println("b="+j);
//				for(int k : c) {
//					System.out.println("c="+k);
//					if(k == 1) {
//						continue out;
//					}
//				}
//				System.out.println("b循环中，循环后");
//			}
//			System.out.println("a循环中，b循环后");
//		}
//	}
}
