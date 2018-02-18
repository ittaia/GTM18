package artzi.gtm.utils.xlsx;
import org.apache.poi.ss.usermodel.*;
import artzi.gtm.utils.elog.EL; 
 
public class ExNewRow  {
	String [] rowFieldNames ; 
	String [] rowFieldValues ;  
	int    [] rowIntValues ; 
	String [] rowFieldFormat  ; 
	int rowFieldNum =  0 ; 
	
	public ExNewRow (String [] fieldNames , String [] stringValues) {
		rowFieldNum  =  fieldNames.length ; 
		rowFieldNames  = new String [rowFieldNum ] ;
		rowFieldValues = new String [rowFieldNum ] ;  
		rowFieldFormat = new String [rowFieldNum ] ; 
		
		rowIntValues = new int [rowFieldNum ] ; 
		for (int i = 0 ; i < rowFieldNum  ; i++) {
			rowFieldNames [i] =  fieldNames [i] ; 
			rowFieldValues [i] = "" ; 
			rowIntValues [i] = 0 ; 
			rowFieldFormat [i] = "" ; 
			if (!(stringValues == null )) { 
				rowFieldValues [i] = stringValues [i] ; 
				rowFieldFormat [i] = "S" ; 
			}
		}
	}	
	public void setFieldValue (String fieldName , String fieldValue ) {
		boolean found = false ;   
		for  (int i = 0; i < rowFieldNum; i++) {			
			if (rowFieldNames[i].trim().equalsIgnoreCase(fieldName.trim())) {  
				found = true ; 
				rowFieldValues [i] = fieldValue ; 
				rowFieldFormat [i] = "S" ; 				
				break; 
			}
		}
		if (!found) {
			EL.WE(991, " Field not found - "+  fieldName ) ; 
		}
	}
	public void setFieldIntValue (String fieldName  , int fieldValue) {
		boolean found = false ;  
		for  (int i = 0; i < rowFieldNum; i++) {
			if (rowFieldNames[i].trim().equalsIgnoreCase(fieldName.trim())) {  
				found = true ; 
				rowIntValues [i] = fieldValue ; 
				rowFieldFormat [i] = "I" ; 	
				break; 
			}
		}
		if (!found) {
			EL.WE(991, " Field not found - "+  fieldName ) ; 
		}
	}
	public void createCells (Row r) {
		for (int i = 0 ; i < rowFieldNum ; i ++) {
			if (rowFieldFormat [i] == "S") {			 
				Cell c = r.createCell(i , Cell.CELL_TYPE_STRING) ; 
				c.setCellValue(rowFieldValues [i]) ; 
			}
			else if ( rowFieldFormat [i] == "I") {			 
				Cell c = r.createCell(i , Cell.CELL_TYPE_NUMERIC) ; 
				Integer iv = rowIntValues [i] ; 
				c.setCellValue ( iv.doubleValue ()) ; 
			}
		}
	}
	public void print () {
		for (int i =  0 ; i < rowFieldNum ; i++)  {
			System.out.println (rowFieldNames [i] + " - " + rowFieldValues [i]) ; 
		}
	}
}
