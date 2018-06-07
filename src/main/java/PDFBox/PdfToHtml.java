package PDFBox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfToHtml {

		public static void main(String[] args) {
		PdfToImage("C:\\Users\\itbys\\Desktop\\vczh_֪��.pdf");
		//����PDF��ַ
		}


		public static void PdfToImage(String pdfurl){
		StringBuffer buffer = new StringBuffer();
		FileOutputStream fos;
		PDDocument document;
		File pdfFile;
		int size;
		BufferedImage image;
		FileOutputStream out;
		Long randStr = 0l;
		//PDFת����HTML������ļ���
		String path = "C:\\Users\\itbys\\Desktop\\vczh_֪��_pic";
		File htmlsDir = new File(path);
		if(!htmlsDir.exists()){
		htmlsDir.mkdirs();
		}
		File htmlDir = new File(path+"/");
		if(!htmlDir.exists()){
		htmlDir.mkdirs();
		}
		try{
		               //��������pdf����
		randStr = System.currentTimeMillis();
		buffer.append("<!doctype html>\r\n");
		buffer.append("<head>\r\n");
		buffer.append("<meta charset=\"UTF-8\">\r\n");
		buffer.append("</head>\r\n");
		buffer.append("<body style=\"background-color:gray;\">\r\n");
		buffer.append("<style>\r\n");
		buffer.append("img {background-color:#fff; text-align:center; width:100%; max-width:100%;margin-top:6px;}\r\n");
		buffer.append("</style>\r\n");
		document = new PDDocument();
		//pdf����
		pdfFile = new File(pdfurl);
		document = PDDocument.load(pdfFile, (String) null);
		size = document.getNumberOfPages();
		Long start = System.currentTimeMillis(), end = null;
		System.out.println("===>pdf : " + pdfFile.getName() +" , size : " + size);
		PDFRenderer reader = new PDFRenderer(document);
		for(int i=0 ; i < size; i++){
		//image = new PDFRenderer(document).renderImageWithDPI(i,130,ImageType.RGB);
		image = reader.renderImage(i, 1.5f);
		//����ͼƬ,����λ��
		out = new FileOutputStream(path + "/"+ "image" + "_" + i + ".jpg");
		ImageIO.write(image, "png", out); //ʹ��png��������
		//��ͼƬ·��׷�ӵ���ҳ�ļ���
		buffer.append("<img src=\"" + path +"/"+ "image" + "_" + i + ".jpg\"/>\r\n");
		image = null; out.flush(); out.close(); 
		}
		reader = null;
		document.close();
		buffer.append("</body>\r\n");
		buffer.append("</html>");
		end = System.currentTimeMillis() - start;
		System.out.println("===> Reading pdf times: " + (end/1000));
		start = end = null;
		//������ҳ�ļ�
		fos = new FileOutputStream(path+randStr+".html");
		System.out.println(path+randStr+".html");
		fos.write(buffer.toString().getBytes());
		fos.flush(); fos.close();
		buffer.setLength(0);



		}catch(Exception e){
		System.out.println("===>Reader parse pdf to jpg error : " + e.getMessage());
		e.printStackTrace();
		}
		}

		}
