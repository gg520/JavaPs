package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

public class ReadConfigList {
	public static String firstUrl;
	public static boolean isPre;
	public static String urlType;
	public static String page;
	public static String dateBase;
	public static String listPath;
	public static String attributeName;
	public static String prefix;
	
	public static HashMap<String, String> pathHashMap;
	
	static {
		Properties properties = new Properties();
		File file = new File("./configDB.properties");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("读取配置文件失败！");
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		firstUrl = properties.getProperty("firstUrl");
		isPre = properties.get("isPre").equals("true") ? true : false;
		urlType = properties.get("urlType").toString();
		page = properties.get("page").toString();
		dateBase = properties.get("dateBase").toString();
		listPath = properties.get("listPath").toString();
		attributeName = properties.get("attributeName").toString();
		prefix=properties.get("prefix").toString();
		String dbs[]=dateBase.split(",");
		if(dbs.length>0){
			pathHashMap=new HashMap<>();
			int index=0;
			while(index<dbs.length){
				
				pathHashMap.put(dbs[index], properties.get(dbs[index]).toString());
				pathHashMap.put(dbs[index]+"_sub", properties.get(dbs[index]+"_sub").toString());
				pathHashMap.put(dbs[index]+"_attr", properties.get(dbs[index]+"_attr").toString());
				index++;
			}
		}
		
		
	}

	public static void showConfig() {
		System.out.println("============================================");
		System.out.println("第一页网址： " + firstUrl);
		System.out.println("是否拼接网站：  "+ isPre);
		System.out.println("网站遍历类型: " + urlType);
		System.out.println("页数: " + page);
		System.out.println("数据库: " + dateBase);
		System.out.println("选择器: " + listPath);
		System.out.println("属性: " + attributeName);
		System.out.println("拼接头: " + prefix);
		System.out.println("字段path:"+pathHashMap);
		System.out.println("============================================");
	}

	public static void main(String[] args) {
		showConfig();
	}


}
