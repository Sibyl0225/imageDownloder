package com.itext7;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;

public class PDFReader {

	public static void main(String[] args) {
		extractImage("C:\\Users\\itbys\\Desktop\\vczh_知乎.pdf");
	}

	public static void extractImage(String filename){  
        
        PdfReader reader = null;  
        try {  
            //读取pdf文件  
            reader = new PdfReader(filename);  
            //获得pdf文件的页数  
            int sumPage = reader.getNumberOfPages();      
            //读取pdf文件中的每一页  
            for(int i = 1;i <= sumPage;i++){  
                //得到pdf每一页的字典对象  
                PdfDictionary dictionary = reader.getPageN(i);  
                //通过RESOURCES得到对应的字典对象  
                PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));  
                //得到XOBJECT图片对象  
                PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(res.get(PdfName.XOBJECT));  
                if(xobj != null){  
                    for(Iterator it = xobj.getKeys().iterator();it.hasNext();){  
                        PdfObject obj = xobj.get((PdfName)it.next());             
                        if(obj.isIndirect()){  
                            PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);                   
                            PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
                            System.out.println(String.format("%d type: %s  __ PdfName.IMAGE: %s",i,type,PdfName.IMAGE));
                            if(PdfName.IMAGE.equals(type)){       
                                PdfObject object =  reader.getPdfObject(obj);  
                                if(object.isStream()){                        
                                    PRStream prstream = (PRStream)object;  
                                    byte[] b;  
                                    try{  
                                        b = reader.getStreamBytes(prstream);  
                                    }catch(UnsupportedPdfException e){  
                                        b = reader.getStreamBytesRaw(prstream);  
                                    }  
                                    FileOutputStream output = new FileOutputStream(String.format("f:/pdf/output%d.jpg",i));  
                                    output.write(b);  
                                    output.flush();  
                                    output.close();                               
                                }
                            }else if(PdfName.FORM.equals(type)){
                            	PdfObject object =  reader.getPdfObject(obj);  
                                System.out.println(String.format("String: %s",object.toString()));      
                            }
                            
                        } 
                    }  
                }  
            }  
              
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
	}
}
