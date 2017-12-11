package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import utils.ProxyHost.IP;

public class GetDocument {
	public static int sleep=ReadConfig.sleep;
	public static boolean isSleep=ReadConfig.isSleep;
	private static Boolean isProxy=ReadConfig.isProxy;
	private static String proxyIp=ReadConfig.proxyIp; //代理ip
	private static int proxyPort=ReadConfig.proxyPort; //代理端口
	private static String encoading=ReadConfig.encoading;
	
	public static Document changeIp(String url,String Encoading){
		Document document1 = null;
		int ii = 1;
		while (ii <= 10) { // 请求超时多次请求
			try {
				document1 = changeIp1(url, Encoading);
				return document1;
			} catch (Exception e1) {
				try {
					if(sleep>0&&isSleep){
						Thread.sleep(sleep*1000);
//						System.out.println("第"+ii+"次访问该网站失败，等待 "+sleep+" 秒");
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				ii++;
			}
	}
		return null;
	}
	
	/**
	 * post请求http协议
	 *
	 * @author guosuzhou
	 *
	 * @param url 路径
	 * @param map 请求头
	 * @return
	 *
	 *date 2017年12月11日 下午6:27:34
	 */
	public Document doPost(String url,Map<String,String> map){
        HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);  
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,ReadConfig.encoading);  
                httpPost.setEntity(entity);  
            }  
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,ReadConfig.encoading);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return Jsoup.parse(result);  
    }  
	
	/**
	 * httpClient请求
	 *
	 * @author guosuzhou
	 *
	 * @param url
	 * @param Encoading
	 * @return
	 *
	 *date 2017年12月11日 下午6:22:04
	 */
	public static Document changeIp1(String url,String Encoading) {
		StringBuilder sBuilder = null;
		InputStream inputStream = null;
		IP ip = null;
		Document document = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			if (ReadConfig.isProxy) {
				HttpHost proxy = null;
				if (ReadConfig.isuseGoAgent) {//手动设置ip
					proxy = new HttpHost(ReadConfig.proxyIp,
							ReadConfig.proxyPort);
				} else {
					ip = ProxyHost.getIp(true);
					proxy = new HttpHost(ip.ip, ip.port);
				}
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);// 
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);// 
			httpClient.getParams().setParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			//链接网址
			HttpGet httpGet = new HttpGet(url.trim());
			httpGet.setHeader("User-Agent", UserAgent.getUserAgent());
			httpGet.setHeader("Cookie",ReadConfig.cookie);
			
            
			
         HttpResponse execute = httpClient.execute(httpGet);
			System.err.println(execute.getStatusLine());
			if (execute.getStatusLine().getStatusCode() != 200) {
				if (ip != null) {
					int code = execute.getStatusLine().getStatusCode();
					System.out.println(ip.ip + ":" + ip.port + "---" + code
							+ "--");
					ProxyHost.removeIp(ip);
				}
				return null;
			}
			BufferedReader bReader = null;
			sBuilder = new StringBuilder();
			HttpEntity entity = execute.getEntity();
			inputStream = entity.getContent();
			if(Encoading.toUpperCase().contains("UTF-8")){
				bReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				for (String line = bReader.readLine(); line != null; line = bReader
						.readLine()) {
					sBuilder.append(line).append("\r\n");
				}
			}else{
				bReader = new BufferedReader(new InputStreamReader(inputStream,
						"gbk"));
				for (String line = new String(bReader.readLine().getBytes("gbk"),"utf-8"); line != null; line = bReader.readLine()) {
					sBuilder.append(line).append("\r\n");
				}
			}
			if (sBuilder != null) {
				document = Jsoup.parse(sBuilder.toString());
			}
		} catch (Exception ex) {
			if (ip != null) {
				ProxyHost.removeIp(ip);
			}
			ex.printStackTrace();

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return document;
	}
	
	/**
	 * jsonp get请求
	 *
	 * @author guosuzhou
	 *
	 * @param url
	 * @return
	 *
	 *date 2017年12月11日 下午6:29:41
	 */
	public static Document connect(String url){
		Document document1 = null;
		int ii = 1;
		while (ii <= 10) { // 请求超时多次请求
			try {
				 
				document1 = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0")
						.ignoreContentType(true).timeout(30000)
						.get();
				return document1;
			} catch (IOException e1) {
				ii++;
			}
	}
		return null;
	}
	/**
	 * 获取json值
	 * @param url
	 * @return
	 */
	public static String jsonIP(String url){
		HttpClient httpClient = new DefaultHttpClient();
		 HttpGet httpGet = new HttpGet(url);
		 if(isProxy){
			 HttpHost proxy=new HttpHost(proxyIp,proxyPort);
			 System.err.println("代理IP："+proxyIp+"     端口号："+proxyPort);
			 httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
		 }
		 httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
			httpClient.getParams().setParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			httpGet.setHeader("User-Agent", UserAgent.getUserAgent());
		 HttpResponse execute = null;
	    try{
	    	execute = httpClient.execute(httpGet);
	        if (execute.getStatusLine().getStatusCode() != 200) {
	        	System.err.println("IP失效！！");
	        	System.err.println(execute.getStatusLine().getStatusCode());
	        	return null;
	        }
	    }catch (Exception e) {
	    	
	    	e.printStackTrace();
	    	return null;
	    }
	    String data=null;
	    try{
	    	System.out.println(execute.getStatusLine());
	        HttpEntity entity = execute.getEntity();
	        data=EntityUtils.toString(entity,"UTF-8");
	        
	        return data;
	    }catch (Exception e) {
	    	
	    	e.printStackTrace();
	    }
		return null;
	}
	
	/**
	 * 可以代理IP也可以增加访问时效
	 *
	 * @author guosuzhou
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 *
	 *date 2017年12月9日 下午4:31:57
	 */
	public static Document HttpClient_SleepAndIP(String url) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		url=url.trim();
		IP ip = null;
		 HttpGet httpGet = new HttpGet(url);
		 if (ReadConfig.isProxy) {
				HttpHost proxy = null;
				if (ReadConfig.isuseGoAgent) {//手动设置ip
					proxy = new HttpHost(ReadConfig.proxyIp,
							ReadConfig.proxyPort);
				} else {
					ip = ProxyHost.getIp(ReadConfig.isfromDataBase);//是否从数据库中读取数据，否为：读取文件中的ip
					proxy = new HttpHost(ip.ip, ip.port);
				}
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
				System.err.println("ip:"+proxy);
			}
		 httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
			httpClient.getParams().setParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			httpGet.setHeader("User-Agent", UserAgent.getUserAgent());
//			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
//			httpGet.setHeader("Host", "www.liepin.com");
//			httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
//			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//			httpGet.setHeader("Cache-Control", "max-age=0");
//			httpGet.setHeader("Connection", "keep-alive");
//			httpGet.setHeader("Upgrade-Insecure-Requests", "1");
//			httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//			httpGet.setHeader("Cookie", "_fecdn_=1; gr_user_id=1ebf4e84-9990-43c1-a1d4-c28d2d8eb9cc; __uuid=1512873959427.42; slide_guide_home=1; abtest=0; JSESSIONID=170D4D42DFCE1A05D34672539724A735; __tlog=1512873959428.27%7CR000000035%7CR000000075%7C00000000%7C00000000; __session_seq=49; __uv_seq=49; Hm_lvt_a2647413544f5a04f00da7eee0d5e200=1512873960; Hm_lpvt_a2647413544f5a04f00da7eee0d5e200=1512881911; gr_session_id_bf8a73282d811a1b=687e0f85-39a6-4a79-b245-941690e059bb");
//			httpGet.setHeader("Referer", "https://www.liepin.com/zhaopin/?d_=&fromSearchBtn=2&ckid=9a01887da5e7ac3b&init=-1&jobKind=2&degradeFlag=0&key=%E6%95%B0%E6%8D%AE%E6%8C%96%E6%8E%98&headckid=475426546bf5b6db&d_pageSize=40&siTag=LiAE77uh7ygbLjiB5afMYg~ZmXRTG3Nx-lODupCxpuySA&d_headId=8125b34d484b4137f0ce3231d36a7f43&d_ckId=00008d85d676fbd9692e6d89162a1b29&d_sfrom=search_unknown&d_&curPage=0");
		 HttpResponse execute = null;
		 int num=0;
 		while(num<10){
		    try{
		    	execute = httpClient.execute(httpGet);
		        if (execute.getStatusLine().getStatusCode() != 200) {
		        	//睡眠一下，重新提交
		        	System.err.println(execute.getStatusLine().getStatusCode());
		        	throw new Exception();
		        }else{
		        	break;
		        }
		    }catch (Exception e) {
		    	if(sleep>0&&isSleep){
	        		Thread.sleep(sleep*1000);
	        	}
	        	num++;
			}
	    }
	    String data=null;
	    try{
	    	System.out.println(execute.getStatusLine().getStatusCode());
	        HttpEntity entity = execute.getEntity();
	        data=EntityUtils.toString(entity,encoading);
	        return Jsoup.parse(data);
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	    if(ip!=null)
	    	throw new Exception("ip："+ip.toString()+"失效，请修改ip,或者增加睡眠时间");
	    else
	    	throw new Exception("本地ip失效，请使用代理ip,或者增加睡眠时间");
	}
	
	/**
	 * jsonp利用代理IP的方法get请求获取http协议
	 *
	 * @author guosuzhou
	 *
	 * @param url
	 * @return
	 *
	 *date 2017年12月9日 下午4:43:22
	 */
	public static Document Jsonp_sleep_IP(String url){
		Document document1 = null;
		int ii = 1;
		
		while (ii <= 10) { // 请求超时多次请求
			try {
				if(isProxy){
					//System.out.println("数据");
					System.err.println("代理IP："+proxyIp+"     端口号："+proxyPort);
					document1 = Jsoup
							.connect(url)
							.proxy(proxyIp, proxyPort)//代理ip，手动添加
							.userAgent(
									"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
							.ignoreContentType(true).timeout(30000)
							.get();
					
				}else{
//					System.out.println("未使用代理IP");
					document1 = Jsoup
							.connect(url)
							.userAgent(
									"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0")
							.ignoreContentType(true).timeout(30000)
							.get();
				}
				
				return document1;
			} catch (IOException e1) {
				try {
					if(sleep>0&&isSleep){
						Thread.sleep(sleep*1000);
						System.out.println("第"+ii+"次访问该网站失败，等待 "+sleep+" 秒");
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				ii++;
			}
	}
		return null;
	}
 
	  
	
	/**
	 * HTTPS请求方式
	 *
	 * @author guosuzhou
	 *
	 * @param requestUrl 链接
	 * @param requestMethod 请求方法 get或post
	 * @param outputStr 参数，请求头
	 * @return
	 *
	 *date 2017年12月11日 下午6:34:46
	 */
	public static Document httpsRequest(String requestUrl,String requestMethod,String outputStr){
	    StringBuffer buffer=null;  
	    try{  
	    //创建SSLContext  
	    SSLContext sslContext=SSLContext.getInstance("SSL");  
	    TrustManager[] tm={new MyX509TrustManager()};  
	    //初始化  
	    sslContext.init(null, tm, new java.security.SecureRandom());;  
	    //获取SSLSocketFactory对象  
	    SSLSocketFactory ssf=sslContext.getSocketFactory();  
	    URL url=new URL(requestUrl);  
	    HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();  
	    conn.setDoOutput(true);  
	    conn.setDoInput(true);  
	    conn.setUseCaches(false);  
	    conn.setRequestMethod(requestMethod);  
	    //设置当前实例使用的SSLSoctetFactory  
	    conn.setSSLSocketFactory(ssf);  
	    conn.connect();  
	    //往服务器端传送的内容
	    if(null!=outputStr){
	        OutputStream os=conn.getOutputStream();  
	        os.write(outputStr.getBytes("utf-8"));  
	        os.close();
	    }  
	      
	    //读取服务器端返回的内容  
	    InputStream is=conn.getInputStream();  
	    InputStreamReader isr=new InputStreamReader(is,ReadConfig.encoading);
	    BufferedReader br=new BufferedReader(isr);
	    buffer=new StringBuffer();
	    String line=null;
	    while((line=br.readLine())!=null){
	        buffer.append(line);
	    }
	    }catch(Exception e){
	        e.printStackTrace();  
	    }
	    return Jsoup.parse(buffer.toString());
	}
	public static void main(String[] args){
		Document doc=httpsRequest("https://www.liepin.com/job/1910511536.shtml?sfrom=recom-recom_jd-fab0d74fa437e71dd927d5c2fb803520-1&d_pageSize=100&d_headId=fab0d74fa437e71dd927d5c2fb803520&d_ckId=fab0d74fa437e71dd927d5c2fb803520&d_sfrom=recom_jd&d_curPage=0&d_posi=0","GET",null);  
	    System.out.println(doc);  
	}  
}
