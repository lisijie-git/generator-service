package com.lisijietech.generator.module.inject.handler.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lisijietech.generator.constant.PlaceholderConstant;
import com.lisijietech.generator.module.inject.annotation.DataValue;
import com.lisijietech.generator.module.inject.handler.DataValueHandler;
import com.lisijietech.generator.utils.ArraysUtils;
import com.lisijietech.generator.utils.JudgeTypeUtils;
import com.lisijietech.generator.utils.StringHandlerUtils;

/**
 * 简单注入处理实现。包括基本类型，基本类型包装类，简单数组类型(组件类型为基本类型，基本包装类型)，枚举类型。
 * field未定义{@link com.lisijietech.generator.module.inject.annotation.DataValue DataValue}注解时，按照field名称的分割符命名匹配。
 * field定义了注解时，如果最终有效prefixes为空则略过处理。
 * 如果在properties中找不到对应数据。则略过此field注入，而不是注入null。防止覆盖对象属性的默认值。
 * @author lisijie
 * @date 2020-6-22
 */
public class SimpleHandlerImpl implements DataValueHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleHandlerImpl.class);

	@Override
	public void handle(Object target,Field f,String[] parentPrefixes,Map<String,String> props,Map<String,Object> cache) {
		Class<?> c = f.getType();
		if(JudgeTypeUtils.isBasicType(c) || JudgeTypeUtils.isSimpleType(c) 
				|| JudgeTypeUtils.isArraySimpleType(c) || c.isEnum()) {
			//最终有效前缀。默认为父层前缀，如果有此field有DataValue注解，需要和此field前缀处理成最终前缀。
			String[] prefixes = parentPrefixes;
			//因为和cache的key相关，所以要进行除重，除null处理。
			prefixes = ArraysUtils.elementUnique(ArraysUtils.arrayNullExclude(prefixes));
			
			//判断field有没有DataValue注解。
			DataValue dv = f.getDeclaredAnnotation(DataValue.class);
			if(dv != null) {
				String[] fPrefixes = dv.prefix();
				//获取父级前缀和当前field注解前缀交集，为最终有效前缀。
				prefixes = ArraysUtils.IntersectionOrBlankUnion(parentPrefixes,fPrefixes);
				//如果父层和子层的prefixes进行交集或有空则并集处理时，最终有效prefixes为空，那么代表忽略此field注入。
				//没有注解则处理逻辑可能会不一样，没有注解而且最终有效prefixes为空，则按照field的name分割符格式匹配。
				if(prefixes == null || prefixes.length == 0) {
					logger.debug("{}方法，最终有效prefixes为空",Thread.currentThread().getStackTrace()[2].getMethodName());
					return;
				}
			}
			
			//成员属性名称
			String name = f.getName();
			//驼峰命名转换为以-连字符命名。需要properties文件命名为标准连字符命名，因为camelToSeparator方法没有校验和格式化驼峰命名。
			String sepName = StringHandlerUtils.camelToSeparator(name, "-");
			//如果没有前缀，默认key就是sepName。
			String key = sepName;
			//如果前缀不为空，前缀+sepName拼接得到key。某个key匹配到一个properties中的数据，就不再匹配之后的前缀+sepName。
			if(prefixes != null && prefixes.length != 0) {
				String pKey = null;
				//这里的p元素不再校验null，更早的时候校验处理过了。
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
			
			//判断默认或者有前缀拼接过的key是否有匹配数据，如果没有，就忽略此field不进行注入。防止覆盖对象属性的默认值。
			//这里判断冗余，但是有必要。如果pKey都不匹配，还是要判断默认的key = sepName是否有匹配。
			if(!props.containsKey(key)) {
				logger.debug("{}方法，properties不包含{}的key",
						Thread.currentThread().getStackTrace()[2].getMethodName(),key);
				return;
			}
			
			//获得properties匹配的value值。
			String value = props.get(key);
			//value字符串转换为对应类型数据。
			Object simpleValue = StringHandlerUtils.convertSimple(value,c);
			if(JudgeTypeUtils.isArraySimpleType(c)) {
				simpleValue = StringHandlerUtils.convertArraySimple(value, c, PlaceholderConstant.ARRAY_SEP);
			}
			if(c.isEnum()){
				simpleValue = StringHandlerUtils.convertEnum(value, c);
			}
			
			//field注入值。
			try {
				f.setAccessible(true);
				f.set(target, simpleValue);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
