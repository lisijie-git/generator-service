package com.lisijietech.generator.module.inject.handler.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lisijietech.generator.constant.PlaceholderConstant;
import com.lisijietech.generator.module.inject.annotation.DataSource;
import com.lisijietech.generator.module.inject.handler.DataSourceHandler;
import com.lisijietech.generator.utils.PropertiesUtils;
import com.lisijietech.generator.utils.StringHandlerUtils;

/**
 * {@link com.lisijietech.generator.module.inject.annotation.DataSource DataSource}注解的处理实现。
 * 数据来源于properties文件。
 * @author lisijie
 * @date 2020-6-25
 */
public class PropSourceHandlerImpl implements DataSourceHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(PropSourceHandlerImpl.class);
	
	@Override
	public Map<String, String> getDataSource(Class<?> clazz) {
		//获取类的数据来源注解
		DataSource ds = clazz.getDeclaredAnnotation(DataSource.class);
		Map<String,String> propsMap = null;
		if(ds != null) {
			String[] paths = ds.value();
			String encode = ds.encode();
			String[] prefixes = ds.prefix();
			
			if(paths == null || paths.length < 1) {
				logger.debug("{}方法所需paths参数为空",Thread.currentThread().getStackTrace()[2].getMethodName());
				return null;
			}
			//解析一个或者多个properties对象到一个Map中，如果有前缀约束就排除不符合数据。不同文件中有重复数据，直接覆盖。
			propsMap = new HashMap<>();
			for(String path : paths) {
				//参数为空，不合法，不做处理。
				if(StringHandlerUtils.isBlank(path)) {
					continue;
				}
				Properties p = PropertiesUtils.load(path, encode);
				for(Entry<Object, Object> entity : p.entrySet()) {
					String key = String.valueOf(entity.getKey());
					//如果有前缀限制，排除不符合的数据。
					if(prefixes != null && prefixes.length > 0) {
						//前缀列表都不符合才排除。
						//flag方法可以改为，java 标号continue out，out:的方法。逻辑为符合就put，并调到p对象循环层
						boolean flag = true;
						for(String pre : prefixes) {
							//前缀元素为null，不做处理
							if(pre == null) {
								continue;
							}
							//前缀之后不能有.符号，否则前缀是不完全匹配的。
							//并且前缀为""字符串时，当做参数传入startsWith时非null字符串都为true。""前缀应该代表没有前缀。
							if(key.startsWith(pre) && !key.substring(pre.length()).contains(".")) {
								//有一个前缀符合，就改变标志状态，表示不排除
								flag = false;
								break;
							}
						}
						if(flag) {
							continue;
						}
					}
					String value = String.valueOf(entity.getValue());
					
					//这里可以优化成对properties文件加载的数据的占位符处理模块。
					//如果替换符是${}格式，则不能直接使用正则相关方法，如replaceAll，会报正则语法错误异常。
					//目前是#<>格式，暂时没有和程序方法，以及业务冲突。占位符不能在业务中有意义。
					//以后再优化，现在暂时用着。
					
					//当前工作目录路径占位符。
					if(value.contains(PlaceholderConstant.USER_DIR)) {
						String projectPath = System.getProperty("user.dir");
						//不能用replaceAll方法，因为替换符是${}格式，在正则表达式中是特殊字符。
						value = value.replace(PlaceholderConstant.USER_DIR, projectPath);
					}
					//因为从properties文件里加载的数据如果key存在，则value不会为null值，只有""空字符串，所以要约定怎么表示null。
					//约定value为"${null}"字符串时，转换为null值。
					//最好在刚获取数据时就统一处理好。占位符不能在业务中有意义。否则就需要细分在业务中处理。
					if(value.equals(PlaceholderConstant.SET_NULL)) {
						value = null;
					}
					propsMap.put(key, value);
				}
			}
		}
		return propsMap;
	}

}
