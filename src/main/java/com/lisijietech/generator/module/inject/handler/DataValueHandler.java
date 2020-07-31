package com.lisijietech.generator.module.inject.handler;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * {@link com.lisijietech.generator.module.inject.annotation.DataValue DataValue}注解处理
 * @author lisijie
 * @date 2020-6-22
 */
public interface DataValueHandler {
	/**
	 * field数据注入处理方法。
	 * @param target 需要注入的目标对象
	 * @param f 当前field
	 * @param parentPrefixes 父层前缀数组
	 * @param props properties文件中加载的数据
	 * @param cache 注入过的数据对象缓存。这里主要用来处理循环依赖注入的。也可以提高注入效率。以后可以优化设计，可参考spring的循环依赖注入。
	 */
	void handle(Object target,Field f,String[] parentPrefixes,Map<String,String> props,Map<String,Object> cache);
}
