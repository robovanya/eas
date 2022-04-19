package eoc.readers;

import org.apache.pdfbox.ExtractText;
import org.apache.pdfbox.pdmodel.*;
import java.io.*;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author rvanya
 */
public class EOC_PDFparser {
/**
 * @(#)EOC_PDFparser.java
 *
 *
 * @author Robo Vanya
 * @version 1.00 2014/3/31
 */

public class BlankPDF {
}
    public static void main(String[] args) {
        PDDocument doc = null;
        try{
//            doc = new PDDocument();
PDDocument document = PDDocument.load("c:\\BCF_FA\\OTP.pdf");
PDFTextStripper s = new PDFTextStripper();
String content = s.getText(document);   
System.out.println(content);
        } catch (IOException ie){
            System.out.println(ie);
        }
    }
}
