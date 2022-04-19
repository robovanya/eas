/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eoc.calendar.dimensions_odlozene;

/**
 *
 * @author rvanya
 */
public interface IDimension {
    public void initializeXFor(Class ptrClass,Object ptrValue);
    public void initializeYFor(Class ptrClass,Object ptrValue);
    public Object getInitialXPointer(); // vrati uchovany inicializacny pointer
    public Object getCurrentXPointer(); // vrati aktualny (UP/DOWN) pointer
    public Object MoveXPointerUp();     // posunie pointer o jedenu hodnotu ++
    public Object MoveXPointerDown();     // posunie pointer o jedenu hodnotu --
    public Object getInitialYPointer(); // vrati uchovany inicializacny pointer
    public Object getCurrentYPointer(); // vrati aktualny (UP/DOWN) pointer
    public Object MoveYPointerUp();     // posunie pointer o jedenu hodnotu ++
    public Object MoveYPointerDown();     // posunie pointer o jedenu hodnotu --
    public void initProperties(); // ulozi potrebne hodnoty do Hashmapu
    public Object getProperty(String propName); // vrati hodnotu vlastnosti alebo null
    public Object setProperty(String propName, Object propVaue); // zmeni hodnotu vlastnosti alebo null
    public Object getTopXBoundary();
    public void setTopXBoundary(Object o);
    public Object getDownXBoundary();
    public void setDownXBoundary(Object o);
    public Object getTopYBoundary();
    public void setTopYBoundary(Object o);
    public Object getDownYBoundary();
    public void setDownYBoundary(Object o);
    public String getXBoundaryDescription();
    public String getYBoundaryDescription();
}
