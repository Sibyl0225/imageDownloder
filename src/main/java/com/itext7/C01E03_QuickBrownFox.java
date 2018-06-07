package com.itext7;


import java.io.File;
import java.io.IOException;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

/**
 * Simple image example.
 */
public class C01E03_QuickBrownFox {
    public static final String DOG = "src/main/resources/img/dog.bmp";
    public static final String FOX = "src/main/resources/img/fox.bmp";

    public static final String DEST = "F:/results/chapter01/quick_brown_fox.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E03_QuickBrownFox().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);

        // Compose Paragraph
        
        Image foxImage = new Image(ImageDataFactory.create(FOX));
        Image dogImage = new Image(ImageDataFactory.create(DOG));
        
        foxImage = foxImage.scale(0.5f , 0.5f);
        dogImage = dogImage.scale(1.5f , 1.5f);
        
//        BufferedImage dogBufferedImage = ImageIO.read(new FileInputStream(DOG));             //读取一幅图像到图像缓冲区
//        dogBufferedImage = ImageUtil.resize(dogBufferedImage, 0.5);
//        
//        BufferedImage foxBufferedImage = ImageIO.read(new FileInputStream(FOX));  
//        foxBufferedImage = ImageUtil.resize(foxBufferedImage, 0.5);

        Paragraph p = new Paragraph("The quick brown ")
                .add(foxImage)
                .add(" jumps over the lazy ")
                .add(dogImage);
        // Add Paragraph to document
        document.add(p);

        //Close document
        document.close();
    }
}