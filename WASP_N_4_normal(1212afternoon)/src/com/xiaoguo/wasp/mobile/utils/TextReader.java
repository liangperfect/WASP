package com.xiaoguo.wasp.mobile.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
/*
 * @author hcq
 * 
 * */
public class TextReader {
	/*
	 * 通过输入流InputStream获取内容
	 * param:InputStream
	 * return:String
	 * */
	public static String getString(InputStream inputStream){
		InputStreamReader inputStreamReader = null;
		try{
			inputStreamReader = new InputStreamReader(inputStream,"gbk");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line=null;
		try{
			while ((line = reader.readLine())!=null) {
				sb.append(line);
				sb.append("\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	/*
	 * 通过文件路径获取文件内容
	 * param:String (filePath)
	 * return:String 
	 * */
	public static String getStringFromFile(String filePath){
		File file = new File(filePath);
		FileInputStream stream = null;
		try{
			stream = new FileInputStream(file);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return getString(stream);
	}
}
