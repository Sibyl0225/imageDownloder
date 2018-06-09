package com.bilibili.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class FileWriterTools {
	
	public static boolean writeJsonArrayToFile(JSONArray jsonAry,String filePath){
		
//		   File file;
//		try {
//			
//			file = new File(filePath+"/fileInfo.txt");
//			PrintStream printStream = new PrintStream(file);
//			String jsonStr = jsonAry.toString();
//			System.out.println(jsonStr);
////			if(jsonStr.indexOf("[")<0){
////				System.out.println("没有找到  list 标记！！！");
////			}else{
////				jsonStr = jsonStr.substring(jsonStr.indexOf("["));
////			} 
//			printStream.print(jsonStr);
//			printStream.close();
//			System.out.println("successful");
//			return true;
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			
//		} 
			   
			if(jsonAry.isEmpty()){
				System.out.println("NULL JSON ……"); 
				return false;		
			}
			System.out.println("Convert Java object to JSON format and save to file");  
			BufferedWriter bw;
			try  { 
				 OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(filePath+"/fileInfo.json"), "UTF-8");  		  
		         bw =new BufferedWriter(osr);
		         bw.write("[");
		         bw.newLine();
		         for(int i=0;i<jsonAry.size();i++){
		        	 JSONObject obj = (JSONObject) jsonAry.get(i);
		        	 Set<String> keySet = obj.keySet();
		        	 Object[] array = keySet.toArray();
			        	 for (int j = 0; j < array.length; j++) {	
			        		 String key = array[j].toString();
			        		 if(j==0){
			        			 bw.newLine();
			        			 bw.write("    {"); 
			        		 }  
			        		 bw.newLine();
			        		 
			        		 //获取文件路径（不是文件名）  这里可作相应处理
			        		 String val = obj.getString(key).toString();
			        		 bw.write("        \""+key+"\":\""+val+"\"");
			        		 if(j!=array.length-1) bw.write(","); 
			        		 if(j==array.length-1){
			        			 bw.newLine();
			        			 bw.write("    }");
			        		 }
						}	        		 
			        
			        	 if(i!=jsonAry.size()-1) bw.write(","); 
		             
		         }
		         bw.newLine();
		         bw.write("]");
		         bw.close();
			     return true;
			  
			} catch (IOException e) {  
				e.printStackTrace();
			  
			} 
	  
		return false;
		}
		
		
		public static JSONArray readFileToJSONArray(String filePath) throws IOException{
			
//			File file;
//			FileReader fr;
//			BufferedReader br;
//			try {
//				
//				file = new File(filePath);
//				fr = new FileReader(file);
//	            br = new BufferedReader(fr);
//	            StringBuffer sb = new StringBuffer();
//	            String s;
//	          
//				while ((s = br.readLine() )!=null) {
//				     System.out.println (s);
//				     sb.append(s);
//				  }
	//
//	             JsonParser jsonParser = new JsonParser();
//	             JsonArray jsonArray = jsonParser.parse(sb.toString()).getAsJsonArray();
//	             fr.close();
//	             return jsonArray;
//	            
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			System.out.println("Read JSON from file, convert JSON string back to JSONArray");  
			  
			try {  
//			   Gson gson = new Gson();
//			   JsonArray jsonArray = new JsonArray();
//				
//			   BufferedReader reader = new BufferedReader(new FileReader(filePath));
//			  
//			    jsonArray = gson.fromJson(reader, jsonArray.getClass());
//			    
//			    return jsonArray;
				JSONArray jsonAry = new JSONArray();
				   InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");  
				   BufferedReader reader = new BufferedReader(isr);
		            StringBuffer sb = new StringBuffer();
		            String s;	          
					while ((s = reader.readLine() )!=null) {
					     System.out.println("转换：+++++++++++++++++JSONArray"+s);
					     sb.append(s);
					  }
				
					jsonAry = JSON.parseArray(sb.toString());
				    
				    return jsonAry;			
			  
			} catch (FileNotFoundException e) {  
			   System.out.println("文件爱你未找到……233");
			   e.printStackTrace();
			} catch (IOException e) {  
				   System.out.println("IOException……233");
				   e.printStackTrace();
			} 
		   
			return new JSONArray();
		}
		
		public static JsonArray readFileToJsonArray(String filePath){
			
			System.out.println("Read JSON from file, convert JSON string back to JsonArray");  
			try {
				
				InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");  
				BufferedReader br = new BufferedReader(isr);
	            StringBuffer sb = new StringBuffer();
	            String s;
	          
				while ((s = br.readLine() )!=null) {
				     System.out.println("转换：+++++++++++++++++JsonArray"+s);
				     sb.append(s);
				  }

	             JsonParser jsonParser = new JsonParser();
	             JsonArray jsonArray = jsonParser.parse(sb.toString()).getAsJsonArray();
	             br.close();
	             return jsonArray;
	            
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        return new JsonArray();

			
		}
		
		public static JSONObject readFileToJSONObject(String filePath){
			
			System.out.println("Read JSON from file, convert JSON string back to JSONObject");  
			try {
				
				InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");  
				BufferedReader br = new BufferedReader(isr);
	            StringBuffer sb = new StringBuffer();
	            String s;
	          
				while ((s = br.readLine() )!=null) {
				     System.out.println (s);
				     sb.append(s);
				  }

	             JSONObject jsonObject = JSON.parseObject(sb.toString());
	             br.close();
	             return jsonObject;
	            
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        return new JSONObject();

			
		}

}
