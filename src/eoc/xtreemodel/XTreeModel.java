/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.xtreemodel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import eoc.xtreemodel.XTreeNode;

/**
 *
 * @author rvanya
 */
public class XTreeModel extends DefaultTreeModel {

  protected boolean filterIsActive;

  public  XTreeModel(TreeNode root) {
    this(root, false);
  }

  public  XTreeModel(TreeNode root, boolean asksAllowsChildren) {
    this(root, false, false);
  }

  public  XTreeModel(TreeNode root, boolean asksAllowsChildren,
      boolean filterIsActive) {
    super(root, asksAllowsChildren);
    this.filterIsActive = filterIsActive;
  }

  public void activateFilter(boolean newValue) {
    filterIsActive = newValue;
  }

  public boolean isActivatedFilter() {
    return filterIsActive;
  }

  public Object getChild(Object parent, int index) {
    if (filterIsActive) {
      if (parent instanceof XTreeNode) {
        return ((XTreeNode) parent).getChildAt(index,
            filterIsActive);
      }
    }
    return ((TreeNode) parent).getChildAt(index);
  }

  public int getChildCount(Object parent) {
    if (filterIsActive) {
      if (parent instanceof XTreeNode) {
        return ((XTreeNode) parent).getChildCount(filterIsActive);
      }
    }
    return ((TreeNode) parent).getChildCount();
  }

}
