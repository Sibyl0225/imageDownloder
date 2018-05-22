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
		System.out.println("�߳�" + threadId + "��ʼ����");
		String imageId = FileNameUtil.getId(map);
		try {
			URL url = new URL(imageUrl);
			// ��������λ�õ��ļ�
			File image = new File(filePath, imageId+".jpg");
            //�洢�ļ������صĴ�С
			File cashTxt = new File(filePath, imageId + ".txt");
			
            int fileHadDownPostion = 0;
            
			if (cashTxt.exists()) {// ����ļ�����
				fileHadDownPostion = FileLengthTools.getFileSizeHadDownload(cashTxt);
			} 
			RandomAccessFile downThreadStream = new RandomAccessFile(image, "rwd");

			int endPostion = FileLengthTools.getRemoteFileLenght(imageUrl);
			System.err.println("�ļ�����λ�ã�   " + endPostion);

			if (endPostion <= fileHadDownPostion && endPostion > 0) {
				System.err.println(imageId+"  �ļ������ز���Ҫ�ظ����أ�");
				
				try {
					Thread.sleep(1500L);
					System.err.println("����ͣ1.5�룬��ʼ������һ��ͼƬ��");

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
				// ���÷ֶ����ص�ͷ��Ϣ�� Range:���ֶ����������õġ���ʽ: Range bytes=0-1024 ����
				// bytes:0-1024
				if(fileHadDownPostion > 0){
					responseCode = 206;
					connection.setRequestProperty("Range", "bytes=" + fileHadDownPostion + "-" + endPostion);
				}

				if (connection.getResponseCode() == responseCode) {// 200������ȫ����Դ�ɹ���
															       // 206��������Դ����ɹ�
					InputStream inputStream = connection.getInputStream();// ��ȡ��
					downThreadStream.seek(fileHadDownPostion);// �ļ�д��Ŀ�ʼλ��.
					/*
					 * ���������е��ļ�д�뱾��
					 */

					byte[] buffer = new byte[1024];
					int length = -1;
					int total = 0;// ��¼�����߳����ص��ܴ�С

					while ((length = inputStream.read(buffer)) > 0) {
						downThreadStream.write(buffer, 0, length);

						total = total + length;
						// ȥ���浱ǰ�߳����ص�λ�ã����浽�ļ���
						int currentThreadPostion = fileHadDownPostion + total;// �������ǰ�̱߳������ص�
						File file = new File(filePath, imageId + ".txt");
						RandomAccessFile accessfile = new RandomAccessFile(file, "rwd");
						accessfile.write(String.valueOf(currentThreadPostion).getBytes());
						accessfile.close();
					}

					downThreadStream.close();
					inputStream.close();
					System.out.println("�߳�" + threadId + "�������");
					System.out.println("���ʱ�䣺   " + new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date()));

					File file = new File(filePath, imageId + ".txt");
					if (file.exists()) {
						System.out.println(file.getAbsolutePath());
						file.delete();
					}
					//У���ļ���С
                    long size = FileLengthTools.getLocalFileLength(image);
			        if(endPostion!=size){
			        	if (file.exists()) {
			        		System.out.println("�ļ����ز�������");
			        		image.delete();
			        	}
			        	//����ǰmap���·�������
			        	queue.put(map);
			        }else{
			        	
			        	try {
			        		Thread.sleep(500L);
			        		System.out.println("����ͣ0.5�룬��ʼ������һ��ͼƬ��");
			        		
			        		map.put("hadDownload", "true");
			        		shardedJedis.hmset(imageId, map);
			        		
			        		System.err.println("ˢ��ֵΪ������  1");
			        		
			        		System.out.println(threadId+"   queue.take()  "+queue.size());
			        		if (queue.size()==0) {
			        			System.out.println("   queue is done!  ������15����Ƴ���");
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
							"��Ӧ����" + connection.getResponseCode() + ". ��������֧�ֶ��߳�����              " + threadId + "�߳�");
					if (connection != null)
						connection.disconnect();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
