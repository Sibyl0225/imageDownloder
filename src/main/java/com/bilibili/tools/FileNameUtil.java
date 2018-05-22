package com.bilibili.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtil {
	
	// 获取下载文件的名称
	public static String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}
	
	
	public static String getId(Map<String, String> map) {
		Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(map.get("largeImg"));
		while (matcher.find()) {
			System.out.println(matcher.group());
			try {
				return  URLDecoder.decode(matcher.group(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}
		return new SimpleDateFormat("yyyyMMdd_HHmm_ss").format(new Date());
	}

    
	public static String getImageUrl(Map<String, String> map) {		
		return map.get("preImg");
	}

}
