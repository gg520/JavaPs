package test;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.GetDocument;
import utils.ReadConfigList;

public class IOTest {

	public static void main(String[] args) {
		try {
			String url="http://www.liepin.com/zhaopin/?industries=&dqs=&salary=&jobKind=2&pubTime=&compkind=&compscale=&industryType=&searchType=1&clean_condition=&isAnalysis=&init=1&sortFlag=15&flushckid=0&fromSearchBtn=1&headckid=2c56b945e4be2dbc&d_headId=53b24275082322ff0d34fed319c5f09e&d_ckId=b15d76f7a0e5f87e5206a3131b878f7c&d_sfrom=search_unknown&d_curPage=0&d_pageSize=40&siTag=LiAE77uh7ygbLjiB5afMYg%7EZmXRTG3Nx-lODupCxpuySA&key=%E6%95%B0%E6%8D%AE%E6%8C%96%E6%8E%98";
			System.err.println(ReadConfigList.firstUrl);
			Document doc=GetDocument.HttpClient_SleepAndIP(url);
			Elements es=doc.select("#sojob > div:nth-child(7) > div > div.job-content > div:nth-child(1) > ul > li");
			System.out.println(es.size());
			for(Element e:es){
				System.out.println(e.attr(ReadConfigList.attributeName).trim());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
}
