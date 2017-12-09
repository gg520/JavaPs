package test;

import java.util.ArrayList;
import java.util.List;

import utils.IOUtils;

public class IOTest {

	public static void main(String[] args) {
		List<String> listURL=new ArrayList<>();
		//listURL.add("");
		//IOUtils.WriteFile_txt("./URL_List.txt",listURL);
		listURL=IOUtils.ReadFile_txt("./URL_List.txt");
//		if(listURL.get(0).trim().equals("")){
//			listURL.clear();
//			System.err.println(listURL);
//		}
		System.err.println(listURL.size());
	}
}
