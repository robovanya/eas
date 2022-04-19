package eoc.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rvanya
 * 
 * porovna obsah dvoch txt-suborov po riadkov a vypise riadky, ktore v druhom
 * subore nie su. Vhodny je na porovnanie dvoch xls-stlcov, ktore predtym 
 * boli odkopirovany na samostatny sheet, a potom viimportovane  do csv suboru.
 * Pre csv treba dat delimiter # !!!
 */
public class compare_by_lines {
    
        public static void main(String[] args) {
            compareCSV();
        }

    public static void compareCSV() {
        String strA = null;
        String strB = null;
        String sInFa = "C:\\comparecsv\\vs_OK.csv";
        String sInFb = "C:\\comparecsv\\vs_bad.csv";
        strA = readFileToStr(sInFa,"#");
        strB = readFileToStr(sInFb,"#");
        String[] arrA = strA.split("#");
        String[] arrB = strB.split("#");
        System.out.println(arrA.length + " -- " + arrB.length + "  spolu:" + (arrA.length + arrB.length));
        System.out.println("Comparing OK to Bad");
        for (String s : arrA) { // citanie OK
            for (String sc : arrB) { // porovnanie s Bad
               if (s.trim().equals(sc.trim())) System.out.println(s);
            }
            
        }
        System.out.println("Comparing Bad to OK");
        for (String s : arrB) { // citanie bad
            for (String sc : arrA) { // porovnanie OK
               if (s.trim().equals(sc.trim())) System.out.println(s);
            }
            
        }
    }
    
    public static String readFileToStr(String sInF, String delimiter) {
        String retVal = "";
        BufferedReader readerF = null;
        try {
            String sInCd = "windows-1250";
            final Path pF = Paths.get(sInF);
            readerF = Files.newBufferedReader(pF,Charset.forName(sInCd));
            String sLine;
            read_block:
            while ((sLine = readerF.readLine()) != null) {
                retVal = retVal + delimiter + sLine;
            }
        } catch (IOException ex) {
            Logger.getLogger(compare_by_lines.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                readerF.close();
            } catch (IOException ex) {
                Logger.getLogger(compare_by_lines.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (retVal.startsWith("delimiter")) retVal = retVal.substring(1);
        return retVal;
    }
}
