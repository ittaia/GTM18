package artzi.gtm.utils.xlsx;
 
import java.util.Date;

import org.apache.poi.ss.usermodel.*;

public class ExTabRow {
	public static int maxFields = 20 ; 
	String [] rowFieldNames ; 
	String [] rowFieldValues ;  
	int    [] rowIntValues ; 
	int [] cellTypes ; 
	double [] rowDoubleValues ;
	
	public int rowFieldNum =  0 ; 
	
	public ExTabRow (ExRead1 mainTable , String SheetName, int rowIndex ) {
		rowFieldNames  = new String [maxFields] ;
		rowFieldValues = new String [maxFields] ;  
		
		rowIntValues = new int [maxFields] ; 
		rowDoubleValues = new double [maxFields] ; 
		cellTypes = new int [maxFields] ; 
		Row r0 = mainTable.ExGetRow(SheetName, 0) ; 
		rowFieldNum = r0.getLastCellNum () ; 
		if (rowFieldNum > maxFields-1 ) rowFieldNum = maxFields-1 ; 
		Row r =  mainTable.ExGetRow(SheetName, rowIndex) ; 
		for (int i = 0; (i < rowFieldNum)&&( i < maxFields)  ; i++)  { 
			 
			Cell c = r0.getCell(i) ;
			if (c == null ) System.out.println ("Null ") ; 
			cellTypes [i] = c.getCellType() ; 			
			if (c.getCellType() == Cell.CELL_TYPE_STRING )  {
				rowFieldNames [i] = c.getStringCellValue ()  ; 				
			}
			else { rowFieldNames [i] = "" ; }
			
			if ((!(r==null)) && i < r.getLastCellNum ()) {
				c = r.getCell(i) ; 
				if ( !(c == null)) {
					if 	 (c.getCellType() == Cell.CELL_TYPE_STRING )   {
						rowFieldValues [i] = c.getStringCellValue ()  ; 
						rowIntValues [i] = 0 ; 
					}	
					else if (c.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
						Double  d = new Double (c.getNumericCellValue())   ; 
						 
						rowFieldValues [i] = Double.toString (d)  ; 
						rowDoubleValues [i] = d ; 
						long l = d.longValue () ;
						rowIntValues [i] = (int)l ; 						 
					}
					else {  
						rowFieldValues [i] = "";
						rowIntValues [i] = 0 ; 
							
					}
				}
				else{  
					rowFieldValues [i] = "";
					rowIntValues [i] = 0 ; 
				}
			}
			else {  
				rowFieldValues [i] = "";
				rowIntValues [i] = 0 ;
			} 
		}
	}
	public String fieldValue (String fieldName) {
		String val = "*" ; 
		for  (int i = 0; i < rowFieldNum; i++) {
			if (rowFieldNames[i].trim().equalsIgnoreCase(fieldName)) {  
				val = rowFieldValues [i] ; 
				
				break; 
			}
		}
		return val ; 		 
	}
	public int fieldIntValue (String fieldName) {
		int val = -9999  ; 
		for  (int i = 0; i < rowFieldNum; i++) {
			if (rowFieldNames[i].trim().equalsIgnoreCase(fieldName)) {  
				val = rowIntValues [i] ; 
				break; 
			}
		}
		return val ; 		
	}
	public String fieldValue(int field) {
		 return rowFieldValues [field] ; 
	}
	public int fieldIntValue (int field ) {
		
		return rowIntValues [field] ; 		
	}
	public double fieldDoubleValue (String fieldName) {
		double  val = -9999  ; 
		for  (int i = 0; i < rowFieldNum; i++) {
			if (rowFieldNames[i].trim().equalsIgnoreCase(fieldName)) {  
				val = rowDoubleValues [i] ; 
				break; 
			}
		}
		return val ; 		
	}
	public double fieldDoubleValue(int i) {
		return rowDoubleValues [i] ; 
	}
	public String [] getFieldNames () { 
		return rowFieldNames ; 
	}
	public void print () {
		for (int i =  0 ; i < rowFieldNum ; i++)  {
			System.out.println (rowFieldNames [i] + " - " + rowFieldValues [i]) ; 
		}
	}
	public int getNumOfFields () { 
		return rowFieldNum ; 		 
	}
	public int getCellType  (int field) { 
		return cellTypes[field] ; 
		
	}
	
	
}
