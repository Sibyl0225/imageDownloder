package com.bilibili.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ThreadImageDownload extends Thread {
	
	private int threadId;
	private String filePath;
	private String imageUrl;
	private int endPostion;
	private int startPostion;
	private CountDownLatch latch;
	
	public ThreadImageDownload(int threadId, String imageUrl,String filePath,int startPostion,int endPostion,CountDownLatch latch) {
		this.threadId = threadId;
		this.filePath = filePath;
		this.imageUrl = imageUrl;
		this.startPostion = startPostion;
		this.endPostion = endPostion;
		this.latch = latch;

	}
	
//	public void run(){
//
//        System.out.println("�߳�"+ threadId + "��ʼ����");
//        try {
//            //�ֶ�������������,�ֶν��ļ����浽����.
//            URL url = new URL(imageUrl);
//            
//            int startPostion = 0;
//
//            //��������λ�õ��ļ�
//            File downThreadFile = new File(filePath,getFileName(imageUrl));
//            
//            RandomAccessFile downThreadStream = null;
//            if(downThreadFile.exists()){//����ļ�����
//                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
//                BufferedReader bufferedReader = new BufferedReader(
//						new InputStreamReader(new FileInputStream(downThreadFile)));
//				String lastPostion_str = bufferedReader.readLine();
//				startPostion = Integer.parseInt(lastPostion_str);
//				bufferedReader.close();
//            }else{
//                downThreadStream = new RandomAccessFile(downThreadFile,"rwd");
//            }
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(6 * 1000);
//            int endPostion = FileLengthTools.getFileLenght(connection);
//            //���÷ֶ����ص�ͷ��Ϣ��  Range:���ֶ����������õġ���ʽ: Range bytes=0-1024  ���� bytes:0-1024
//            connection.setRequestProperty("Range", "bytes="+ startPostion + "-" + endPostion );
//
//            if(connection.getResponseCode() == 206){//200������ȫ����Դ�ɹ��� 206��������Դ����ɹ�
//                InputStream inputStream = connection.getInputStream();//��ȡ��
//                downThreadStream.seek(startPostion);//�ļ�д��Ŀ�ʼλ��.
//                /*
//                 * ���������е��ļ�д�뱾��
//                 */
//                byte[] buffer = new byte[1024];
//                int length = -1;
//                while((length = inputStream.read(buffer)) > 0){
//                	downThreadStream.write(buffer, 0, length);
//                }
//
//                downThreadStream.close();
//                inputStream.close();               
//                System.out.println("�߳�"+ threadId + "�������");
//                			
//            }else{
//                System.out.println("��Ӧ����" +connection.getResponseCode() + ". ��������֧�ֶ��߳�����              "+threadId+"�߳�");
//                if(connection!=null)  connection.disconnect();                                   
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//             
//        super.run();
//	}
	
	public void run(){
		
		System.out.println("�߳�"+ threadId + "��ʼ����");
        String imageId = getId(imageUrl) ;
        
        try {
            //�ֶ�������������,�ֶν��ļ����浽����.
            URL url = new URL(imageUrl);           

            //��������λ�õ��ļ�
            File image = new File(filePath,getFileName(imageUrl));
            
			File cashTxt = new File(filePath,imageId +"_"+threadId+ ".txt");
            
            RandomAccessFile downThreadStream = null;
            if(cashTxt.exists()){//����ļ�����
                downThreadStream = new RandomAccessFile(image,"rwd");
                
                BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(cashTxt)));
				String lastPostion_str = bufferedReader.readLine();
				startPostion = Integer.parseInt(lastPostion_str) - 1;
				System.err.println("�ļ���ʼλ�ã�   "+startPostion);
				bufferedReader.close();
            }else{
                downThreadStream = new RandomAccessFile(image,"rwd");
            }

            System.err.println("�ļ�����λ�ã�   "+endPostion);
            
            if(endPostion <= startPostion){
            	System.err.println(imageId+"  �ļ������ز���Ҫ�ظ����أ�");
            	System.exit(0);;
            }
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6 * 1000);
            //���÷ֶ����ص�ͷ��Ϣ��  Range:���ֶ����������õġ���ʽ: Range bytes=0-1024  ���� bytes:0-1024
            connection.setRequestProperty("Range", "bytes="+ startPostion + "-" + endPostion );

            if(connection.getResponseCode() == 206){//200������ȫ����Դ�ɹ��� 206��������Դ����ɹ�
            	System.err.println("��ʼ���ز�д�ļ������ء���");
                InputStream inputStream = connection.getInputStream();//��ȡ��
                downThreadStream.seek(startPostion);//�ļ�д��Ŀ�ʼλ��.
               
                byte[] buffer = new byte[1024];
                int length = -1;
                int total = 0;// ��¼�����߳����ص��ܴ�С
                
                while((length = inputStream.read(buffer)) > 0){
                	downThreadStream.write(buffer, 0, length);
                	
                	total = total + length;
                    // ȥ���浱ǰ�߳����ص�λ�ã����浽�ļ���
                    int currentThreadPostion = startPostion + total;// �������ǰ�̱߳������ص�
                    File file = new File(filePath,imageId +"_"+threadId+ ".txt");
                    RandomAccessFile accessfile = new RandomAccessFile(
                            file, "rwd");
                    accessfile.write(String.valueOf(currentThreadPostion).getBytes());
                    accessfile.close();
                }
                

                downThreadStream.close();
                inputStream.close();  
                System.out.println("�߳�"+ threadId + "�������");
                latch.countDown();
                
                // �������߳����ؽ�����ɾ���������λ�õ��ļ���
                synchronized (ThreadImageDownload.class) {
                    if (latch.getCount() == 0) {
                        System.out.println("�����߳��������");
                        for (int i = 0; i < 20; i++) {
                            File file = new File(filePath,imageId +"_"+i+ ".txt");
                            if(file.exists()){
                            	System.out.println(file.getAbsolutePath());
                            	file.delete();
                            } 
                            
                        }
                    }
                }
                			
            }else{
                System.out.println("��Ӧ����" +connection.getResponseCode() + ". ��������֧�ֶ��߳�����              "+threadId+"�߳�");
                if(connection!=null)  connection.disconnect();  
                latch.countDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		super.run();	
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
