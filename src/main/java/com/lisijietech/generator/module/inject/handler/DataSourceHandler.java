package com.lisijietech.generator.module.inject.handler;

import java.util.Map;

import com.lisijietech.generator.module.inject.annotation.DataSource;

/**
 * {@link com.lisijietech.generator.module.inject.annotation.DataSource DataSource}注解处理
 * @author lisijie
 * @date 2020-6-22
 */
public interface DataSourceHandler {
	/**
	 * 通过{@link DataSource}注解得到properties文件数据。
	 * @param clazz 有{@link DataSource}注解标记的类型类
	 * @return properties对象数据整理到Map中返回
	 */
	Map<String,String> getDataSource(Class<?> clazz);
}
