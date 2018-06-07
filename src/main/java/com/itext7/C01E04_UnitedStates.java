/**
 * Created by CuteKe on 2017/7/10.
 */
package com.itext7;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;

/**
 * Simple table example.
 */
public class C01E04_UnitedStates {
    public static final String DATA = "src/main/resources/data/united_states.csv";

    public static final String DEST = "F:/results/chapter01/united_states.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E04_UnitedStates().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        //PageSize.A4.rotate() 横向显示
        //PageSize.A4.LEDGER   纵向显示
        Document document = new Document(pdf, PageSize.A4.Default);
        document.setMargins(20, 20, 20, 20);

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        PdfFont bold = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        Table table = new Table(new float[]{4, 1, 3, 4, 3, 3, 3, 3, 1});
        /**构建表格以100%的宽度 */
        table.setWidth(UnitValue.createPercentValue(80));
        BufferedReader br = new BufferedReader(new FileReader(DATA));
        String line = br.readLine();
        process(table, line, bold, true);
        while ((line = br.readLine()) != null) {
            process(table, line, font, false);
        }
        br.close();
        /**将表格添加入文档并页面居中*/  
        //document.add(table);
        document.add(table.setHorizontalAlignment(HorizontalAlignment.CENTER));

        //Close document
        document.close();
    }

    public void process(Table table, String line, PdfFont font, boolean isHeader) {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                table.addHeaderCell(new Cell()
                		.add(new Paragraph(tokenizer.nextToken())
                				.setFont(font)
                				/**文字背景色    不要和cell背景色搞混了*/
                				//.setBackgroundColor(new DeviceRgb(221,234,238))
                				/**文字对齐*/
                				//.setTextAlignment(TextAlignment.CENTER)
                				/**有边框  实线 1px rgbColor*/
                				//.setBorder(new SolidBorder(new DeviceRgb(221,234,238), 1))
                				/**无边框*/
                				//.setBorder(Border.NO_BORDER)
                			)
                		.setBackgroundColor(new DeviceRgb(221,234,238))      
                					);
            } else {
                table.addCell(new Cell().add(new Paragraph(tokenizer.nextToken()).setFont(font)));
            }
        }
    }
}