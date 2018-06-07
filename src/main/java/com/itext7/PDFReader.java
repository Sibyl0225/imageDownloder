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
		extractImage("C:\\Users\\itbys\\Desktop\\vczh_֪��.pdf");
	}

	public static void extractImage(String filename){  
        
        PdfReader reader = null;  
        try {  
            //��ȡpdf�ļ�  
            reader = new PdfReader(filename);  
            //���pdf�ļ���ҳ��  
            int sumPage = reader.getNumberOfPages();      
            //��ȡpdf�ļ��е�ÿһҳ  
            for(int i = 1;i <= sumPage;i++){  
                //�õ�pdfÿһҳ���ֵ����  
                PdfDictionary dictionary = reader.getPageN(i);  
                //ͨ��RESOURCES�õ���Ӧ���ֵ����  
                PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));  
                //�õ�XOBJECTͼƬ����  
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
