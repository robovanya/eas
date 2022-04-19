/* EOC_pgModelerSQLtransferToSybase.java
 *======================================================
 *
 * Program je sucastou systemu EaSys V1
 */

package eoc.readers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class EOC_pgModelSQLtransferToSybase {

    public static void main(String[] args) throws IOException {
        Kernel.staticMsg("PO VYGENEROVANI VYRIESIT VIACNASOBNY AUTOINCREMENT PROBLEM !!!");
        //exit(0);
       String sqlFile = 
//          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\TRANSFORM\\easys_20150115_orig.sql"; 
//          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20150311\\easys.sql"; 
//          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20150903\\easys_cp1250_Quoted.sql"; 
          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20170914\\eas_UTF-8_Quoted.sql"; 
       String sOutputFile    = sqlFile.replace(".sql","_fullLines.sql");
       String sFinalizedFile = sqlFile.replace(".sql","_sybase.sql");
       sFinalizedFile = sFinalizedFile.replace("UTF-8","cp1250");
        transformFileToFullLines(sqlFile,sFinalizedFile); 
       transformFileToFullLines(sqlFile,sOutputFile); 
       //convertFile(sOutputFile,sFinalizedFile,"windows-1250","windows-1250");
         convertFile(sOutputFile,sFinalizedFile,"UTF-8","windows-1250");
          File f = new File(sOutputFile);
          f.delete();
          System.exit(0);
          
          
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
            System.out.println("WRRTT:" + sLine);
        writer.write(sLine);
        writer.newLine();
        writer.flush();
    }
    }
    
    public static void transformFileToFullLines(String sInFile, String sOutFile) 
            throws FileNotFoundException, IOException {
       FileInputStream fstream = new FileInputStream(sInFile);
       BufferedReader br  = new BufferedReader(new InputStreamReader(fstream));
       BufferedWriter out = new BufferedWriter(new FileWriter(sOutFile));

       out.append(
           "-- Generovane z SQL suboru programu pgModeler pre Sybase\n " + 
           "-- generatorom systemu Easys V2 - ERISS" +
           "(Easys Reduced Instructions - SQL Set) \n\n\n" + 
           "/* odpoznamkovat, ked treba \n" +
           "CREATE USER \"rest\" IDENTIFIED BY 'rest';\n" +
           "GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, " +
           "PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO \"rest\";\n" +
           "COMMENT ON USER \"rest\" IS 'Správca systému Rest';\n" +
           "\n" +
           "CREATE USER \"eas\" IDENTIFIED BY 'eas';\n" +
           "GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, " +
           "PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO \"eas\";\n" +
           "COMMENT ON USER \"eas\" IS 'Konto pre jadro systemu Easys';\n" +
           "\n" +
           "CREATE USER \"easys\" IDENTIFIED BY 'easys';\n" +
           "GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, " +
           "PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO \"easys\";\n" +
           "COMMENT ON USER \"easys\" IS 'Správca systému Easys';\n" +
           "\n" +
           "GRANT GROUP TO \"eas\";\n" +
           "GRANT MEMBERSHIP IN GROUP \"eas\" TO \"rest\";\n" +
           "GRANT MEMBERSHIP IN GROUP \"eas\" TO \"easys\";\n" +
           "*/\n\n"
/* odpoznamkovat, ked treba 
CREATE USER "nzbd" IDENTIFIED BY 'nzbd';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "nzbd";
COMMENT ON USER "nzbd" IS 'Uzivatel systemu Nzbd';

CREATE USER "eas" IDENTIFIED BY 'eas';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "eas";
COMMENT ON USER "eas" IS 'Kernel systemu Easys';

CREATE USER "easys" IDENTIFIED BY 'easys';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "easys";
COMMENT ON USER "easys" IS 'Spravca systemu Easys';

 GRANT GROUP TO "eas";
 GRANT MEMBERSHIP IN GROUP "eas" TO "nzbd";
 GRANT MEMBERSHIP IN GROUP "eas" TO "easys";
*/
       );
       out.flush();

       String sLine;
       String sFullLine = "";

       //Citanie suboru po riadkoch
       read_block:
       while ((sLine = br.readLine()) != null)   {
           if (!sFullLine.equals("")) {
              sFullLine = sFullLine + "^<BR>^";
           }
           sFullLine = sFullLine + " " + sLine;
           if (sLine.startsWith("--")) {
              if (sLine.startsWith("-- ddl")) {
                 sFullLine = "";
                 continue read_block;
              }
              //krn.OutPrintln (sFullLine);
              sFullLine = "";
              continue read_block;
           }
           if (sFullLine.endsWith(";")) {
              if (badLine(sFullLine)) {
                  System.out.println(">>>>badLine::" + sFullLine);
                 sFullLine = "";
                 continue read_block;
              }
        
              //krn.OutPrintln ("=== " + sFullLine);
              sFullLine = eraseUnnecessarySequences(sFullLine); 
              sFullLine = sFullLine.replace("^<BR>^","\n");
              // "^<BR>^"
              //krn.OutPrintln ("ER- " + sFullLine);
              out.append(sFullLine + "\n");
             out.flush();
             sFullLine = "";
          }
       }
       //Close the input stream
       br.close();        
    }
    
    public static String eraseUnnecessarySequences(String sLn) {
       String newLn; 
       newLn = sLn.replace("nextval('tblmasterid'::regclass)", "AUTOINCREMENT");
       newLn = newLn.replace("USING btree", "");
       newLn = newLn.replace("NULLS LAST", "");
       newLn = newLn.replace("NOT DEFERRABLE", "");
       newLn = newLn.replace("DEFERRABLE", "");
       newLn = newLn.replace("ON DELETE NO ACTION", "");
       newLn = newLn.replace("INITIALLY IMMEDIATE", "");
       newLn = newLn.replace("NULLS FIRST", "");
       newLn = newLn.replace("DOMAIN nzbd.", "DOMAIN ");
       newLn = newLn.replace("DOMAIN \"nzbd\".", "DOMAIN ");
       newLn = newLn.replace("OWNED BY NONE", "");
       //tu odstranit AUTOINCREMENT, OKREM DEFINICIE S masterkyom tabulky
       //  sLn.replace("", "");
       return newLn; 
    }
    
    public static boolean badLine(String sLn) {
        // vynechanie nepotrebnych riadkov
        if ( sLn.contains("check_function_bodies") ||
             sLn.contains("CREATE ROLE") ||
             sLn.contains("COMMENT ON ROLE") ||
             sLn.contains("SCHEMA") ||
             sLn.contains("SET search_path TO") ||
             sLn.contains("COMMENT ON DOMAIN") ||
             (sLn.contains("CREATE TABLE") && sLn.contains("ana\".")) ||
             (sLn.contains("ALTER TABLE") && sLn.contains("ana\".")) ||
             (sLn.contains("COMMENT ON COLUMN") && sLn.contains("ana\".")) ||
             (sLn.contains("COMMENT ON TABLE") && sLn.contains("ana\".")) ||
             (sLn.contains("CREATE TABLE") && sLn.contains("ana.")) ||
             (sLn.contains("ALTER TABLE") && sLn.contains("ana.")) ||
             (sLn.contains("COMMENT ON COLUMN") && sLn.contains("ana.")) ||
             (sLn.contains("COMMENT ON TABLE") && sLn.contains("ana.")) ||
             sLn.contains("CREATE USER") ||
             sLn.contains("GRANT DBA")  ||
             sLn.contains("COMMENT ON USER") ||
             sLn.replace("^<BR>^","").trim().equals(";")
 //            sLn.contains("tblmasterid")
                
) {
            return true;   
        }
        return false;
    }
    
}
