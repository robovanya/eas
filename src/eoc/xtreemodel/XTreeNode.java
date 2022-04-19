/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.xtreemodel;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author rvanya
 */
public class XTreeNode extends DefaultMutableTreeNode {

  protected boolean isVisible;

  public XTreeNode() {
    this(null);
  }

  public XTreeNode(Object userObject) {
    this(userObject, true, true);
  }

  public XTreeNode(Object userObject, boolean allowsChildren,
      boolean isVisible) {
    super(userObject, allowsChildren);
    this.isVisible = isVisible;
  }

  public TreeNode getChildAt(int index, boolean filterIsActive) {
    if (!filterIsActive) {
      return super.getChildAt(index);
    }
    if (children == null) {
      throw new ArrayIndexOutOfBoundsException("node has no children");
    }

    int realIndex = -1;
    int visibleIndex = -1;
    Enumeration e = children.elements();
    while (e.hasMoreElements()) {
      XTreeNode node = (XTreeNode) e.nextElement();
      if (node.isVisible()) {
        visibleIndex++;
      }
      realIndex++;
      if (visibleIndex == index) {
        return (TreeNode) children.elementAt(realIndex);
      }
    }

    throw new ArrayIndexOutOfBoundsException("index unmatched");
    //return (TreeNode)children.elementAt(index);
  }

  public int getChildCount(boolean filterIsActive) {
    if (!filterIsActive) {
      return super.getChildCount();
    }
    if (children == null) {
      return 0;
    }

    int count = 0;
    Enumeration e = children.elements();
    while (e.hasMoreElements()) {
      XTreeNode node = (XTreeNode) e.nextElement();
      if (node.isVisible()) {
        count++;
      }
    }
    return count;
  }

  public void setVisible(boolean visible) {
    this.isVisible = visible;
  }

  public boolean isVisible() {
    return isVisible;
  }

}
