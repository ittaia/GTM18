package artzi.gtm.utils.xlsx;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.io.OutputStream ;
import java.util.ArrayList;

public class ExWriteTable { 
	
	Workbook wb     = null ; 
	ArrayList <XSSFDataValidationConstraint> valueLists = null ; 
	
	public ExWriteTable  ( String [] SheetNames) {
		wb = new XSSFWorkbook () ; 
		valueLists = new ArrayList <XSSFDataValidationConstraint> () ; 
		for (int i = 0 ; i < SheetNames.length ; i++)  {			
			wb.createSheet (SheetNames [i]) ; 
		}
	}		
	
	public int getSheetLinesNumber (String sheetName) {
		Sheet sheet = getSheet (sheetName) ;  
		return sheet.getLastRowNum() ; 
	}
	public void setSheetFieldNames (String sheetName , String [] FieldNames ) {		
		Sheet sheet = getSheet (sheetName) ;		
		Row r0 = sheet.createRow(0) ; 
		CellStyle style = wb.createCellStyle() ; 
		style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	     
	 
		for (int i = 0 ; i < FieldNames.length; i ++) {
			Cell c = r0.createCell(i , Cell.CELL_TYPE_STRING) ;
			
			c.setCellValue(FieldNames [i]) ; 
			c.setCellStyle(style);
			
			
		}
		sheet.createFreezePane(0, 1) ; 
	}
	public void setColumnWidth (String sheetName , int colIndx , int width) { 
		Sheet sheet = getSheet (sheetName) ;  
		sheet.setColumnWidth(colIndx, 256* width) ; 
	}
	public void setWrapText (String sheetName ,  int colIndx ) { 
		Sheet sheet = getSheet (sheetName) ;  
		CellStyle style = wb.createCellStyle() ; 
		style.setWrapText(true) ; 
		((XSSFSheet) sheet).setDefaultColumnStyle(colIndx , style) ; 
	}
	public int CreateValueList (String [] valueList ) { 
		final String valueListSheet = "VLists" ; 
		Sheet sheet = getSheet (valueListSheet) ; 
		if (sheet == null) { 
			sheet = wb.createSheet (valueListSheet)  ; 
		}
		int newLine  = sheet.getLastRowNum()+1 ;
		Row r  = sheet.createRow(newLine ) ; 
		for (int i = 0 ; i < valueList.length ; i ++ ) {  	    	
	    	r.createCell(i).setCellValue(valueList[i]) ; 
	    }
		XSSFCell cell0 = (XSSFCell) r.getCell(0) ; 
		XSSFCell lastCell = (XSSFCell) r.getCell(valueList.length-1) ; 
	    String reference =  "="+valueListSheet+"!" +cell0.getReference() + ":" + lastCell.getReference(); // area reference
	    XSSFDataValidationConstraint dvConstraint = new XSSFDataValidationConstraint 
				(DataValidationConstraint.ValidationType.LIST , "="+reference) ; 
		valueLists.add (dvConstraint) ; 
		return valueLists.size()-1 ; 
	}
	public void setValueList (String sheetName  , String [] valueList , int rowIndx , int colIndx) { 
		Sheet sheet = getSheet (sheetName) ; 
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint)
				dvHelper.createExplicitListConstraint(valueList);
		CellRangeAddressList addressList = new CellRangeAddressList(rowIndx, rowIndx, colIndx, colIndx);
		XSSFDataValidation validation =(XSSFDataValidation)dvHelper.createValidation(
				dvConstraint, addressList);
		validation.setSuppressDropDownArrow(true);

		validation.setShowErrorBox(true);
		((XSSFSheet) sheet).addValidationData(validation);
	}
	public void setValueList (String sheetName  , int valueListIndx , int rowIndx , int colIndx) { 
		Sheet sheet = getSheet (sheetName) ; 
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
		XSSFDataValidationConstraint dvConstraint = valueLists.get(valueListIndx) ;  
		CellRangeAddressList addressList = new CellRangeAddressList(rowIndx, rowIndx, colIndx, colIndx);
		XSSFDataValidation validation =(XSSFDataValidation)dvHelper.createValidation(
				dvConstraint, addressList);
		validation.setSuppressDropDownArrow(true);

		validation.setShowErrorBox(true);
		((XSSFSheet) sheet).addValidationData(validation);
	}	
	
	public ExRow addNewRowToSheet (String sheetName  ) {
		Sheet sheet = getSheet (sheetName) ;   
		int lineCount  = sheet.getLastRowNum() ;
		int newLine = lineCount + 1 ;  
		Row r  = sheet.createRow(newLine ) ; 
		return new ExRow (wb , r) ; 	
	}
	public ExRow addNewRowToSheetCopyRow(String sheetName, ExTabRow row) {
		Sheet sheet = getSheet (sheetName) ;   
		int lineCount  = sheet.getLastRowNum() ;
		int newLine = lineCount + 1 ;  
		Row r  = sheet.createRow(newLine ) ; 
		
		for (int i = 0 ; i < row.getNumOfFields() ; i ++ ) {
			if (row.getCellType(i) == Cell.CELL_TYPE_STRING) { 
				Cell c = r.createCell(i /*, Cell.CELL_TYPE_STRING*/) ; 
				c.setCellValue(row.fieldValue(i)) ;  
			}
			else { 
				Cell c = r.createCell(i , Cell.CELL_TYPE_NUMERIC) ; 
				System.out.println ("nn") ; 
				c.setCellValue ( row.fieldDoubleValue(i)) ; 			
			}
		}
		return new ExRow (wb , r) ;
	}
	
	public void addRowToSheetWithValues (String sheetName , String [] values ) {
		Sheet sheet = getSheet (sheetName) ; 		
		int lineCount  = sheet.getLastRowNum() ;
		int newLine = lineCount + 1 ;  
		Row r  = sheet.createRow(newLine ) ; 
		for (int i = 0 ; i < values.length  ; i ++) {
			Cell c = r.createCell(i /*, Cell.CELL_TYPE_STRING*/) ; 
			c.setCellValue(values [i]) ; 
		}		
	}
	
	public ExRow ExGetRow (String SheetName , int rowIndex) {
		Sheet sheet = getSheet(SheetName ) ; 
		Row r = sheet.getRow( rowIndex ) ; 
		return new ExRow(wb,r) ;   
	}
	public void writeTable (String fileName) { 
		try {
			OutputStream myxls = new FileOutputStream (fileName) ; 
			wb.write    (myxls);
		}
		catch (Exception e) {e.printStackTrace();} 
	} 
	private Sheet getSheet (String sheetName) { 
		Sheet sheet ; 
		if (sheetName == null ) {
			sheet = wb.getSheetAt (0); 
		}
		else {
			sheet = wb.getSheet (sheetName) ; 
		}
		return sheet ; 		
	}

	

	
}