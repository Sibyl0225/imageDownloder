package com.bilibili.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipartDownloadTools {

	/**
	 * @param args
	 */
	
	private static int currentRunThreadCount = 0;
	private static int threadCount = 3;

	public static void main(String[] args) {
		String filePath = "D:\\yande";
		String path = "https://files.yande.re/sample/83b05d07df90d4c3c94f91a46f7fe014/yande.re%20441758%20sample%20animal_ears%20bathing%20homunculus%20naked%20nipples%20onsen%20tagme%20tail%20wet.jpg";
		// 加载下载位置的文件
//		File tempFile = new File(filePath, getId(path) + "_downThread_" + 1 + ".dt");
//		if (!tempFile.exists()) {// 如果文件不存在 表示此文件已下载完或者未下载
//			System.out.println("临时文件未找到！");
//			if (new File(filePath + "\\" + getFileName(path)).exists()) { // 文件已下载
//				System.out.println("文件已下载！跳过……");
//				return;
//			}
//		}
//		URL url = null;
//		try {
//			url = new URL(path);
//		} catch (MalformedURLException e3) {
//			// TODO Auto-generated catch block
//			e3.printStackTrace();
//		}
//		HttpURLConnection connection = null;
//		try {
//			connection = (HttpURLConnection) url.openConnection();
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		try {
//			connection.setRequestMethod("GET");
//			connection.setConnectTimeout(60 * 5 * 1000);
//		} catch (ProtocolException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		int code = 0;
//		try {
//			code = connection.getResponseCode();
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		if (code == 200) {
//			int fileLength = connection.getContentLength();
//			if (fileLength > (1024 * 512 * 4)) {
//				System.out.println("more then 512K*4 ,so pass!");
//			}
//			int blockSize = fileLength/threadCount;//计算每个线程理论上下载的数量.
//			try {
//				RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath + "\\" + getFileName(path)),
//						"rw");
//				randomAccessFile.setLength(fileLength);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			//threadCount = (int) Math.ceil(fileLength / blockSize);
		    int fileLength = FileLengthTools.getRemoteFileLenght(path);

			CountDownLatch latch = new CountDownLatch(threadCount);
			MultipartDownloadTools.DownImage(path, filePath,fileLength,threadCount,latch);
			try {
				latch.await();
				System.out.println("所有线程任务结束！下载完成！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}

	public static void DownImage(String imageUrl,String filePath, int fileLength, int threadCount, CountDownLatch latch) {

		int blockSize = fileLength/threadCount;//计算每个线程理论上下载的数量.
		HttpURLConnection connection = null;

		try {
				for (int i = 0; i < threadCount; i++) {
					int startPostion = i * blockSize;
					int endPostion = (i + 1) * blockSize - 1;
					if (i == (threadCount - 1))
						endPostion = fileLength-1  ;
					new ThreadImageDownload(i, imageUrl, filePath,startPostion,endPostion,latch).start();

				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(connection!=null)  connection.disconnect();	
		}
	}

	public static class DownloadThread extends Thread {
		private int threadId;
		private int endIndex;
		private int startIndex;
		private String filePath;
		private String imageUrl;
		private CountDownLatch latch;

		public DownloadThread(int threadId, int startThred, int endThread,int fileLength,String imageUrl,String filePath, CountDownLatch latch) {
			this.threadId = threadId;
			this.startIndex = startThred;
			this.endIndex = endThread;
			this.filePath = filePath;
			this.imageUrl = imageUrl;
			this.latch = latch;
		}
		
		public void run(){
			synchronized (DownloadThread.class) {
				currentRunThreadCount += 1;
			}
            System.out.println("线程"+ threadId + "开始下载");
            try {
                //分段请求网络连接,分段将文件保存到本地.
                URL url = new URL(imageUrl);

                //加载下载位置的文件
                File downThreadFile = new File(filePath,getId(imageUrl)+"_downThread_" + threadId+".dt");
                RandomAccessFile downThreadStream = null;
                if(downThreadFile.exists()){//如果文件存在
                    downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
                    String startIndex_str = downThreadStream.readLine();
                    if(null==startIndex_str||"".equals(startIndex_str)){  //网友 imonHu 2017/5/22  
                    }else{  
                        this.startIndex = Integer.parseInt(startIndex_str)-1;//设置下载起点  
                    }
                }else{
                    downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
                }

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                //设置分段下载的头信息。  Range:做分段数据请求用的。格式: Range bytes=0-1024  或者 bytes:0-1024
                connection.setRequestProperty("Range", "bytes="+ startIndex + "-" + endIndex);

                System.out.println("线程_"+threadId + "的下载起点是 " + startIndex + "  下载终点是: " + endIndex);

                if(connection.getResponseCode() == 206){//200：请求全部资源成功， 206代表部分资源请求成功
                    InputStream inputStream = connection.getInputStream();//获取流
                    RandomAccessFile randomAccessFile = new RandomAccessFile(
                            new File(filePath,getFileName(imageUrl)), "rw");//获取前面已创建的文件.
                    randomAccessFile.seek(startIndex);//文件写入的开始位置.


                    /*
                     * 将网络流中的文件写入本地
                     */
                    byte[] buffer = new byte[1024];
                    int length = -1;
                    int total = 0;//记录本次下载文件的大小
                    while((length = inputStream.read(buffer)) > 0){
                        randomAccessFile.write(buffer, 0, length);
                        total += length;
//						float currentRate = (float) (total/Double.valueOf(endIndex-startIndex));
//						if(currentRate-lastRate>0.1){
//							lastRate = currentRate;
//							System.out.println(threadId+"线程：进度为："+lastRate);	
//						}
                        /*
                         * 将当前现在到的位置保存到文件中
                         */
                        downThreadStream.seek(0);
                        downThreadStream.write((startIndex + total + "").getBytes("UTF-8"));
                    }

                    downThreadStream.close();
                    inputStream.close();
                    randomAccessFile.close();                   
                    System.out.println("线程"+ threadId + "下载完毕");
                    
					synchronized (DownloadThread.class) {  //删除临时文件
						currentRunThreadCount -= 1;
						if (currentRunThreadCount == 0) {
							for (int i = 0; i < threadCount; i++) {
								File downTempReadFile = new File(filePath,getId(imageUrl)+"_downThread_" + i+".dt");//获取前面已创建的文件.
								cleanTemp(downTempReadFile);
								
							}
							System.out.println("多线程下载完毕！");
						}
					 }
					
					latch.countDown();
					
                }else{
                    System.out.println("响应码是" +connection.getResponseCode() + ". 服务器不支持多线程下载              "+threadId+"线程");
                    if(connection!=null)  connection.disconnect();                                   
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            super.run();
		}

	    public static String getId(String url){
			 Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(url);
			 while (matcher.find()){
				 //System.out.println(matcher.group());
				 return  matcher.group();
			 }
			 return "缺省"+new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date());
	    }

/*		public void run() {
			synchronized (DownloadThread.class) {
				currentRunThreadCount += 1;
			}
			// 分段请求网络连接，分段保存在本地
			try {
				System.err.println("理论线程:" + threadId + ",开始位置:" + startThred + ",结束位置:" + endThread);
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(60*5 * 1000);//网络状态不好的时候可以设置比较成的时间  以免超时后关闭连接
				File file = new File(filePath+"/"+threadId + ".jpg");
				if (file.exists()) { // 是否断点
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(new FileInputStream(file)));
					String lastPostion_str = bufferedReader.readLine();
					startThred = Integer.parseInt(lastPostion_str);
					bufferedReader.close();
				}
				//conn.setRequestProperty("Range","bytes="+ startPos+"-"+endPos);
				// 设置分段下载的头信息 Range:做分段
				connection.setRequestProperty("Range", "bytes:" + startThred + "-" + endThread);

				if (connection.getResponseCode() == 206) { // 200:请求全部资源成功 206:代表部分资源请求成功
					System.out.println(connection.getResponseCode());
					
					InputStream inputStream = connection.getInputStream();
					System.out.println(getFileName(imageUrl));
					
					RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath+"/"+getFileName(imageUrl)), "rw");
					randomAccessFile.seek(startThred);
					
					byte[] buffer = new byte[1024*24];
					int length = -1;
					int total = 0;// 记录下载的总量
					System.err.println("实际线程:" + threadId + ",开始位置:" + startThred + ",结束位置:" + endThread);
					while ((length = inputStream.read(buffer)) != -1) {
						randomAccessFile.write(buffer, 0, length);
						
						total += length;
						float currentRate = (float) (total/Double.valueOf(fileLength));
						if(currentRate-lastRate>0.1){
							lastRate = currentRate;
							System.out.println(threadId+"线程：进度为："+lastRate);	
						}
						 
                         * 将当前现在到的位置保存到文件中
                         
						int currentThreadPostion = startThred + total;
						RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rwd");
						randomAccessFile2.seek(0);
						randomAccessFile2.write(String.valueOf(currentThreadPostion).getBytes());
						randomAccessFile2.close();
					}
					randomAccessFile.close();
					inputStream.close();
					System.err.println("线程:" + threadId + "下载完毕");
					
					synchronized (DownloadThread.class) {
						currentRunThreadCount -= 1;
						if (currentRunThreadCount == 0) {
							for (int i = 0; i < threadCount; i++) {
								File file2 = new File(filePath+"/"+i + ".jpg");
								file2.delete();
							}
							System.out.println("多线程下载完毕！");
						}
					}
					
					latch.countDown();
				}else{
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			super.run();
		}
	}*/
	    
	    //删除线程产生的临时文件
	    private synchronized void cleanTemp(File file) throws IOException{
	    	String canonicalPath = file.getCanonicalPath();
	        if(file.delete()){
	        	System.out.println(canonicalPath+"删除成功");
	        }else{
	        	System.out.println(canonicalPath+"删除失败");
	        };
	    }

	}
	
	// 获取下载文件的名称
	public static String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}
	
    public static String getId(String url){
		 Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(url);
		 while (matcher.find()){
			 System.out.println(matcher.group());
			 return  matcher.group();

		 }
		 return "缺省"+new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date());
    }
}
