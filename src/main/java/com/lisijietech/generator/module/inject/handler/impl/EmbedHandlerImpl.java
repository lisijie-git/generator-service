package com.lisijietech.generator.module.inject.handler.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lisijietech.generator.module.inject.annotation.DataValue;
import com.lisijietech.generator.module.inject.handler.DataValueHandler;
import com.lisijietech.generator.utils.ArraysUtils;
import com.lisijietech.generator.utils.JudgeTypeUtils;
import com.lisijietech.generator.utils.StringHandlerUtils;

/**
 * 嵌套注入处理实现。
 * field定义了{@link com.lisijietech.generator.module.inject.annotation.DataValue DataValue}注解时的处理。
 * field如果是嵌入类型(POJO对象类型)，则生成对象，给对象成员属性注入值。
 * 对象成员属性类型如果也是嵌入类型，且注解配置嵌入处理器，则继续嵌入注入。
 * 对象成员属性是简单类型，且没有注解，没配置处理器，默认用SimpleHandlerImpl处理器处理。
 * @author lisijie
 * @date 2020-6-24
 */
public class EmbedHandlerImpl implements DataValueHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(EmbedHandlerImpl.class);
	
	//POJO类型的field的直接赋null值的key后缀。
	//因为POJO类型field的数据注入，是需要创建其类型的实例对象，然后嵌套注入其实例对象的成员属性。
	//而实例对象的key规则(prefixes之一 + 成员属性名)，当前嵌入类型field的key规则如果是一样的，就会冲突。
	//当然，最终有效prefixes是空，就忽略注入，防止覆盖默认值。但此field有可能是有默认实例对象，也有可能是null。
	//所以要明确此嵌入类型field注入null值，需要约定properties文件的key规则。
	//嵌入类型field注入逻辑就是：先判断有没有cache防止循环依赖注入，再判断有没有直接null注入key，最后在进行此field实例对象的成员变量注入
	//嵌入类型field直接赋值null的key规则：prefixes元素之一 + field的name的连字符命名 + CLAZZ_NULL_SUFFIX
	private static final String CLAZZ_NULL_SUFFIX = "{}";
	
	@Override
	public void handle(Object target,Field f,String[] parentPrefixes,Map<String,String> props,Map<String,Object> cache) {
		//最终有效前缀。默认为父层前缀，如果有此field有DataValue注解，需要和此field前缀处理成最终前缀。
		//因为和cache的key相关，所以要进行除重，除null处理。
		String[] prefixes = ArraysUtils.elementUnique(ArraysUtils.arrayNullExclude(parentPrefixes));
		//最终处理器。默认为简单处理器。只处理简单类型field注入。
		DataValueHandler handler = new SimpleHandlerImpl();
		
		//当无论什么类型的field定义了注解DataValue，prefixes和handler就要按照注解逻辑设置。
		DataValue dv = f.getDeclaredAnnotation(DataValue.class);
		if(dv != null) {
			String[] fPrefixes = dv.prefix();
			Class<?> fHandler = dv.handler();
			
			//获取父级前缀和当前field注解前缀交集，为最终有效前缀。
			prefixes = ArraysUtils.IntersectionOrBlankUnion(parentPrefixes,fPrefixes);
			//如果父层和子层的prefixes进行交集或有空则并集处理时，最终有效prefixes为空，那么代表忽略此field注入。
			//没有注解则处理逻辑可能会不一样，没有注解而且最终有效prefixes为空，是按照SimpleHandlerImpl处理。
			if(prefixes == null || prefixes.length == 0) {
				logger.debug("{}方法，最终有效prefixes为空",Thread.currentThread().getStackTrace()[2].getMethodName());
				return;
			}
			
			//当注解中设置了处理器。handler设置为注解的处理器。
			//未设置处理器，为默认SimpleHandlerImpl处理器。
			if(fHandler != null) {
				try {
					handler = (DataValueHandler) fHandler.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		//如果处理器最终不是嵌入类型处理器，就执行处理，并且终止后续处理。
		if(handler.getClass() != EmbedHandlerImpl.class) {
			handler.handle(target, f, prefixes, props, cache);
			return;
		}
		
		//嵌入注入。
		
		//当不是嵌入类型，注解上却设置了EmbedHandlerImpl嵌入处理器，要进行校验处理。
		Class<?> c = f.getType();
		//简单类型，由String数据转换成对应类型数据注入
		if(JudgeTypeUtils.isBasicType(c) || JudgeTypeUtils.isSimpleType(c) || JudgeTypeUtils.isArraySimpleType(c)) {
			//调用SimpleHandlerImpl类型对象的方法，虽然有点冗余，图简便，懒得再写。
			new SimpleHandlerImpl().handle(target, f, prefixes, props, cache);
			return;
		}
		//集合，复杂数组，接口等复杂类型，略过不做处理。自定义相应类型处理器才能处理。
		if(JudgeTypeUtils.isComplexType(c)) {
			return;
		}
		
		//嵌入类型，最终有效前缀是空，略过不处理。
		//这里的prefixes已经是当前field注解和父层的处理后的最终有效前缀。
		if(prefixes == null || prefixes.length ==0) {
			logger.debug("{}方法，进入嵌入注入，父层和当前field注解处理后的最终prefixes为空，略过注入不做处理",
					Thread.currentThread().getStackTrace()[2].getMethodName());
			return;
		}
		
		//判断是否有获取缓存。
		String cacheKey = generateCacheKey(f.getName(),f.getType(),prefixes,handler.getClass());
		if(cache.containsKey(cacheKey)) {
			Object cacheValue = cache.get(cacheKey);
			try {
				f.setAccessible(true);
				f.set(target, cacheValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return;
		}
		
		//判断是否有嵌入类型直接赋值key(一般为直接赋null值)。有就给嵌入类型field直接赋值null，进行缓存，然后结束嵌入处理。
		String dirKey = null;
		//field的name连字符命名
		String sepName = StringHandlerUtils.camelToSeparator(f.getName(), "-");
		for(String prefix : prefixes) {
			dirKey = prefix + sepName + CLAZZ_NULL_SUFFIX;
			if(props.containsKey(dirKey)) {
				//目前properties文件中的直接赋值key，只有设置"${null}"字符串值，才会在props数据对象中转换为null值。
				//被处理后在props的map对象数据里就是null值
				String directValue = props.get(dirKey);
				//这里冗余判断value是不是null，万一是其他值，就可能有其他意义了。以后优化，思考直接赋值key有没有其他功能
				if(directValue == null) {
					try {
						f.setAccessible(true);
						f.set(target, directValue);
						//进行缓存
						cache.put(cacheKey, directValue);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		}
		
		//POJO类型嵌入注入。
		try {
			f.setAccessible(true);
			//判断当前field是否有值，没有就创建当前field类型的实例对象，再进行注入。
			Object fValue = f.get(target);
			if(fValue == null) {
				fValue = f.getType().newInstance();
			}
			f.set(target, fValue);
			
			//嵌入注入属性值。类似递归。
			Field[] ffs = fValue.getClass().getDeclaredFields();
			for(Field ff : ffs) {
				handle(fValue,ff,prefixes,props,cache);
			}
			
			//当前field进行缓存。要在嵌入注入属性值成功后才缓存，否则是个数据残缺的缓存对象。
			cache.put(cacheKey, fValue);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 注入的缓存key生成方法。可以是field的缓存，也可以是Class的缓存。关键是key生成规则。
	 * @param name 当前名。如果是field会有name，如果是Class则没有此属性。
	 * @param currentClazz 当前对象类型
	 * @param prefixes 最终有效前缀属性值。父层前缀和当前注解前缀处理后的前缀，或者仅仅是父层前缀，或者没有。
	 * @param handlerClazz 最终处理器类型
	 * @return
	 */
	public String generateCacheKey(String name,Class<?> currentClazz,String[] prefixes,Class<?> handlerClazz) {
		StringBuffer n = new StringBuffer();
		if(name != null) {
			n.append("/" + name);
		}
		
		StringBuffer preStr = new StringBuffer();
		if(prefixes != null && prefixes.length != 0) {
			for(String p : prefixes) {
				preStr.append("[" + p);
			}
		}
		
		StringBuffer key = new StringBuffer();
		key.append(n).append(":" + currentClazz.getTypeName())
			.append(preStr).append(handlerClazz.getTypeName());
		return key.toString();
	}

}
