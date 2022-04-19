/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package system.perm;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class PermTreeNode extends DefaultMutableTreeNode 
    /* implements system.perm.IPermSource */ {
    private PermDefinition myPermDef;
//    private PermDefinition superPermDef;
//    private PermDefinition userPermDef;
    JTree myTree;
    Pnl_usrperms pnl_perms;
    private boolean bValidated = false;
    private boolean bUpdated = false;
    PermTreeNode(Pnl_usrperms pnl, JTree mTree, PermDefinition pDef) {
        //userObject = nodeLabel;
        pnl_perms = pnl;
        myTree = mTree;
        this.setUserObject(pDef);
        myPermDef = pDef;
        //this.set
    }
    /****
    public PermDefinition getPermDef() {
        return permDef;
    }
    * *****/
/*
    public void setPermDef(PermDefinition permDf) {
        this.permDef = permDf;
    }
    */
    
    public String getPermLabel() {
        return myPermDef.getPermLabel();
    }

    public void setValidated(boolean bTstd) {
        this.bValidated = bTstd;
    }
    
    public boolean getValidated() {
        return bValidated;
    }
/*
    public void setPermLabel(String permLabel) {
        permDef.setPermLabel(permLabel);
    }
*/
    
/*    
    public String getPermProgObject() {
        return permDef.getPermProgObject();
    }

    public void setPermProgObject(int parentPermProgObjectID, String permProgObject) {
        permDef.setPermProgObject(parentPermProgObjectID, permProgObject);
    }
*/
    public boolean isPermNew() {
        return myPermDef.isPermNew();
    }

    public void setPermNew(boolean permNew) {
        if (myPermDef.isPermNew() != permNew) {
            bUpdated = true;
            myPermDef.setPermNew(permNew);
        }
   }

    public boolean isPermUpdate() {
        return myPermDef.isPermUpdate();
    }

    public void setPermUpdate(boolean permUpdate) {
        if (myPermDef.isPermUpdate() != permUpdate) {
            bUpdated = true;
            myPermDef.setPermUpdate(permUpdate);
        }
    }

    public boolean isPermDelete() {
        return myPermDef.isPermDelete();
    }

    public void setPermDelete(boolean permDelete) {
        if (myPermDef.isPermDelete() != permDelete) {
            bUpdated = true;
            myPermDef.setPermDelete(permDelete);
        }
    }

    public boolean isPermPrint() {
        return myPermDef.isPermPrint();
    }

    public void setPermPrint(boolean permPrint) {
        if (myPermDef.isPermPrint() != permPrint) {
            bUpdated = true;
            myPermDef.setPermPrint(permPrint);
        }
    }

    public String getPermRestriction() {
        return myPermDef.getPermRestriction();
    }

    public void setPermRestriction(String permRestriction) {
        myPermDef.setPermRestriction(permRestriction);
    }

    @Override
    public Object getUserObject() {
        return super.getUserObject(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUserObject(Object userObject) {
        ////System.out.println("PERMTREENODE_SETUSEROBJECT" + userObject.getClass().getSimpleName());
        PermDefinition pdfn = null;
        if (userObject instanceof PermTreeNode) {
            pdfn = ((PermTreeNode) userObject).myPermDef;
        }
        else 
           pdfn =  (PermDefinition) userObject;
        myPermDef = pdfn;
        userObject = myPermDef;
        super.setUserObject(userObject); //To change body of generated methods, choose Tools | Templates.
    }
public boolean isRecursive() {
    return pnl_perms.getRecursive();
}
public void printNodeLabels() {
    if (pnl_perms.getRecursive()) {
        System.out.println("WALKING_IS_ON");
        printNodeLabels(this);
    }
    else
        System.out.println("WALKING_IS_OFF");
}
    
public void printNodeLabels(PermTreeNode node) {

    int childCount = node.getChildCount();
    int levels = node.getLevel();
    System.out.println(FnEaS.repeat(" ",levels) +  "WALKING ON: " + node.getPermLabel());
    System.out.println(FnEaS.repeat(" ", levels) + node.getPermLabel());

    for (int i = 0; i < childCount; i++) {

        PermTreeNode childNode = (PermTreeNode) node.getChildAt(i);
        if (childNode.getChildCount() > 0) {
            printNodeLabels(childNode);
        } else {
            levels = childNode.getLevel();
            System.out.println(FnEaS.repeat(" ", levels) + childNode.getPermLabel());
        }

    }

    levels = node.getLevel();
    System.out.println(FnEaS.repeat("_", levels) + node.getPermLabel());

}

public void propagateNodeOnSubTree() {
    if (pnl_perms.getRecursive()) {
        System.out.println("PROPAGATING_IS_ON");
        propagateNodeOnSubTree(this);
    }
}
    
public void propagateNodeOnSubTree(PermTreeNode node) {
    boolean bN = node.isPermNew();
    boolean bU = node.isPermUpdate();
    boolean bD = node.isPermDelete();
    boolean bP = node.isPermPrint();
    int childCount = node.getChildCount();
    int levels = node.getLevel();
    System.out.println("P_ON" + FnEaS.repeat(" ",levels) +  "PROPAGATING " + bN + bU + bD + bP + " ON: " + node.getPermLabel());

    for (int i = 0; i < childCount; i++) {
        PermTreeNode childNode = (PermTreeNode) node.getChildAt(i);
        childNode.setPermNew(bN);
        childNode.setPermUpdate(bU);
        childNode.setPermDelete(bD);
        childNode.setPermPrint(bP);
        childNode.commit();
        if (childNode.getChildCount() > 0) propagateNodeOnSubTree(childNode);
        /*
        else {
            levels = childNode.getLevel();
            System.out.println("P_ON" + FnEaS.repeat(" ", levels) + childNode.getPermLabel());
        }
        */

    }
    levels = node.getLevel();
    System.out.println("P_ON" + FnEaS.repeat("_", levels) + node.getPermLabel());

}
public boolean commit() {
    System.out.println("PermTreeNode: " + myPermDef.getPermLabel() + " commit:" + bUpdated);
    //if (bUpdated) {
        //my
        return myPermDef.commit();
        // return true;
    //}
   // return false;
}
/* STARE VERZIE - ZATIAL NEMAZEM, KVOLI PREHLADNOSTI4
    @Override
    public PermDefinition getPermDefinition() {
        return permDef;
    }

    @Override
    public void setPermDefinition(PermDefinition permDf) {
        permDef = permDf; 
        setUserObject(permDef);
    }
*/
/*
    @Override
    public PermDefinition getSuperPermDefinition() {
        return superPermDef;
    }

    @Override
    public void setSuperPermDefinition(PermDefinition permDf) {
        superPermDef = permDf;
    }

    @Override
    public PermDefinition getUserPermDefinition() {
        return userPermDef;
    }

    @Override
    public void setUserPermDefinition(PermDefinition permDf) {
        userPermDef = permDf;
        setUserObject(permDef);
    }
    */
    public PermDefinition getPermDefinition() {
        return myPermDef;
    }

    public void setPermDefinition(PermDefinition permDf) {
        myPermDef = permDf; 
        setUserObject(myPermDef);
    }
    
    public boolean isUpdated() {
        return bUpdated;
    }

    public void initForUser(String usrType, String usrName) {
        myPermDef.initForUser(usrType, usrName);
        this.setUserObject(myPermDef);
    }
}
