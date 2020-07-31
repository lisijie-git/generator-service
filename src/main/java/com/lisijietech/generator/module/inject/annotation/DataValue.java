package com.lisijietech.generator.module.inject.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.lisijietech.generator.module.inject.handler.DataValueHandler;

/**
 * 数据值注入处理配置的注解。
 * 是{@link DataSource}的补充。
 * 目前做了properties文件相关的注解解析。字段注入数据的具体处理注解。
 * 一般自动匹配字段注入，不需要此注解。但处理复杂注入可以声明此注解。
 * @author lisijie
 * @date 2020年6月15日
 */
@Target({ METHOD, FIELD, CONSTRUCTOR})
@Retention(RUNTIME)
@Repeatable(DataValue.List.class)
@Documented
public @interface DataValue {
	/**
	 * 数据key名称前缀。
	 * 指定此field的数据值前缀。
	 * 前缀格式是aa.bb.格式。
	 * Spring的前缀格式是aa.bb格式，在实际使用的时候应是对分割符做了容错处理。
	 * 当field匹配到对应数据，就不会往下继续遍历剩下的前缀去匹配。
	 * 如果field是简单类型(int,Integer等)，直接与field的name拼接为key获取数据值。
	 * 如果field是个复杂类型(引用类型，枚举，集合类型等)，需要多个数据进行嵌套注入，就可能有多种前缀来获取数据值。
	 * 如果prefix == null 或者 prefix.length == 0，则文件数据范围和父层相同，field对应的数据的key会默认使用父层的prefix。
	 * 如果父层也是null或者length==0，则field对应数据key没有前缀，默认为field的name连字符命名。
	 * @return
	 */
	String[] prefix() default { };
	
	/**
	 * 注入数据处理类。
	 * 用于自定义处理field对应的数据，转换成对应类型的对象。
	 * @return
	 */
	Class<? extends DataValueHandler> handler();
	
	/**
	 * 内部定义一个注解。
	 * 用于注解间接存在，Repeatable元注解。
	 * 暂时没用，DataValue注解不会重复修饰某个类或成员属性等。
	 */
	@Target({ METHOD, FIELD, CONSTRUCTOR})
	@Retention(RUNTIME)
	@Documented
	@interface List {
		DataValue[] value();
	}
}
