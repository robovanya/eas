package eoc.readers;

import static eoc.readers.EOC_XMLparser_SPP.getTagTextContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
 
public class EOC_XMLparser {
 
public static void main(String argv[]) {
   Short shNodeType = null;
   try {
      File fXmlFile = new File("d:/fa_orig.xml");
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(fXmlFile);
      doc.getDocumentElement().normalize();
 
      System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
      System.out.println("Typ suboru : " + doc.getDocumentElement().getAttribute("Popis"));

      NodeList nList = doc.getElementsByTagName("*");
 
      for (int temp = 0; temp < nList.getLength(); temp++) {
 
         Node nNode = nList.item(temp);
         String nodeName = nNode.getNodeName();
         shNodeType = nNode.getNodeType();
         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
             Element eElement = (Element) nNode;
             String sNName = nNode.getNodeName();
             System.out.println("NODE_TYPE:" + nNode.getNodeType() + " " + sNName + " : " 
                     + getTagTextContext(eElement,sNName));
             if (nodeName.equals("Hlavicka")) {
//               System.out.println("Popis : " + eElement.getAttribute("Popis"));
                 System.out.println("\n" + nodeName + "\n------------------------");
                 System.out.println("Header_OPBEL : " + getTagTextContext(eElement,"Header_OPBEL"));
                 System.out.println("Header_PARTNER : " + getTagTextContext(eElement,"Header_PARTNER"));
                 System.out.println("Header_VKONT : " + getTagTextContext(eElement,"Header_VKONT"));
                 System.out.println("Header_FAEDN : " + getTagTextContext(eElement,"Header_FAEDN"));
                 System.out.println("Header_DODANIE : " + getTagTextContext(eElement,"Header_DODANIE"));
                 System.out.println("Header_BLDAT : " + getTagTextContext(eElement,"Header_BLDAT"));
                 System.out.println("Header_AB : " + getTagTextContext(eElement,"Header_AB"));
                 System.out.println("Header_BIS : " + getTagTextContext(eElement,"Header_BIS"));
             }
             if (nodeName.equals("TabulkaUhrada")) {
                 System.out.println("\n" + nodeName + "\n------------------------");
                 System.out.println("CIAS_CMS : " + getTagTextContext(eElement,"CIAS_CMS"));
                 System.out.println("AMS : " + getTagTextContext(eElement,"AMS"));
                 System.out.println("GS_CIAS_PREDPOK_SPOT2 : " + getTagTextContext(eElement,"GS_CIAS_PREDPOK_SPOT2"));
                 System.out.println("GS_CIAS_SPOT_DAN_HLAV : " + getTagTextContext(eElement,"GS_CIAS_SPOT_DAN_HLAV"));
                 System.out.println("GS_CIAS_SPOT_DAN : " + getTagTextContext(eElement,"GS_CIAS_SPOT_DAN"));
                 System.out.println("GS_CIAS_SUMAB2 : " + getTagTextContext(eElement,"GS_CIAS_SUMAB2"));
                 System.out.println("GS_CIAS_STPRZ : " + getTagTextContext(eElement,"GS_CIAS_STPRZ"));
                 System.out.println("GS_CIAS_DPHSK : " + getTagTextContext(eElement,"GS_CIAS_DPHSK"));
                 System.out.println("GS_CIAS_CELKOM : " + getTagTextContext(eElement,"GS_CIAS_CELKOM"));
             }
             if (nodeName.equals("TabulkaSpotreba")) {
                 System.out.println("\n" + nodeName + "\n------------------------");
                 System.out.println("CIAS_PORC : " + getTagTextContext(eElement,"CIAS_PORC"));
                 System.out.println("CIAS_PORCCIAS_DO : " + getTagTextContext(eElement,"CIAS_DO"));
                 System.out.println("CIAS_OD : " + getTagTextContext(eElement,"CIAS_OD"));
                 System.out.println("CIAS_CMS : " + getTagTextContext(eElement,"CIAS_CMS"));
                 System.out.println("CIAS_AMS0 : " + getTagTextContext(eElement,"CIAS_AMS0"));
                 System.out.println("CIAS_AMS : " + getTagTextContext(eElement,"CIAS_AMS"));
                 System.out.println("CIAS_AMS1 : " + getTagTextContext(eElement,"CIAS_AMS1"));
                 System.out.println("CIAS_PREDPOK_SPOT : " + getTagTextContext(eElement,"CIAS_PREDPOK_SPOT"));
                 System.out.println("CIAS_SUMAB : " + getTagTextContext(eElement,"CIAS_SUMAB"));
                 System.out.println("CIAS_SP_DAN : " + getTagTextContext(eElement,"CIAS_SP_DAN"));
                 System.out.println("CIAS_SUM_RIAD : " + getTagTextContext(eElement,"CIAS_SUM_RIAD"));
             }
          }
          else {
              System.out.println("ERROR -- nNode.getNodeType() = " + nNode.getNodeType());
          }
       }
    } catch (Exception e) {
        System.out.println("ERROR - nNode.getNodeType() ->> = " + shNodeType);
	e.printStackTrace();
    }
  }
  
  public static String getTagTextContext(Element eElement,String tagName) {
      String val = null;
      if (!(eElement==null)) {
          try {
          val = eElement.getElementsByTagName(tagName).item(0).getTextContent();
          }
          catch (Exception e) {
              val = " !!! Netusim co to jeeee !!!";
          }
      }
      return val;
  }
}