package eoc.readers;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Precita subor po riadkoch metodou readFileByMethod() a riadky vrati do metody 
 * volajuceho programu (mtd), ktora je parametrom odovzdana volajucim programom, a ma mat 
 * jeden vstupny parameter typu String, v ktorej sa mu vracia prave nacitany riadok.
 * 
 * Objekt EOC_PingPongFileReader sluzi na citanie textovych suborov bez toho aby sa to 
 * muselo programovat v roznych objektoch znovu, a znovu
 * 
 * @author rvanya
 */
public class PingPongFileReader {
private String inputCP  = "Cp1250";
private String outputCP = "Cp1250";
// konstruktor
public PingPongFileReader() throws FileNotFoundException, IOException {

}

public void readFileByMethod_csv (Object oCaller, String file, Method mtd) 
     /* throws FileNotFoundException, IOException, IllegalAccessException, 
            IllegalArgumentException */ {
      File f = new File(file);
      readFileByMethod_csv (oCaller, f, mtd);
}

public void readFileByMethod_csv (Object oCaller, File f, Method mtd) 
     /* throws FileNotFoundException, IOException, IllegalAccessException, 
            IllegalArgumentException */{
    // File f = new File(file);
    String line;
    BufferedReader br = null;
    try {
        br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(f),inputCP));
    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, ex);
    } catch (FileNotFoundException ex) {
        Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, ex);
    }
//                        new FileInputStream(file),"Cp1250"));
    if (br != null) { 
    int i = 0;    
        try {
            while ((line = br.readLine()) != null) {
                i++;
                ////System.out.println("LENEEEEE:" + line);
                try {
                    ////PERM System.out.println("INVOOKEMETHODD:" + mtd.getName() + " lINE:" + line);
                    //System.out.println(i + " readFileByMethod-line==-> " + line);
                    mtd.invoke(oCaller, line);//Spracovanie riadku volajucim programom
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, /* "XXXXEEE>" + */ ex);
                    
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   } catch (IOException ex) {
            Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(PingPongFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
    

public void setInputCP  (String cp) {
    inputCP = cp;
}

public void setOutputCP  (String cp) {
    outputCP = cp;
}

public void setInputOutputCP  (String cp) {
    inputCP  = cp;
    outputCP = cp;
}

public void readFileByMethod_xls (Object oCaller, String file, Method mtd) 
     throws FileNotFoundException, IOException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, BiffException {
    File f = new File(file);
    WorkbookSettings ws = new WorkbookSettings();
    ws.setEncoding(inputCP);
    Workbook wb = Workbook.getWorkbook(f,ws);
    Sheet sheet = wb.getSheet(0);    
    String line = "";
    String data = "";
    int columns = sheet.getColumns();
    int rows    = sheet.getRows();
    
    row_block:
    for (int row = 0; row < rows; row++) {
        line = "";
        for (int col = 0; col < columns; col++) {
              data = sheet.getCell(col, row).getContents();
                            line = line + data + "|";
    //                      System.out.print(data + " ");
        }
        if (line.startsWith("$QUIT")) {
            break row_block;
        }
        //krn.OutPrintln((row + 1) + " readFileByMethod-> " + line);
        mtd.invoke(oCaller, line);//Spracovanie riadku volajucim programom
    }
    wb.close();
}
}
