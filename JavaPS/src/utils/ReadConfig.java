/**  
 * @author yokoboy
 * @date 2013-9-25
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author yokoboy
 * @date 2013-9-25
 */
public class ReadConfig {

//	public static String filedir;
	public static int thread;
//	public static boolean isDebug;
	public static boolean isProxy;
	public static boolean isuseGoAgent;
	public static String proxyIp;
	public static int proxyPort;
	public static String cookie;
	public static String tablename;
//	public static String orderBy;
//	public static String downloadType;
//	public static int mark1;  //截止nark
//	public static int mark2;  //完成mark
	//添加功能
	public static int sleep;

	public static boolean isSleep;
	
	public static String encoading;
	
	public static boolean isfromDataBase;
	
	public static String IpFilePath;
	static {
		Properties properties = new Properties();
		File file = new File("./config.properties");
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
//		filedir = properties.getProperty("filedir");
		thread = Integer.parseInt(properties.getProperty("thread"));
//		isDebug = properties.get("debug").equals("true") ? true : false;
		isProxy = properties.get("isProxy").equals("true") ? true : false;
		isuseGoAgent = properties.get("isuseGoAgent").equals("true") ? true : false;
		proxyIp = properties.get("proxyIp").toString();
		proxyPort = Integer.parseInt(properties.get("proxyPort").toString());
		cookie = properties.get("cookie").toString();
		tablename = properties.get("tablename").toString();
//		orderBy = properties.get("orderBy").toString();
//		downloadType = properties.get("downloadType").toString();
//		mark1 = Integer.parseInt(properties.get("mark1").toString());
//		mark2 = Integer.parseInt(properties.get("mark2").toString());
		sleep=Integer.parseInt(properties.get("sleep").toString());
		isSleep=properties.get("isSleep").equals("true")?true:false;
		encoading=properties.get("encoading").toString();
		isfromDataBase=properties.get("isfromDataBase").equals("true")?true:false;
		IpFilePath=properties.get("IpFilePath").toString();
	}

	public static void showConfig() {
		System.out.println("============================================");
//		System.out.println("文件地址： " + filedir);
		System.out.println("线程数：  "+ thread);
//		System.out.println("debug: " + isDebug);
		System.out.println("isProxy: " + isProxy);
		System.out.println("isuseGoAgent: " + isuseGoAgent);
		System.out.println("IP: " + proxyIp);
		System.out.println("Port: " + proxyPort);
		System.out.println("cookie: " + cookie);
		System.out.println("tableName: " + tablename);
//		System.out.println("key: "+key);
//		System.out.println("year_: "+year_);
		System.out.println("sleep: "+sleep);
		System.out.println("isSleep: "+isSleep);
		System.out.println("encoading:"+encoading);
		System.out.println("isfromDataBase:"+isfromDataBase);
		System.out.println("IpFilePath:"+IpFilePath);
		System.out.println("============================================");
	}

	public static void main(String[] args) {
		showConfig();
//		if(key.equals("EnglishiKey")){
//			System.out.println("判断成功==="+key);
//		}
//		String url_T="http://xueshu.baidu.com/s?wd="+"word"+"&ie=utf-8&tn=SE_baiduxueshulib_9r82kicg&filter=sc_year%3D%7B"+year_+"%2C"+year_+"%7D";
//		System.out.println(url_T);
	}

}
