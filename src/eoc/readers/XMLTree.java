/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eoc.readers;

/**
 *
 * @author rvanya
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/** Given a filename or a name and an input stream,
 *  this class generates a JTree representing the
 *  XML structure contained in the file or stream.
 *  Parses with DOM then copies the tree structure
 *  (minus text and comment nodes).
 */

public class XMLTree extends JTree {
    DefaultTreeModel myTreeModel;
    TreeNode         myTreeNode;
    public XMLTree() {
    }
  public void create_XMLTree(String filename) throws FileNotFoundException {
       // vlozenie prazdneho tree-model-u do objektu

    this.setModel(myTreeModel);
      //    super(makeRootNode(in));
      System.out.println("making tree for xml: " + filename);
     // DefaultMutableTreeNode node;
      /*
      for (int i = 0; i < this.getRowCount(); i++) {
          TreePath paths = this.getPathForRow(i);
          System.out.println("current deleting path: " + i);
          node = (DefaultMutableTreeNode) (paths.getLastPathComponent());
               System.out.println("removing node: " + node.toString());
               try {
                   myTreeModel.removeNodeFromParent(node);
               }
               catch (Exception ex) {
                   System.out.println("CHYBA: " + ex.getMessage());
               }    
    }  
      */
   // System.out.println("Tree for xml vas removed1a: " + filename);
   // System.out.println("Tree for xml vas removed2a: " + filename);
    FileInputStream in = new FileInputStream(new File(filename));
    myTreeNode = makeRootNode(in);
    myTreeModel = new DefaultTreeModel(myTreeNode);
    this.setModel(myTreeModel);
    System.out.println("Nuber of created paths: " + this.getRowCount());
  }
  // This method needs to be static so that it can be called
  // from the call to the parent constructor (super), which
  // occurs before the object is really built.
  
  private /* static */ DefaultMutableTreeNode makeRootNode(InputStream in) {
    try {
      // Use JAXP's DocumentBuilderFactory so that there
      // is no code here that is dependent on a particular
      // DOM parser. Use the system property
      // javax.xml.parsers.DocumentBuilderFactory (set either
      // from Java code or by using the -D option to "java").
      // or jre_dir/lib/jaxp.properties to specify this.
      DocumentBuilderFactory builderFactory =
        DocumentBuilderFactory.newInstance();
      DocumentBuilder builder =
        builderFactory.newDocumentBuilder();
      // Standard DOM code from hereon. The "parse"
      // method invokes the parser and returns a fully parsed
      // Document object. We'll then recursively descend the
      // tree and copy non-text nodes into JTree nodes.
      Document document = builder.parse(in);
      document.getDocumentElement().normalize();
      Element rootElement = document.getDocumentElement();
      DefaultMutableTreeNode rootTreeNode =
        buildTree(rootElement);
      return(rootTreeNode);
    } catch(Exception e) {
      String errorMessage =
        "Error making root node: " + e;
      System.err.println(errorMessage);
      e.printStackTrace();
      return(new DefaultMutableTreeNode(errorMessage));
    }
  }

  private /* static */ DefaultMutableTreeNode buildTree(Element rootElement) {
    // Make a JTree node for the root, then make JTree
    // nodes for each child and add them to the root node.
    // The addChildren method is recursive.
    DefaultMutableTreeNode rootTreeNode =
      new DefaultMutableTreeNode(treeNodeLabel(rootElement));
    addChildren(rootTreeNode, rootElement);
    return(rootTreeNode);
  }
  private /* static */ void addChildren
                       (DefaultMutableTreeNode parentTreeNode,
                        Node parentXMLElement) {
    // Recursive method that finds all the child elements
    // and adds them to the parent node. We have two types
    // of nodes here: the ones corresponding to the actual
    // XML structure and the entries of the graphical JTree.
    // The convention is that nodes corresponding to the
    // graphical JTree will have the word "tree" in the
    // variable name. Thus, "childElement" is the child XML
    // element whereas "childTreeNode" is the JTree element.
    // This method just copies the non-text and non-comment
    // nodes from the XML structure to the JTree structure.
    
    NodeList childElements =
      parentXMLElement.getChildNodes();
    for(int i=0; i<childElements.getLength(); i++) {
      Node childElement = childElements.item(i);
      if (!(childElement instanceof Text ||
            childElement instanceof Comment)) {
        DefaultMutableTreeNode childTreeNode =
          new DefaultMutableTreeNode
            (treeNodeLabel(childElement));
        parentTreeNode.add(childTreeNode);
        addChildren(childTreeNode, childElement);
      }
    }
  }

  // If the XML element has no attributes, the JTree node
  // will just have the name of the XML element. If the
  // XML element has attributes, the names and values of the
  // attributes will be listed in parens after the XML
  // element name. For example:
  // XML Element: <blah>
  // JTree Node:  blah
  // XML Element: <blah foo="bar" baz="quux">
  // JTree Node:  blah (foo=bar, baz=quux)

  private /* static */ String treeNodeLabel(Node childElement) {
    NamedNodeMap elementAttributes =
      childElement.getAttributes();
    String treeNodeLabel = childElement.getNodeName();
    if (elementAttributes != null &&
        elementAttributes.getLength() > 0 
            ) {
      treeNodeLabel = treeNodeLabel + " (";
      int numAttributes = elementAttributes.getLength();
      for(int i=0; i<numAttributes; i++) {
        Node attribute = elementAttributes.item(i);
        if (i > 0) {
          treeNodeLabel = treeNodeLabel + ", ";
        }
        treeNodeLabel =
          treeNodeLabel + attribute.getNodeName() +
          "=" + attribute.getNodeValue();
      }
      treeNodeLabel = treeNodeLabel + ")";
    }
    // Robo - pridanie comment-u
    if (treeNodeLabel.equals("comment")) {
    System.out.println("comment===" +  childElement.getTextContent());   
    }
    return(treeNodeLabel);
  }
}