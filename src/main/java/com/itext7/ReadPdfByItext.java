package com.itext7;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfException;
import com.itextpdf.text.pdf.PdfIndirectReference;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class ReadPdfByItext {

	public static void main(String[] args) throws PdfException, DocumentException {
		readPdfContentNew("C:\\Users\\itbys\\Desktop\\yande.re.pdf");
	}
	/** 
	 * 读取Pdf文件的内容 
	 * @param path :文件地址 
	 */  
	public static void readPdfContent(String path){  
		try {  
			PdfReader pr = new PdfReader(path);  
			int page = pr.getNumberOfPages();  
			String content = "";  
			for(int i = 1 ;i<page+1;i++){  
				content += PdfTextExtractor.getTextFromPage(pr, i); //遍历页码,读取Pdf文件内容  
			}
			System.err.println("内容：");
			System.err.println(content);
			
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}
	
	/** 
	 * 读取Pdf文件的内容 
	 * @param path :文件地址 
	 * @throws DocumentException 
	 * @throws PdfException 
	 */  
	public static void readPdfContentNew(String path) throws PdfException, DocumentException{  
		try {  
			PdfReader reader = new PdfReader(path);  
			int page = reader.getNumberOfPages();  
			String content = "";  
			
			PdfStamper stp = new PdfStamper(reader, new FileOutputStream("c:\\out.pdf"));
			PdfWriter writer = stp.getWriter();
			Image img = Image.getInstance("D:\\yande\\20180411\\yande.re%20440536%20azur_lane%20belfast_%28azur_lane%29%20cleavage%20maid%20stockings%20takehana_note%20thighhighs%20weapon.jpg");
			
			for(int i = 1 ;i<page+1;i++){  
				PdfDictionary pageNdict = reader.getPageN(i);
                //通过RESOURCES得到对应的字典对象  
                PdfDictionary resDic = (PdfDictionary) PdfReader.getPdfObject(pageNdict.get(PdfName.RESOURCES));  
                PdfDictionary xobjDic = (PdfDictionary) PdfReader.getPdfObject(resDic.get(PdfName.XOBJECT));  
                if(xobjDic == null) continue;
                //遍历字典中的对象
                Iterator<PdfName> resKeys = xobjDic.getKeys().iterator();         
                if(resKeys.hasNext()){
					PdfName key = resKeys.next();
					PdfObject obj= xobjDic.get(key);
					if(obj.isDictionary()){
						System.out.println("isDictionary");
					}else if(obj.isIndirect()){
						System.out.println("isIndirect");
						 if(obj.isIndirect()){  
	                        PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);                   
	                        PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
	                        System.out.println(String.format("%d type: %s  __ PdfName.IMAGE: %s",i,type,PdfName.IMAGE));
	                        if(PdfName.IMAGE.equals(type)){       
	                            PdfObject object =  reader.getPdfObject(obj);  
	                            if(object.isStream()){  
	                            	
//	                            	  PdfIndirectReference ref = xobjDic.getAsIndirectObject(key);
//	                            	  
//	                            	  PdfReader.killIndirect(ref);
//	                            	  Image maskImage = img.getImageMask();
//	                            	  if(maskImage!= null){
//	                            	    writer.addDirectImageSimple(maskImage);
//	                            	  }
//	                            	  writer.addDirectImageSimple(img,ref);
	                            	
	                            	PRStream prstream = (PRStream)object;  
	                                byte[] b;  
	                                try{  
	                                    b = reader.getStreamBytesRaw(prstream); 
	                                    FileOutputStream output = new FileOutputStream(String.format("C:\\Users\\itbys\\Desktop\\yande\\%d.jpg",i));  
	                                    output.write(b);  
	                                    output.flush();  
	                                    output.close();                               
	        
	                                }catch(UnsupportedPdfException e){  
	                                   // b = reader.getStreamBytesRaw(prstream);  
	                                }  
	                            }
	                        }else if(PdfName.FORM.equals(type)){
	                        	PdfObject object =  reader.getPdfObject(obj);  
	                            System.out.println(String.format("String: %s",object.toString()));      
	                        }
						 }
					}else if(obj.isStream()){
						System.out.println("isStream");
					}else if(obj.isArray()){
						System.out.println("isArray");
					}else if(obj.isName()){
						System.out.println("isName");
					}
				}
//				PdfDictionary pageNres  = (PdfDictionary)  PdfReader.getPdfObject(pageNdict.get(PdfName.RESOURCES));
//				PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(pageNres.get(PdfName.XOBJECT));
//				Iterator<PdfName> keys = xobj.getKeys().iterator();
//				
//				if(keys.hasNext()){
//					PdfName key = keys.next();
//					PdfObject obj = xobj.get(key); 
//                    if(obj.isIndirect()){  
//                        PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);                   
//                        PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
//                        System.out.println(String.format("%d type: %s  __ PdfName.IMAGE: %s",i,type,PdfName.IMAGE));
//                        if(PdfName.IMAGE.equals(type)){       
//                            PdfObject object =  reader.getPdfObject(obj);  
//                            if(object.isStream()){                        
//                                PRStream prstream = (PRStream)object;  
//                                byte[] b;  
//                                try{  
//                                    b = reader.getStreamBytes(prstream);  
//                                }catch(UnsupportedPdfException e){  
//                                    b = reader.getStreamBytesRaw(prstream);  
//                                }  
//                                FileOutputStream output = new FileOutputStream(String.format("f:/pdf/output%d.jpg",i));  
//                                output.write(b);  
//                                output.flush();  
//                                output.close();                               
//                            }
//                        }else if(PdfName.FORM.equals(type)){
//                        	PdfObject object =  reader.getPdfObject(obj);  
//                            System.out.println(String.format("String: %s",object.toString()));      
//                        }
//                        
//                    } 
//				}
				
			}
			System.err.println("内容：");
			System.err.println(content);
			
			stp.close();
			
			
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}

}
