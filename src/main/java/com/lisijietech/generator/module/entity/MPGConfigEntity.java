package com.lisijietech.generator.module.entity;

import java.io.Serializable;
import java.util.Map;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.lisijietech.generator.module.inject.annotation.DataSource;
import com.lisijietech.generator.module.inject.annotation.DataValue;
import com.lisijietech.generator.module.inject.handler.impl.EmbedHandlerImpl;
import com.lisijietech.generator.module.inject.handler.impl.MapSimpleHandlerImpl;

/**
 * mybatis-plus-generator的配置实体类
 * @author lisijie
 * @date 2020年6月3日
 */
//@DataSource(value = {"generator-config.properties"})
@DataSource(value = {"file:config/generator-config.properties"})
public class MPGConfigEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//全局配置
	@DataValue(prefix = {"generator.global."}, handler = EmbedHandlerImpl.class)
	private GlobalConfig globalConfig;
	
	//数据源配置
	@DataValue(prefix = {"generator.data-source."}, handler = EmbedHandlerImpl.class)
	private DataSourceConfig dataSourceConfig;
	
	//包配置
	@DataValue(prefix = {"generator.package."}, handler = EmbedHandlerImpl.class)
	private PackageConfig packageConfig;
	
	//自定义输出文件配置
	//Map的key为template路径(带模板引擎类型后缀，可以是加上file:协议前缀的路径，也可以是classpath)，value是文件输出路径。
	@DataValue(prefix = {"generator.file-out."}, handler = MapSimpleHandlerImpl.class)
	private Map<String,String> fileOutConfig;
	
	//配置模板。
	@DataValue(prefix = {"generator.template."}, handler = EmbedHandlerImpl.class)
	private TemplateConfig templateConfig;
	
	//策略配置
	@DataValue(prefix = {"generator.strategy."}, handler = EmbedHandlerImpl.class)
	private StrategyConfig strategyConfig;

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	public DataSourceConfig getDataSourceConfig() {
		return dataSourceConfig;
	}

	public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
	}

	public PackageConfig getPackageConfig() {
		return packageConfig;
	}

	public void setPackageConfig(PackageConfig packageConfig) {
		this.packageConfig = packageConfig;
	}

	public Map<String, String> getFileOutConfig() {
		return fileOutConfig;
	}

	public void setFileOutConfig(Map<String, String> fileOutConfig) {
		this.fileOutConfig = fileOutConfig;
	}

	public TemplateConfig getTemplateConfig() {
		return templateConfig;
	}

	public void setTemplateConfig(TemplateConfig templateConfig) {
		this.templateConfig = templateConfig;
	}

	public StrategyConfig getStrategyConfig() {
		return strategyConfig;
	}

	public void setStrategyConfig(StrategyConfig strategyConfig) {
		this.strategyConfig = strategyConfig;
	}
	
}
