/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class EASpropertiesFile {
    private String fileName; // subor s ulozenymi udajmi
    private File propFile; // subor s ulozenymi udajmi
//    private Scanner scanner;
    private BufferedReader buffRdr;
    private BufferedWriter buffWrt;
    private DefaultTableModel tblMdl; // tabulka s udajmi
    private String myFileName;
    private final HashMap<String,String> hmFileContents = new HashMap<>();
    private Kernel krn; 
    private String inOutCP = "Cp1250"; // ; "Cp1250" / "utf8";
    public EASpropertiesFile(String fileName, Kernel kr)
        throws UnsupportedEncodingException, FileNotFoundException, IOException {
        krn = kr;
        propFile = initializeForFile(fileName);
        /*
        String cpProp = getProperty("IO_codepage");
    System.out.println("------------------EASSPROPERRFILE:::::" + System.getProperty("sun.jnu.encoding") + "  cpProp=" + cpProp);
    if (cpProp == null) {
        inOutCP = System.getProperty("sun.jnu.encoding");
        setProperty("IO_codepage", inOutCP);
    }
    else inOutCP = cpProp;
        */
    }
    
    public File initializeForFile(String flName) 
        throws UnsupportedEncodingException, FileNotFoundException, IOException {
        fileName = flName;
///        Kernel.Msg("11");
        System.out.print("initializing properties file: " + fileName + " ... ");
        propFile = new File(fileName);
////        Kernel.Msg("11a");
        File parent = propFile.getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }   
////        Kernel.Msg("12");
        boolean newFile = (!propFile.exists());
////        Kernel.Msg("12a");
////        Kernel.Msg("12b");
        if (newFile) {
            buffWrt = new BufferedWriter(new FileWriter(propFile));
            buffWrt.append("#EASYS property file\n#\n#\n");
            buffWrt.flush();
            buffWrt.close();
        }
        System.out.print(propFile.exists()?" OK\n":" FAILED\n");
////       Kernel.Msg("12c");
        buffRdr = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(propFile),inOutCP));
////        Kernel.Msg("12d");
////      scanner = new Scanner(myFile);
        myFileName = fileName;
////        Kernel.Msg("13");
        return propFile;
    }
    
    public String getFileName() {
        return myFileName;    
    }
    
    private void loadFile() {
        hmFileContents.clear();
        String sLine;
        int iLineNum = 0;
        int i;
        String prop;
        String val;
        read_block:
        try {
        buffRdr = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(propFile),inOutCP));

            while ((sLine = buffRdr.readLine()) != null) {
                sLine.trim();
                iLineNum++;
////                System.out.println("LOODLN:" + sLine);
                prop = "";
                val = "";
                if (sLine.startsWith("$")) {
                    i = sLine.indexOf("=");
                    if (i > -1) {
                        prop = sLine.substring(0, i);
                        val  = sLine.substring(i + 1);
                        
                    }
                    else {
                        val = "#BAD! " + sLine;
                        prop  = "MARK" + iLineNum;
                    }
                }
                else  {
                    val = sLine;
                    prop  = "MARK" + iLineNum;
                }
 ////               System.out.println("PUTTIG PROPERTY_TO_HashMap: " + prop + " VALUE: " + val);
                hmFileContents.put(prop, val);
            }
        } catch (IOException ex) {
            Logger.getLogger(EASpropertiesFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeFile() {
        try {
            buffWrt = new BufferedWriter(new FileWriter(propFile));
//            buffWrt.write("###\n");
        } catch (IOException ex) {
            Logger.getLogger(EASpropertiesFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Map.Entry<String, String> entry : hmFileContents.entrySet()) {
           String propName  = entry.getKey();
           String propValue = entry.getValue();
            try {
                if (propName.startsWith("$"))
                    buffWrt.append(propName + "=" + propValue + "\n");
                else
                    buffWrt.append(propValue + "\n");
                
                buffWrt.flush();
            } catch (IOException ex) {
                Logger.getLogger(EASpropertiesFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            buffWrt.close();
        } catch (IOException ex) {
            Logger.getLogger(EASpropertiesFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        public boolean setProperty(String propName, String propValue) {
        //try {
            //System.out.println("FILE: " + propFile.getAbsolutePath() + "  SETTING PROPERTY: " + propName + " TO VALUE: " + propValue);
            // toto by malo byt debug-out
            
            loadFile();
            hmFileContents.put("$" + propName, propValue);
            writeFile();
            /*
            buffWrt.append("$" + propName + "=" + propValue + "\n");
            buffWrt.flush();
            // buffWrt.
        } catch (IOException ex) {
            Kernel.Msg("Chyba pri zápise užívateľského attribútu.\n\n" + ex.getMessage());
            return false;
            //Logger.getLogger(EASpropertiesFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        return true;
    }

    public String getProperty(String propName) {
        // hmFileContents.containsKey(propValue);
        /*
        String propVal = ""; // default je prazdny retazec
        String sLine;
        String fullPropName = "$" + propName + "=";
        // buffRdr.
        read_block:
        while ((sLine = buffRdr.readLine()) != null) {
            if (sLine.startsWith(fullPropName)) {
                propVal = sLine.substring(fullPropName.length());
            }
        }
        */
        loadFile();
        String propVal = ""; // default je prazdny retazec
////        System.out.println("getProperty()++> " + propName + " ==> " + propVal);
        propVal = hmFileContents.get("$" + propName);
        return propVal;
    }
    
  /*  
        int lineNum = 0;
    while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        lineNum++;
        if(<some condition is met for the line>) { 
            System.out.println("ho hum, i found it on line " +lineNum);
        }
    }
*/
}
