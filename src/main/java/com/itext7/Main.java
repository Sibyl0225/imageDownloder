package com.itext7;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import com.bilibili.tools.HtmlTools;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
	      String html = "F:/pdf/test.html";
	      String pdf = "F:/pdf/test.pdf";
	      

	      File pdfFile = new File(pdf);
	      InputStream inputStream = PDFUtil.htmlToPDF(new FileInputStream(new File(html)));
	      try{
	          if(!pdfFile.exists()){
	            pdfFile.createNewFile();
	           }
	        }catch (IOException e ){
	        	System.err.println("dfFile.createNewFile() Ê§°Ü£¡");
               e.printStackTrace();
	       }
	     IOUtil.copyCompletely(inputStream,new FileOutputStream(pdf));
	     inputStream.close();
	     System.out.println("×ª»»³É¹¦!");
	    
	}

}
