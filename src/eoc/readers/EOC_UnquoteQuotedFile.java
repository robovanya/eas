/* EOC_pgModelerSQLtransferToFirebird.java
 *======================================================
 *
 * Program je sucastou systemu EaSys V1
 */

package eoc.readers;

import system.FnEaS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author rvanya
 */
public class EOC_UnquoteQuotedFile {
    static String sUnquotedOutputFile;
    public static void main(String[] args) throws IOException {
       String quotedSqlFile = 
//          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20170904\\eas_UTF-8_Quoted_sybase.sql"; 
          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20170926\\eas_CP1250_Quoted_firebird_tbls.sql"; 
       sUnquotedOutputFile = quotedSqlFile.replace(".sql","_U.sql");
//       String sOutputFile    = sqlFile.replace(".sql","_cp1250.sql");
//       String sFinalizedFile = sqlFile.replace("_sybase.sql","_firebird.sql");
//       sOutputFileTbls = sqlFile.replace("_sybase.sql","_firebird_tbls.sql");
//       sOutputFileGens = sqlFile.replace("_sybase.sql","_firebird_gens.sql");
//       sOutputFileTrgs = sqlFile.replace("_sybase.sql","_firebird_trgs.sql");
      // transformFileToFullLines(quotedSqlFile); 
      convertFile(quotedSqlFile,sUnquotedOutputFile);
      quotedSqlFile = quotedSqlFile.replace("_tbls","_gens");
      sUnquotedOutputFile = sUnquotedOutputFile.replace("_tbls","_gens");
      convertFile(quotedSqlFile,sUnquotedOutputFile);
      quotedSqlFile = quotedSqlFile.replace("_gens","_trgs");
      sUnquotedOutputFile = sUnquotedOutputFile.replace("_gens","_trgs");
      convertFile(quotedSqlFile,sUnquotedOutputFile);
    }
    
    public static void convertFile(String sInF,String sOuF) throws IOException {
       final Path src = Paths.get(sInF);
       final Path dst = Paths.get(sOuF);
       BufferedReader reader = 
          Files.newBufferedReader(src,Charset.forName("windows-1250"));
       BufferedWriter writer = 
          Files.newBufferedWriter(dst,Charset.forName("windows-1250"));
       //Citanie suboru po riadkoch
       String sLine;
       read_block:
        while ((sLine = reader.readLine()) != null) {
            sLine = convertToUnquoted(sLine);
            System.out.println("SLINE==" + sLine);
        writer.write(sLine);
        writer.newLine();
    }
    writer.flush();
    }
    
    public static String convertToUnquoted(String sLine) {
        String curChar = "";
        String curWord = null;
        String sNewLine = sLine; // toto sa bude menit
        int finder = 0;
        System.out.println("OLDD: " + sLine);
        if (sLine.trim().equals("")) return sLine;
        for (int i = 0; i < sLine.length(); i++) {
            curChar = sLine.substring(i, i + 1);
            System.out.println(i + " -- " + curChar);
            if (curChar.equals("\"")) finder++;
            if (finder == 1 && curWord == null) {
                curWord = "";
            }
            if (finder > 0) curWord = curWord + curChar;
            if (finder == 2) {
                System.out.println("CW: " + curWord);
                sNewLine = sNewLine.replace(curWord, curWord.substring(1, curWord.length() - 1).toUpperCase());
                finder = 0;
                curWord = null;
            }
        }
        System.out.println("NEWW: " + sNewLine);
        return sNewLine;
    }
    
    
}
