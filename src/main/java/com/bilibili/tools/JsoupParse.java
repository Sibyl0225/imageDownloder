package com.bilibili.tools;
import java.awt.datatransfer.FlavorListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bilibili.main.Flags;
import com.bilibili.main.Main;

import junit.framework.Assert;
import redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;  




public class JsoupParse {
	  
	static String filePath = "C:/yande";
	static int retrayTimes = 0;
	static final String yande = "https://yande.re";
	static RedisClient redisClient = new RedisClient();
	static Flags flags = null;
	
	static Logger logger = Logger.getLogger(Main.class);
	  

	public static void main(String[] args) throws IOException, InterruptedException {
		// String url = "http://www.weather.com.cn/weather/101200101.shtml";//�人��������
//		 int startPage = 0;
//		 int endPage = 0;
//		 if(args.length >1){
//			 startPage = Integer.valueOf(args[0]);
//			 endPage =  Integer.valueOf(args[1]);
//		 }
		
		//BasicConfigurator.configure();
		PropertyConfigurator.configure("src/main/resources/log4j.propertier"); 
		
		
		     flags = Flags.getInstance();
			Jedis jedis = getJedis();
			   // ��ȡ����key  
		     Set<String> set = jedis.keys("*");  
		     java.util.Iterator<String> it = set.iterator();
		     
		     ArrayBlockingQueue<Map<String,String>> queue = new ArrayBlockingQueue<Map<String,String>>(1000);
	  
		     while (it.hasNext()) {
		      String key = it.next();
		      Map<String,String> valueMap = jedis.hgetAll(key);
		      if(!valueMap.get("hadDownload").equals("true")){	
		    	  String keyImage = FileNameUtil.getId(valueMap);
		    	  logger.error("key:  "+keyImage);
		    	  queue.put(valueMap);
		      }
		      //logger.info(key +":"+ valueMap.toString());
		     }
		    
			Assert.assertNotNull(queue);
			logger.info("������"+queue.size()+"��ͼƬ��");
//			 System.exit(0);
			//normalDownloadImg(imgs,3);		
			
	    	for (int i = 0; i < 12; i++) {			
	    		NormalImageDownloadSimple normalImage = new NormalImageDownloadSimple(i,filePath,queue);
	    		normalImage.start();
			}
				

	}
	 

	public static void getPostlist(int pageIndex, int endPage) {

		long start = System.currentTimeMillis();
		Document doc = null;
		ShardedJedis jedis = null;
		String yandeUrl = new String(yande);
		if (pageIndex > endPage) {
			logger.error("������ϣ�");
			return;
			// System.exit(0);
		} else {
			if (pageIndex == 0||pageIndex == 1) yandeUrl += "/post";
			if (pageIndex > 1) yandeUrl += "/post?page=" + pageIndex;

			try {
				doc = Jsoup.connect(yandeUrl).get();
				logger.info("��ȡ������ϣ���ʼ����html����");
				RedisClient redisClient = new RedisClient();
				jedis = redisClient.shardedJedis;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("����              " + yandeUrl + "            ʧ�ܣ�");
				logger.error("���ԣ�");
				retrayTimes++;
				if (retrayTimes <= 3)
					getPostlist(pageIndex, endPage);
			} finally {
				logger.info("Time is:" + (System.currentTimeMillis() - start) / 1000 + "s");
			}
			Elements selectlis = doc.select("img.preview");
			logger.info("����html���,�����ӽڵ㡭��");

			Map<String, String> imgInfo = new LinkedHashMap<String, String>();

			for (Element element : selectlis) {
				imgInfo.clear();
				String imgUrl = null;
				Element parent = element.parent().parent().parent();
				// id
				if (parent.hasAttr("id")) {
					String imgId = parent.attr("id");
					imgInfo.put("imgId", imgId);
					logger.info(imgId);
				}
				// Ԥ��ͼ
				String preImg = element.attr("src");
				// logger.info(preImg);
				imgInfo.put("preImg", preImg);

				Elements children = parent.children();
				if (children.hasClass("directlink")) {
					Elements directlink = children.select("a.directlink");
					// ��ͼ
					String largeImg = directlink.attr("href");
					imgUrl = largeImg;
					imgInfo.put("largeImg", largeImg);
					// logger.info(largeImg);
					// ��ͼ�ߴ�
					String imgSize = directlink.select("span.directlink-res").html();
					imgInfo.put("imgSize", imgSize);
					// logger.info(imgSize);
				}
				imgInfo.put("hadDownload", "false");
				imgInfo.put("createTime", new SimpleDateFormat().format(new Date()));
				// ����Ѿ����ؾ�ֱ������
				if (validationPId(imgInfo)){
					logger.error("������!");
					System.exit(0);
					continue;
				}
				// �������ӵ�list
				//imgs.add(imgInfo);
				// �����д��redis
				jedis.hmset(FileNameUtil.getId(imgInfo), imgInfo);
			}
			pageIndex++;
			retrayTimes = 0;

			try {
				Thread.sleep(1000 * 8L);
				logger.info("����ͣ8�룬��ʼ���ص�                                                                                      " + pageIndex + "ҳͼƬ��");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			getPostlist(pageIndex, endPage);

		}

		
	} 
	    
//	    public static void multipartDownloadImg(List<String> imgs) throws InterruptedException, IOException{
//			//��������λ�õ��ļ�
//            File tempFile = new File(filePath,getId(imgs.get(i))+"_downThread_" + 1+".dt");
//            if(!tempFile.exists()){//����ļ�������   ��ʾ���ļ������������δ����
//            	if(new File(filePath+"\\"+getFileName(imgs.get(i))).exists()){ //�ļ�������
//					logger.info("�ļ������أ���������");
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
//					logger.info("more then 512K*4 ,so pass!");
//				} 
//				int blockSize = fileLength/threadCount;//����ÿ���߳����������ص�����.
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
//				logger.info("����ͣ10�룬��ʼ���ص� "+i+"��ͼƬ��");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//	    	if(i+1<imgs.size()) multipartDownloadImg(imgs);	    	
//	    }
	

	    
//	    public static void downloadImg(List<Map<String, String>> imgs){
//	    	int ImageIndex = flags.getImageIndex();
//	    	Map<String, String> map = imgs.get(ImageIndex);
//			boolean downImages = HtmlTools.MultipartDownloadImages(filePath, map, 1);
//			logger.info("��"+(ImageIndex+1)+"��ͼƬ������ϣ��ȴ�0.5s...");
//			logger.info("���ʱ�䣺   "+new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date()));
//			if(downImages&& ImageIndex<imgs.size()-1){
//				String key = getId(map.get("largeImg"));
//				map.put("hadDownload", "true");
//				redisClient.shardedJedis.hmset(key,map);
//				flags.setImageIndex(flags.getImageIndex()+1);
//				try {
//					Thread.sleep(1500L);
//					logger.info("����ͣ1.5�룬��ʼ���ص� "+ImageIndex+"��ͼƬ��");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				downloadImg(imgs);
//			}else{
//				logger.error("pre�ļ�������ϣ�");
//				System.exit(0);
//			}
//	
//	    }
	
	
		// �����ļ��Ƿ��ѱ�����
		public static boolean validationPId(Map<String, String> map) {
			String imgUrl = map.get("largeImg");
			if(imgUrl!=null&&!imgUrl.equals("")){
				Jedis jedis = getJedis();
				String Pid = FileNameUtil.getId(map);
				if(jedis.exists(Pid)){					
					Map<String, String> hgMap = jedis.hgetAll(Pid);
					String hadDownload = hgMap.get("hadDownload");
					if(hadDownload.equals("true")){
						return true;
					}
				}
			} 
			return false;
		}
		
		public static Jedis getJedis(){
			return redisClient.jedis;
			
		}
	}
