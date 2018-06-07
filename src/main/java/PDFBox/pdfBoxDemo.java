package PDFBox;


import java.awt.Graphics2D;
import java.awt.List;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;    
import org.apache.pdfbox.util.*;

import com.bilibili.tools.PDF2Image;
/**  
* 这里用一句话描述这个类的作用 
* @author   
* @date 2015-09-17  
*/  
public class pdfBoxDemo {    
  
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";    
      
  /**  
   * 解析pdf文档信息  
   * @param pdfPath   pdf文档路径  
   * @throws Exception  
   */    
  public static void pdfParse( String pdfPath, String imgSavePath ) throws Exception    
  {    
      InputStream input = null;    
      File pdfFile = new File( pdfPath );    
      PDDocument document = null;    
      try{    
          input = new FileInputStream( pdfFile );    
          //加载 pdf 文档    
          document = PDDocument.load( input );    
              
          /** 文档属性信息 **/    
          PDDocumentInformation info = document.getDocumentInformation();    
          System.out.println( "标题:" + info.getTitle() );    
          System.out.println( "主题:" + info.getSubject() );    
          System.out.println( "作者:" + info.getAuthor() );    
          System.out.println( "关键字:" + info.getKeywords() );    
              
          System.out.println( "应用程序:" + info.getCreator() );    
          System.out.println( "pdf 制作程序:" + info.getProducer() );    
              
          System.out.println( "作者:" + info.getTrapped() );    
              
          System.out.println( "创建时间:" + dateFormat( info.getCreationDate() ));    
          System.out.println( "修改时间:" + dateFormat( info.getModificationDate()));    
          
         
        int pages = document.getNumberOfPages();  
		//获取内容信息    
		PDFTextStripper stripper  = new PDFTextStripper();    
		// 设置按序输出
		stripper.setSortByPosition(true);
		stripper.setStartPage(1);
		stripper.setEndPage(pages);
		
		String content = stripper.getText(document);
		System.out.println(content);  
		
		
		new Pdfh
		

              
		  ArrayList<PDImageXObject> images = new ArrayList<PDImageXObject>();  
          /** 文档页面信息 **/    
          PDDocumentCatalog cata = document.getDocumentCatalog();    
  		  PDPageTree pageTree = cata.getPages();
  		  Iterator<PDPage> it = pageTree.iterator();
  		  int pageConut=0;
          while(it.hasNext())  
          {    pageConut++;
      
              PDPage page = it.next(); 
              if( null != page )    
              {    
                  PDResources pdRes = page.getResources();                  
                  Iterator<COSName> xObjecIt = pdRes.getXObjectNames().iterator();
                  while( xObjecIt.hasNext()){
						COSName cosName = xObjecIt.next();
						PDXObject xObject = pdRes.getXObject(cosName);
//						// 是图片
//						if (pdRes.isImageXObject(cosName)) {
//							PDImageXObject pdxObject = (PDImageXObject) pdRes.getXObject(cosName);
//						ImageIO.write( pdxObject.getImage(), pdxObject.getSuffix(),new File(imgSavePath + i + "."+pdxObject.getSuffix()));
//						}
						if (xObject instanceof PDImageXObject) {
							images.add((PDImageXObject)xObject);
						} else if (xObject instanceof PDFormXObject) {
							PDResources resources = ((PDFormXObject) xObject).getResources();
							images.addAll(getImagesFromResources(resources));
						} else {
						}
                  }
			        
              }    
          } 
      	  System.out.println("page数量:"+pageConut);
          System.out.println("images图片数量:"+images.size());
			for (int i = 0; i < images.size(); i++) {
				PDImageXObject xobjct = (PDImageXObject) images.get(i);
				BufferedImage image = xobjct.getImage();
				if (image.getWidth() > 50 && image.getHeight() > 50) {
				//	ImageIO.write(image, xobjct.getSuffix(), new File(imgSavePath + i + "." + xobjct.getSuffix()));
				}
			}
      }catch( Exception e)    
      {    
          throw e;    
      }finally{    
          if( null != input )    
              input.close();    
          if( null != document )    
              document.close();    
      }    
  } 
  
	public ArrayList<Object> getImagesFromPDF(PDDocument document) throws IOException {
		ArrayList<Object> images = new ArrayList<Object>();
		for (PDPage page : document.getPages()) {
			images.addAll(getImagesFromResources(page.getResources()));
		}

		return images;
	}
      
  private static ArrayList<PDImageXObject> getImagesFromResources(PDResources resources) throws IOException {
	    ArrayList<PDImageXObject> images = new ArrayList<PDImageXObject>();
        Iterator<COSName> xObjecIt = resources.getXObjectNames().iterator();     
		COSName cosName = xObjecIt.next();
		//是图片
		if (resources.isImageXObject(cosName)) {
			PDImageXObject pdxObject = (PDImageXObject) resources.getXObject(cosName);
			images.add(pdxObject);
		} 
		return images;
	}

/**  
   * 获取格式化后的时间信息  
   * @param dar   时间信息  
   * @return  
   * @throws Exception  
   */    
  public static String dateFormat( Calendar calendar ) throws Exception    
  {    
      if( null == calendar )    
          return null;    
      String date = null;    
      try{    
          String pattern = DATE_FORMAT;    
          SimpleDateFormat format = new SimpleDateFormat( pattern );    
          date = format.format( calendar.getTime() );    
      }catch( Exception e )    
      {    
          throw e;    
      }    
      return date == null ? "" : date;    
  }  
  
  private static BufferedImage resize(BufferedImage source, int targetW,  int targetH) {  
      int type=source.getType();  
      BufferedImage target=null;  
      double sx=(double)targetW/source.getWidth();  
      double sy=(double)targetH/source.getHeight();  
      if(sx>sy){  
          sx=sy;  
          targetW=(int)(sx*source.getWidth());  
      }else{  
          sy=sx;  
          targetH=(int)(sy*source.getHeight());  
      }  
      if(type==BufferedImage.TYPE_CUSTOM){  
          ColorModel cm=source.getColorModel();  
               WritableRaster raster=cm.createCompatibleWritableRaster(targetW, targetH);  
               boolean alphaPremultiplied=cm.isAlphaPremultiplied();  
               target=new BufferedImage(cm,raster,alphaPremultiplied,null);  
      }else{  
          target=new BufferedImage(targetW, targetH,type);  
      }  
      Graphics2D g=target.createGraphics();  
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);  
      g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));  
      g.dispose();  
      return target;  
  }  
      
  public static void main( String [] args ) throws Exception{    
      pdfParse("C:\\Users\\itbys\\Desktop\\yande.re.pdf","C:\\Users\\itbys\\Desktop\\yande\\1.");    
  }    
}
