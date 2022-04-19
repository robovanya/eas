/*
 * Program je s��as�ou syst�mu EaSys V1
 * Each line should be prefixed with  * 
 */

package eoc.grafikon;

import system.Kernel;
import eoc.ColumnHeaderRenderer;
import eoc.calendar.XD_calendar_event;
import eoc.dbdata.DBtableColumn;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import system.FnEaS;

/**
 *
 * @author rvanya
 */
public class EOC_GrafTable extends JTable {

    private boolean                 isColumnWidthChanged = false; 
    private boolean                 bRendering = false;
    private DefaultTableModel       myTableModel;
    private DefaultTableColumnModel myColumnModel;  
    private JTableHeader            myTableHeader;
    private ColumnHeaderRenderer    colHdrRenderer;
    private EOC_graf                myGraf;
    private boolean                 isDimension;
    private ArrayList<Object[]>     alSharedIDX;
    private List<RowSorter.SortKey> sortKeys;
    private MouseAndKeyListener     mouseAndKeyListener;
    volatile private boolean        ctrlIsDown;
    private EOC_GrafTable           gThis;
    private String                  sRendererMode = "HH:mm:ss";
    boolean bLivingCells           = false;
    Kernel krn;
    XD_calendar_event calendarUpdater = null; 

//########################################################################
private class TableHeaderMouseListener extends MouseAdapter {
    @Override
    public void mouseReleased(MouseEvent e)
    {
        // Pri spusteni tlacitka mysi sa testuje, ci sa zmenila sirka stlpca
        if(getColumnWidthChanged())
        {
            int iNewHdrWidth = sumColumnWidths();
            if (iNewHdrWidth != myTableHeader.getWidth()) {
                myGraf.setHeaderWidth(iNewHdrWidth);
            }
            // Reset flag-u v tabulke
           setColumnWidthChanged(false);
        }
    }
} // private class TableHeaderMouseListener extends MouseAdapter
//*************************************************************************
private class MouseAndKeyListener implements MouseListener, KeyListener {

/*
public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (x > 0 && a[x - 1][y] == 0) {
                shape.move(-20, 0);
                x--;
            }
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (x < 9 && a[x + 1][y] == 0) {
                shape.move(+20, 0);
                x++;
            }
        }

    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                if (x > 0 && a[x - 1][y] == 0) {
                    shape.move(-20, 0);
                    x--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (x < 9 && a[x + 1][y] == 0) {
                    shape.move(+20, 0);
                    x++;
                }
                break;
        }
    }
*/
        @Override
        public void mouseClicked(MouseEvent e) {
//            if (e.isPopupTrigger()) {
            if (SwingUtilities.isRightMouseButton(e)) {
                Integer[] selRows = FnEaS.intToIntegerArray(gThis.getSelectedRows());
                Integer[] selCols = FnEaS.intToIntegerArray(gThis.getSelectedColumns());
                System.out.println("RightMausClicked -- \nRows: "
                    + Arrays.deepToString(selRows) + "\nCols: "
                    + Arrays.deepToString(selCols)
                );
                DimensionChunk dch = new DimensionChunk(); 
                System.out.println("CALUPNULL=" + (calendarUpdater == null));
                calendarUpdater.setVisible(true);
            } 
            else {
                System.out.println("LeftMausClicked");
                myGraf.getParentBrowser().setSelectionMode("STD", false /* must */);
                ctrlIsDown = false;
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
//            if (me.isPopupTrigger()) {
            if (SwingUtilities.isRightMouseButton(me)) {
                System.out.println("RightMausPressed");
            } 
            else {
                System.out.println("MausPessed");
                if (ctrlIsDown) {
                    myGraf.getParentBrowser().setSelectionMode("3DI", false /* must */);
                    System.out.println("Ez dógozik, bazmeg down! ");
            }
            }
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
           // System.out.println("MausEntered");
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // System.out.println("exit, bazmeg!!!");
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyTyped(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            System.out.println("KeyTyped");
            
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!ctrlIsDown)
            if (e.getKeyCode()==17) {
                System.out.println("ctrl - KeyPressed ");
                        //+ e.getKeyCode() + "KS " + InputEvent.CTRL_DOWN_MASK + " " + e.isControlDown());
                ctrlIsDown = true;
            }

            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (ctrlIsDown)
            if (e.getKeyCode()==17) {
            System.out.println("ctrl - KeyReleased ");
            //+ e.getKeyCode() + " " + e.isControlDown());
                ctrlIsDown = false;
            //brwGrafikon.setSelectionMode("STD");
          }
           //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

public void headerWidthChanged() {
            int iNewHdrWidth = sumColumnWidths();
            if (iNewHdrWidth != myTableHeader.getWidth()) {
                myGraf.setHeaderWidth(iNewHdrWidth);
            }
            // Reset flag-u v tabulke
           setColumnWidthChanged(false);
}
    // konstruktor
@SuppressWarnings("unchecked")
    EOC_GrafTable(EOC_graf grf, boolean isDim, Kernel kr) {
        myGraf          = grf;
        isDimension     = isDim;
        krn             = kr;
        ctrlIsDown      = false;
        alSharedIDX     = new ArrayList<>();
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gThis = this;
        myTableModel = new <DBtableColumn>DefaultTableModel() {
            @Override
            public Class getColumnClass(int column) {
//bRendering
//                System.out.println("getColumnClass for column: " + column);
                if (myColumnModel.getColumnCount() < column) return null;
                
                return ((DBtableColumn) myColumnModel.getColumn(column))
                                                          .getDataTypeClass();
            }            

            @Override
            public boolean isCellEditable(int row, int column) {
            //all cells false
                return false;
            }
        };
        
        this.setModel(myTableModel);
        myColumnModel = new <DBtableColumn>DefaultTableColumnModel();
        myColumnModel.addColumnModelListener(new TableColumnWidthListener());
        this.setColumnModel(myColumnModel);
        
        this.setRowSorter(new TableRowSorter((DefaultTableModel)this.getModel()));
        this.getRowSorter().addRowSorterListener(new RowSorterListener() {
            @Override
            public void sorterChanged(RowSorterEvent e) {
                if (bRendering) return;
                alSharedIDX.clear();
                if (e.getType().toString().equals("SORTED")) {
                    for (int i = 0; i < getRowCount(); i++) {
                        /*
                        System.out.print("DDD=" + getValueAt(i, 0)
                                          + "  " + getValueAt(i, 1) 
                                          + "  " + getValueAt(i, 2) 
                                          + "  " + getValueAt(i, 3));
                        */
                        // zmena hodnoty aktualneho spojovacieho kluca dimenzie                      
                        setValueAt(i,i,2);
                        Object[] objIdx = new Object[2];
                        objIdx[0] = getValueAt(i, 0); // originalny index-key
                        objIdx[1] = i /* getValueAt(i, 2) */; // aktualny index-key
                        alSharedIDX.add(objIdx);
                        //System.out.print(" == " + getValueAt(i, 2) + "\n");
                } // for (int i = 0; i < getRowCount(); i++) 
                repaint();    
                    System.out.println("sharing IDX by keys -> "
                    + getRowSorter().getSortKeys().get(0).getSortOrder()
                            + Arrays.deepToString(alSharedIDX.toArray()));
                            //.get(0).get().getSortOrder());
                myGraf.shareIdxKeyList(alSharedIDX, 
                        getRowSorter().getSortKeys().get(0).getSortOrder(),
                        (EOC_graf) myGraf);    
                }
            }
        });
        myTableHeader = this.getTableHeader();
        myTableHeader.addMouseListener(new TableHeaderMouseListener());
        
        mouseAndKeyListener = new MouseAndKeyListener();
        this.addMouseListener(mouseAndKeyListener);
        this.addKeyListener(mouseAndKeyListener);

        calendarUpdater = 
                new XD_calendar_event(krn.getDsk(), true, krn, null,null);

    } // konstruktor
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
                if (bRendering) {
                 // bRendering musi prepnut povodca, inak je to FATAL
                 // -- Dorobit moznost prepnut to kliknutim na ikonke
                 //   bRendering = false;
                    return;
                }
              super.valueChanged(e);
           changeSelection(this.getSelectedRow()); 
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return ((DBtableColumn) (this.getColumnModel()
                .getColumn(columnIndex))).getDataTypeClass();
    }
    
    
    public boolean getColumnWidthChanged() {
        return isColumnWidthChanged;
    }
////        colHdrRenderer = new ColumnHeaderRenderer(this);

    public void setColumnWidthChanged(boolean widthChanged) {
        isColumnWidthChanged = widthChanged;
    }
    
    public EOC_GrafTable getTable() {
        return this;
    }
    
          /* funguje, len teraz zavadza
          @Override
          public TableCellRenderer getCellRenderer(int row, int column) {
              return renderer;
          }
          */
    

//########################################################################
  private class TableColumnWidthListener implements TableColumnModelListener
{
    @Override
    public void columnMarginChanged(ChangeEvent e)
    {
        /* columnMarginChanged is called continuously as the column width is changed
           by dragging. Therefore, execute code below ONLY if we are not already
           aware of the column width having changed */
        if(!getColumnWidthChanged())
        {
            // Podmienka nizsie NIE-JE splnena, ked sa sirka slpca meni kodom
            if(getTableHeader().getResizingColumn() != null)
            {
                
                // Uzivatel ma chytenu hlavicku mysou, a meni sirku stlpca
                setColumnWidthChanged(true);
            }
        }
    }
    
    @Override
    public void columnMoved(TableColumnModelEvent e) { }

    @Override
    public void columnAdded(TableColumnModelEvent e) { }

    @Override
    public void columnRemoved(TableColumnModelEvent e) { }

    @Override
    public void columnSelectionChanged(ListSelectionEvent e) { }
} // private class TableColumnWidthListener implements TableColumnModelListener
//*************************************************************************
  
private int sumColumnWidths() {
   int w = 0;
   for (int i = 0; i < myColumnModel.getColumnCount(); i++) {
       w = w + myColumnModel.getColumn(i).getWidth();
   }
   return w;
}

private void changeSelection(int rowIdx) {
    ////System.out.println("SEELECTING:" + myGraf.getcGrafName() + " rowIDX:" + rowIdx);
    myGraf.changeSelection(rowIdx);
}
///        UIManager.put("Table.ascendingSortIcon", new EmptyIcon());
///        UIManager.put("Table.descendingSortIcon", new EmptyIcon());

@SuppressWarnings("unchecked")
public void treatIdxKey(ArrayList<Object[]> alShrIdxKey, 
                                /*SortOrder sortOrder,*/ EOC_graf ListOwner) {
//                    System.out.println(myGraf.getcGrafName() + " -- treating IDX -> "
//                            + Arrays.deepToString(alShrIdxKey.toArray()));
        xblock:
        for( int x = 0 ; x < alShrIdxKey.size() ; x++ )
        {
            Object[] o = alShrIdxKey.get(x);
           // System.out.println("TTesting: " + Arrays.deepToString(o));
            // 3. stlpec (2) je variabilny index, ktore sa meni na 1 .. n
            // pri zmene triedenia. Vzdy obsahuje poradie 1 .. n
            zblock:
            for (int row = 0; row <= getRowCount() - 1; row++) {
                //System.out.print(" is " + getValueAt(row, 0) + " as " + o[0]);
                if (getValueAt(row, 0).equals(o[0])) {
                   // System.out.print("Setting value: " + getValueAt(row, 2)
                   //     + " to " + o[1] + " ");
                    setValueAt(o[1], row, 2);
                    break; // zblock:
                }
            }
        } // xblock:
        //System.out.println("");
//    getRowSorter().
    sortKeys = new ArrayList<>();
 
//int columnIndexToSort = 2;
// sortorder treba otocit najprv do opacneho smeru
//if (sortOrder.)
sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
//System.out.println(myGraf.getcGrafName() + " -> Setting SOR ORDER to:" + sortOrder.name()); 
TableRowSorter sorter = (TableRowSorter) getRowSorter();
sorter.setSortKeys(sortKeys);  
sorter.sort();
   // getRowSorter().toggleSortOrder(2);
    repaint();    
}

public void setRendering (boolean renderingState) {
    bRendering = renderingState;
}

public void setRendererMode(String renderingState) {
    sRendererMode = renderingState;
    this.repaint();
}

public String getRendererMode() {
    return sRendererMode;
}

public boolean getbLivingCells() {
    return bLivingCells;
}
public void reviveCells(boolean bRevive) {
    bLivingCells = bRevive;
    repaint();
}



}
