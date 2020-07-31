package com.lisijietech.generator.module.inject.handler.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lisijietech.generator.constant.PlaceholderConstant;
import com.lisijietech.generator.module.inject.annotation.DataValue;
import com.lisijietech.generator.module.inject.handler.DataValueHandler;
import com.lisijietech.generator.utils.ArraysUtils;
import com.lisijietech.generator.utils.StringHandlerUtils;

/**
 * Map类型注入简单处理。以后再优化。
 * field定义了{@link com.lisijietech.generator.module.inject.annotation.DataValue DataValue}注解时的处理。
 * Map的key和value都是String类型的。以后优化设计，判断泛型参数类型，和String类型转换成对应泛型参数类型。
 * @author lisijie
 * @date 2020-6-26
 */
public class MapSimpleHandlerImpl implements DataValueHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(MapSimpleHandlerImpl.class);
	
	@Override
	public void handle(Object target,Field f,String[] parentPrefixes,Map<String, String> props,Map<String, Object> cache) {
		//判断是否是Map类型。不是则返回不做处理
		if(!Map.class.isAssignableFrom(f.getType())) {
			logger.debug("{}方法，成员变量不是Map类型",Thread.currentThread().getStackTrace()[2].getMethodName());
			return;
		}
		//判断是否是Map类型
		//最终有效前缀。默认为父层前缀，如果有此field有DataValue注解，需要和此field前缀处理成最终前缀。
		String[] prefixes = ArraysUtils.elementUnique(ArraysUtils.arrayNullExclude(parentPrefixes));
		
		//判断field有没有DataValue注解。
		DataValue dv = f.getDeclaredAnnotation(DataValue.class);
		if(dv != null) {
			String[] fPrefixes = dv.prefix();
			//获取父级前缀和当前field注解前缀交集，为最终有效前缀。
			prefixes = ArraysUtils.IntersectionOrBlankUnion(parentPrefixes,fPrefixes);
			//如果父层和子层的prefixes进行交集或有空则并集处理时，最终有效prefixes为空，那么代表忽略此field注入。
			//没有注解则处理逻辑可能会不一样，没有注解而且最终有效prefixes为空，也是略过处理。
			if(prefixes == null || prefixes.length == 0) {
				logger.debug("{}方法，最终有效prefixes为空",Thread.currentThread().getStackTrace()[2].getMethodName());
				return;
			}
		}
		
		//最终有效前缀是空，略过不处理。
		if(prefixes == null || prefixes.length ==0) {
			logger.debug("{}方法，父层和当前field注解处理后的最终prefixes为空，略过注入不做处理",
					Thread.currentThread().getStackTrace()[2].getMethodName());
			return;
		}
		
		//驼峰命名转换为以-连字符命名。需要properties文件命名为标准连字符命名，因为camelToSeparator方法没有校验和格式化驼峰命名。
		String sepName = StringHandlerUtils.camelToSeparator( f.getName(), "-");
		//key默认为null。
		String key = null;
		//前缀+sepName拼接得到key。某个key匹配到一个properties中的数据，就不再匹配之后的前缀+sepName。
		//这里prefixes空判断有点冗余。
		if(prefixes != null && prefixes.length != 0) {
			String pKey = null;
			for(String p : prefixes) {
				pKey = p + sepName;
				//properties中有此pKey的匹配数据，就不再匹配后面的pKey。
				if(props.containsKey(pKey)) {
					//匹配到，就赋值到key中。
					key = pKey;
					break;
				}
			}
		}
		
		//判断key是否有匹配数据，如果没有，就忽略此field不进行注入。
		if(!props.containsKey(key)) {
			logger.debug("{}方法，properties不包含{}的key",Thread.currentThread().getStackTrace()[2].getMethodName(),key);
			return;
		}
		
		Map<Object,Object> map = null;
		
		//获得properties匹配的value值。
		String value = props.get(key);
		//value字符串转换为对应类型数据。
		if(value != null && value.trim().length() != 0) {
			map = new HashMap<>();
			String[] entryStr = value.split(PlaceholderConstant.ARRAY_SEP);
			for(String s : entryStr) {
				int sepIndex = s.indexOf(PlaceholderConstant.KEY_VALUE_SEP);
				//判断是否有分割符
				if(sepIndex == -1) {
					logger.debug("{}方法，数据不包含KEY_VALUE_SEP分割符，格式错误，略过注入不做处理",
							Thread.currentThread().getStackTrace()[2].getMethodName());
					return;
				}
				String mapKey = s.substring(0, sepIndex);
				String mapValue = s.substring(sepIndex + PlaceholderConstant.KEY_VALUE_SEP.length());
				map.put(mapKey, mapValue);
			}
		}
		
		//field注入值。
		try {
			f.setAccessible(true);
			f.set(target, map);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		String place = "#<22>aa#<22>bb#<22>";
//		System.out.println(place.replaceAll("#<22>", "qqqq"));
//		System.out.println(Arrays.toString(place.split("#<22>")));
//		System.out.println(Arrays.toString("aaa".split("#<22>")));
//		
//		Map<Object,Object> map = new HashMap<>();
//		map.put(1, new HashMap<Object, Object>());
//		map.put(2, "ss");
//		MapTest maptest = new MapSimpleHandlerImpl().new MapTest();
//		
//		try {
//			Field f = maptest.getClass().getDeclaredField("map");
//			f.setAccessible(true);
//			f.set(maptest, map);
//			System.out.println(maptest.getMap());
//			System.out.println(maptest.getMap().getClass());
//			System.out.println(maptest.getMap().get(2));
//			System.out.println(maptest.getMap().get(2).getClass());
//			//会报错，HashMap不能转换为String。参数化类型的泛型参数可以用来校验类型的，但通过反射注入会绕过校验。
//			//报错信息混乱。https://blog.csdn.net/antchen88/article/details/72236984
//			System.out.println(maptest.getMap().get(1));
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	class MapTest{
//		private Map<String,String> map;
//		
//		public Map<String,String> getMap(){
//			return this.map;
//		}
//	}
}
