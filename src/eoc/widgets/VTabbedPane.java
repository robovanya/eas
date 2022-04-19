package eoc.widgets;

import eoc.IEOC_VisualObject;
import system.desktop.Desktop;
import system.Kernel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jxl.write.WritableFont;
import system.FnEaS;

public class VTabbedPane extends JTabbedPane {

    private JTabbedPane pane = this;    
    public String nazovModulu;
    public String nazovTabu;
    public Kernel krn;
    private TabListComponent currentTabListComponent = null;
   // private Component myComponent; // 2015-8-20
    private ArrayList<TabListComponent> myTabListComponents = new ArrayList<>(); // 2017-6-27 - Robo
    public VTabbedPane(String nazov, Kernel krn) {  
        this.nazovModulu = nazov;
        this.krn = krn;
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                VTabbedPane sourceTabbedPane = (VTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                
                TabListComponent tlc = getTabListComponent(index);
                if (tlc != null) {
                    ////System.out.println("Tab changed to: " + index + " LABEL: " + tlc.myLabel + " COMPONENT:" + FnEaS.sObjName(tlc.myComponent));
                    sourceTabbedPane.changeTab(tlc);
                    //krn.TabChanging(nazovModulu, nazovTabu, (Component) tlc.myComponent);
                }    
                else {
                    ////System.out.println("Tab changed to: " + index + " LABEL: NULL_TabListComponent");
                    sourceTabbedPane.changeTab(tlc);
//                    krn.TabChanging(nazovModulu, nazovTabu, null);
                }
                
            }
        };
      this.addChangeListener(changeListener);
    }
    
    public TabListComponent getTabListComponent(int iIdxTabList) {
        TabListComponent tc = null;
        for (TabListComponent t: myTabListComponents) {
            if (t.myTabIndex == iIdxTabList) {
                tc = t;
                break;
            };
        } 
        return tc;
    }
    
    public void setNazov(String nazov){
        this.nazovModulu = nazov;
    }
    
    public String getNazov(){
        return nazovModulu;
    }

    public void addClosableTab(String titulka, Component comp){

        if (!(comp instanceof IEOC_VisualObject)) { // 2017-6-27
            Kernel.staticMsg("E", "Pridávaný objekt nie je typu IEOC_VisualObject."
                     + "\nPridanie bolo odmietnuté."
                     + "\n\nObjekt: " + comp.toString(), "Nevhodný typ komponentu");
            return;
        }
        
        boolean moze = true;
        this.nazovTabu = titulka;
        for(int i=0; i<pane.getTabCount(); i++ ){
            if(pane.getComponentAt(i).equals(comp)){
                moze = false;   // aby nebolo mozne dva krat pridat presne to iste
            }
        }
        if(moze){
            TabListComponent tbc = new TabListComponent();
            tbc.myComponent = (IEOC_VisualObject) comp;
            tbc.myLabel     = titulka;
            tbc.myTabIndex  = pane.getTabCount(); //  - 1; // Prvy je nulty tab !
            myTabListComponents.add(tbc);
            pane.add(comp);        

            TabButton tbtn = new TabButton(titulka, tbc);
            pane.setTabComponentAt(pane.getTabCount()-1, tbtn);
            pane.setSelectedIndex(pane.getTabCount()-1); // po pridani, nech je tab hned aj selectovany
            
        }
        
    }

    public Kernel getKrn() {
        return krn;
    }

    public void setKrn(Kernel krn) {
        this.krn = krn;
    }
    
    public void showAllData() {
        String s = "";
        for (TabListComponent t: myTabListComponents) {
             s = s + t.getData() + "\n##\n";
        } 
        Kernel.staticMsg(s);

       
    }
    
    public class TabListComponent {
         public Integer myTabIndex;
         public IEOC_VisualObject myComponent;
         public String myLabel;
         public void showData() {
             Kernel.staticMsg(getData());
         }
         public String getData() {
             return "myLabel = " + myLabel 
                     + "\nmyTabIndex = " + myTabIndex
                     + "\nmyObject = " + FnEaS.sObjName(myComponent)
                     ;
         }
    }
            
      class TabButton extends JPanel{
          
        JLabel titulka = new JLabel();
        JLabel vypinac = new JLabel();
        TabListComponent myTabComponent;
                
        //JButton vypinac = new JButton();
        
        public TabButton( String label , TabListComponent mtbc){
        setOpaque(false);
        titulka.setText(label);
        myTabComponent = mtbc;
//        vypinac.setFont(null);
        Font f = vypinac.getFont();
        vypinac.setFont(f.deriveFont(Font.BOLD));
        vypinac.setText("X");
        vypinac.setForeground(Color.BLACK);
        vypinac.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.isControlDown()) {
                   myTabComponent.showData();
                }
                else if (evt.isAltDown()) {
                   showAllData();
                }
                else vypnutTab(myTabComponent);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                vypinac.setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                vypinac.setForeground(Color.BLACK);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(titulka, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vypinac))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titulka)
                    .addComponent(vypinac)))
        );
        
        }  
        
        public void vypnutTab(TabListComponent tbc){                        
            krn.TabClosing(nazovModulu, nazovTabu, (Component) tbc.myComponent);
            pane.remove(pane.indexOfTabComponent( this ) );
            removeTabListComponent(tbc);
            if (pane.getTabCount() == 0) {
                currentTabListComponent = null;
            }
            /*
            else {
                currentTabListComponent = .getTabListComponent();
            }
                    */
        }

    }


        private void removeTabListComponent(TabListComponent tbc) {
        
        // znizenie indexu teblistkomponentov s vissim indexom   
        for (TabListComponent t: myTabListComponents) {
            if (t.myTabIndex > tbc.myTabIndex) 
                t.myTabIndex = t.myTabIndex - 1;
        }

        myTabListComponents.remove(tbc);
            
    }
    public void changeTab(TabListComponent tbc){        
        // po uzevreti posledneho programu v aktualnom module
        // ma tbc hodnotu null !
        if (tbc != null) 
            krn.TabChanging(nazovModulu, tbc.myLabel, tbc);
        else 
            krn.TabChanging(nazovModulu, null, tbc);
        
        currentTabListComponent = tbc;
    }
    
    public TabListComponent getCurrentTabListComponent() {
        return currentTabListComponent;
    }

}
