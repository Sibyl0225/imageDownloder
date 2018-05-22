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
     * ������ҳ�ͱ����ȡ��ҳ���ݺ�Դ����
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
            // ������������
            urlObj = new URL(url);
            // ����������
            uc     = urlObj.openConnection();
            uc.setConnectTimeout(30000);//30s
            uc.setReadTimeout(30000);//30s
            // ����������
            in     = new InputStreamReader(uc.getInputStream(),encoding);
            // ����һ������д����
            reader = new BufferedReader(in);
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                // һ��һ��׷��
                buffer.append(line+"\r\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("����ʱ,���ֶ����ԣ�");
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
     * ����ͼƬ��URL���ص�ͼƬ�����ص�filePath
     * @param filePath �ļ���
     * @param imageUrl ͼƬ����ַ
     */
    public static boolean downImages(String filePath,Map<String,String> map){
    	String imageUrl =  map.get("largeImg");
    	String preImage =  map.get("preImg");
        // ��ȡͼƬ������
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
        
        //�����ļ���Ŀ¼�ṹ
        File files = new File(filePath);
        if(!files.exists()){// �ж��ļ����Ƿ���ڣ���������ھʹ���һ���ļ���
            files.mkdirs();
        }
        try {
        	// �����ļ�
        	File file = new File(filePath+fileName);
        	if(file.exists()) return true; //���ظ�����
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
     * ����ͼƬ��URL���ص�ͼƬ�����ص�filePath
     * @param filePath �ļ���
     * @param imageUrl ͼƬ����ַ
     */
    public static boolean  MultipartDownloadImages(String filePath,Map<String,String> map,int threadCount){
    	
    	//String path =  map.get("largeImg");
    	String path =  map.get("preImg");
    
	    int fileLength = FileLengthTools.getRemoteFileLenght(path);
	
		CountDownLatch latch = new CountDownLatch(threadCount);
		MultipartDownloadTools.DownImage(path, filePath,fileLength,threadCount,latch);
		try {
			latch.await();
			System.out.println("�����߳����������������ɣ�");
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
//        // ��ȡͼƬ������
//        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
//        
//        //�����ļ���Ŀ¼�ṹ
//        File files = new File(filePath);
//        if(!files.exists()){// �ж��ļ����Ƿ���ڣ���������ھʹ���һ���ļ���
//            files.mkdirs();
//        }
//        try {
//        	// �����ļ�
//        	File file = new File(filePath+fileName);
//        	if(file.exists()) return false; //���ظ�����
//            //�ֶ�������������,�ֶν��ļ����浽����.
//            URL url = new URL(imageUrl);
//
//            //��������λ�õ��ļ�
//            RandomAccessFile downThreadStream = null;
//            if(downThreadFile.exists()){//����ļ�����
//                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
//                String startIndex_str = downThreadStream.readLine();
//                if(null==startIndex_str||"".equals(startIndex_str)){  //���� imonHu 2017/5/22  
//                }else{  
//                    this.startIndex = Integer.parseInt(startIndex_str)-1;//�����������  
//                }
//            }else{
//                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
//            }
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(10000);
//            //���÷ֶ����ص�ͷ��Ϣ��  Range:���ֶ����������õġ���ʽ: Range bytes=0-1024  ���� bytes:0-1024
//            connection.setRequestProperty("Range", "bytes="+ startIndex + "-" + endIndex);
//
//            System.out.println("�߳�_"+threadId + "����������� " + startIndex + "  �����յ���: " + endIndex);
//
//            if(connection.getResponseCode() == 206){//200������ȫ����Դ�ɹ��� 206��������Դ����ɹ�
//                InputStream inputStream = connection.getInputStream();//��ȡ��
//                RandomAccessFile randomAccessFile = new RandomAccessFile(
//                        new File(filePath,getFileName(imageUrl)), "rw");//��ȡǰ���Ѵ������ļ�.
//                randomAccessFile.seek(startIndex);//�ļ�д��Ŀ�ʼλ��.
//
//
//                /*
//                 * ���������е��ļ�д�뱾��
//                 */
//                byte[] buffer = new byte[1024];
//                int length = -1;
//                int total = 0;//��¼���������ļ��Ĵ�С
//                while((length = inputStream.read(buffer)) > 0){
//                    randomAccessFile.write(buffer, 0, length);
//                    total += length;
////					float currentRate = (float) (total/Double.valueOf(endIndex-startIndex));
////					if(currentRate-lastRate>0.1){
////						lastRate = currentRate;
////						System.out.println(threadId+"�̣߳�����Ϊ��"+lastRate);	
////					}
//                    /*
//                     * ����ǰ���ڵ���λ�ñ��浽�ļ���
//                     */
//                    downThreadStream.seek(0);
//                    downThreadStream.write((startIndex + total + "").getBytes("UTF-8"));
//                }
//
//                downThreadStream.close();
//                inputStream.close();
//                randomAccessFile.close();                   
//                System.out.println("�߳�"+ threadId + "�������");
//                
//				synchronized (DownloadThread.class) {  //ɾ����ʱ�ļ�
//					currentRunThreadCount -= 1;
//					if (currentRunThreadCount == 0) {
//						for (int i = 0; i < threadCount; i++) {
//							File downTempReadFile = new File(filePath,getId(imageUrl)+"_downThread_" + i+".dt");//��ȡǰ���Ѵ������ļ�.
//							cleanTemp(downTempReadFile);
//							
//						}
//						System.out.println("���߳�������ϣ�");
//					}
//				 }
//				
//				
//            }else{
//                System.out.println("��Ӧ����" +connection.getResponseCode() + ". ��������֧�ֶ��߳�����              "+threadId+"�߳�");
//                if(connection!=null)  connection.disconnect();                                   
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
