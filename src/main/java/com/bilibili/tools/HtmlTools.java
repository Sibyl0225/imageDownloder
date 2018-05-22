package com.bilibili.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.bilibili.tools.MultipartDownloadTools.DownloadThread;

public class HtmlTools {
	
	
    /**
     * 根据网页和编码获取网页内容和源代码
     * @param url
     * @param encoding
     */
    public static String getHtmlResourceByUrl(String url,String encoding){
        StringBuffer buffer   = new StringBuffer();
        URL urlObj            = null;
        URLConnection uc      = null;
        InputStreamReader in  = null;
        BufferedReader reader = null;
        
        try {
            // 建立网络连接
            urlObj = new URL(url);
            // 打开网络连接
            uc     = urlObj.openConnection();
            uc.setConnectTimeout(30000);//30s
            uc.setReadTimeout(30000);//30s
            // 创建输入流
            in     = new InputStreamReader(uc.getInputStream(),encoding);
            // 创建一个缓冲写入流
            reader = new BufferedReader(in);
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                // 一行一行追加
                buffer.append(line+"\r\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求超时,请手动重试！");
			System.exit(0);
        } finally{
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }
    
    /**
     * 根据图片的URL下载的图片到本地的filePath
     * @param filePath 文件夹
     * @param imageUrl 图片的网址
     */
    public static boolean downImages(String filePath,Map<String,String> map){
    	String imageUrl =  map.get("largeImg");
    	String preImage =  map.get("preImg");
        // 截取图片的名称
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
        
        //创建文件的目录结构
        File files = new File(filePath);
        if(!files.exists()){// 判断文件夹是否存在，如果不存在就创建一个文件夹
            files.mkdirs();
        }
        try {
        	// 创建文件
        	File file = new File(filePath+fileName);
        	if(file.exists()) return true; //不重复下载
            URL url = new URL(preImage);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            FileOutputStream out = new FileOutputStream(file);
            int i = 0;
            while((i = is.read()) != -1){
                out.write(i);
            }
            is.close();
            out.close();           
        } catch (Exception e) {
            e.printStackTrace();
            return false;  
        }
        return true;                             
    }
    
    /**
     * 根据图片的URL下载的图片到本地的filePath
     * @param filePath 文件夹
     * @param imageUrl 图片的网址
     */
    public static boolean  MultipartDownloadImages(String filePath,Map<String,String> map,int threadCount){
    	
    	//String path =  map.get("largeImg");
    	String path =  map.get("preImg");
    
	    int fileLength = FileLengthTools.getRemoteFileLenght(path);
	
		CountDownLatch latch = new CountDownLatch(threadCount);
		MultipartDownloadTools.DownImage(path, filePath,fileLength,threadCount,latch);
		try {
			latch.await();
			System.out.println("所有线程任务结束！下载完成！");
			 return true;   
		} catch (InterruptedException e) {
			e.printStackTrace();
			 return false;  
		}
    }
    
    
//    public boolean closeableImageDownloader(String filePath,Map<String,String> map){
//    	
//    	String imageUrl =  map.get("largeImg");
//    	String preImage =  map.get("preImg");
//        // 截取图片的名称
//        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
//        
//        //创建文件的目录结构
//        File files = new File(filePath);
//        if(!files.exists()){// 判断文件夹是否存在，如果不存在就创建一个文件夹
//            files.mkdirs();
//        }
//        try {
//        	// 创建文件
//        	File file = new File(filePath+fileName);
//        	if(file.exists()) return false; //不重复下载
//            //分段请求网络连接,分段将文件保存到本地.
//            URL url = new URL(imageUrl);
//
//            //加载下载位置的文件
//            RandomAccessFile downThreadStream = null;
//            if(downThreadFile.exists()){//如果文件存在
//                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
//                String startIndex_str = downThreadStream.readLine();
//                if(null==startIndex_str||"".equals(startIndex_str)){  //网友 imonHu 2017/5/22  
//                }else{  
//                    this.startIndex = Integer.parseInt(startIndex_str)-1;//设置下载起点  
//                }
//            }else{
//                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
//            }
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(10000);
//            //设置分段下载的头信息。  Range:做分段数据请求用的。格式: Range bytes=0-1024  或者 bytes:0-1024
//            connection.setRequestProperty("Range", "bytes="+ startIndex + "-" + endIndex);
//
//            System.out.println("线程_"+threadId + "的下载起点是 " + startIndex + "  下载终点是: " + endIndex);
//
//            if(connection.getResponseCode() == 206){//200：请求全部资源成功， 206代表部分资源请求成功
//                InputStream inputStream = connection.getInputStream();//获取流
//                RandomAccessFile randomAccessFile = new RandomAccessFile(
//                        new File(filePath,getFileName(imageUrl)), "rw");//获取前面已创建的文件.
//                randomAccessFile.seek(startIndex);//文件写入的开始位置.
//
//
//                /*
//                 * 将网络流中的文件写入本地
//                 */
//                byte[] buffer = new byte[1024];
//                int length = -1;
//                int total = 0;//记录本次下载文件的大小
//                while((length = inputStream.read(buffer)) > 0){
//                    randomAccessFile.write(buffer, 0, length);
//                    total += length;
////					float currentRate = (float) (total/Double.valueOf(endIndex-startIndex));
////					if(currentRate-lastRate>0.1){
////						lastRate = currentRate;
////						System.out.println(threadId+"线程：进度为："+lastRate);	
////					}
//                    /*
//                     * 将当前现在到的位置保存到文件中
//                     */
//                    downThreadStream.seek(0);
//                    downThreadStream.write((startIndex + total + "").getBytes("UTF-8"));
//                }
//
//                downThreadStream.close();
//                inputStream.close();
//                randomAccessFile.close();                   
//                System.out.println("线程"+ threadId + "下载完毕");
//                
//				synchronized (DownloadThread.class) {  //删除临时文件
//					currentRunThreadCount -= 1;
//					if (currentRunThreadCount == 0) {
//						for (int i = 0; i < threadCount; i++) {
//							File downTempReadFile = new File(filePath,getId(imageUrl)+"_downThread_" + i+".dt");//获取前面已创建的文件.
//							cleanTemp(downTempReadFile);
//							
//						}
//						System.out.println("多线程下载完毕！");
//					}
//				 }
//				
//				
//            }else{
//                System.out.println("响应码是" +connection.getResponseCode() + ". 服务器不支持多线程下载              "+threadId+"线程");
//                if(connection!=null)  connection.disconnect();                                   
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
