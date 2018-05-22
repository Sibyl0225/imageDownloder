package com.bilibili.tools;

import java.io.File;
import java.io.IOException;
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

public class NormalImageDownloadSimple extends Thread {

	private int threadId;
	private String filePath;
	private ArrayBlockingQueue<Map<String, String>> queue;
	static ShardedJedis shardedJedis = null;

	public NormalImageDownloadSimple(int threadId, String filePath, ArrayBlockingQueue<Map<String, String>> queue) {
		this.threadId = threadId;
		this.filePath = filePath;
		this.queue = queue;
		shardedJedis = new RedisClient().shardedJedis;
	}

	public void run() {

		try {
			endlessDownloader();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void endlessDownloader() throws InterruptedException, IOException {

		Map<String, String> map = null;
		map = queue.take();

		String imageUrl = FileNameUtil.getImageUrl(map);
		System.out.println("�߳�" + threadId + "��ʼ����");
		String imageId = FileNameUtil.getId(map);

			URL url = new URL(imageUrl);
			// ��������λ�õ��ļ�
			File image = new File(filePath, imageId + ".jpg");

			RandomAccessFile downThreadStream = new RandomAccessFile(image, "rwd");

			int fileLength = FileLengthTools.getRemoteFileLenght(imageUrl);
			System.err.println("�ļ���С��   " + fileLength);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(12 * 1000);
			int responseCode = 200;
			if (connection.getResponseCode() == responseCode) {// 200������ȫ����Դ�ɹ���
																// 206��������Դ����ɹ�
				InputStream inputStream = connection.getInputStream();// ��ȡ��
				downThreadStream.seek(0);// �ļ�д��Ŀ�ʼλ��.
				byte[] buffer = new byte[1024];
				int length = -1;
				int total = 0;// ��¼�����߳����ص��ܴ�С
				while ((length = inputStream.read(buffer)) > 0) {
					downThreadStream.write(buffer, 0, length);
					total = total + length;
				}
				downThreadStream.close();
				inputStream.close();
				System.out.println("�߳�" + threadId + "����������Ϊ�� " + total + ",�������!");
				System.out.println("���ʱ�䣺   " + new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date()));

				// У���ļ���С
				long size = FileLengthTools.getLocalFileLength(image);
				if (fileLength != size) {
					if (image.exists()) {
						System.out.println("�ļ����ز�������");
						image.delete();
					}
					// ����ǰmap���·�������
					queue.put(map);
				} else {
					// ���سɹ���ı�redis״̬,��������һ���ļ�
					suceessHandle(map, imageId);
				}

			} else {
				System.out.println(
						"��Ӧ����" + connection.getResponseCode() + ". ��������֧�ֶ��߳�����              " + threadId + "�߳�");
				if (connection != null)
					connection.disconnect();
			}

	}

	private void suceessHandle(Map<String, String> map, String imageId) throws InterruptedException {

		Thread.sleep(500L);
		System.out.println("����ͣ0.5�룬��ʼ������һ��ͼƬ��");

		map.put("hadDownload", "true");
		shardedJedis.hmset(imageId, map);

		System.err.println("ˢ��ֵΪ������  1");

		System.out.println(threadId + "   queue.take()  " + queue.size());
		if (queue.size() == 0) {
			System.out.println("   queue is done!  ������15����Ƴ���");
			Thread.sleep(15000L);
			//�������� 
			System.exit(0);
		} else {
			//����
			try {
				endlessDownloader();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
