package com.bilibili.main;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.alibaba.fastjson.JSONArray;
import com.bilibili.tools.FileReaderTools;
import com.bilibili.tools.FileWriterTools;
import com.bilibili.tools.RenameTools;

import redis.RedisClient;


public class Main {
	
	private static Log logger = LogFactory.getLog(Main.class); 

	public static void main(String[] args) {
		
		//Log logger= LogFactory.getFactory().getInstance(Main.class);
		
		PropertyConfigurator.configure("src/main/resources/log4j.propertier"); 
		//System.out.println("logger:"+logger);
	
		
		logger.info("info");
		
/*		URL resource = Thread.currentThread().getContextClassLoader().getResource("");
		//URLResource:   \file:/C:/Users/itbys/workspace/yande.re/target/classes/
		String path = String.valueOf(resource).replaceAll("file:/", "").replaceAll("%20", "").trim();
		if(path.indexOf(":")!=1){
			path = File.separator + path;
		}
		System.out.println("ResourcePath:   "+path);
		//ResourcePath:   C:/Users/itbys/workspace/yande.re/target/classes/
*/		
		System.exit(1);

			
		 String filePath ="F:\\BiliBiliDownload";
		 
        //有s_开头的时候 ，放开注释再运行一次
		// hasAnimations = true;
		 
		 Flags flags = Flags.getInstance();
		 flags.setReverseFlag(false);
		 flags.setHasAnimations(false);
		 RedisClient redisClient = new RedisClient();
		 
		 if(args.length >0){		 
			 filePath = args[0];
		 }	
		 if(args.length >1){
			 String flag = args[1];
			 if(flag.equals("1")){			 
				 flags.setHasAnimations(true);
			 }
		 }
		 
		 if(args.length >2){
			 String flag = args[2];
			 if(flag.equals("1")){			 
				 flags.setReverseFlag(true);
			 }
		 }
		 
		 if(filePath==null || filePath.equals("") || filePath.equals("null")){
			 System.out.println("缺少文件路径");
			 return; 
		 } 
		 	
		// JsonArray jsonAry = maven_gson.getJsonArray();
		 
		 JSONArray ranamejsonAry = new JSONArray();
		 
		FileReaderTools.setFlags(flags);
		FileReaderTools.setRedisClient(redisClient);
		           
        File file = FileReaderTools.getFile(filePath);
        //获取文件        
        try {
			FileReaderTools.listFile(file,ranamejsonAry);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}//遍历文件
        
        boolean isSuccess = FileWriterTools.writeJsonArrayToFile(ranamejsonAry, filePath);
        //由于测试需要，关闭重命名
        isSuccess = true;
        
        if(!isSuccess){
       	 System.out.println("写入失败");
        }else{        	 
       	 System.out.println("写入文件完毕，下一步进行重命名……");  
       	 
	       	try {
				RenameTools.renameFilesWithJSONFilePath(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}

		 }

	}
	


}
