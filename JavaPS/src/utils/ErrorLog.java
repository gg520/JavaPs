package utils;

import java.util.ArrayList;
import java.util.List;

public class ErrorLog {

	public static void addErrorLog(String list){
		List<String> strList=IOUtils.ReadFile_txt("./errorLog.txt");
		if(strList==null){
			strList=new ArrayList<>();
		}
		strList.add(list);
		IOUtils.WriteFile_txt("./errorLog.txt", strList);
	}
	public static void main(String[] args) {
		
		
	}
}
