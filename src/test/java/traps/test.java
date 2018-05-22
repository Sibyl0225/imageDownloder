package traps;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bilibili.tools.FileLengthTools;

public class test {

	public static void main(String[] args) {
		
		String filePath = "D:\\yande";
		String imageUrl = "https://files.yande.re/image/9a81f5a8c83337c1b566ceb431b0f577/yande.re%20447287%20gun%20heels%20japanese_clothes%20kancell%20thighhighs.jpg";
		int threadId  =  1;
        System.out.println("�߳�"+ threadId + "��ʼ����");
        String imageId = getId(imageUrl) ;
        try {
            //�ֶ�������������,�ֶν��ļ����浽����.
            URL url = new URL(imageUrl);
            
            int startPostion = 0;

            //��������λ�õ��ļ�
            File image = new File(filePath,getFileName(imageUrl));
            
			File cashTxt = new File(filePath,imageId +"_"+threadId+ ".txt");
            
            RandomAccessFile downThreadStream = null;
            if(cashTxt.exists()){//����ļ�����
                downThreadStream = new RandomAccessFile(image,"rwd");
                
                BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(cashTxt)));
				String lastPostion_str = bufferedReader.readLine();
				startPostion = Integer.parseInt(lastPostion_str);
				System.err.println("�ļ���ʼλ�ã�   "+startPostion);
				bufferedReader.close();
            }else{
                downThreadStream = new RandomAccessFile(image,"rwd");
            }

            int endPostion = FileLengthTools.getRemoteFileLenght(imageUrl);
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
                /*
                 * ���������е��ļ�д�뱾��
                 */
               
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
                			
            }else{
                System.out.println("��Ӧ����" +connection.getResponseCode() + ". ��������֧�ֶ��߳�����              "+threadId+"�߳�");
                if(connection!=null)  connection.disconnect();                                   
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

	}
	
    public static String getId(String url){
		 Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(url);
		 while (matcher.find()){
			 System.out.println(matcher.group());
			 return  matcher.group();

		 }
		 return "ȱʡ"+new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date());
    }
	
	// ��ȡ�����ļ�������
	public static String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

}
