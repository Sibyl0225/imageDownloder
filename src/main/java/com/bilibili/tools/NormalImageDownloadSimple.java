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
		System.out.println("线程" + threadId + "开始下载");
		String imageId = FileNameUtil.getId(map);

			URL url = new URL(imageUrl);
			// 加载下载位置的文件
			File image = new File(filePath, imageId + ".jpg");

			RandomAccessFile downThreadStream = new RandomAccessFile(image, "rwd");

			int fileLength = FileLengthTools.getRemoteFileLenght(imageUrl);
			System.err.println("文件大小：   " + fileLength);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(12 * 1000);
			int responseCode = 200;
			if (connection.getResponseCode() == responseCode) {// 200：请求全部资源成功，
																// 206代表部分资源请求成功
				InputStream inputStream = connection.getInputStream();// 获取流
				downThreadStream.seek(0);// 文件写入的开始位置.
				byte[] buffer = new byte[1024];
				int length = -1;
				int total = 0;// 记录本次线程下载的总大小
				while ((length = inputStream.read(buffer)) > 0) {
					downThreadStream.write(buffer, 0, length);
					total = total + length;
				}
				downThreadStream.close();
				inputStream.close();
				System.out.println("线程" + threadId + "最终下载量为： " + total + ",下载完毕!");
				System.out.println("完成时间：   " + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date()));

				// 校验文件大小
				long size = FileLengthTools.getLocalFileLength(image);
				if (fileLength != size) {
					if (image.exists()) {
						System.out.println("文件下载不完整！");
						image.delete();
					}
					// 将当前map重新放入下载
					queue.put(map);
				} else {
					// 下载成功后改变redis状态,并下载下一个文件
					suceessHandle(map, imageId);
				}

			} else {
				System.out.println(
						"响应码是" + connection.getResponseCode() + ". 服务器不支持多线程下载              " + threadId + "线程");
				if (connection != null)
					connection.disconnect();
			}

	}

	private void suceessHandle(Map<String, String> map, String imageId) throws InterruptedException {

		Thread.sleep(500L);
		System.out.println("已暂停0.5秒，开始下载下一张图片！");

		map.put("hadDownload", "true");
		shardedJedis.hmset(imageId, map);

		System.err.println("刷新值为已下载  1");

		System.out.println(threadId + "   queue.take()  " + queue.size());
		if (queue.size() == 0) {
			System.out.println("   queue is done!  即将在15秒后推出！");
			Thread.sleep(15000L);
			//结束程序 
			System.exit(0);
		} else {
			//迭代
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
