package com.bilibili.tools;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import redis.RedisClient;
import redis.clients.jedis.ShardedJedis;

public class NormalImageDownload extends Thread {

	private int threadId;
	private String filePath;
	private ArrayBlockingQueue<Map<String, String>> queue;
	static ShardedJedis shardedJedis = null;

	public NormalImageDownload(int threadId, String filePath, ArrayBlockingQueue<Map<String, String>> queue) {
		this.threadId = threadId;
		this.filePath = filePath;
		this.queue = queue;
		this.shardedJedis = new RedisClient().shardedJedis;
	}

	public void run() {
		
		try {
			endlessDownloader();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void endlessDownloader() throws InterruptedException {

		Map<String, String> map = null;
		map = queue.take();
		
		String imageUrl =  FileNameUtil.getImageUrl(map);
		System.out.println("线程" + threadId + "开始下载");
		String imageId = FileNameUtil.getId(map);
		try {
			URL url = new URL(imageUrl);
			// 加载下载位置的文件
			File image = new File(filePath, imageId+".jpg");
            //存储文件已下载的大小
			File cashTxt = new File(filePath, imageId + ".txt");
			
            int fileHadDownPostion = 0;
            
			if (cashTxt.exists()) {// 如果文件存在
				fileHadDownPostion = FileLengthTools.getFileSizeHadDownload(cashTxt);
			} 
			RandomAccessFile downThreadStream = new RandomAccessFile(image, "rwd");

			int endPostion = FileLengthTools.getRemoteFileLenght(imageUrl);
			System.err.println("文件结束位置：   " + endPostion);

			if (endPostion <= fileHadDownPostion && endPostion > 0) {
				System.err.println(imageId+"  文件已下载不需要重复下载！");
				
				try {
					Thread.sleep(1500L);
					System.err.println("已暂停1.5秒，开始下载下一张图片！");

					map.put("hadDownload", "true");
					shardedJedis.hmset(imageId, map);

					endlessDownloader();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} else {
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(12 * 1000);
				
				int responseCode = 200;
				// 设置分段下载的头信息。 Range:做分段数据请求用的。格式: Range bytes=0-1024 或者
				// bytes:0-1024
				if(fileHadDownPostion > 0){
					responseCode = 206;
					connection.setRequestProperty("Range", "bytes=" + fileHadDownPostion + "-" + endPostion);
				}

				if (connection.getResponseCode() == responseCode) {// 200：请求全部资源成功，
															       // 206代表部分资源请求成功
					InputStream inputStream = connection.getInputStream();// 获取流
					downThreadStream.seek(fileHadDownPostion);// 文件写入的开始位置.
					/*
					 * 将网络流中的文件写入本地
					 */

					byte[] buffer = new byte[1024];
					int length = -1;
					int total = 0;// 记录本次线程下载的总大小

					while ((length = inputStream.read(buffer)) > 0) {
						downThreadStream.write(buffer, 0, length);

						total = total + length;
						// 去保存当前线程下载的位置，保存到文件中
						int currentThreadPostion = fileHadDownPostion + total;// 计算出当前线程本次下载的
						File file = new File(filePath, imageId + ".txt");
						RandomAccessFile accessfile = new RandomAccessFile(file, "rwd");
						accessfile.write(String.valueOf(currentThreadPostion).getBytes());
						accessfile.close();
					}

					downThreadStream.close();
					inputStream.close();
					System.out.println("线程" + threadId + "下载完毕");
					System.out.println("完成时间：   " + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date()));

					File file = new File(filePath, imageId + ".txt");
					if (file.exists()) {
						System.out.println(file.getAbsolutePath());
						file.delete();
					}
					//校验文件大小
                    long size = FileLengthTools.getLocalFileLength(image);
			        if(endPostion!=size){
			        	if (file.exists()) {
			        		System.out.println("文件下载不完整！");
			        		image.delete();
			        	}
			        	//将当前map重新放入下载
			        	queue.put(map);
			        }else{
			        	
			        	try {
			        		Thread.sleep(500L);
			        		System.out.println("已暂停0.5秒，开始下载下一张图片！");
			        		
			        		map.put("hadDownload", "true");
			        		shardedJedis.hmset(imageId, map);
			        		
			        		System.err.println("刷新值为已下载  1");
			        		
			        		System.out.println(threadId+"   queue.take()  "+queue.size());
			        		if (queue.size()==0) {
			        			System.out.println("   queue is done!  即将在15秒后推出！");
			        			Thread.sleep(15000L);
			        			System.exit(0);							
			        		}else{								
			        			endlessDownloader();
			        		}
			        		
			        		
			        	} catch (InterruptedException e) {
			        		e.printStackTrace();
			        	}
			        }


				} else {
					System.out.println(
							"响应码是" + connection.getResponseCode() + ". 服务器不支持多线程下载              " + threadId + "线程");
					if (connection != null)
						connection.disconnect();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
