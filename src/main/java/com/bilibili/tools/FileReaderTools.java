package com.bilibili.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibili.main.Flags;

import redis.RedisClient;

public abstract class FileReaderTools {
	
	protected static Flags flags;
	
	protected static RedisClient redisClient;

    public static File  getFile(String path){
		 return  new File(path);
	}
    
    public static void  setFlags(Flags flagsIn){
		 flags = flagsIn;
	}
    
    public static void  setRedisClient(RedisClient redisClientIn){
    	redisClient = redisClientIn;
	}
	
	public static void listFile(File fileIn,JSONArray ranamejsonAry ) throws UnsupportedEncodingException{
        File[] files = fileIn.listFiles();
        if(!(files !=null && files.length > 0)){
        	System.err.println("文件集合是空的！");
        	return;
        }
        for (File file : files) {
	       	 if(file.isDirectory()){	      
	       		 System.out.println("++++++++++++++++++++++文件夹分割线+++++++++++++++++++");  
	       		 try {  System.out.println(fileIn.getCanonicalPath() +"     下的所有文件");  //打印输入文件(夹)的名称
	       		        System.out.println("");
				 } catch (IOException e) { e.printStackTrace(); } 
	       		 
	       		 FileReaderTools.listFile(file,ranamejsonAry);
	       	 }else{//是文件直接输出
	       		//System.out.println(file.getName()); 
//	       		 if(file.getName().equals("boot_files.efi")){
//	       			 System.out.println("找到文件，将进行重命名……");
//	       			 renameFile(FileReader.getLevelsPreFile(file.getPath(), 2),FileReader.getLevelsPreFile(file.getPath(),3)+"\\boot_files");
//	       		 }
	       		 
//	       		 if(file.getName().endsWith("jar")){       			 
//	       			 redisClient.jedis.set(file.getName(),file.getAbsolutePath());
//	       		 }
	       		 
	       		 if(file.getName().equals("entry.json")){
       			 System.out.println("找到文件，entry.json");
       			 System.out.println();
       			 JSONObject jsonObject = FileWriterTools.readFileToJSONObject(file.getPath());
		         if(jsonObject.isEmpty()){
	        		 System.out.println("读取失败或者是空JSON!"); 
	        		 return;
	        	 }  
	        	 foreachObject(jsonObject.toString(),file.getPath(),jsonObject,ranamejsonAry);
       		 }
//	       		 index++;
//	       		System.out.println(file.getPath());
//	       		JsonObject jsonObject = new JsonObject();
//	       		jsonObject.addProperty(index+"", file.getPath().replaceAll("\\\\", "/"));
//	       		jsonAry.add(jsonObject);
	       	 }
			
		}
	}
	
	protected  static void foreachObject(String jsonString,String filePath,JSONObject fristObj,JSONArray ranamejsonAry) 
			throws UnsupportedEncodingException{
		JSONObject  obj  = (JSONObject) JSON.parse(jsonString);
		
		Set<String> keySet = obj.keySet();
		for(String key: keySet){
			Object jsonObj = obj.get(key);
			if(jsonObj==null)  continue; 
			if(jsonObj instanceof JSONObject){
				
				System.out.println("104+++++++++++"+jsonObj);
				
				foreachObject(jsonObj.toString(),filePath,fristObj,ranamejsonAry);
			}else if(jsonObj instanceof JSONArray){
				System.out.println("+++++++++++++++++++++++List++++++++++++++++++++++++++++++++++");
				JSONArray jsonAry = (JSONArray)jsonObj;
				for(int i=0;i<jsonAry.size();i++){
					JSONObject  listObj = (JSONObject) jsonAry.get(i);
					foreachObject(listObj.toString(),filePath,fristObj,ranamejsonAry);
				}
			}else if(jsonObj instanceof String){
			    int oldIndex = 2;
			    int newIndex = 3;
				if(key.equals("title") || key.equals("part")){
					
					
					String str=jsonObj.toString();
					
					String avid = obj.getString("avid");
					
		            //有s_开头的时候 ，放开注释再运行一次
					if(flags.hasAnimations){
							System.out.println("title+++++++++++"+obj);
							if(keySet.contains("ep")){
								
								JSONObject epJSONObject = (JSONObject) obj.get("ep");
								Set<String> otherKeySet = epJSONObject.keySet();
								if(otherKeySet.contains("index")&&otherKeySet.contains("index_title")){
									oldIndex = 1;
									newIndex = 2;
									str= epJSONObject.get("index")+"."+(epJSONObject.get("index_title")+"").replaceAll("/", "-");
									
								}
							}
					}
					
					
						
					str = str.replaceAll("】", "]");
					str = str.replaceAll("【", "[");
					System.out.println(key+":"+str);
					                      
					JSONObject jsonObject = new JSONObject();
					
					if(key.equals("part")){
						oldIndex = 1;
						newIndex = 2;					
					}
					
					jsonObject.put("oldName",RenameTools.getLevelsPreFile(filePath, oldIndex).replaceAll("\\\\", "/"));
					jsonObject.put("newName",RenameTools.getLevelsPreFile(filePath,newIndex).replaceAll("\\\\", "/")+"/"+str);
				
					
					if(flags.reverseFlag){
						
						String page = fristObj.getJSONObject("page_data").getString("page");
						String part = fristObj.getJSONObject("page_data").getString("part");
						if(avid==null&&part!=null){
		                    //avid为空
							jsonObject.put("oldName",RenameTools.getLevelsPreFile(filePath,oldIndex+1).replaceAll("\\\\", "/")+"/"+part);
							jsonObject.put("newName",RenameTools.getLevelsPreFile(filePath,newIndex+1).replaceAll("\\\\", "/")+"/"+fristObj.getString("avid")+"/"+page);
						}else{
							jsonObject.put("newName",RenameTools.getLevelsPreFile(filePath,newIndex).replaceAll("\\\\", "/")+"/"+avid);

						}
					}
					ranamejsonAry.add(jsonObject);
					
					if(key.equals("part")){
							
						String preTitle = fristObj.getString("title");
						if(preTitle.contains("【")||preTitle.contains("】")){							
							preTitle = preTitle.replaceAll("】", "]");
							preTitle = preTitle.replaceAll("【", "[");
						}
						String isContain = redisClient.shardedJedis.get(preTitle);
						if(isContain!=null && redisClient.shardedJedis.exists(preTitle)){		
							redisClient.shardedJedis.del(preTitle);
						}else{
							System.out.println("key-->"+preTitle+"不存在，可能已被删除！");
						}
						
						//str = preTitle+":("+obj.getString("page")+")"+":"+str;
						str = preTitle+":"+str;
						System.out.println(obj);
				
					}
					redisClient.shardedJedis.set(str, jsonObject.getString("newName"));
					
				}else{
					continue;
				}
			}
		}
	}
}
