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
		// ��������λ�õ��ļ�
//		File tempFile = new File(filePath, getId(path) + "_downThread_" + 1 + ".dt");
//		if (!tempFile.exists()) {// ����ļ������� ��ʾ���ļ������������δ����
//			System.out.println("��ʱ�ļ�δ�ҵ���");
//			if (new File(filePath + "\\" + getFileName(path)).exists()) { // �ļ�������
//				System.out.println("�ļ������أ���������");
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
//			int blockSize = fileLength/threadCount;//����ÿ���߳����������ص�����.
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
				System.out.println("�����߳����������������ɣ�");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}

	public static void DownImage(String imageUrl,String filePath, int fileLength, int threadCount, CountDownLatch latch) {

		int blockSize = fileLength/threadCount;//����ÿ���߳����������ص�����.
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
            System.out.println("�߳�"+ threadId + "��ʼ����");
            try {
                //�ֶ�������������,�ֶν��ļ����浽����.
                URL url = new URL(imageUrl);

                //��������λ�õ��ļ�
                File downThreadFile = new File(filePath,getId(imageUrl)+"_downThread_" + threadId+".dt");
                RandomAccessFile downThreadStream = null;
                if(downThreadFile.exists()){//����ļ�����
                    downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
                    String startIndex_str = downThreadStream.readLine();
                    if(null==startIndex_str||"".equals(startIndex_str)){  //���� imonHu 2017/5/22  
                    }else{  
                        this.startIndex = Integer.parseInt(startIndex_str)-1;//�����������  
                    }
                }else{
                    downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
                }

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                //���÷ֶ����ص�ͷ��Ϣ��  Range:���ֶ����������õġ���ʽ: Range bytes=0-1024  ���� bytes:0-1024
                connection.setRequestProperty("Range", "bytes="+ startIndex + "-" + endIndex);

                System.out.println("�߳�_"+threadId + "����������� " + startIndex + "  �����յ���: " + endIndex);

                if(connection.getResponseCode() == 206){//200������ȫ����Դ�ɹ��� 206��������Դ����ɹ�
                    InputStream inputStream = connection.getInputStream();//��ȡ��
                    RandomAccessFile randomAccessFile = new RandomAccessFile(
                            new File(filePath,getFileName(imageUrl)), "rw");//��ȡǰ���Ѵ������ļ�.
                    randomAccessFile.seek(startIndex);//�ļ�д��Ŀ�ʼλ��.


                    /*
                     * ���������е��ļ�д�뱾��
                     */
                    byte[] buffer = new byte[1024];
                    int length = -1;
                    int total = 0;//��¼���������ļ��Ĵ�С
                    while((length = inputStream.read(buffer)) > 0){
                        randomAccessFile.write(buffer, 0, length);
                        total += length;
//						float currentRate = (float) (total/Double.valueOf(endIndex-startIndex));
//						if(currentRate-lastRate>0.1){
//							lastRate = currentRate;
//							System.out.println(threadId+"�̣߳�����Ϊ��"+lastRate);	
//						}
                        /*
                         * ����ǰ���ڵ���λ�ñ��浽�ļ���
                         */
                        downThreadStream.seek(0);
                        downThreadStream.write((startIndex + total + "").getBytes("UTF-8"));
                    }

                    downThreadStream.close();
                    inputStream.close();
                    randomAccessFile.close();                   
                    System.out.println("�߳�"+ threadId + "�������");
                    
					synchronized (DownloadThread.class) {  //ɾ����ʱ�ļ�
						currentRunThreadCount -= 1;
						if (currentRunThreadCount == 0) {
							for (int i = 0; i < threadCount; i++) {
								File downTempReadFile = new File(filePath,getId(imageUrl)+"_downThread_" + i+".dt");//��ȡǰ���Ѵ������ļ�.
								cleanTemp(downTempReadFile);
								
							}
							System.out.println("���߳�������ϣ�");
						}
					 }
					
					latch.countDown();
					
                }else{
                    System.out.println("��Ӧ����" +connection.getResponseCode() + ". ��������֧�ֶ��߳�����              "+threadId+"�߳�");
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
			 return "ȱʡ"+new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date());
	    }

/*		public void run() {
			synchronized (DownloadThread.class) {
				currentRunThreadCount += 1;
			}
			// �ֶ������������ӣ��ֶα����ڱ���
			try {
				System.err.println("�����߳�:" + threadId + ",��ʼλ��:" + startThred + ",����λ��:" + endThread);
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(60*5 * 1000);//����״̬���õ�ʱ��������ñȽϳɵ�ʱ��  ���ⳬʱ��ر�����
				File file = new File(filePath+"/"+threadId + ".jpg");
				if (file.exists()) { // �Ƿ�ϵ�
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(new FileInputStream(file)));
					String lastPostion_str = bufferedReader.readLine();
					startThred = Integer.parseInt(lastPostion_str);
					bufferedReader.close();
				}
				//conn.setRequestProperty("Range","bytes="+ startPos+"-"+endPos);
				// ���÷ֶ����ص�ͷ��Ϣ Range:���ֶ�
				connection.setRequestProperty("Range", "bytes:" + startThred + "-" + endThread);

				if (connection.getResponseCode() == 206) { // 200:����ȫ����Դ�ɹ� 206:��������Դ����ɹ�
					System.out.println(connection.getResponseCode());
					
					InputStream inputStream = connection.getInputStream();
					System.out.println(getFileName(imageUrl));
					
					RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath+"/"+getFileName(imageUrl)), "rw");
					randomAccessFile.seek(startThred);
					
					byte[] buffer = new byte[1024*24];
					int length = -1;
					int total = 0;// ��¼���ص�����
					System.err.println("ʵ���߳�:" + threadId + ",��ʼλ��:" + startThred + ",����λ��:" + endThread);
					while ((length = inputStream.read(buffer)) != -1) {
						randomAccessFile.write(buffer, 0, length);
						
						total += length;
						float currentRate = (float) (total/Double.valueOf(fileLength));
						if(currentRate-lastRate>0.1){
							lastRate = currentRate;
							System.out.println(threadId+"�̣߳�����Ϊ��"+lastRate);	
						}
						 
                         * ����ǰ���ڵ���λ�ñ��浽�ļ���
                         
						int currentThreadPostion = startThred + total;
						RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rwd");
						randomAccessFile2.seek(0);
						randomAccessFile2.write(String.valueOf(currentThreadPostion).getBytes());
						randomAccessFile2.close();
					}
					randomAccessFile.close();
					inputStream.close();
					System.err.println("�߳�:" + threadId + "�������");
					
					synchronized (DownloadThread.class) {
						currentRunThreadCount -= 1;
						if (currentRunThreadCount == 0) {
							for (int i = 0; i < threadCount; i++) {
								File file2 = new File(filePath+"/"+i + ".jpg");
								file2.delete();
							}
							System.out.println("���߳�������ϣ�");
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
	    
	    //ɾ���̲߳�������ʱ�ļ�
	    private synchronized void cleanTemp(File file) throws IOException{
	    	String canonicalPath = file.getCanonicalPath();
	        if(file.delete()){
	        	System.out.println(canonicalPath+"ɾ���ɹ�");
	        }else{
	        	System.out.println(canonicalPath+"ɾ��ʧ��");
	        };
	    }

	}
	
	// ��ȡ�����ļ�������
	public static String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}
	
    public static String getId(String url){
		 Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(url);
		 while (matcher.find()){
			 System.out.println(matcher.group());
			 return  matcher.group();

		 }
		 return "ȱʡ"+new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date());
    }
}
