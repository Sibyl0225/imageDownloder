//package com.bilibili.tools;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import junit.framework.Assert;
//import redis.RedisClient;
//import redis.clients.jedis.ShardedJedis;  
//
//
//public class JsoupParse2 {
//	  
//	    static String filePath ="C:\\yande";
//	    static int i = 0;
// 
//	    public static  void main(String[] args) throws IOException{  
//	    //String url = "http://www.weather.com.cn/weather/101200101.shtml";//武汉城区  
//	    String yande = "https://yande.re/post?page=2";
//	    long start = System.currentTimeMillis();  
//	    Document doc=null;  
//	    ShardedJedis jedis = null;
//	    List<String> imgs = new ArrayList<String>();
//	   
//	    try{  
//	        doc =  Jsoup.connect(yande).timeout(1000*60).get();
//	        RedisClient redisClient = new RedisClient();
//	        jedis = redisClient.shardedJedis;
//	    }  
//	    catch(Exception e){  
//	        e.printStackTrace();  
//	    }  
//	    finally{  
//	        System.out.println("Time is:"+(System.currentTimeMillis()-start) + "ms");  
//	    }  
////	    Element elem = doc.getElementById("post-list-posts"); 
////	    
////	    System.out.println("Title is:" +elem.html());  
//	    Elements selectImgs = doc.select("a.directlink");
//	    if(selectImgs==null){
//		    selectImgs = doc.select("a.directlink largeimg");
//		    System.out.println("directlink 不可行！");
//	    }
////	    for (Element element : selectImgs) {
////			System.out.println("html:"+element.html());
////		}
////	    for (int i = 0; i < 5; i++) {			
////	    	System.out.println("++++++++++++++++++++++++++++++++++");
////		}
//	    for (Element element : selectImgs) {
//			String imgUrl = element.attr("href");
//			String imgSize = null;
//			Elements childrens = element.children();
//			if(childrens.hasClass("directlink-res")){				
//				imgSize = childrens.select("span.directlink-res").html();
//			}
//			System.out.println("imgSize:"+imgSize);
//			System.out.println("         imgUrl:"+imgUrl);
//			Map<String, String> imgInfo = new LinkedHashMap<String,String>();
//			imgInfo.put("imgSize", imgSize+"");
//			imgInfo.put("imgUrl", imgUrl+"");
//			imgInfo.put("hadDownload", "false");
//			
//			imgs.add(imgUrl);
//			
//			jedis.hmset(getId(imgUrl), imgInfo);
//		}
//        
//	    Assert.assertNotNull(imgs);
//	    System.out.println("解析完毕开始下载第一张图片！");
//	    //if(imgs.size()>0)  downloadImg(imgs);
//	    if(imgs.size()>0){
//	    	
//	    	try {
//	    		multipartDownloadImg(imgs);
//	    	} catch (InterruptedException e) {
//	    		// TODO Auto-generated catch block
//	    		e.printStackTrace();
//	    	}
//	    }
//	    }  
//	    
//	    public static void multipartDownloadImg(List<String> imgs) throws InterruptedException, IOException{
//			//加载下载位置的文件
//            File tempFile = new File(filePath,getId(imgs.get(i))+"_downThread_" + 1+".dt");
//            if(!tempFile.exists()){//如果文件不存在   表示此文件已下载完或者未下载
//            	if(new File(filePath+"\\"+getFileName(imgs.get(i))).exists()){ //文件已下载
//					System.out.println("文件已下载！跳过……");
//            	}
//            }
//        	int threadCount = 3;
//			URL url = new URL(imgs.get(i));
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//			connection.setRequestMethod("GET");
//			connection.setConnectTimeout(60 * 5 * 1000);
//			int code = connection.getResponseCode();
//			if (code == 200) {
//				int fileLength = connection.getContentLength();
//				if(fileLength>(1024*512*4)){
//					System.out.println("more then 512K*4 ,so pass!");
//				} 
//				int blockSize = fileLength/threadCount;//计算每个线程理论上下载的数量.
//				RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath+"\\"+getFileName(imgs.get(i))), "rw");
//				randomAccessFile.setLength(fileLength);
//				threadCount = (int) Math.ceil( fileLength/blockSize);
//	    	CountDownLatch latch = new CountDownLatch(threadCount);
//	    	MultipartDownloadTools.DownImage(imgs.get(i), filePath,fileLength,threadCount,latch);
//	    		latch.await();
//			}
//	    	
//	    	i++;
//			try {
//				Thread.sleep(1000*10L);
//				System.out.println("已暂停10秒，开始下载第 "+i+"张图片！");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//	    	if(i+1<imgs.size()) multipartDownloadImg(imgs);	    	
//	    }
//	    
//	    public static void downloadImg(List<String> imgs){
//	    	
//			boolean downImages = HtmlTools.downImages(filePath, imgs.get(i));
//			System.out.println("第"+(i+1)+"张图片下载完毕！等待10s...");
//			if(downImages){
//				i++;
//				try {
//					Thread.sleep(1000*10L);
//					System.out.println("已暂停10秒，开始下载第 "+i+"张图片！");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				downloadImg(imgs);
//			}else{
//				System.out.println(i);
//			}
//	
//	    }
//	    
//		// 获取下载文件的名称
//		public static String getFileName(String path) {
//			return path.substring(path.lastIndexOf("/") + 1);
//		}
//	    
//	    
//	    public static String getId(String url){
//			 Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(url);
//			 while (matcher.find()){
//				 System.out.println(matcher.group());
//				try {
//					return  URLDecoder.decode(matcher.group(), "UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			 }
//			 return "缺省"+new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date());
//	    }
//	}
