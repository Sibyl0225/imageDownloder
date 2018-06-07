package com.itext7;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

public class PDFUtil {

	public static InputStream htmlToPDF(InputStream htmlInputStream) {
		ByteArrayOutputStream out = null;
		ByteArrayInputStream inputStream = null;
		Document document = new Document();
		MyFontProvider fontProvider = new MyFontProvider();
        // 使用我们的字体提供器，并将其设置为unicode字体样式   
        fontProvider.addFontSubstitute("lowagie", "garamond");  
        fontProvider.setUseUnicode(true);  
        CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);  
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);  
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());  
        XMLWorkerHelper.getInstance().getDefaultCssResolver(true); 
        
        
		try {
			out = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new BufferedInputStream(htmlInputStream),
					Charset.forName("UTF-8"), fontProvider);
			document.close();
			inputStream = new ByteArrayInputStream(out.toByteArray());

		} catch (DocumentException e) {
			System.err.printf(e.getMessage(), e);
		} catch (IOException e) {
			document.close();
			System.err.printf(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					System.err.printf(e.getMessage(), e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.err.printf(e.getMessage(), e);
				}
			}
		}
		return inputStream;
	}
}
