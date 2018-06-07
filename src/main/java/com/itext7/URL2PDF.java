package com.itext7;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.WritableDirectElement;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementHandler;
import com.itextpdf.tool.xml.Writable;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.pipeline.WritableElement;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

/**
 * HTML�ļ�ת��ΪPDF
 *
 * @author <a href="http://www.micmiu.com">Michael Sun</a>
 */
public class URL2PDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String blogURL = "https://www.zhihu.com/people/excited-vczh/activities";

		// ֱ�Ӱ���ҳ����תΪPDF�ļ�
		String pdfFile = "F:/demo-URL.pdf";
		URL2PDF.parseURL2PDFFile(pdfFile, blogURL);

		// ����ҳ����תΪPDF�е�Elements
		//String pdfFile2 = "F:/demo-URL2.pdf";
		//URL2PDF.parseURL2PDFElement(pdfFile2, blogURL);
	}

	/**
	 * ����URL��ǰblog�Ļ�����Ϣ�����ؽ��>>:[���� ,����,����,����]��.
	 *
	 * @param blogURL
	 * @return
	 * @throws Exception
	 */
	public static String[] extractBlogInfo(String blogURL) throws Exception {
		String[] info = new String[4];
		org.jsoup.nodes.Document doc = Jsoup.connect(blogURL).get();
		org.jsoup.nodes.Element e_title = doc.select("h1.ProfileHeader-title").first();
		info[0] = e_title.text();

//		org.jsoup.nodes.Element e_category = doc.select("a[rel=category tag]").first();
//		info[1] = e_category.attr("href").replace("http://www.micmiu.com/", "");

//		Elements e_date = doc.select("#post-date");
//
//		String dateStr = e_date.text();
		info[1] = new Date().toLocaleString();
		Elements entry = doc.select("div.List-item");
		//System.err.println("entry.html():  "+entry.html());
		//��ʽ��image��ǩ
		info[2] = formatContentTag(entry);

		return info;
	}

	/**
	 * ��ʽ�� img��ǩ
	 *
	 * @param entry
	 * @return
	 */
	private static String formatContentTag(Elements entry) {
		try {
			//entry.select("div").remove();
			// �� <a href="*.jpg" ><img src="*.jpg"/></a> �滻Ϊ <img
			// src="*.jpg"/>
//			for (org.jsoup.nodes.Element imgEle : entry.select("a[href~=(?i)\\.(png|jpe?g)]")) {
//				System.out.println(imgEle.html());
//			}
//			Elements imgs = entry.select("img[src]");
//	        //String regEx_img = "(?is)<img\\s*((?<key>[^=]+)=\"*(?<value>[^\"]+)\")+?\\s*/?>";
//			System.out.println("imgs.html()"+imgs.text());
//			for (org.jsoup.nodes.Element imgEle : imgs) {
//				String imgReplace = imgEle.toString().replaceAll(">$", "/>");
//				Elements node = Jsoup.parse(imgReplace).select("img[src]");
//				imgEle.replaceWith(node.get(0));
//				System.out.println(imgEle.toString());
//			}
			
			System.err.println(entry.html());
			return entry.html()
					.replace("<br>", "")
					.replace("<hr>", "")
					.replaceAll("<img[^>]+>", "")
					.replaceAll("<meta[^>]+>", "")
					.replace("<param>", "")
					.replace("<link>", "");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ��String תΪ InputStream
	 *
	 * @param content
	 * @return
	 */
	public static InputStream parse2Stream(String content) {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes("utf-8"));
			return stream;
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * ֱ�Ӱ���ҳ����תΪPDF�ļ�
	 *
	 * @param fileName
	 * @throws Exception
	 */
	public static void parseURL2PDFFile(String pdfFile, String blogURL) throws Exception {

		BaseFont bfCN = BaseFont.createFont("C:/WINDOWS/Fonts/SIMSUN.TTC,1",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		// �������嶨��
		Font chFont = new Font(bfCN, 14, Font.NORMAL, BaseColor.BLUE);
		Font secFont = new Font(bfCN, 12, Font.NORMAL, new BaseColor(0, 204, 255));
		Font textFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLACK);

		Document document = new Document();
		PdfWriter pdfwriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
		document.open();

		String[] blogInfo = extractBlogInfo(blogURL);

		int chNum = 1;
		Chapter chapter = new Chapter(new Paragraph("URLתPDF����", chFont), chNum++);

		Section section = chapter.addSection(new Paragraph(blogInfo[0], secFont));
		section.setIndentation(10);
		section.setIndentationLeft(10);
		section.setBookmarkOpen(false);
		section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
		section.add(new Chunk(" ���ڣ�" + blogInfo[1], textFont));

		LineSeparator line = new LineSeparator(1, 100, new BaseColor(204, 204, 204), Element.ALIGN_CENTER, -2);
		Paragraph p_line = new Paragraph(" ");
		p_line.add(line);
		section.add(p_line);
		section.add(Chunk.NEWLINE);

		document.add(chapter);
		
		MyFontProvider fontProvider = new MyFontProvider();
        // ʹ�����ǵ������ṩ��������������Ϊunicode������ʽ   
        fontProvider.addFontSubstitute("lowagie", "garamond");  
        fontProvider.setUseUnicode(true);  
        CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);  
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);  
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());  
        XMLWorkerHelper.getInstance().getDefaultCssResolver(true); 
        
        InputStream   in_withcode   =   new   ByteArrayInputStream(blogInfo[2].getBytes("UTF-8"));  
		// html�ļ�
		XMLWorkerHelper.getInstance().parseXHtml(pdfwriter, document, new BufferedInputStream(in_withcode),
				Charset.forName("UTF-8"), fontProvider);
		document.close();
	}

	/**
	 * ����ҳ����תΪPDF�е�Elements
	 *
	 * @param pdfFile
	 * @param htmlFileStream
	 */
	public static void parseURL2PDFElement(String pdfFile, String blogURL) {
		try {
			Document document = new Document(PageSize.A4);

			FileOutputStream outputStream = new FileOutputStream(pdfFile);
			PdfWriter pdfwriter = PdfWriter.getInstance(document, outputStream);
			// pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
			document.open();

			BaseFont bfCN = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
			// �������嶨��
			Font chFont = new Font(bfCN, 14, Font.NORMAL, BaseColor.BLUE);
			Font secFont = new Font(bfCN, 12, Font.NORMAL, new BaseColor(0, 204, 255));
			Font textFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLACK);

			int chNum = 1;
			Chapter chapter = new Chapter(new Paragraph("URLתPDFԪ�أ�����׷����������", chFont), chNum++);

			String[] blogInfo = extractBlogInfo(blogURL);

			Section section = chapter.addSection(new Paragraph(blogInfo[0], secFont));

			section.setIndentation(10);
			section.setIndentationLeft(10);
			section.setBookmarkOpen(false);
			section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
			section.add(new Chunk(" �������ڣ�" + blogInfo[1], textFont));
			LineSeparator line = new LineSeparator(1, 100, new BaseColor(204, 204, 204), Element.ALIGN_CENTER, -2);
			Paragraph p_line = new Paragraph();
			p_line.add(line);
			section.add(p_line);
			section.add(Chunk.NEWLINE);

			final List<Element> pdfeleList = new ArrayList<Element>();
			ElementHandler elemH = new ElementHandler() {

				public void add(final Writable w) {
					if (w instanceof WritableElement) {
						pdfeleList.addAll(((WritableElement) w).elements());
					}

				}
			};
			XMLWorkerHelper.getInstance().parseXHtml(elemH, new InputStreamReader(parse2Stream(blogInfo[2]), "utf-8"));

			List<Element> list = new ArrayList<Element>();
			for (Element ele : pdfeleList) {
				if (ele instanceof LineSeparator || ele instanceof WritableDirectElement) {
					continue;
				}
				list.add(ele);
			}
			section.addAll(list);

			section = chapter.addSection(new Paragraph("��������½�", secFont));

			section.setIndentation(10);
			section.setIndentationLeft(10);
			section.setBookmarkOpen(false);
			section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
			section.add(new Chunk("����URLתΪPDFԪ�أ�����׷����������", textFont));

			document.add(chapter);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}