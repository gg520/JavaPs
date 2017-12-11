package test;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.ErrorLog;
import utils.GetDocument;
import utils.IOUtils;
import utils.MD5Utils;
import utils.ReadConfig;
import utils.ReadConfigList;
import utils.SQLHelper;
import utils.TimeUtils;

public class test {

	/**
	 * 抓取网站
	 *
	 * @author guosuzhou
	 *
	 * @return
	 *
	 *date 2017年12月9日 下午9:53:01
	 */
	public static List<String> getUrlList(){
		
		List<String> listURL = new ArrayList<>();
		try {
			
			String url="";
			String urlType=ReadConfigList.urlType;
			String page=ReadConfigList.page;
			String pa[]=page.split("-");
			int x=Integer.valueOf(pa[1]);
			while(x>=Integer.valueOf(pa[0])){
				url=urlType.replace("{0}", String.valueOf(x));
				//链接方式；内部设置是否使用代理
				Document doc=GetDocument.HttpClient_SleepAndIP(url);
				if(doc==null){
					System.err.println("IP失效请修改IP");
				}
				Elements es=doc.select(ReadConfigList.listPath);
				for(Element e:es){
					String pageurl=e.attr(ReadConfigList.attributeName).trim();
					listURL.add(pageurl);
				}
				x--;
				
			}
			Document doc=GetDocument.HttpClient_SleepAndIP(ReadConfigList.firstUrl);
			if(doc==null){
				
				System.err.println("IP失效请修改IP");
			}
			Elements es=doc.select(ReadConfigList.listPath);
			for(Element e:es){
				String pageurl=e.attr(ReadConfigList.attributeName).trim();
				listURL.add(pageurl);
			}
			System.out.println("数据共"+listURL.size()+"条");
			return listURL;
		} catch (Exception e) {
			String massage=e.getMessage();
			String errorLog="数据库异常，异常信息："+massage+"；网站查找异常；发生时间："+TimeUtils.getTime();
			ErrorLog.addErrorLog(errorLog);
		}
		return listURL;
		
	}
	
	public static void getData(){
		List<String> listURL=IOUtils.ReadFile_txt("./URL_List.txt");
		if(listURL==null||listURL.size()<=0||(listURL.size()==1&&listURL.get(0).trim().length()<0)){
			listURL=getUrlList();
			IOUtils.WriteFile_txt("./URL_List.txt",listURL);
		}
		//测试数据
		
		try {
			//抓取字段信息
			while(listURL.size()>0){
				String urlpage=listURL.get(0);
				if(ReadConfigList.isPre){
					urlpage=ReadConfigList.prefix+urlpage;
				}
				listURL.remove(urlpage);
				String MD5=MD5Utils.md5Encode(urlpage);
				Document document=null;
				try {
					document=GetDocument.HttpClient_SleepAndIP(urlpage);
					if(document==null){
						//ip失效
						if(listURL!=null&&listURL.size()>0){
							IOUtils.WriteFile_txt("./URL_List.txt",listURL);
						}
						throw new Exception("ip失效，请修改ip,或者增加睡眠时间");
					}
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
				String dataBase[]=ReadConfigList.dateBase.split(",");
				if(dataBase.length>0){
					HashMap<String, String> pathMap=ReadConfigList.pathHashMap;
//				List<HashMap<String, String>> strList=new ArrayList<>();
					int index=0;
					HashMap<String, String> hashMap=new HashMap<>();
					while(index<dataBase.length){
						String selector=pathMap.get(dataBase[index]);
						String data_sub=pathMap.get(dataBase[index]+"_sub");
						String attr=pathMap.get(dataBase[index]+"_attr");
						String str=null;
						if(selector.equals("pageurl")){
							str=urlpage.trim();
						}else if(selector.equals("pagehtml")){
							str=document.toString().trim();
						}else{
							if(selector!=null&&selector.length()>0){
								if(attr!=null&&attr.length()>0&&!attr.equals("text")){
									str=document.select(selector).attr(attr).trim();
									if(attr.equals("pagehtml")){
										str=document.select(selector).toString().trim();
									}
								}else if(attr.equals("pagehtml")){
									str=document.toString().trim();
								}else{
									str=document.select(selector).text().trim();
								}
								
								if(data_sub!=null&&data_sub.length()>0){
									if(data_sub.contains(",")){
										String subStrs[]=data_sub.split(",");
										int start=0;
										int end=0;
										try {
											start=Integer.valueOf(subStrs[0]);
											
										} catch (Exception e) {
											start=str.indexOf(subStrs[0])+subStrs[0].length();
										}
										try{
											end=Integer.valueOf(subStrs[1]);
										} catch (Exception e) {
											end=str.indexOf(subStrs[1]);
										}
										try {
											str=str.substring(start, end).trim();
										} catch (Exception e) {
											String massage=e.getMessage();
											String errorLog="字符截取，异常信息："+massage+"；str:"+str+";总长："+str.length()+"截取的段："+start+","+end+"；url:"+urlpage+"发生时间："+TimeUtils.getTime();
											ErrorLog.addErrorLog(errorLog);
										}
									}else{
										try {
											int index_num=Integer.valueOf(data_sub);
											str=str.substring(index_num).trim();
										} catch (Exception e) {
											try {
												str=str.substring(str.indexOf(data_sub)+data_sub.length()).trim();
											} catch (Exception e2) {
												String massage=e2.getMessage();
												String errorLog="字符截取，异常信息："+massage+"；str:"+str+";总长："+str.length()+"截取的段："+str.indexOf(data_sub)+data_sub.length()+"；url:"+urlpage+"发生时间："+TimeUtils.getTime();
												ErrorLog.addErrorLog(errorLog);
											}
										}
									}
								}else{
									str=str.trim();
								}
							}else
								str="";
						}
						hashMap.put(dataBase[index], str);
						index++;
					}
//					System.out.println("数据："+hashMap);
					String dbStr="MD5,"+ReadConfigList.dateBase.trim()+",createtime";
					Object objs[]=dbStr.split(",");
					objs[0]=MD5;
					int x1=1;
					String valuesNum="?";
					while(objs.length-1>x1){
						objs[x1]=hashMap.get(objs[x1]);
						valuesNum+=",?";
						x1++;
					}
					valuesNum+=",?";
					objs[objs.length-1]=TimeUtils.getTime();
					try {
						String sql="insert into "+ReadConfig.tablename+"("+dbStr+") values("+valuesNum+")";
						SQLHelper.insertBySQL(sql, objs);
						//完成一个删除一个
						IOUtils.WriteFile_txt("./URL_List.txt",listURL);
					} catch (SQLException e) {
						String massage=e.getMessage();
						String errorLog="数据库异常，异常信息："+massage+"；url:"+urlpage+"发生时间："+TimeUtils.getTime();
						ErrorLog.addErrorLog(errorLog);
						/*if(massage.contains("PRIMARY")){
							System.err.println("插入重复数据: url");
						}else if(massage.contains("\\xE7\\xAE...")||massage.contains("\\xC2\\xA0&nbs...")){
							System.err.println("字符集错误，列名为："+massage.substring(massage.indexOf("column")+6,massage.indexOf("at row 1")));
						}else if(massage.contains("Data too long")){
							System.err.println("字段较长，请修改数据库中"+massage.substring(massage.indexOf("column")+6,massage.indexOf("at row 1"))+"字段的长度");
						}else{
							System.err.println("未处理的数据库异常");
							e.printStackTrace();
						}*/
						System.err.println(massage);
					}
				}
				if(ReadConfig.isProxy)
					Thread.sleep(ReadConfig.sleep*1000);
			}
			//所有都计算完毕,
			if(listURL==null||listURL.size()<=0){
				listURL.add("");
			}
			IOUtils.WriteFile_txt("./URL_List.txt",listURL);
		} catch (Exception e) {
			String massage=e.getMessage();
			String errorLog="其他异常，异常信息："+massage+"；发生时间："+TimeUtils.getTime();
			ErrorLog.addErrorLog(errorLog);
			throw new RuntimeException(e);
		}
	}
	public static void main(String[] args) {
		
		getData();
	}
}
