package com.lisijietech.generator.module.inject.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 对象注入文件数据的配置注解。
 * 目前做了properties文件相关的注解解析。
 * @author lisijie
 * @date 2020年6月8日
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(DataSource.List.class)
@Documented
public @interface DataSource {
	/**
	 * 路径名称对象数组。
	 * 可以是多个文件路径。多个文件加载时，要注意数据名重复的处理，目前就覆盖处理。
	 * file:/a/b代表从系统文件路径加载文件数据源,classpath:/a/b代表用类路径加载文件数据源,
	 * classpath:a/b在解析时按照/a/b处理。
	 * @return
	 */
	String[] value() default { };
	
	/**
	 * 文件编码。默认UTF-8
	 * @return
	 */
	String encode() default "UTF-8";
	
	/**
	 * 数据key名称前缀。
	 * 要用到的数据的前缀列表。
	 * 前缀格式是aa.bb.格式。
	 * Spring的前缀格式是aa.bb格式，在实际使用的时候应是对分割符做了容错处理。
	 * 如果prefix == null 或者 prefix.length == 0，从文件加载的数据就没有前缀约束(全部包括)。
	 * 但是默认field对应的数据的key也是没有前缀的，是field的name连字符命名。
	 * 最好指定prefix，除非field的name连字符命名对应的文件数据key就是没有前缀的。或者给fleid声明{{@link DataValue}注解，并配置prefix。
	 * @return
	 */
	String[] prefix() default { };
	
	/**
	 * 内部定义一个注解。
	 * 用于注解间接存在，Repeatable元注解。
	 * 暂时没用，DataSource注解不会重复修饰某个类或成员属性等。
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		DataSource[] value();
	}
}
