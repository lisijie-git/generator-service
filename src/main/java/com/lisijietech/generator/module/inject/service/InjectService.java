package com.lisijietech.generator.module.inject.service;

import java.util.Map;

/**
 * 文件注入service接口
 * @author lisijie
 * @date 2020年6月15日
 */
public interface InjectService {
	/**
	 * 对象注入数据。
	 * 需要对象类型上有类注解，没有则没有数据来源，无法注入。
	 * @param obj
	 */
	void inject(Object obj);
	
	/**
	 * 对象成员变量注入值。
	 * 无论是什么对象，对象类型上有没有类注解，都无关系，只要参数对。
	 * 需要成员变量上有注解配置，没有就是默认注入处理。
	 * @param target 需要注入的对象
	 * @param parentPrefixes 注入值父层前缀
	 * @param props 数据
	 */
	void injectFields(Object target,String[] parentPrefixes,Map<String,String> props);
}
