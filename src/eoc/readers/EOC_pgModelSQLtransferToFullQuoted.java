/* EOC_pgModelerSQLtransferToFullQuoted.java
 *======================================================
 *
 * Program je sucastou systemu EaSys V1
 */

package eoc.readers;

import system.Kernel;
import system.FnEaS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author rvanya
 * 
 * Konvertuje standardfny SQL subor, generovany programom Pgmodeler
 * pre databazu Postgres z kodovej stranky UTF-8 do windows-1250,
 * a potom prida potrebne uvodzovky do kodu tak, aby bol citatelny
 * aj pre databazy Sybase, MySQL a Firebird.
 * 
 */
public class EOC_pgModelSQLtransferToFullQuoted {
    
    public static void main(String[] args) throws IOException {
       String sqlFile = 
//          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\TRANSFORM\\easys_20150115_orig.sql"; 
          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20170914\\eas.sql"; 
       String sOutputFile    = sqlFile.replace(".sql","_UTF-8_Quoted.sql");
       String sFinalizedFile = sqlFile.replace(".sql","_cp1250_Quoted.sql");
       transformFileToFullQuoted(sqlFile,sOutputFile); 
       convertFile(sOutputFile,sFinalizedFile,"UTF-8","windows-1250");
    }
    
    public static void convertFile(String sInF,String sOuF,
       String sInC, String sOuC) throws IOException {
       final Path src = Paths.get(sInF);
       final Path dst = Paths.get(sOuF);
       BufferedReader reader = 
          Files.newBufferedReader(src,Charset.forName(sInC));
       BufferedWriter writer = 
          Files.newBufferedWriter(dst,Charset.forName(sOuC));
       //Citanie suboru po riadkoch
       String sLine;
       read_block:
       while ((sLine = reader.readLine()) != null) {
           //System.out.println("CCOOnVERTING-LINE:" + sLine);
           try {
           writer.write(sLine);
           writer.flush();
           }
           catch (Exception ex) {
           System.out.println("----CCOOnVERTING-LINE-EXCEPTION: " + ex.getMessage()
                              + "\n----LINE: (" + sLine.length() + ") " + sLine + "\n");
           }
           writer.newLine();
           writer.flush();
       }
    }
    
    public static void transformFileToFullQuoted(String sInFile, String sOutFile) 
            throws FileNotFoundException, IOException {
       FileInputStream fstream = new FileInputStream(sInFile);
       BufferedReader br  = new BufferedReader(new InputStreamReader(fstream));
       BufferedWriter out = new BufferedWriter(new FileWriter(sOutFile));
// original SQL-kod       
/* odpoznamkovat, ked treba 
CREATE USER "eas" IDENTIFIED BY '***';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "eas";
COMMENT ON USER "eas" IS 'Spravca systemu EaSys';
GRANT GROUP TO eas;

CREATE USER "rest" IDENTIFIED BY '***';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "rest";
COMMENT ON USER "rest" IS 'Uzivatel systemu EaSys';
GRANT MEMBERSHIP IN GROUP "eas" TO "rest";
*/

       out.append(
           "EaSys_EOC - PgModeler Bridge \n -- Generovane z SQL suboru programu pgModeler pre Postgres " +
           "generatorom systemu Easys V2 - ERISS " +
           "(Easys Reduced Instructions SQL Set) \n\n\n" + 
           "/* odpoznamkovat, ked treba \n" +
           "CREATE USER \"eas\" IDENTIFIED BY '***';\n" +
           "GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, " +
           "PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO \"eas\";\n" +
           "COMMENT ON USER \"eas\" IS 'Spravca systemu EaSys';\n" +
           "GRANT GROUP TO \"eas\";\n" +
           "\n" + 
           "CREATE USER \"rest\" IDENTIFIED BY '***';\n" +
           "GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, " +
           "PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO \"rest\";\n" +
           "COMMENT ON USER \"rest\" IS 'Spravca systemu Rest';\n" +
           "GRANT MEMBERSHIP IN GROUP \"eas\" TO \"rest\";\n" +
           "*/\n\n"

/* odpoznamkovat, ked treba 
CREATE USER "eas" IDENTIFIED BY '***';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "eas";
COMMENT ON USER "eas" IS 'Spravca systemu EaSys';
GRANT GROUP TO "eas";

CREATE USER "rest" IDENTIFIED BY '***';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "rest";
COMMENT ON USER "rest" IS 'Uzivatel systemu EaSys';
GRANT MEMBERSHIP IN GROUP "eas" TO "rest";
*/
       
       );
       out.flush();

       String sLine;
       String sOriginalLine;
       String sDBevent    = "";
       String sEventState = "";
       int    leftSpaces  = 0; // pocet odsadzovaqcich medzier
       //Citanie suboru po riadkoch
       read_block:
       while ((sLine = br.readLine()) != null)   {
////           System.out.println("SLINE: ----- " + sLine);
           if (sLine.equals("")) {
             out.append(sLine + "\n");
             out.flush();
             sLine = "";
             continue read_block;
            }
           if (sLine.startsWith("--")) {
             out.append(sLine + "\n");
             out.flush();
             sLine = "";
             continue read_block;
           }
           sOriginalLine = sLine;
           sLine = sLine.trim();
           leftSpaces = sOriginalLine.length() - sLine.length();
           if (sLine.endsWith(";")) {
               sEventState = "END_EVENT";
           }
           if (sLine.startsWith("CREATE TABLE")) {
//               System.out.println("-----Tested line: " + sLine);
               sDBevent = "CREATE_TABLE";
               sEventState = "BEGIN";
           }

           if (sLine.startsWith("ALTER TABLE")) {
//               System.out.println("-----Tested line: " + sLine);
               sDBevent = "ALTER_TABLE";
               sEventState = "BEGIN";
           }
           if (sLine.startsWith("CREATE INDEX")) {
//               System.out.println("-----Tested line: " + sLine);
               sDBevent = "CREATE_INDEX";
               sEventState = "BEGIN";
           }

           if (sLine.startsWith("CREATE UNIQUE INDEX")) {
//               System.out.println("-----Tested line: " + sLine);
               sDBevent = "CREATE_INDEX";
               sEventState = "BEGIN";
           }

           if (!(sDBevent.equals(""))) {
 ////              System.out.println("-----DBevent=" + sDBevent + " testing line: " + sLine);
               
/********* CREATE TABLE - spracovanie ************************************/
           if (sDBevent.equals("CREATE_TABLE") && sLine.startsWith("CONSTRAINT")) {
               //CONSTRAINT "id_cis_vytahy" PRIMARY KEY (id_nz_cis_vytahy)
//               System.out.println("A==" + sLine);
               sLine = addQuotes(sLine, new Integer [] {1,2}, new String [] {"("," "});
//               System.out.println("BB==" + sLine);
               sLine = addQuotes(sLine, new Integer [] {2,1}, new String [] {"(",")"});
//               System.out.println("CCC==" + sLine);
               sEventState = "CONTINUE"; // pokracovat na dalsom riadku udalosti
           }
           else if (sDBevent.equals("CREATE_TABLE") && sEventState.equals("CONTINUE")) {
//               System.out.println("-----DBBBevent=" + sDBevent + " testing line: " + sLine);
               sLine = addQuotes(sLine, new Integer [] {1,1}, new String [] {"("," "});
               sEventState = "CONTINUE"; // pokracovat na dalsom riadku udalosti
           }
           else if (sDBevent.equals("CREATE_TABLE") && sEventState.equals("BEGIN")) {
               sLine = addQuotes(sLine, new Integer [] {1,3}, new String [] {"("," "});
               sEventState = "CONTINUE"; // prepnutie na spracovanie prikazu
           }
           else if (sDBevent.equals("CREATE_INDEX") && sEventState.equals("CONTINUE")) {
//               System.out.println("-----DBBBevent=" + sDBevent + " testing line: " + sLine);
               if (!(sLine.contains("USING") || sLine.startsWith("("))) 
                   sLine = addQuotes(sLine, new Integer [] {1,1}, new String [] {"("," "});
               sEventState = "CONTINUE"; // pokracovat na dalsom riadku udalosti
           }
           else if (sDBevent.equals("CREATE_INDEX") && sEventState.equals("BEGIN")) {
//               sLine = addQuotes(sLine, new Integer [] {1,3}, new String [] {"("," "});
               sLine = addQuotesAfter("ON",sLine," ");
               sEventState = "CONTINUE"; // prepnutie na spracovanie prikazu
           }
/********* ALTER TABLE - spracovanie ************************************/
           if (sDBevent.equals("ALTER_TABLE") && sEventState.equals("CONTINUE")) {
               //CONSTRAINT "id_cis_vytahy" PRIMARY KEY (id_nz_cis_vytahy)
               // ALTER TABLE rest."nz_Zazn_o_skuskach" ADD CONSTRAINT nz_cis_vytahy__fk FOREIGN KEY (id_nz_cis_vytahy_nz_cis_vytahy)
               // REFERENCES rest.nz_cis_vytahy (id_nz_cis_vytahy) MATCH FULL
               sLine = addQuotes(sLine, new Integer [] {1,2}, new String [] {"("," "});
//               sLine = addQuotes(sLine, new Integer [] {1,6}, new String [] {"("," "});
               sLine = addQuotes(sLine, new Integer [] {2,1}, new String [] {"(",")"});
               sEventState = "END_EVENT"; // pokracovat na dalsom riadku udalosti
           }
           if (sDBevent.equals("ALTER_TABLE") && sEventState.equals("BEGIN")) {
               //CONSTRAINT "id_cis_vytahy" PRIMARY KEY (id_nz_cis_vytahy)
               // ALTER TABLE rest."nz_Zazn_o_skuskach" ADD CONSTRAINT nz_cis_vytahy__fk FOREIGN KEY (id_nz_cis_vytahy_nz_cis_vytahy)
               // REFERENCES rest.nz_cis_vytahy (id_nz_cis_vytahy) MATCH FULL
               sLine = addQuotes(sLine, new Integer [] {1,3}, new String [] {"("," "});
               sLine = addQuotes(sLine, new Integer [] {1,6}, new String [] {"("," "});
               sLine = addQuotes(sLine, new Integer [] {2,1}, new String [] {"(",")"});
               sEventState = "CONTINUE"; // pokracovat na dalsom riadku udalosti
           }
           
           } // if (!(sDBevent.equals("")))
           if (sEventState.equals("END_EVENT")) {
               sDBevent = "";
               sEventState = "";
           }
           if (sLine.startsWith("COMMENT ON")) {
//               System.out.println("-----Tested line: " + sLine);
               sLine = addQuotes(sLine, new Integer [] {1,4}, new String [] {";"," "});
           }
           /*
           if (sLine.startsWith("CREATE") && sLine.contains("INDEX")) {
               sLine = addQuotesAfter("ON",sLine," ");
           }
           */
           
           //sDBevent = "";
           sLine = FnEaS.repeat('\t', leftSpaces) + sLine; // pridanie odsadenia
           sLine = sLine.replace(";",";\n"); // pridanie prazdneho riadklu medzi
           //System.out.println("SSLL> " + sLine);
           out.append(sLine + "\n");
           out.flush();
           sLine = "";
       } // while ((sLine = br.readLine()) != null)
       
       //Close the input stream
       br.close();        
    }
    
    static String addQuotes(String sLn, Integer entry[], String delim[]) {
        if (entry.length != delim.length) {
            Kernel.staticMsg("Počet elemetov pola entry[" + entry.length +
                    "] sa nerovná počtu elementov pola delim[" + 
                    delim.length + "]\n\n" + 
                    "vraciam pôvodný reťazec:\n" + sLn);
            return sLn;
        }
        String newLn = ""; // skladany retazec
        String entStr = ""; // upravovany retazec/entry
        String newEnt = ""; // upraveny retazec/entry
        String relevantDelim = ""; // rozhodujuci odelovac, prida sa k entry
        int numIter = entry.length;
        // odrezanie spracovavanej casti retazca (entStr)
        entStr = FnEaS.sEntry(entry[0], sLn, delim[0]);
        // vybere sa vzdy posledny entry ako spracovavany !!!
         for (int i = 1; i < numIter; i++) {
            entStr = FnEaS.sEntry(entry[i], entStr, delim[i]);
            relevantDelim = delim[i];
       }
        newEnt = entStr;
        newEnt.replace("\"",""); // odstranenie moznych apostrof
        newEnt = "\"" + newEnt.replace(".", "\".\"") + "\"";
        newEnt = newEnt.replace("\"\"", "\""); // 
        relevantDelim = relevantDelim.trim();
        newLn = sLn.replace(entStr + relevantDelim, newEnt + relevantDelim);
        return newLn;
    }
    
    static String addQuotesAfter(String sAfter, String sLn, String sDelim) {
           System.out.println("SIDX_LINE: ----- >> " + sAfter + "  >> " + sLn + "  >> " + sDelim);
        
        String sOut = "";
        int idxON = FnEaS.iEntryIdx(sAfter, sLn, sDelim);
        String tb = FnEaS.sEntry(idxON + 1,sLn, sDelim).trim();
        //EaS_krn.Msg("A-QTS " + sLn + " >> " + tb);
        //EaS_krn.Msg(fullLine + "\nTB+ " + idxON + " " + tb + " END_TB");
        String tbu = tb.replace("\"",""); // odstranenie moznych apostrof
        //EaS_krn.Msg("B-QTS " + sLn + " >> " + tbu);
        tbu = "\"" + tbu + "\"";
        tbu = tbu.replace(".","\".\"");
       // EaS_krn.Msg("C-QTS " + (idxON + 1) + " " + sLn + " >> " + tbu);
        sOut = FnEaS.sReplaceEntry(idxON + 1, sLn, tbu, sDelim);
       // EaS_krn.Msg("D-QTS " + (idxON + 1) + " " + sLn + " >> " + tbu);
        return sOut;
    }
}
