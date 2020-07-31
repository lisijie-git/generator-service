package com.lisijietech.generator.module.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 自定义模板引擎。
 * mybatis-plus-generator自定义模板引擎。这里只是把mpg自带的freemarker配置做了下修改。
 * 因为模板路径配置默认的只能获取classpath下的模板目录，这里动态判断模板目录路径，能获取系统文件路径下的模板。
 * @author lisijie
 * @date 2020年6月2日
 */
public class CustomTemplateEngine extends AbstractTemplateEngine{
	
//	//相对路径目录名字
//	private static final String RELATIVE_DIRECTORY = "tamplates";	
	
	private Configuration configuration;
	
	@Override
	public CustomTemplateEngine init(ConfigBuilder configBuilder) {
		super.init(configBuilder);
		configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setDefaultEncoding(ConstVal.UTF8);
//		configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, StringPool.SLASH);
		return this;
	}
	
	@Override
	public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
		//统一文件路径分割符。windows系统的文件路径会是C:\a\b\c这样，也能是C:/a/b/c
		templatePath = templatePath.replace("\\", "/");
//		configuration.clearTemplateCache();
		//约定路径以file:开头时，就系统文件方式加载模板，否则就是类路径方式加载模板。
		String loaderType = "classLoader";
		if(templatePath.startsWith("file:")) {
			loaderType = "file";
			String basePath = "";
			//去除file:字符串
			String filePath = templatePath.replaceAll("file:", "");
			
			//判断模板是否是绝对路径。file:协议绝对路径是file:///a/b或者file:/a格式。
			String p = "^/\\w\\S*|^///\\w\\S*";
			if(Pattern.compile(p).matcher(filePath).matches()) {
				//判断是什么系统的绝对路径。
				//file:协议的绝对路径是一个或多个/斜杠开头，获取windows的绝对路径是盘符开头如C:，不需要/斜杠
				if(filePath.contains(":")) {
					filePath = filePath.replaceAll("^/+", "");
				}else {
					filePath = filePath.replaceAll("^/+", "/");
				}
				//如果父目录是盘符，如D:,格式需要为D:\\或者D:/,不然目录可能会是当前工程目录(运行程序在同一盘符时)。
				//所以保留最后斜杠。保留最后斜杠不影响非盘符目录的程序定位。
				basePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
				//模板路径开头暂时不包含文件分割符
				templatePath = filePath.substring(filePath.lastIndexOf("/") + 1);
			}else {
				//相对路径，基础目录就是当前用户工作目录。
				basePath = System.getProperty("user.dir");
				//相对路径有可能写为file:a/b，file://a/b
				templatePath = filePath.replaceAll("^//", "");
			}
			
			//设置模板加载器为系统文件方式，并且初始化模板所在基础目录路径
			configuration.setDirectoryForTemplateLoading(new File(basePath));
		}else {
			//设置模板加载器为类路径方式。模板基础目录路径就是工程根路径(或者war,jar路径)，并且只能获取classpath中(war,jar中)的文件
			//方法第一个参数是FreemarkerTemplateEngine.class，本因该是CustomTemplateEngine.class，但是没有区别。
			//都是调用getClassLoader().getResource()方法，都是获取classpath根路径。
			configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, StringPool.SLASH);
		}
		Template template = configuration.getTemplate(templatePath);
		try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
			template.process(objectMap, new OutputStreamWriter(fileOutputStream, ConstVal.UTF8));
		}
		logger.debug("模板加载方式:" + loaderType + ";  模板:" + templatePath + ";  文件:" + outputFile);
	}
	
	@Override
	public String templateFilePath(String filePath) {
		return filePath + ".ftl";
	}
}
