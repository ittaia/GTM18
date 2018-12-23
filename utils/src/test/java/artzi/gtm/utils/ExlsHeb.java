package artzi.gtm.utils;


import artzi.gtm.utils.hebrew.Hebrew;
import artzi.gtm.utils.xlsx.ExRow;
import artzi.gtm.utils.xlsx.ExWriteTable;

public class ExlsHeb {
	
	public static void main(String[] args) throws Exception {
		String file = "C:\\Users\\ittai\\OneDrive\\Desktop\\hb.xlsx" ; 
		String [] sh = new String [1] ; 
		sh[0] = "Sheet1" ; 
		String [] fn =  new String [1] ; 
		fn [0] = "f" ; 
		
		ExWriteTable th = new ExWriteTable (sh) ; 
		th.setSheetFieldNames(null, fn) ;
		ExRow row = th.addNewRowToSheet(null) ;
		String c1 = "\u05d0" ; 
		String c2 = "\u05d1" ;
		String c3 = "\u05d2" ;
		String str = c1+c2+c3+ " "  + "14" ; 
		String c = "ylem xa 0.5 - ? yeaj 11 12 13.15.16 " ; 
		row.setCell(0, Hebrew.tr2heb(c));	
		th.writeTable (file) ;
	}
}	