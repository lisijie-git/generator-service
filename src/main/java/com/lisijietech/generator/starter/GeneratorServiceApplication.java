package com.lisijietech.generator.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.lisijietech.generator.module.service.impl.MPGServiceImpl;

public class GeneratorServiceApplication {

	public static void main(String[] args) {
		MPGServiceImpl mpg = new MPGServiceImpl();
		//代码中配置，生成代码。
//		mpg.mpg();
		
//		testUserDir();
		
		//properties文件中配置，生成代码。
		//properties文件路径可以在classpath中，或者系统文件路径中。
		//classpath写法如，classpath:/a/b.properties,classpath:a/b.properties,/a/b.properties,a/b.properties，四种写法效果一样。
		//系统文件写法如，绝对路径file:/C:/a/b/c.prop，file:///C:/a/b/c.prop,相对路径file:C:/a/b/c.prop,file://C:/a/b/c.prop。
		mpg.mpgFromProp();
		

	}
	
	/**
	 * 测试System.getProperty("user.dir")方法，在用命令行调用可执行jar时是什么值，双击可执行jar时是什么值。
	 * https://blog.csdn.net/wangjun5159/article/details/49820615
	 */
	public static void testUserDir() {
		String path = "C:\\Users\\ASUS\\Desktop\\";
		String name = "usr-dir.txt";
		String content = System.getProperty("user.dir") + "\r\n";
		File f = new File(path + name);
		
		//父目录不存在，就创建目录
		File pf = f.getParentFile();
		if(!pf.exists()) {
			pf.mkdirs();
		}
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		OutputStreamWriter fw = null;
		try {
			fos = new FileOutputStream(f,true);
			fw = new OutputStreamWriter(fos,"UTF-8");
			fw.write(content);
			fw.flush();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(fos != null) {
					fos.close();
				}
				if(fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
