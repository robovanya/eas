/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.readers;

import java.io.File;
import java.io.IOException;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author rvanya
 */

public class EOC_XLS_to_CSV_reader {

    /**
     * @param args the command line arguments
     */
  //private String inputFile;
     private String inputFile = "c:\\Techem_20150321\\157.xls";

  public void setInputFile(String inputFile) {
    this.inputFile = inputFile;
  }

  public void read() throws IOException, BiffException  {
    File inputWorkbook = new File(inputFile);
    Workbook w;
    try {
      w = Workbook.getWorkbook(inputWorkbook);
      // Get the first sheet
      Sheet sheet = w.getSheet(0);
      // Loop over first 10 column and lines

        for (int i = 0; i < sheet.getRows(); i++) {
      for (int j = 0; j < sheet.getColumns(); j++) {
          Cell cell = sheet.getCell(j, i);
          CellType type = cell.getType();
          if (type == CellType.LABEL) {
            System.out.print("; "
                + cell.getContents());
          }

          if (type == CellType.NUMBER) {
            System.out.print("; "
                + cell.getContents());
          }

        }
      System.out.println("");
      }
    } catch (BiffException e) {
      e.printStackTrace();
    }
  } 
    public static void main(String[] args) throws IOException, BiffException {
        EOC_XLS_to_CSV_reader rdr = new EOC_XLS_to_CSV_reader();
        rdr.read();
        // TODO code application logic here
    }
    
}
