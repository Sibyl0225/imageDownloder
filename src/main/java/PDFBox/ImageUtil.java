package PDFBox;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageUtil {
	
	  /**
	   * 调整图片大小
	   * @param source
	   * @param targetW
	   * @param targetH
	   * @return
	   */
	  public static BufferedImage resize(BufferedImage source, int targetW,  int targetH) {  
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
	  
	  
	  /**
	   * 调整图片大小  等比缩放
	   * @param source
	   * @param rate  比例   0.1 ~ 1.0
	   * @return
	   */
	  public static BufferedImage resize(BufferedImage source, double rate ) {  
	      int type=source.getType();  
	      BufferedImage target=null;  

	      int targetW = (int) (rate * source.getWidth());
	      int targetH = (int) (rate * source.getHeight());
	          
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
	      g.drawRenderedImage(source, AffineTransform.getScaleInstance(rate, rate));  
	      g.dispose();  
	      return target;  
	  } 

}
