/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rvanya
 */
public class del_file {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        delInCycle(); 
    }
    
    static private void delInCycle() {
        while (1==1) {
        try {
            Path fileToDeletePath = Paths.get("c:\\OTP_VYP\\Vypis OTP (no reply).eml");
            Files.delete(fileToDeletePath);
        } catch (IOException ex) {
            continue;
        }
        }
    }
}
