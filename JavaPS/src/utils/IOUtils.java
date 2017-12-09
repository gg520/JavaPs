package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {

	/**
	 *
	 * @author guosuzhou
	 *
	 * @param path ./URL_List.txt
	 * @return
	 *
	 *date 2017年12月9日 下午1:35:28
	 */
	public static List<String> ReadFile_txt(String path){
		List<String> strList=new ArrayList<>();
		File file=new File(path);
		FileReader fileReader=null;
		BufferedReader bufferedReader=null;
		if(file!=null){
			try {
				fileReader=new FileReader(file);
				bufferedReader=new BufferedReader(fileReader);
				String line=null;
				while((line=bufferedReader.readLine())!=null){
					if(line!=null&&line.trim().length()>0){
						strList.add(line.trim());
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
			}finally {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fileReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		return strList;
	}
	
	
	public static void WriteFile_txt(String path,List<String> list) {
		if(list==null||list.size()<=0){
			
			return ;
		}
		File file=new File(path);
		FileWriter fileWriter=null;
		BufferedWriter bufferedWriter=null;
		try {
			fileWriter=new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			if(list!=null&&list.size()>0){
				for(String str:list){
					bufferedWriter.write(str+"\t\n");
//					System.err.println(str);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		System.out.println(ReadFile_txt("./URL_List.txt"));
		List<String> strList=new ArrayList<>();
		int index=1;
		while(index<=20){
			strList.add("数据："+index);
			index++;
		}
		WriteFile_txt("./URL_List.txt", strList);
		System.out.println(ReadFile_txt("./URL_List.txt"));
		
		}
}
