package com.bilibili.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

public class FileLengthTools {
	/**
	 * 获取远程文件大小
	 * @param imageUrl  图片地址
	 * @return
	 */
	public static int  getRemoteFileLenght(String imageUrl){
		HttpURLConnection connection = null;
		try {
			URL url = new URL(imageUrl);
			connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6 * 1000);
			int code = connection.getResponseCode();		    
			if (code == 200) {
				int fileLength = connection.getContentLength();
				//System.out.println("获取长度成功！长度："+fileLength);
				return fileLength;
			}
			System.out.println("响应码是" +connection.getResponseCode());
			return 0;
		} catch (IOException e2) {
			e2.printStackTrace();
			return 0;
		} finally {
            if(connection!=null)  connection.disconnect();      
		}
	}
	
	public static  int  getFileSizeHadDownload(File cashTxt) throws IOException{
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(cashTxt)));
		String lastPostion_str = bufferedReader.readLine();
		int startPostion = 0;
        if(null==lastPostion_str||"".equals(lastPostion_str)){   
        }else{  
        	startPostion = Integer.parseInt(lastPostion_str)-1;//设置下载起点
        }
		System.err.println("文件开始位置：   " + startPostion);
		bufferedReader.close();
		return startPostion;
	}
	
	public static long getLocalFileLength(File image) throws IOException{
		FileChannel fc= null;
		long size = 0L;
        if (image.exists() && image.isFile()){  
            FileInputStream fis= new FileInputStream(image);  
            fc= fis.getChannel();  
            size = fc.size();
            fis.close();
           // System.err.println("imageSize:"+size);  
        }else{  
        	 System.err.println("file doesn't exist or is not a file");  
        }
        
        return size;    
	}

}
