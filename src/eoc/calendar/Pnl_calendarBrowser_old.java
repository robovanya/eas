/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar;

import eoc.cube.CubeMiner;
import eoc.database.DBconnection;
//import eoc.grafikon.DimensionCellRenderer;
import eoc.widgets.PObject;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import system.FnEaS;
import system.Kernel;

/**
 *
 * @author rvanya
 */
public class Pnl_calendarBrowser_old extends Pnl_calendarTable {
    /**
     * Creates new form Pnl_brwHourDayMonth
     */
    private String mySelectionMode = "STD";
    
    
    public class calMenuItem extends javax.swing.JMenuItem {
       public CalendarEvent myCalEvent;
       // konstruktor
       public calMenuItem (String txt, CalendarEvent evt) {
           super(txt);
           myCalEvent = evt;
       }
    }
    
    // konstruktor
    public Pnl_calendarBrowser_old() {
        calOriginalToday = FnEaS.setTimeToNull(Calendar.getInstance());
        calCurrentToday = (Calendar) calOriginalToday.clone();
        monthMaxDay = calCurrentToday.getActualMaximum(Calendar.DATE);
        initComponents();
        myTableModel.setColumnCount(0);
        myTableModel.setRowCount(0);
        
        TableColumn tcNazov = new TableColumn();
        tcNazov.setHeaderValue("Názov");
        myColumnModel.addColumn(tcNazov);

        TableColumn tcZaciatok = new TableColumn();
        tcZaciatok.setHeaderValue("Začiatok");
        myColumnModel.addColumn(tcZaciatok);
        
        TableColumn tcKoniec = new TableColumn();
        tcKoniec.setHeaderValue("Splniť do");
        myColumnModel.addColumn(tcKoniec);
        
        TableColumn tcMiesto = new TableColumn();
        tcMiesto.setHeaderValue("Miesto konania");
        myColumnModel.addColumn(tcMiesto);

        TableColumn tcTermin = new TableColumn();
        tcTermin.setHeaderValue("Termín o");
        myColumnModel.addColumn(tcTermin);

        myTable.setColumnModel(myColumnModel);
        myTable.setModel(myTableModel);
        jointPointClass = Calendar.class;
        cellPopUpMenu = new JPopupMenu();
   }

    @Override
    public boolean joinToGroup(Pnl_calendarTableGroup grp) {
        calendarTableGroup = grp;
        calendarTableGroup.addToGroup(this, jointPointClass);
        return true;
    }
    @Override
    public String setDimensionBoundsFor(Calendar cal) {
        krn.Message("W", "Not overrided setDimensionBoundsFor() in " + FnEaS.sObjName(this),"NEIMPLEMENTOVANÁ METÓDA !");
        return "";
    }
    
    @Override
    public String setCurrentToday(Object oSender,Object newToday) {
        krn.Message("W", "Not overrided setCurrentToday() in " + FnEaS.sObjName(this),"NEIMPLEMENTOVANÁ METÓDA !");
        return "";
    }
    
    @Override
    public String goToPrev() {
        krn.Message("W", "Not overrided goToPrev() in " + FnEaS.sObjName(this),"NEIMPLEMENTOVANÁ METÓDA !");
        return "";
    }
    @Override
    public String goToNext() {
        krn.Message("W", "Not overrided goToNext() in " + FnEaS.sObjName(this),"NEIMPLEMENTOVANÁ METÓDA !");
        return "";
    }
    @Override
    public String goToToday() {
        krn.Message("W", "Not overridded goToToday() in " + FnEaS.sObjName(this),"NEIMPLEMENTOVANÁ METÓDA !");
        return "";
    }
    
    public void setDimensionLabel(String s) {
        lbl_Dimension.setText(s);
    }
    
    public String displayedValueTypeChanged(String s) {
        displayedValueType = (String) s;
        if (displayedValueType.equals("Pocet")) {
            tgb_pocet.setSelected(true);
            tgb_popis.setSelected(false);
        }
        if (displayedValueType.equals("Text")) {
            tgb_popis.setSelected(true);
            tgb_pocet.setSelected(false);
        }
        myTable.revalidate();
        myTable.repaint();
        ////myPnlCalendarTable.displayedValueTypeChanged(displayedValueType);
        //krn.Message("W", "Not overrided displayedValueTypeChanged() in " + FnEaS.sObjName(this),"NEIMPLEMENTOVANÁ METÓDA !");
        return "";
    }
    
    @Override
    public String initialize(Kernel kr, DBconnection cX) {
        if (bInitialized) return "";
        
        super.initialize(kr, cX); //To change body of generated methods, choose Tools | Templates.

        myTable.setDefaultRenderer(Object.class, new CalendarCellRenderer(this));
        myTable.getTableHeader().setDefaultRenderer(new HeaderCellRenderer(this));
        
        MouseAndKeyListener mouseAndKeyListener = new MouseAndKeyListener();
        myTable.addMouseListener(mouseAndKeyListener);
        myTable.addKeyListener(mouseAndKeyListener);

////        myTable.getColumnModel().setColumnSelectionAllowed(false); 
////        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel selectionModel = myTable.getSelectionModel();

        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                handleSelectionEvent(e);
            }
        });        
        // toto chybalo do kelu - 2016-6-22 19:13
        myTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                handleSelectionEvent(e);
            }
        });         
        calendarUpdater = 
                new XD_calendar_event(krn.getDsk(), true, krn, null, null);
        calendarUpdater.initialize(krn, cX);
        // null-sDimension (moze byt HourDay alebo DayMonth) 
        // treba nastavit v odvodenom objekte
        cubeMinner = new CubeMiner(krn, MyCn, null /* sDimension */); 
        setSelectionMode("3DI", false /* must */);
        setSelectionMode("STD", false /* must */);
        bInitialized = true;
        return "";
    }
/*
    public void setParameters(String head, String colHdrs[], String rowHdrs[],
            int hdrHeight, int minRwHeight , int minClWidth) {
    numDimCols = colHdrs.length; 
    numDimRows = rowHdrs.length;
    minRowHeight     =  minRwHeight;
    int headerHeight = 32;
    int firstColWidth = 70;
    int minColWidth = 25;
    
    }
*/
    public JTable getMyTable() {
        return myTable;
    }
    protected void handleSelectionEvent(ListSelectionEvent e) {
    //if (e.getValueIsAdjusting())  return;

    String strSource = e.getSource().toString();
    selRows = FnEaS.intToIntegerArray(myTable.getSelectedRows());
    selCols = FnEaS.intToIntegerArray(myTable.getSelectedColumns());
    //System.out.println("SELLECTONN>>>>: R:" + Arrays.toString(selRows) + " C:" + Arrays.toString(selCols));
    repaintHeaders();
   }

   private void repaintHeaders() {
     if (selRows != null) {
       myTable.repaint();
     }
     if (selCols != null) {
       myTable.getTableHeader().repaint();
     }
   }
private class MouseAndKeyListener implements MouseListener, KeyListener {
    
//odchytit mausmove v  DALSOM listeneri
@Override
     public void mouseClicked(MouseEvent e) {
//            if (e.isPopupTrigger()) {
            if (SwingUtilities.isRightMouseButton(e)) {
//                krn.Message("mySelectionMode===" + mySelectionMode);
                if (mySelectionMode.equals("STD")) {
//                    Component cellComp = myTable.getComponentAt(e.getX(), e.getY());
            Point p = e.getPoint();
            int row = myTable.rowAtPoint(p);
            int col = myTable.columnAtPoint(p);
            myTable.setRowSelectionInterval(row, row);
            myTable.setColumnSelectionInterval(col, col);
                    cellPopUpMenu.removeAll();
                    selRows = FnEaS.intToIntegerArray(myTable.getSelectedRows());
                    selCols = FnEaS.intToIntegerArray(myTable.getSelectedColumns());
////          krn.Message("mySelection===\n" + Arrays.deepToString(selRows) + "\n"
////                      + Arrays.deepToString(selCols));
        if ((selRows==null||selRows.length==0) || (selCols==null||selCols.length==0)) return;
        Object value = myTable.getModel().getValueAt(selRows[0], selCols[0]);
        //Object value = myTable.g .getModel(). .getValueAt(selRows[0], selCols[0]);
        //Object value = ((JLabel) cellComp).getV
                
        String valueClassName = value.getClass().getSimpleName();
        Double dbl = 0.0;
        Integer rowCnt = 0;
        if (valueClassName.equals("CalendarCellTblModel")) {
            CalendarCellTblModel cmdl = (CalendarCellTblModel) value; 
            rowCnt = cmdl.getRowCount();
                if (rowCnt > 0) {
                  for (int r = 0; r < rowCnt; r++) { 
                      CalendarEvent cev = (CalendarEvent) cmdl.getValueAt(r, 2);
                      calMenuItem item1 = new calMenuItem(
                              r + " - " + cev.getProperty("c_nazov_ulohy") + " -> " 
                              + cev.getProperty("c_miesto_konania") + " -> " 
                              + cev.getProperty("c_popis_ucelu"), cev);
                      item1.myCalEvent = cev;
                      //Kernel.staticMsg("cev.htbMyEvent.get(\"d_od_date\")===" + cev.htbMyEvent.get("d_od_date"));
                      item1.addActionListener(new ActionListener() {
  
                          @Override
                          public void actionPerformed(ActionEvent e) {
                              String txt = ((calMenuItem) e.getSource()).getText();
                              int i = Integer.parseInt(FnEaS.sEntry(1, txt, "-").trim());
                              CalendarEvent calEvt = ((calMenuItem) e.getSource()).myCalEvent;
                              /*
                              krn.Message("Selected ittemm:" + ((calMenuItem) e.getSource()).getText()
                              + "\nSELECTIONINDEX IS:" + i
                              + "\nEVENTIDISS:" + calEvt.getProperty("id_eas_calEvt"));
                              */
                              // toto tu je HORIBILNA PIIICOVINA ! ...myCalEvent.myOwner ma byt nastaveny pri vzniku
                              // objektu myCalEvent
                              calEvt.myOwner = myCalendarOwner;
                             calendarUpdater.view(calEvt, "UPDATE");
                             cubeMinner.refresh();
                             myTable.repaint();

                          }
                      });

                      cellPopUpMenu.add(item1);
                  }
            }
            cellPopUpMenu.show(e.getComponent(), e.getX(), e.getY());
        }
                   // maybeShowPopup(e);
 
                }
                else {
                    selRows = FnEaS.intToIntegerArray(myTable.getSelectedRows());
                    selCols = FnEaS.intToIntegerArray(myTable.getSelectedColumns());
                
                    TimeChunk tch = getSelectedTimeChunk(); 
                    if (tch != null)
                        calendarUpdater.view(myCalendarOwner, tch, null, "add");
                    cubeMinner.refresh();
                    myTable.repaint();
                }
            } 
            else {
//                System.out.println("LeftMausClicked");
                setSelectionMode("STD", false /* must */);
                //super.ctrlIsDown = false;
                Integer[] selRws = FnEaS.intToIntegerArray(myTable.getSelectedRows());
                Integer[] selCls = FnEaS.intToIntegerArray(myTable.getSelectedColumns());
 //               System.out.println("NOOO_RightMausClicked -- \nRows: "
 //                  + Arrays.deepToString(selRws) + "\nCols: "
 //                   + Arrays.deepToString(selCls)
 //               );
                selCols = selCls;
                selRows = selRws;
                repaintHeaders();
                myTable.repaint();
            }
            e.consume();
        }
     /*
     public void mousePressed(MouseEvent e) {
          maybeShowPopup(e);
          }

       public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
       }
       */
/*
       private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            cellPopUpMenu.show(e.getComponent(),
                    e.getX(), e.getY());
        }
       }
*/     
/*
class PopupListener extends MouseAdapter{

     public void mousePressed(MouseEvent e) {
          maybeShowPopup(e);
          }

       public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
       }

       private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            pop.show(e.getComponent(),
                    e.getX(), e.getY());
        }
      }

    }
   */     
        
        
      public TimeChunk getSelectedTimeChunk() {
          TimeChunk tmc = null;
          if (myTable.getSelectedRows().length == 0 || myTable.getSelectedColumns().length == 0) {
              krn.Message("Nemáte označený žiadny časový úsek.", "Metóda: getSelectedTimeChunk()");
              return tmc;
          }
//          System.out.println("SEROWSS:" + myTable.getSelectedRows().length + " SECOLSS:" + myTable.getSelectedColumns().length);
          Integer[] selRows = FnEaS.intToIntegerArray(myTable.getSelectedRows());
          Integer[] selCols = FnEaS.intToIntegerArray(myTable.getSelectedColumns());
          
          Calendar calOd; //  = Calendar.getInstance();
          Calendar calDo; // = Calendar.getInstance();
          CalendarTableColumn ctc;
          ctc = (CalendarTableColumn) (myColumnModel.getColumn(selCols[0]));
          calOd = (Calendar) ctc.myDate.clone();
          ctc = (CalendarTableColumn) (myColumnModel.getColumn(selCols[(selCols.length - 1)]));
          calDo = (Calendar) ctc.myDate.clone();
//          cOd.set(Calendar.DAY_OF_MONTH, selCols[0]);
          calOd.set(Calendar.HOUR_OF_DAY, selRows[0]);
          calOd.set(Calendar.MINUTE, 0);
//          cDo.set(Calendar.DAY_OF_MONTH, selCols[selCols.length - 1]);
          calDo.set(Calendar.HOUR_OF_DAY, selRows[selRows.length - 1]);
          calDo.set(Calendar.MINUTE, 59);
          tmc = new TimeChunk(calOd,calDo);
                System.out.println("RETTIMECHUNKFROM-- \nRows: "
                    + Arrays.deepToString(selRows) + "\nCols: "
                    + Arrays.deepToString(selCols) 
                    + "\n od-do: " + FnEaS.calToStr(calOd, "dd.MM.yyyy") + " + " + FnEaS.calToStr(calDo, "dd.MM.yyyy")
                    + "\nHourOd: " + selRows[0] + " -- " + calOd.get(Calendar.HOUR_OF_DAY)
                    + "\nHourDo: " + selRows[selRows.length - 1] + " -- " + calDo.get(Calendar.HOUR_OF_DAY));
          return tmc;
      }       
        


        @Override
        public void mousePressed(MouseEvent me) {
////            if (me.isPopupTrigger()) {
            if (SwingUtilities.isRightMouseButton(me)) {
////                System.out.println("RightMausPressed");
            } 
            else {
////                System.out.println("MausPessed");
                
//QQQD                if (ctrlIsDown) {
//QQQD                    setSelectionMode("3DI", false /* must */);
////                    System.out.println("Ez dógozik, bazmeg down! ");
//QQQD                }
                myTable.repaint();
            }
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //vstup ukazovatela mysi do UDAJOVEJ! oblasti tabulky
            //System.out.println("MausEntered");
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
            System.out.println("KeyTyped: " + e.getKeyChar());
            
        }

        @Override
        public void keyPressed(KeyEvent e) {
            
            /*
            if (!ctrlIsDown)
            if (e.getKeyCode()==17) {
                System.out.println("ctrl - KeyPressed ");
                        //+ e.getKeyCode() + "KS " + InputEvent.CTRL_DOWN_MASK + " " + e.isControlDown());
                ctrlIsDown = true;
            }
            */
            if (e.getKeyCode() == KeyEvent.VK_F4) {
                System.out.println("F4 - KeyPressed ");
            }
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyReleased(KeyEvent e) {
            /*
            if (ctrlIsDown)
            if (e.getKeyCode()==17) {
            System.out.println("ctrl - KeyReleased ");
            //+ e.getKeyCode() + " " + e.isControlDown());
                ctrlIsDown = false;
            //brwGrafikon.setSelectionMode("STD");
          }
            */
            if (e.getKeyCode() == KeyEvent.VK_F4) {
                System.out.println("F4 - KeyReleased ");
            }
                // klavesy - Ctrl+j
                if (e.getKeyCode() == 74 && e.isControlDown()) {
                    krn.Message(myObjectID, "I", 
                        "Tento objekt nepoužíva SQL.\n\n"
                        + "Nachádzate sa v tabuľke rezu multidimenzionálnej časovej kocky."
                        + "\n\nEOC_TimeCutter_query ani výsledok dotazu nedoporučujem skúmať."
                        + "\n\nPoužíva komprimované kvantované časové úseky typu KTODZA a KOLKODZA"
                        + "\nuložené v dočasnej viacrozmernej matrici. :o)"
                        , "Aktuálny dotaz (ctrl+j)"
                    );
                    //e.consume();
                    return;
                }
           //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

      public void  setSelectionMode(String mode, boolean must) {
      if (mySelectionMode.equals(mode) && (!must)) return;
      if (mode.equals("2DI")) {
          System.out.println("2DIselON");
         myTable.getColumnModel().setColumnSelectionAllowed(false); 
         myTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);       
      }
      else if (mode.equals("3DI")) {
          System.out.println("3DIselON");
         myTable.getColumnModel().setColumnSelectionAllowed(true); 
         myTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);       
      }
      else { // mode.equals("STD")
         mode = "STD"; // ked mode <> 2DI/3DI tak sa prepise na STD
          System.out.println("STDselON");
         myTable.getColumnModel().setColumnSelectionAllowed(false); 
         myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      }
      mySelectionMode = mode;
      }  // public void  setSelectionMode(String mode, boolean must) {
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        myScrollPane = new javax.swing.JScrollPane();
        myTable = new javax.swing.JTable();
        lbl_Dimension = new javax.swing.JLabel();
        tgb_pocet = new javax.swing.JToggleButton();
        tgb_popis = new javax.swing.JToggleButton();

        myScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                myScrollPaneComponentResized(evt);
            }
        });

        myTable.setAutoCreateColumnsFromModel(false);
        myTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        myTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        myTable.setAutoscrolls(false);
        myTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        myTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                myTableMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                myTableMouseMoved(evt);
            }
        });
        myScrollPane.setViewportView(myTable);

        lbl_Dimension.setBackground(new java.awt.Color(153, 153, 255));
        lbl_Dimension.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbl_Dimension.setForeground(new java.awt.Color(255, 255, 204));
        lbl_Dimension.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Dimension.setText("jLabel1");
        lbl_Dimension.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbl_Dimension.setOpaque(true);

        tgb_pocet.setSelected(true);
        tgb_pocet.setText("Počet úkonov");
        tgb_pocet.setToolTipText("Ukazovať počty úloh/udalostí");
        tgb_pocet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgb_pocetActionPerformed(evt);
            }
        });

        tgb_popis.setText("Popis úkonov");
        tgb_popis.setToolTipText("Ukazovať začiatky textu popisov úloh/udalostí");
        tgb_popis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgb_popisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbl_Dimension, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tgb_pocet, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tgb_popis, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(myScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Dimension, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tgb_pocet)
                        .addComponent(tgb_popis)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    public void fireScrollPaneResized(Component c) {
        //            myTable.getColumnModel().getColumn(0).setPreferredWidth(firstColWidth);
        //            myTable.getColumnModel().getColumn(0).setResizable(false);
        //ScrollPane spn = (ScrollPane) c;
        int curHeight = c.getHeight();
        int curWidth  = c.getWidth();

        // urcenie novej sirky stlpcov tabulky
        int marg = myTable.getRowMargin();
/*
        if ((curWidth != lastTestedWidth) || (curHeight != lastTestedHeight)) 
        System.out.println(">>>>>>SP__fireScrollPaneResized() WIDTH:" + c.getWidth() 
            + " (last:" + lastTestedWidth + ")"
            + "  >>>>>>HEIGHT:" + c.getHeight() + " (last:" + lastTestedHeight 
            + ")" + " myTable.getRowMargin()=" + marg);
  */      
        /* AUTORESIZE_COLUMNS je zapnuty */
        int resizableCols = myTable.getColumnCount() -1 /* row-header column */;
        if (curWidth != lastTestedWidth) {
            int myNewWidth = c.getWidth(); // aktualna sirka scroll-pane
            int delitelneW =  myNewWidth - firstColWidth - ((marg / 2) * resizableCols);
            int myNewColWidth = delitelneW / resizableCols;
            if (myNewColWidth < minColWidth) myNewColWidth = minColWidth;
            myTable.getColumnModel().getColumn(0).setPreferredWidth(firstColWidth);
            for (int i = 1; i <= resizableCols; i++) {
                myTable.getColumnModel().getColumn(i).setPreferredWidth(myNewColWidth);
                myTable.getColumnModel().getColumn(i).setWidth(myNewColWidth);
            }
            lastTestedWidth = curWidth;
            //System.out.println(
        }
            /*
            */
        int resizableRows = myTable.getRowCount();
        // urcenie novej hlbky riadkov tabulky
        if (curHeight != lastTestedHeight) {
            int myNewHeight = c.getHeight();
            int delitelneH =  myNewHeight - headerHeight - ((marg / 2) * resizableRows);
            int myNewRowHeight = delitelneH / resizableRows;
            //              if (myNewRowHeight < minRowHeight) myNewColWidth = minRowHeight;
            myTable.getColumnModel().getColumn(0).setPreferredWidth(firstColWidth);
            /*
             for (int i = 1; i <= resizableRows; i++) {
                  myTable.setRowHeight(myNewRowHeight);
             }
             */
            myTable.setRowHeight(myNewRowHeight);
            lastTestedHeight = curHeight;
        }
        
    };
    
    public void fireScrollpaneResized() {
        lastTestedWidth = 0; // donútenie k prepocianiu rozmerov tabulky
        lastTestedHeight = 0; // donútenie k prepocianiu rozmerov tabulky
        fireScrollPaneResized((Component) myScrollPane);
    };

    private void myScrollPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_myScrollPaneComponentResized
        Component c = (Component)evt.getSource();
        fireScrollPaneResized(c);
            
    }//GEN-LAST:event_myScrollPaneComponentResized

    private void myTableMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myTableMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)) return;
        Point p = new Point(evt.getX(),evt.getY());
        int drgc = myTable.columnAtPoint(p);
        if(lastDraggedCol == drgc) return;
        //System.out.println("MouseDragged at:" + myTable.columnAtPoint(p) + " on ROW:"  + myTable.getSelectedRow());
        myTable.getTableHeader().repaint();
        lastDraggedCol = drgc;
//        nepomoze ani toto. po prepnuti selcetion-typ-u to funguje dobre !!!!
        
    }//GEN-LAST:event_myTableMouseDragged

    private void myTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myTableMouseMoved
     //   Point p = new Point(evt.getX(),evt.getY());
     //   System.out.println("MouseMoved at:" + myTable.columnAtPoint(p));
    }//GEN-LAST:event_myTableMouseMoved

    private void tgb_pocetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgb_pocetActionPerformed
        tgb_popis.setSelected(!tgb_pocet.isSelected());
        if (tgb_pocet.isSelected()) {
            displayedValueType = "Pocet";
//            krn.krn_sendMessage((Object) myObjectID, "displayedValueTypeChanged",
//                displayedValueType, "navigation", "target", "");
            displayedValueTypeChanged(displayedValueType);
        }
    }//GEN-LAST:event_tgb_pocetActionPerformed

    private void tgb_popisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgb_popisActionPerformed
        tgb_pocet.setSelected(!tgb_popis.isSelected());
        if (tgb_popis.isSelected()) {
            displayedValueType = "Text";
//            krn.krn_sendMessage((Object) myObjectID, "displayedValueTypeChanged",
//                displayedValueType, "navigation", "target", "");
            displayedValueTypeChanged(displayedValueType);
        }
    }//GEN-LAST:event_tgb_popisActionPerformed

    public String nullVisibilityChanged(boolean bVisible) {
        if (bVisibleNullValues != bVisible) {
            bVisibleNullValues = bVisible;
//            System.out.println("VISBIA");
            cubeMinner.expandToTable(myTable); // iba test !!!!
//            System.out.println("VISBIB");
            myTable.repaint();
//            System.out.println("VISBIC");
        }
        return "";
    }

    public String displayedValueTypeChanged(Object sDispValueType) {
        displayedValueType = (String) sDispValueType;
        ////myPnlCalendarTable.displayedValueTypeChanged(displayedValueType);
        return "";
    }

    public boolean isSelectedRow(int rw) {
        if (selRows == null) return false;
        return Arrays.asList(selRows).contains(rw);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbl_Dimension;
    private javax.swing.JScrollPane myScrollPane;
    public javax.swing.JTable myTable;
    private javax.swing.JToggleButton tgb_pocet;
    private javax.swing.JToggleButton tgb_popis;
    // End of variables declaration//GEN-END:variables
}
