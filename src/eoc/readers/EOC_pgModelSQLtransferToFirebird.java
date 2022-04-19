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
public class EOC_pgModelSQLtransferToFirebird {
    static String owner = "eas."; // toto sa likviduje z retazcov
    static ArrayList<String> aTables = new ArrayList<>(); // sem sa ulozia nazvy tabuliek
    static ArrayList<String> aGenerators = new ArrayList<>(); // sem sa ulozia potrebne generatory
    static ArrayList<String> aTriggers =  new ArrayList<>();   // sem sa ulozia potrebne triggery
    static String createdTriggers = ""; // obchadza viacnasobne vytvorenie triggrov
    static String sOutputFileTbls;
    static String sOutputFileGens;
    static String sOutputFileTrgs;
    public static void main(String[] args) throws IOException {
       String sqlFile = 
//          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20170904\\eas_UTF-8_Quoted_sybase.sql"; 
          "d:\\EaSys\\Documentation\\Schemes\\PgModeler\\20170926\\eas_CP1250_Quoted_sybase.sql"; 
//       String sOutputFile    = sqlFile.replace(".sql","_cp1250.sql");
//       String sFinalizedFile = sqlFile.replace("_sybase.sql","_firebird.sql");
       sOutputFileTbls = sqlFile.replace("_sybase.sql","_firebird_tbls.sql");
       sOutputFileGens = sqlFile.replace("_sybase.sql","_firebird_gens.sql");
       sOutputFileTrgs = sqlFile.replace("_sybase.sql","_firebird_trgs.sql");
       transformFileToFullLines(sqlFile); 
      //convertFile(sOutputFile,sFinalizedFile,"UTF-8","windows-1250");
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
            System.out.println("SLINE==" + sLine);
        writer.write(sLine);
        writer.newLine();
    }
    writer.flush();
    }
    
    private static String resolve_DEFAULT_AUTOINCREMENT(String fullLine, String aktLine) {
        fullLine = fullLine.replace( "^<BR>^","\0");
        fullLine = fullLine.replace( "CREATE","\0");
        fullLine = fullLine.replace( "ALTER","\0");
        fullLine = fullLine.replace( "TABLE","\0").trim();
        ////System.out.println("FuUULINE::" + fullLine + " AktLINN ::" + aktLine);
        String aktTable = FnEaS.sEntry(1, fullLine, "(").trim();
        String pureAktTable;
       //// System.out.println("AKKTA:" + aktTable);
        while (aktTable.contains("  ")) {
           aktTable = aktTable.replace("  "," "); // cistenie dvojmedzier
        }
        while (aktTable.contains("\t")) {
           aktTable = aktTable.replace("\t"," "); // cistenie tabelatorov
        }
        aktTable = FnEaS.sEntry(3, aktTable, " ");
        //aktTable = FnEaS.sEntry(1, aktTable, ";").trim();
        aktTable = aktTable.replace("\".\"",".");
        aktTable = aktTable.replace('"','\0').trim();
        if (aktTable.contains("."))
            pureAktTable = FnEaS.sEntry(2,aktTable,".").trim();
        else
            pureAktTable = aktTable;
        String aktField = FnEaS.sEntry(1, aktLine.trim(), " ").trim();
        aktField = aktField.replace('"','\0').trim();
//        System.out.println( /* "resolving: " + fullLine + " +++AKTLN=" + aktLine  
//                + */ " +++" + aktTable + "+++" + pureAktTable + "+++" + aktField); 
        // pridanie generatora
        //DELETE FROM RDB$GENERATORS WHERE RDB$GENERATOR_NAME = '<GeneratorName>';
        aGenerators.add(
             "SET TERM !! ;" + "^<BR>^" +
             "EXECUTE BLOCK AS BEGIN" + "^<BR>^" + 
             "if (not exists(select 1 from rdb$generators where rdb$generator_name = " +
             "'g_" + aktField + "'^<BR>^" +
             ")) then" + "^<BR>^" + 
             "execute statement 'CREATE GENERATOR \"g_" + aktField + "\";\';" + "^<BR>^" +
             "END!!" + "^<BR>^" +
             "SET TERM ; !!" + "^<BR>^" +
/* OLD-CODE    
             "^<BR>^CREATE GENERATOR \"g_" // + pureAktTable + "_" 
                   + aktField + "\";^<BR>^" + 
*/                
             "SET GENERATOR \"g_" /* + pureAktTable + "_" */
                   + aktField + "\" TO 0" + ";^<BR>^^<BR>^");
//        if (FnEaS.iLookup(pureAktTable, createdTriggers,",") == 0) {
        if (!createdTriggers.contains(pureAktTable + ",")) {
        createdTriggers = createdTriggers + pureAktTable + ",";
        aTriggers.add(       
             "set term !! ;" + "^<BR>^" +
             "CREATE TRIGGER \"" + pureAktTable + "_BI\" FOR \"" 
                + aktTable.replace(".","$") + "\"^<BR>^" +
             "ACTIVE BEFORE INSERT POSITION 0" + "^<BR>^" +
             "AS" + "^<BR>^" +
             "BEGIN" + "^<BR>^" +
             "if (NEW.\"" + aktField + "\" is NULL) then NEW.\"" +aktField + 
              "\" = GEN_ID(\"g_" + aktField +"\", 1);"
              + "^<BR>^" + "END!!"+ "^<BR>^" 
              + "set term ; !!"
              + "^<BR>^" + "^<BR>^");   
        }
        return "";
    }

    private static String resolve_CREATE_INDEX(String fullLine) {
       //System.out.println("resolve_CREATE_INDEX >> " + fullLine);
       String resolution;
       resolution = fullLine.replace("DESC"," ");
       if (fullLine.contains("ASC")) {
           resolution = fullLine.replace("ASC"," ");
           // bud je defenicia takato
           resolution = resolution.replace("CREATE INDEX","CREATE ASC INDEX");
           // ... alebo je defenicia takato
           resolution = resolution.replace("CREATE UNIQUE INDEX",
                                           "CREATE UNIQUE ASC INDEX");
       }
       else if (fullLine.contains("DESC")) {
           resolution = fullLine.replace("DESC"," ");
           // bud je defenicia takato
           resolution = resolution.replace("CREATE INDEX","CREATE DESC INDEX");
           // ... alebo je defenicia takato
           resolution = resolution.replace("CREATE UNIQUE INDEX",
                                           "CREATE UNIQUE DESC INDEX");
       }
       
       String beginn = resolution.substring(0, resolution.indexOf("(") + 1);
       String idxFlds = resolution.substring(resolution.indexOf("(") + 1);
              idxFlds = idxFlds.substring(0, idxFlds.indexOf(")"));
              System.out.println("FILDSSSSSSSSS:" + idxFlds);
       String[] idxFlda = idxFlds.split(",");
       String[] idxFld  = idxFlds.split(",");
       String[] idxFldb = idxFlds.split(",");
              System.out.println("FILDSSSSSSSSS_ARRAYA:" + idxFld.length);
              System.out.println("FILDSSSSSSSSS_ARRAYA:" + Arrays.deepToString(idxFld));
       for (int i = 0; i < idxFld.length; i++) {
           idxFlda[i] = idxFlda[i].replace("^<BR>^","XBX");
           idxFlda[i] = idxFlda[i].replace("\n ","XBX");
           idxFld[i] = idxFld[i].replace("^<BR>^","");
           idxFld[i] = idxFld[i].replace("\n ","XBX");
           idxFld[i] = idxFld[i].trim();
           idxFldb[i] = idxFld[i];
           if (!idxFldb[i].startsWith("\"")) idxFldb[i] = "\"" + idxFldb[i]; 
           if (!idxFldb[i].endsWith("\"")) idxFldb[i] = idxFldb[i] + "\"";
       }
       System.out.println("FILDSSSSSSSSS_ARRAYB:" + Arrays.deepToString(idxFlda));
       System.out.println("FILDSSSSSSSSS_ARRAYB:" + Arrays.deepToString(idxFld));
       System.out.println("FILDSSSSSSSSS_ARRAYB:" + Arrays.deepToString(idxFldb));
       System.out.println("RESOLUTTAA:" + resolution);
       resolution = beginn;
       for (int i = 0; i < idxFldb.length; i++) {
           resolution = resolution + idxFlda[i].replace(idxFld[i], idxFldb[i]);
           System.out.println("REPLACINGG:" + idxFld[i] + " WITH " + idxFldb[i]); 
           if (i < idxFldb.length - 1) resolution = resolution + ","; 
////           resolution.replace(idxFld[i], idxFldb[i]);
       }
       resolution = resolution + ");\n ";
       resolution = resolution.replace("XBX","^<BR>^"); 
       System.out.println("IDXXXSS:" + Arrays.deepToString(idxFld));
       System.out.println("RESOLUTTBB:" + resolution);
       
       return resolution;
    }
    
    private static String resolve_CREATE_DOMAIN(String fullLine) {
       String resolution;
       resolution = fullLine.trim();
       while (resolution.contains("  ")) {
           resolution = resolution.replace("  "," "); // cistenie dvojmedzier
       }
       String domainName = FnEaS.sEntry(3, resolution, " ");

       if (!(domainName.startsWith("\""))) { 
           resolution = resolution.replace(domainName,
                   "\"" + domainName + "\"");
       }

       return resolution;
    }
    
    public static void transformFileToFullLines(String sInFile) 
            throws FileNotFoundException, IOException {
////       String sOutFile = sOutputFileTbls;
       FileInputStream fstream = new FileInputStream(sInFile);
       BufferedReader br  = new BufferedReader(new InputStreamReader(fstream,"cp1250"));
       BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                               new FileOutputStream(sOutputFileTbls), "cp1250"));
//dAT OUTPUTT DO CP1250 kua -idem skladat puzzle.
       out.append(
           "-- Generovane z SQL suboru programu pgModeler pre Firebird\n\n\n");
       out.flush();

       String sLine;
       String sFullLine = "";
       String sAfterCreateTable = ""; // pridavok k jednej tabulke
       String sAllAfterCreateTable = ""; // pridavok k vsetky tabulkam,
                                        // dolozi sa na koniec SQL kodu
       boolean bFirstTabDef = false;
       //Citanie suboru po riadkoch
       read_block:
       while ((sLine = br.readLine()) != null)   {
           if (sLine.contains("CREATE TABLE")) bFirstTabDef = true;
           if (sLine.trim().startsWith("CREATE TABLE")) {
               String sTbl = FnEaS.sEntry(3, sLine.trim(), " ");
               sTbl = sTbl.replace("\"", "");
               sTbl = sTbl.replace("eas.", "");
               sTbl = sTbl.replace("(", "");
               aTables.add(sTbl);
           }
           if (!bFirstTabDef) continue read_block;
           if (sLine.trim().startsWith("*/")) {
              sFullLine = sFullLine + sLine;
              System.out.println (/* "FULLLINE: " + */ sFullLine);
              out.append(sFullLine + "\n");
              out.flush();
              sFullLine = "";
              continue read_block;
           }
           
           sLine = sLine.replace("nextval('tblmasterid'::regclass)", "AUTOINCREMENT");
           sLine = sLine.replace(owner," ");
           sLine = sLine.replace("eas$"," ");
           // do sAfterCreateTable sa ulozia generatory sekvencii a triggre
           if (sLine.contains("CREATE TABLE")) sAfterCreateTable = "";
           
           // hladanie a riesenie frazy DEFAULT AUTOINCREMENT
           if (sLine.contains("DEFAULT AUTOINCREMENT")) {
               sAfterCreateTable = resolve_DEFAULT_AUTOINCREMENT(sFullLine, sLine);
               sLine = sLine.replace("DEFAULT AUTOINCREMENT", " ");
           }

           // koniec SQL prikazu, pripadny novy privesok sa prida k existujucim
           if (sLine.contains(";")) {
               sAllAfterCreateTable = sAllAfterCreateTable + sAfterCreateTable;
               sAfterCreateTable = "";
           }    
           
           if (!sFullLine.equals("")) {
              sFullLine = sFullLine + "^<BR>^";
           }
           if (sLine.contains("NOT NULL")) {
           ////    bassz ra Robko. Holnap is van nap. :-)
               sLine = sLine.replace("NOT NULL", "");
               sLine = sLine.replace(",", "");
               sLine = sLine + " NOT NULL,";
           }
           /*
           */
           sFullLine = sFullLine + " " + sLine;
           if (sLine.startsWith("--")) {
              if (sLine.startsWith("-- ddl")) {
                 sFullLine = "";
                 continue read_block;
              }
              sFullLine = "";
              continue read_block;
           }
           if (sFullLine.endsWith(";")) {
              if (badLine(sFullLine)) {
                 sFullLine = "";
                 continue read_block;
           }
        
           //System.out.println ("=== " + sFullLine);
           sFullLine = eraseUnnecessarySequences(sFullLine).trim(); 
           while (sFullLine.contains("  ")) {
              sFullLine = sFullLine.replace("  "," "); // cistenie dvojmedzier
           }

           if (sFullLine.contains("CREATE") && sFullLine.contains("INDEX")) {
               sFullLine = resolve_CREATE_INDEX(sFullLine);
           }

           if (sFullLine.contains("CREATE") && sFullLine.contains("DOMAIN")) {
               sFullLine = resolve_CREATE_DOMAIN(sFullLine);
           }
           
           if (sFullLine.contains("CREATE") && sFullLine.contains("SEQUENCE")) {
               sFullLine = "CREATE SEQUENCE " 
                           + FnEaS.sEntry(3, sFullLine, " ") + ";";
           }
           sFullLine = sFullLine.replace("^<BR>^","\n");
              // "^<BR>^"
              //System.out.println ("ER- " + sFullLine);
              sFullLine = sFullLine.replace("eas\".\"", "eas$");
              sFullLine = sFullLine.replace("eas$","");
              System.out.println (/* "FULLLINE: " + */ sFullLine);
              sFullLine = sFullLine.replace(";", ";\n\r\n\r");
              //out.append(sFullLine + "\n\r");
              out.append(sFullLine + "\n");
             out.flush();
             sFullLine = "";
          }
       } // while ((sLine = br.readLine()) != null) // read_block:
      sFullLine = sFullLine + sAllAfterCreateTable;
    //  --majd jovo heten :-))
// 2017-9-20      sFullLine = sFullLine + generators + triggers;
      sFullLine = sFullLine.replace("^<BR>^","\n\r");
      out.append(sFullLine + "\n\r");
      out.flush();
      System.out.println("TABBLESS:");
      out.append("CREATE ROLE \"eas\";\n\n");
       out.flush();

      String sGrant;
      for (int x = 0; x< aTables.size(); x++) {
           String sTbl = aTables.get(x);
//           System.out.println(sTbl);
           sGrant = "GRANT ALL ON \""  + sTbl + "\" TO \"eas\";\n";
            out.append(sGrant);     
            out.flush();
       }
       sGrant = "GRANT \"eas\" TO \"easys\";\n" +
                "GRANT \"eas\" TO \"rest\";\n" +
                "GRANT \"eas\" TO \"rest_user\";\n";
       out.append(sGrant);     
       out.flush();
       br.close();  
       out.close();
       System.out.println (/* "FULLLINE: " + */ sFullLine);

       
       out = new BufferedWriter(new OutputStreamWriter(
                               new FileOutputStream(sOutputFileGens), "cp1250"));
       out.append(
           "-- Generovane z SQL suboru programu pgModeler pre Firebird\n\n\n");
       out.flush();
       for (int x = 0; x< aGenerators.size(); x++) {
            out.append(aGenerators.get(x).replace("^<BR>^","\n") + "\n");     
            out.flush();
       }
       out.close();
              
       out = new BufferedWriter(new OutputStreamWriter(
                               new FileOutputStream(sOutputFileTrgs), "cp1250"));
       out.append(
           "-- Generovane z SQL suboru programu pgModeler pre Firebird\n\n\n");
       out.flush();
       for (int x = 0; x< aTriggers.size(); x++) {
            out.append(aTriggers.get(x).replace("^<BR>^","\n") + "\n");     
            out.flush();
       }
       out.close();
              
    }
    
    public static String eraseUnnecessarySequences(String sLn) {
       String newLn = sLn; 
       newLn = newLn.replace("USING btree", "");
       newLn = newLn.replace("NULLS LAST", "");
       newLn = newLn.replace("NOT DEFERRABLE", "");
       newLn = newLn.replace("DEFERRABLE", "");
       newLn = newLn.replace("ON DELETE NO ACTION", "");
       newLn = newLn.replace("INITIALLY IMMEDIATE", "");
       newLn = newLn.replace("NULLS FIRST", "");
       newLn = newLn.replace("DOMAIN rest.", "DOMAIN ");
       newLn = newLn.replace("OWNED BY NONE", "");
       newLn = newLn.replace("MATCH FULL", "");
       newLn = newLn.replace("ON DELETE RESTRICT", ""); // u FB automaticky
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
             (sLn.contains("CREATE TABLE") && sLn.contains("ana.")) ||
             (sLn.contains("ALTER TABLE") && sLn.contains("ana.")) ||
             (sLn.contains("COMMENT ON COLUMN") && sLn.contains("ana.")) ||
             (sLn.contains("COMMENT ON TABLE") && sLn.contains("ana.")) ||
             sLn.contains("CREATE SEQUENCE")
        ) {
            return true;   
        }
        return false;
    }
    
}
