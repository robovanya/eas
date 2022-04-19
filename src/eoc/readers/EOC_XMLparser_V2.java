package eoc.readers;

import java.io.File;
import javax.swing.JTree;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class EOC_XMLparser_V2 {

public JTree dTree = new JTree();

public static void main(String argv[]) {
    parse_XML("d:/Pnl_licencia.xml");
  }

  public static String parse_XML(String sFileName) {
   Short shNodeType = 0; // pre catch-exception blok   
   try {
      File fXmlFile = new File(sFileName);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document xmlDoc = dBuilder.parse(fXmlFile);
      xmlDoc.getDocumentElement().normalize();
       
      System.out.println("Root element súboru "  + sFileName + ":" 
              + xmlDoc.getDocumentElement().getNodeName());
      //System.out.println("Typ suboru : " + xmlDoc.getDocumentElement().getAttribute("Popis"));

      NodeList nodeList = xmlDoc.getElementsByTagName("*");

      // premenne - pouzite v slucke
      Node nNode; // aktualny uzol
      
      for (int i = 0; i < nodeList.getLength(); i++) {
 
         nNode = nodeList.item(i);
         String nodeName = nNode.getNodeName();
         System.out.println("aktual node == " + nodeName);
         if (nNode.getParentNode().getNodeName().equals("table")) {
         System.out.println("aktual node parent == " 
             + nNode.getParentNode().getNodeName()+ "->" + nNode.getParentNode().getAttributes().getNamedItem("name"));
         }
         shNodeType = nNode.getNodeType();
         
         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
             System.out.println("AKTUAL node == " + nodeName);
             Element eElement = (Element) nNode;
             NamedNodeMap attrs = eElement.getAttributes();
             
             for (int ia = 0; ia < attrs.getLength(); ia++) {
                 System.out.println("attrs == " + attrs.item(ia));
             }
          }
          else {
              System.out.println("ERROR -- nNode.getNodeType() = " + nNode.getNodeType());
          }
       }
    } catch (Exception e) {
        System.out.println("ERROR - nNode.getNodeType() ->> = " + shNodeType);
	e.printStackTrace();
        return "ERROR - nNode.getNodeType() ->> = " + shNodeType;
    }
      return "";  // V pripade problemov sa vrzti sprava s popisom chyby
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