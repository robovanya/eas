/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;
//import TestPkg.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author rvanya
 */
public class EOC_array_sorter {

    /**
     * @param args the command line arguments
     */
public static void main(String[] args) throws Exception {
        ArrayList<String[]> listOfStringArrays = new ArrayList<String[]>();
        listOfStringArrays.add(new String[] {"c","3","5"});
        listOfStringArrays.add(new String[] {"b","1","6"});
        listOfStringArrays.add(new String[] {"a","2","9"});
        Collections.sort(listOfStringArrays,new Comparator<String[]>() {
            public int compare(String[] strings, String[] otherStrings) {
                return strings[1].compareTo(otherStrings[1]);
            }
        });
        for (String[] sa : listOfStringArrays) {
            System.out.println(Arrays.toString(sa));
        }
        /* prints out 
          [a, b, c]
          [m, n, o]
          [x, y, z]
        */ 

    }    
}
