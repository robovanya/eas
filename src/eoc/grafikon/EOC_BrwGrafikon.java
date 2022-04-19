// 
package eoc.grafikon;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * @version 1.0 14/12/2014
 * 
 * @author  Robo Vanya
 */

public class EOC_BrwGrafikon  extends eoc.widgets.PObject implements IEOC_graf_obj {
  ArrayList<EOC_graf>     grafList = new ArrayList<>(); // pole graf-objektov
  Pnl_GrafikonHeader      headerPanel;
  Pnl_GrafikonFooter      footerPanel;
  EOC_graf                currentGraf; // aktualne zvoleny graf-objekt
  EOC_GrafTable           currentHeaderTable; 
  EOC_GrafTable           currentDimensionTable;
  JViewport               rowHeaderViewport;  
  JViewport               dimensionViewport;  
  JScrollPane             rowHeaderScrollPane;
  JScrollPane             dimensionScrollPane;
////  HeaderCellRenderer      hdrCellRenderer;
////  DimensionCellRenderer   dimCellRenderer;
  private JComboBox<String>       jcbGrafikons;
  private JComboBox<String>       jcbDimensions;
  boolean             bRendering = false; // sluzi na obmedzenie cinnosti
                                          // ked sa sklada struktura dimenzii
  EOC_PnlGrafikon     pnlContainer;
  private String      mySelectionMode = "STD";
  private String      myRendererMode = "HH:mm:ss";
 
  public EOC_BrwGrafikon() {
      headerPanel = new Pnl_GrafikonHeader();
      footerPanel = new Pnl_GrafikonFooter();
      rowHeaderScrollPane = new JScrollPane();
      dimensionScrollPane = new JScrollPane();
      dimensionViewport = dimensionScrollPane.getViewport();
      dimensionViewport.setBackground(new java.awt.Color(204, 204, 225));
      rowHeaderViewport = new JViewport();
      rowHeaderViewport.setView(rowHeaderScrollPane);
      rowHeaderScrollPane.setAutoscrolls(true);
      rowHeaderViewport.setBackground(new java.awt.Color(180, 180, 200));  
////      hdrCellRenderer = new HeaderCellRenderer();
////      dimCellRenderer = new DimensionCellRenderer();
      headerPanel.setBrwGrafikon(this);      
      footerPanel.setBrwGrafikon(this);      
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dimensionScrollPane)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
            .addComponent(footerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dimensionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
      
  } // constructor - end
  
  
  public void  setSelectionMode(String mode, boolean must) {
      if (mySelectionMode.equals(mode) && (!must)) return;
      if (mode.equals("2DI")) {
         currentDimensionTable.getColumnModel().setColumnSelectionAllowed(false); 
         currentDimensionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);       
      }
      else if (mode.equals("3DI")) {
         currentDimensionTable.getColumnModel().setColumnSelectionAllowed(true); 
         currentDimensionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);       
      }
      else { // mode.equals("STD")
         mode = "STD"; // ked mode <> 2DI/3DI tak sa prepise na STD
         currentDimensionTable.getColumnModel().setColumnSelectionAllowed(false); 
         currentDimensionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      }
      mySelectionMode = mode;
  }  
  
  public void  setRendererMode(String mode, boolean must) {
      if (myRendererMode.equals(mode) && (!must)) return;
      if (mode.equals("HH:mm:ss")) {
          currentDimensionTable.setRendererMode(mode);
         //currentDimensionTable.getColumnModel().setColumnSelectionAllowed(false); 
         //currentDimensionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);       
      }
      else if (mode.equals("mm:ss")) {
          currentDimensionTable.setRendererMode(mode);
         //currentDimensionTable.getColumnModel().setColumnSelectionAllowed(true); 
         //currentDimensionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);       
      }
      else if (mode.equals("ss")) {
         currentDimensionTable.setRendererMode(mode);
         //currentDimensionTable.getColumnModel().setColumnSelectionAllowed(false); 
         //currentDimensionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      }
      else { // mode.equals("<none>")
         mode = "<none>"; // 
         currentDimensionTable.setRendererMode(mode);
      }
      myRendererMode = mode;
  }  
  
  public boolean addGraf(EOC_graf grf) {
      //QQQ sem patri test, ci cPrimaryKey je sucastou oRowHeaders
      //QQQ sem patri test, ci cGrafName uz neexistuje v ArrayListe grafList

      // jcbDimensions = jcbDims;
      // test existencie grafikonu
      for (EOC_graf egf : grafList) {
          if (egf.getcGrafName().equals(grf.getcGrafName() /*cGrafName*/)) {
              krn.Message("Grafikón s názvom '" + grf.getcGrafName() /*cGrafName*/ + "' už existuje !");
              return false;
          }
      }
      /* v objekte graf sa vytvori columnModel a Table model pre 
       * vytvarany graf-objekt plus tri fake-dimenzia objekty na testovanie,
       * ktore tiez obsahuju svoj columnModel a tableModel 
       * a mozu pridavat svoje podriadene dimenzie
       ********************************************************************/
      grafList.add(grf);
      grf.setParentBrowser(this);
      grf.setDimensionCombo(jcbDimensions);
      //grf.initialize();
      ////headerPanel.setHeaderLabel(grf.getcGrafLabel() /*cGrafLabel*/);
      ////krn.OutPrintln("GFADD1=pk==" + sPrimaryKeyFieldName);
      
          // graf.createFakeDimension();
      return true;
  }
  
  public ArrayList<EOC_graf> getGrafList() {
      return grafList;
  }
  
  public void setContainer (EOC_PnlGrafikon pnl) {
      pnlContainer = pnl;
      headerPanel.setContainer(pnl);
      footerPanel.setContainer(pnl);
  }
  
  public void setGrafCombo (JComboBox<String> cbg) {
      jcbGrafikons = cbg;
  }

  public void setDimensionCombo (JComboBox<String> cbd) {
      jcbDimensions = cbd;
  }

  @Override
  public void setRendering (boolean renderingState, String propagateMode,
          Object caller, boolean ignoreCaller) {
      bRendering = renderingState;
      // grafikon-browser ma nad sebou iba objekt panela grafikonu 
      if (propagateMode.equals("UP") || propagateMode.equals("ALL")) {
         if (pnlContainer != null) {
             // posiela signal kontaineru len ked treba
             if (ignoreCaller && ((Object) pnlContainer != caller))
                 pnlContainer.setRendering(renderingState, "NO", this, true);
         }
      }
      // odosielanie signalu podriadenim graf-objektom
      if (propagateMode.equals("DOWN") || propagateMode.equals("ALL")) {
         for (EOC_graf egf : grafList) {
             egf.setRendering(renderingState, "DOWN", this, true);
         }
      }

  }
  
  private void refresh() {
      // NEMAZAT!  Bude pouzite pre funkcionality refreshovania udajov
      // currentGraf.refresh();
      //DbUtils.
  }

public void setHeaderTable(EOC_GrafTable tbl) {
    currentHeaderTable = tbl;
    rowHeaderViewport.setView(currentHeaderTable);
    dimensionScrollPane.setRowHeaderView(rowHeaderViewport);
    dimensionScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,currentHeaderTable.getTableHeader());
}
  
public void setDimensionTable(EOC_GrafTable tbl) {
    currentDimensionTable = tbl;
    dimensionViewport.setView(currentDimensionTable);
}
  
  /* inicializacia tabuliek hdrTable a dimTable pre vybrany graf-objekt
   * pomocou volania metody initiateTable v danom objekte
   *************************************************************************/
public void selectGrafikon(int iGrafIdx) {
    currentGraf = grafList.get(iGrafIdx);
    ////krn.OutPrintln("Selecting currentGraf: " + currentGraf.getcGrafLabel());
    currentGraf.initialize();
    // currentGraf.makeSelected() spatne nastavi hodnoty premennych tabuliek
    currentGraf.makeSelected();
    headerPanel.setHeaderLabel(currentGraf.getcGrafLabel());

    /* tabulky su naplnene columnModelmi a tableModelmi vybrateho grafikonu
     * nasleduje uprava vyzoru obrazovky podla rozmerov aktualnych tabuliek
     ***********************************************************************/
    this.doLayout();
  } // selectGrafikon(int iGrafIdx)

public void reselectCurrentGraficon() {
    currentGraf.makeSelected();
}
  
private void grafikonWideRemoveRow(int rw) {
    if (currentGraf != null) currentGraf.grafikonWideRemoveRow(rw);
}

public JScrollPane getDimScrollPane() {
    return dimensionScrollPane;
}

public JViewport getRowHeaderViewport() { 
    return rowHeaderViewport;
}

public EOC_graf getSelectedGrafikon() {
    return currentGraf;  
}

  protected void changeSelection(int rowIdx, boolean isDimension) {
      
    int rowSelectedIndex = currentHeaderTable.getSelectedRow();
    int dimSelectedIndex = currentDimensionTable.getSelectedRow();
      if (isDimension) {
          if (dimSelectedIndex < 0) return;
          // krn.OutPrintln("changeSelection--dimSelectedIndex==" + dimSelectedIndex);
          currentHeaderTable.setRowSelectionInterval(dimSelectedIndex,dimSelectedIndex);
      } else {
          // krn.OutPrintln("changeSelection--rowSelectedIndex==" + rowSelectedIndex);
          if (rowSelectedIndex < 0) return;
          currentDimensionTable.setRowSelectionInterval(rowSelectedIndex,rowSelectedIndex);
          for (EOC_Gdimension gdim: currentGraf.getDimensionList()) {
              if (gdim.getTable() != currentDimensionTable) {
                  gdim.getTable().setRowSelectionInterval(rowSelectedIndex,rowSelectedIndex);
              }
          }
      }
  }

private void DEPRACEDgrafikonWideAddRow(Object[] oHeaderRowValues, Object[] oDimRowValues) {
    /******************    
        // transformacia pola riadku hlavicky na ArrayList-objekt
        rowHeaderRow = new ArrayList<>(Arrays.asList(oHeaderRowValues));
        // Vytvorenie pola dimenzie s prazdnymi hodnotami, 
        // ked oDimRowValues nie je zadany (==null)
        if (oDimRowValues==null) {
            // vytvori sa prazdne pole hodnot riadku dimenzie
            // buduci riadok dimenzie
            oDimRowValues = new Object[dimensionTableModel.getColumnCount()];
            // pridanie hodnot do riadku dimenzie
            for (int j = 0; j < dimensionTableModel.getColumnCount(); j++) {
                oDimRowValues[j] = "";
            }
        }
        // transformacia pola riadku dimenzie na ArrayList-objekt
        dimensionRow = new ArrayList<>(Arrays.asList(oDimRowValues));

        // tu uz existuju platne ArrayList-objekty pre riadok rowHeaders   
        // aj dimenziu, nasleduje ich spojenie do jedneho ArrayList-objektu
        fullRow = FnEaS.joinArrayLists(rowHeaderRow, dimensionRow);

        // spojenie dvoch poli
           int iDimColumnCount;
           if (oDimRowValues!=null)
               iDimColumnCount = oDimRowValues.length;
           else 
               iDimColumnCount = dimensionTableModel.getColumnCount();
           Object[] oFullRowValues = new Object[oHeaderRowValues.length + iDimColumnCount];
           for (int i = 0; i < oHeaderRowValues.length; i++) {
               oFullRowValues[i] = oHeaderRowValues[i];
           }
           for (int i = 0; i < iDimColumnCount; i++) {
               if (oDimRowValues!=null)
                   oFullRowValues[i + oHeaderRowValues.length] = oDimRowValues[i];
               else
                   oFullRowValues[i + oHeaderRowValues.length] = "";
           }
           if (oDimRowValues == null) { // vytvori sa pole prazdnych udajov
               oDimRowValues = new Object[iDimColumnCount];
               for (int i = 0; i < iDimColumnCount; i++) {
                   oDimRowValues[i] = "";
               }
           }
  ********/
           /*
        krn.OutPrintln(
                "\n\n###grafikonWideAddRow >> "  
                + "\n rowHeaderTableModel.getColumnCount()=" + rowHeaderTableModel.getColumnCount()
                + "\n dimensionTableModel.getColumnCount()=" + dimensionTableModel.getColumnCount()
                + "\n rowHeaderTableModel.getRowCount()="    + rowHeaderTableModel.getRowCount()
                + "\n dimensionTableModel.getRowCount()="    + dimensionTableModel.getRowCount()
                + "\n rowHeaderColumnModel.getColumnCount()=" + rowHeaderTable.getColumnModel().getColumnCount()
                + "\n rowHeaderColumnModel.firsdtCount()=" + rowHeaderTable.getColumnModel().getColumn(1)
                + "\n  oFullRowValues=" + oFullRowValues.length
                + "\n  oHeaderRowValues=" + oHeaderRowValues.length
                + "\n  oDimRowValues=" + oDimRowValues.length
                + "\n iDimColumnCount=" + iDimColumnCount
        );
        */
   //     bRendering = true;
//           krn.OutPrintln("0adding headerRow: " + rowHeaderTableModel.getRowCount() + ">>" + oHeaderRowValues.length + " >> " + Arrays.toString(oHeaderRowValues));
//           rowHeaderTableModel.insertRow(rowHeaderTableModel.getRowCount(),
//                   oFullRowValues /*oHeaderRowValues*/ );
//           krn.OutPrintln("1adding dimensionRow:" + Arrays.toString(oDimRowValues));
  //      bRendering = true;
 //       dimensionTableModel.insertRow(dimensionTableModel.getRowCount(), oDimRowValues);
//        krn.OutPrintln("2adding dimensionRow:" + Arrays.toString(oDimRowValues));
//        rowHeaderData.add(rowHeaderRow); // pridanie riadku do tabulky hlaviciek
//        dimensionData.add(dimensionRow); // pridanie riadku do tabulky dimenzie
//        allData.add(fullRow); // pridanie riadku do spojovacej tabulky
}
    
public void hideDimensions() {
 //   dimensionScrollPane.setVisible(false);
    dimensionViewport.setView(null);
}   

public void hideIdxColumns(boolean bHide) {
    for (EOC_graf g: grafList) {
        g.hideIdxColumns(bHide);
        g.getTable().headerWidthChanged();
    }
    repaint();
}
public void hideMasterKey(boolean bHide) {
    for (EOC_graf g: grafList) {
        g.hideMasterKey(bHide);
        g.getTable().headerWidthChanged();
    }
    repaint();
}
public void reviveCells(boolean bRevive) {
    for (EOC_graf g: grafList) {
        g.reviveCells(bRevive);
    }
    repaint();
}

}

