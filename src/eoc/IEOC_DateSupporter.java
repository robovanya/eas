/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package eoc;

/**
 *
 * @author rvanya
 */
public interface IEOC_DateSupporter {

    public int getDay(); 
    public int getMonth();
    public int getYear();
    public String getDayStr();
    public String getMonthStr();
    public String getYearStr();
    public void setSupportForDateString(String dtStr /* yyyy-MM-yy */);
    public void setDateStringSource(iEOC_DBtableField dateStrSource);
}
