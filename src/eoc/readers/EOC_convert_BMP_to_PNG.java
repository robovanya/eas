package eoc.readers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * konvertovanie suborov typu BMP na typ PNG
 * 
 * @author rvanya
 */
public class EOC_convert_BMP_to_PNG {
   static String myFile   = null;
   public static void main(String[] args) throws IOException {
   File folder = new File("d:/FirmV30/obj/robo");
   File[] listOfFiles = folder.listFiles();
   x_block:
   for (int i = 0; i < listOfFiles.length; i++) {
      myFile = listOfFiles[i].getAbsolutePath();
      if (!myFile.toLowerCase().endsWith(".bmp") && !myFile.toLowerCase().endsWith(".BMP")) {
          continue x_block;
      }
      if (listOfFiles[i].isFile()) {
         File inputFile = new File(myFile);  
         //nacitanie subor BMP do BufferedImage objektu
         BufferedImage image = ImageIO.read(inputFile);

        //vytvorenie objektu pre vystupny subor
        File output = null;
        if (myFile.endsWith(".BMP")) { 
            output = new File(myFile.replace(".BMP",".png"));
        }
        if (myFile.endsWith(".bmp")) { 
             output = new File(myFile.replace(".bmp",".png"));  
        }
        System.out.println(inputFile.getName() +  " transforming to png");
        //Write the image to the destination as a JPG  
        ImageIO.write(image, "png", output);
      } else if (listOfFiles[i].isDirectory()) {
        System.out.println("Directory " + listOfFiles[i].getName());
      }
    }
      }

}
