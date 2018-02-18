package artzi.gtm.utils.xlsx;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class ExRow {
	Workbook wb ; 
	Row row ; 
	ExRow (Workbook wb, Row row) { 
		this.row = row ; 
		this.wb = wb ; 
	}
	public void setCell (int indx , String value) { 
		Cell c = row.createCell(indx , Cell.CELL_TYPE_STRING) ; 
		c.setCellValue(value) ; 
	}
	
	public void setCell (int indx , int value) { 
		Cell c = row.createCell(indx , Cell.CELL_TYPE_NUMERIC) ; 
		Integer iv = value ; 
		c.setCellValue ( iv.doubleValue ()) ; 
	}
	public void setCellWrap (int indx , String text) { 
		Cell c = row.createCell(indx , Cell.CELL_TYPE_STRING) ; 
		c.setCellValue(text) ; 
		CellStyle cs = wb.createCellStyle();
	    cs.setWrapText(true);
	    c.setCellStyle(cs);		
	}
	public void setCellHyperLink (int indx , String text) { 
		CellStyle hlink_style = wb.createCellStyle();
	    Font hlink_font = wb.createFont();
	    hlink_font.setUnderline(Font.U_SINGLE);
	    hlink_font.setColor(IndexedColors.BLUE.getIndex());
	    hlink_style.setFont(hlink_font);
	    hlink_style.setWrapText(true);
	    CreationHelper createHelper = wb.getCreationHelper();
	    Cell c = row.createCell(indx , Cell.CELL_TYPE_STRING) ; 
	    
	   
	   Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
	   link.setAddress(text);
	   c.setCellValue(text) ; 
	   c.setHyperlink(link) ; 
	  
	   c.setCellStyle(hlink_style);
	}
	public void setCellFill(int indx, String text) {
		Cell c = row.createCell(indx , Cell.CELL_TYPE_STRING) ; 
		c.setCellValue(text) ; 
		CellStyle cs = wb.createCellStyle();
		
		cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cs.setFillPattern(CellStyle.SOLID_FOREGROUND);	
		cs.setBorderBottom(CellStyle.BORDER_MEDIUM) ; 
		cs.setBorderTop(CellStyle.BORDER_MEDIUM) ; 
		cs.setBorderLeft(CellStyle.BORDER_MEDIUM) ; 
		cs.setBorderRight(CellStyle.BORDER_MEDIUM) ; 
		c.setCellStyle(cs);
	}
	public void setCell (int indx , double value) { 
		Cell c = row.createCell(indx , Cell.CELL_TYPE_NUMERIC) ; 
		c.setCellValue ( value ) ; 
	}
	public int getRowNum () { 
		return row.getRowNum() ; 
	}
	
}
