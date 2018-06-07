package com.bilibili.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.PropertyConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDF2Image {

	public static void main(String[] args) {
		PropertyConfigurator.configure("src/main/resources/log4j.propertier"); 
		pdf2Image("C:/Users/itbys/Desktop/vczh_֪��.pdf", "C:/Users/itbys/Desktop", 180);  
	}
	
	/*** 
     * PDF�ļ�תPNGͼƬ��ȫ��ҳ�� 
     *  
     * @param PdfFilePath pdf����·�� 
     * @param imgFilePath ͼƬ��ŵ��ļ��� 
     * @param dpi dpiԽ��ת����Խ���������ת���ٶ�Խ�� 
     * @return 
     */  
    public static void pdf2Image(String PdfFilePath, String dstImgFolder, int dpi) {  
        File file = new File(PdfFilePath);  
        PDDocument pdDocument;  
        try {  
            String imgPDFPath = file.getParent();  
            int dot = file.getName().lastIndexOf('.');  
            String imagePDFName = file.getName().substring(0, dot); // ��ȡͼƬ�ļ���  
            String imgFolderPath = null;  
            if (dstImgFolder.equals("")) {  
                imgFolderPath = imgPDFPath + File.separator + imagePDFName;// ��ȡͼƬ��ŵ��ļ���·��  
            } else {  
                imgFolderPath = dstImgFolder + File.separator + imagePDFName;  
            }  
  
            if (createDirectory(imgFolderPath)) {  
  
                pdDocument = PDDocument.load(file);  
                PDFRenderer renderer = new PDFRenderer(pdDocument);  
                /* dpiԽ��ת����Խ���������ת���ٶ�Խ�� */ 
                PDDocument reader = PDDocument.load(new File(PdfFilePath));  
                int pages = reader.getNumberOfPages();  
                StringBuffer imgFilePath = null;  
                for (int i = 0; i < pages; i++) {  
                    String imgFilePathPrefix = imgFolderPath + File.separator + imagePDFName;  
                    imgFilePath = new StringBuffer();  
                    imgFilePath.append(imgFilePathPrefix);  
                    imgFilePath.append("_");  
                    imgFilePath.append(String.valueOf(i + 1));  
                    imgFilePath.append(".png");  
                    File dstFile = new File(imgFilePath.toString());  
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi);  
                    //BufferedImage image = renderer.renderImage(i);
                    ImageIO.write(image, "png", dstFile); 
                    image.flush();
                }  
                System.out.println("PDF�ĵ�תPNGͼƬ�ɹ���");  
  
            } else {  
                System.out.println("PDF�ĵ�תPNGͼƬʧ�ܣ�" + "����" + imgFolderPath + "ʧ��");  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    private static boolean createDirectory(String folder) {  
        File dir = new File(folder);  
        if (dir.exists()) {  
            return true;  
        } else {  
            return dir.mkdirs();  
        }  
    }  
    

}
