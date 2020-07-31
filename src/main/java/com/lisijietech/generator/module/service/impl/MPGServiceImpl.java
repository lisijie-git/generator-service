package com.lisijietech.generator.module.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.lisijietech.generator.module.engine.CustomTemplateEngine;
import com.lisijietech.generator.module.entity.MPGConfigEntity;
import com.lisijietech.generator.module.inject.service.impl.PropInjectServiceImpl;
import com.lisijietech.generator.module.service.MPGService;

/**
 * mybatis-plus-generator代码生成器service实现类
 * @author lisijie
 * @date 2020年5月24日
 */
public class MPGServiceImpl implements MPGService{
	
	private static final Logger logger = LoggerFactory.getLogger(MPGServiceImpl.class);
	
	//生成代码输出目录
	private String outputDir;
	//开发人员
	private String author = "lisijie";
	//输出基础文件名的模板。代码默认设置模块都可以，只用配置dao层和service接口层模板
	private String entityName = "%sEntity";
	private String mapperName = "%sDAO";
	private String serviceName = "%sService";
	
	//数据源配置
	private String driverName = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/xxx?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&failOverReadOnly=false";
	private String userName = "username";
	private String password = "password";
	
	//代码生成包路径配置。有默认配置，可以直接把包配置对象设置到mpg。但一般要部分调整才能方便达到需求。
	private String parent = "com.lisijietech.generator";
	private String moduleName = "code";
	private String mapper = "dao";
	//实际mapper xml文件不生成在同一个父包中。要自定义输出。并且默认模板配置设置为空，才不会输出默认模块文件。
	//private String xml = "mapper";
	
	//自定义模板路径，注意后缀是完整的。
//	private String customXmlTemp = "/templates/myMapper.xml.ftl";
	private String customXmlTemp = "file:///C:/Users/ASUS/Desktop/myMapper.xml.ftl";
	
	//基本模板路径。官网说不要最后的模板引擎标识的后缀，会自动识别。
	private String entityTemp = "/templates/myEntity.java";
//	private String daoTemp = "/templates/myDAO.java";
	private String daoTemp = "file:/C:/Users/ASUS/Desktop/myDAO.java";
	private String serviceTemp = "/templates/myService.java";
	private String serviceImplTemp = "/templates/myServiceImpl.java";
	private String controllerTemp = "/templates/myController.java";
	private String xmlTemp = null;
	
	//表前缀。包含有此前缀的表。生成时删除表前缀名称
	private String tablePrefix = "t_";
	//自定义继承父类。框架设置了部分默认值，并且做了空值赋值。如果不需要继承父类，修改模板判断，约定渲染判断值，目前约定为no。
	private String superMapperClass = "no";
	private String superServiceClass = "no";
	private String superServiceImplClass = "no";
	//包含和排除的标明数组。
	private String[] includes;
	private String[] excludes;

	@Override
	public void mpg() {
		//代码生成器
		AutoGenerator mpg = new AutoGenerator();
		
		//全局配置
		GlobalConfig gc = new GlobalConfig();
		//当前用户工作目录(war,jar包或者工程目录的路径，但是在tomcat中会是tomcat的bin目录)
		String projectPath = System.getProperty("user.dir");
		String sep = File.separator;
		outputDir = projectPath + sep +"src"+ sep + "main" + sep + "java";
		
		gc.setOutputDir(outputDir);
		gc.setOpen(false);
		gc.setAuthor(author);
		gc.setBaseResultMap(true);
		gc.setBaseColumnList(true);
		gc.setEntityName(entityName);
		gc.setMapperName(mapperName);
		gc.setServiceName(serviceName);
		
		mpg.setGlobalConfig(gc);
		
		//数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDriverName(driverName);
		dsc.setUrl(url);
		dsc.setUsername(userName);
		dsc.setPassword(password);
		
		mpg.setDataSource(dsc);
		
		//包配置
		PackageConfig pc = new PackageConfig();
		pc.setParent(parent);
		pc.setModuleName(moduleName);
		pc.setMapper(mapper);
		//pc.setXml(xml);
		
		mpg.setPackageInfo(pc);
		
		//自定义配置。 
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				//目前没有自定义方法，不用实现。
			}
		};
		
		//自定义输出文件配置
		List<FileOutConfig> focList = new ArrayList<>();
		focList.add(new FileOutConfig(customXmlTemp) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				//自定义输出文件名。这里配置了mapper xml文件输出路径。因为开发时的资源文件路径和java文件路径不同
				String nameSuffix = entityName.replace("%s", "");
				String name = tableInfo.getEntityName().replace(nameSuffix, "");
				return projectPath + sep + "src" + sep + "main" + sep + "resources" 
						+ sep + "mapper" + sep + pc.getModuleName() 
						+ sep + name + "Mapper" + StringPool.DOT_XML;
			}
		});
		
		//自定义是否允许创建文件。没有必要自定义，一般逻辑来说文件存在就不允许覆盖创建。
//		cfg.setFileCreate(new IFileCreate() {
//			@Override
//			public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
//				return !new File(filePath).exists();
//			}
//		});
		
		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);
		
		//配置模板。
		TemplateConfig templateConfig = new TemplateConfig();
		templateConfig.setEntity(entityTemp);
		templateConfig.setMapper(daoTemp);
		templateConfig.setService(serviceTemp);
		templateConfig.setServiceImpl(serviceImplTemp);
		templateConfig.setController(controllerTemp);
		//模板设置为空或null，将不生成该模块。mapper xml需要自定义，默认模块设置为null不生成。
		templateConfig.setXml(xmlTemp);
		
		mpg.setTemplate(templateConfig);
		
		//策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setTablePrefix(tablePrefix);
		strategy.setSuperMapperClass(superMapperClass);
		strategy.setSuperServiceClass(superServiceClass);
		strategy.setSuperServiceImplClass(superServiceImplClass);
//		strategy.setInclude(includes);
//		strategy.setExclude(excludes);
		strategy.setRestControllerStyle(true);
		strategy.setControllerMappingHyphenStyle(true);
//		strategy.setEntityTableFieldAnnotationEnable(true);
		
		mpg.setStrategy(strategy);
		
		//设置模板引擎
//		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.setTemplateEngine(new CustomTemplateEngine());
		
		mpg.execute();
	}
	
	/**
	 * 通过properties文件配置生成代码。
	 * 暂时够用，可以优化。
	 */
	public void mpgFromProp() {
		MPGConfigEntity config = new MPGConfigEntity();
		
		//给实体类注入数据。
		PropInjectServiceImpl propInject = new PropInjectServiceImpl();
		propInject.inject(config);
		
		//代码生成器
		AutoGenerator mpg = new AutoGenerator();
		
		mpg.setGlobalConfig(config.getGlobalConfig());
		mpg.setDataSource(config.getDataSourceConfig());
		mpg.setPackageInfo(config.getPackageConfig());
		
		Map<String,String> fileOutConfig = config.getFileOutConfig();
		if(fileOutConfig != null && fileOutConfig.size() != 0) {
			//自定义配置。 
			InjectionConfig cfg = new InjectionConfig() {
				@Override
				public void initMap() {
					//目前没有自定义方法，不用实现。
				}
			};
			//自定义输出文件配置
			List<FileOutConfig> focList = new ArrayList<>();
			for(Entry<String, String> entry : fileOutConfig.entrySet()) {
				//自定义模板路径，带模板类型后缀
				String customTempPath = entry.getKey();
				//自定义文件输出路径。
				String customFileOutPath = entry.getValue();
				focList.add(
					new FileOutConfig(customTempPath) {
						@Override
						public String outputFile(TableInfo tableInfo) {
							//自定义输出文件名。一般配置mapper xml文件输出路径。因为开发时的资源文件路径和java文件路径不同
							String nameSuffix = config.getGlobalConfig().getEntityName().replace("%s", "");
							String name = tableInfo.getEntityName().replace(nameSuffix, "");
							String path = String.format(customFileOutPath, name);
							return path;
						}
					}
				);
			}
			
			cfg.setFileOutConfigList(focList);
			mpg.setCfg(cfg);
		}
		
		mpg.setTemplate(config.getTemplateConfig());
		mpg.setStrategy(config.getStrategyConfig());
		//设置模板引擎
		mpg.setTemplateEngine(new CustomTemplateEngine());
		
		mpg.execute();
		
	}
	
//	public static void main(String[] args) {
//		MPGServiceImpl mpg = new MPGServiceImpl();
//		//代码中配置，生成代码。
////		mpg.mpg();
//		
//		//properties文件中配置，生成代码。
//		//properties文件路径可以在classpath中，或者系统文件路径中。
//		//classpath写法如，classpath:/a/b.properties,classpath:a/b.properties,/a/b.properties,a/b.properties，四种写法效果一样。
//		//系统文件写法如，绝对路径file:/C:/a/b/c.prop，file:///C:/a/b/c.prop,相对路径file:C:/a/b/c.prop,file://C:/a/b/c.prop。
//		mpg.mpgFromProp();
//	}

}
