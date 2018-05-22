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
        System.out.println("线程"+ threadId + "开始下载");
        String imageId = getId(imageUrl) ;
        try {
            //分段请求网络连接,分段将文件保存到本地.
            URL url = new URL(imageUrl);
            
            int startPostion = 0;

            //加载下载位置的文件
            File image = new File(filePath,getFileName(imageUrl));
            
			File cashTxt = new File(filePath,imageId +"_"+threadId+ ".txt");
            
            RandomAccessFile downThreadStream = null;
            if(cashTxt.exists()){//如果文件存在
                downThreadStream = new RandomAccessFile(image,"rwd");
                
                BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(cashTxt)));
				String lastPostion_str = bufferedReader.readLine();
				startPostion = Integer.parseInt(lastPostion_str);
				System.err.println("文件开始位置：   "+startPostion);
				bufferedReader.close();
            }else{
                downThreadStream = new RandomAccessFile(image,"rwd");
            }

            int endPostion = FileLengthTools.getRemoteFileLenght(imageUrl);
            System.err.println("文件结束位置：   "+endPostion);
            
            if(endPostion <= startPostion){
            	System.err.println(imageId+"  文件已下载不需要重复下载！");
            	System.exit(0);;
            }
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6 * 1000);
            //设置分段下载的头信息。  Range:做分段数据请求用的。格式: Range bytes=0-1024  或者 bytes:0-1024
            connection.setRequestProperty("Range", "bytes="+ startPostion + "-" + endPostion );

            if(connection.getResponseCode() == 206){//200：请求全部资源成功， 206代表部分资源请求成功
            	System.err.println("开始下载并写文件到本地……");
                InputStream inputStream = connection.getInputStream();//获取流
                downThreadStream.seek(startPostion);//文件写入的开始位置.
                /*
                 * 将网络流中的文件写入本地
                 */
               
                byte[] buffer = new byte[1024];
                int length = -1;
                int total = 0;// 记录本次线程下载的总大小
                
                while((length = inputStream.read(buffer)) > 0){
                	downThreadStream.write(buffer, 0, length);
                	
                	total = total + length;
                    // 去保存当前线程下载的位置，保存到文件中
                    int currentThreadPostion = startPostion + total;// 计算出当前线程本次下载的
                    File file = new File(filePath,imageId +"_"+threadId+ ".txt");
                    RandomAccessFile accessfile = new RandomAccessFile(
                            file, "rwd");
                    accessfile.write(String.valueOf(currentThreadPostion).getBytes());
                    accessfile.close();
                }
                

                downThreadStream.close();
                inputStream.close();  
                System.out.println("线程"+ threadId + "下载完毕");
                			
            }else{
                System.out.println("响应码是" +connection.getResponseCode() + ". 服务器不支持多线程下载              "+threadId+"线程");
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
		 return "缺省"+new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date());
    }
	
	// 获取下载文件的名称
	public static String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

}
