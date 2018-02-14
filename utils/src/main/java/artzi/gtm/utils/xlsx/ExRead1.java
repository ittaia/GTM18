package artzi.gtm.utils.xlsx;
import java.io.FileInputStream ; 
import java.io.InputStream ; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.util.LinkedList;
 
public class ExRead1 { 
	InputStream myxls = null ;  
	XSSFWorkbook wb     = null ; 	
	
	public ExRead1 ( String fileName) {
		try {
			myxls = new FileInputStream (fileName) ; 
			wb     = new XSSFWorkbook(myxls);
		}
		catch (Exception e) {e.printStackTrace();} 
	} 
	public LinkedList<String> ExGetSheetList ()  {
		LinkedList <String> sheetNames  ; 
		sheetNames = new LinkedList <String> () ; 	 
		int sheetNumber = wb.getNumberOfSheets() ; 
		for (int i = 0 ;  i <  sheetNumber ; i ++) {
			sheetNames.add(wb.getSheetName (i)) ; 
		}
	 return sheetNames ; 
	}
	public int getSheetLinesNumber (String SheetName) {
		Sheet sheet ; 
		if (SheetName == null ) {
			sheet = wb.getSheetAt (0); 
		}
		else {
			sheet = wb.getSheet (SheetName) ; 
		}
		return sheet.getLastRowNum() ; 
	}
	public Row ExGetRow (String SheetName , int rowIndex) {
		Sheet sheet ; 
		if (SheetName == null ) {
			sheet = wb.getSheetAt (0); 
		}
		else {
			sheet = wb.getSheet (SheetName) ; 
		}
		 
		return sheet.getRow(rowIndex) ;  
	}		
}