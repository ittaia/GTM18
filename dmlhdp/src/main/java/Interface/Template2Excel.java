package Interface;

import java.util.ArrayList;
import java.util.Arrays;

import MLHDP.MLHDPModel;
import Xlsx.ExRow;
import Xlsx.ExWriteTable;

public class Template2Excel {

	public static void createExcel (int level , Template template, MLHDPModel tcrp , String excelFile) {
		String [] SheetNames = {"Sheet1"} ;  
		int [] numOfTemplates = tcrp.getNumOfTemplates () ; 
		double [][] multLevel = null ;  
		if (level < numOfTemplates.length -1 ) {
			multLevel = tcrp.getMultLevels().get(level) ;  
		}
		ArrayList <double [][]> multFeatures =  tcrp.getMultFeatures().get(level) ; 
		int numOfVLists = template.getValueLists().getNumOfLists() ; 
		int numOfCols = 1 + numOfVLists ;  
		if (level < numOfTemplates.length -1 ) { 
			numOfCols += numOfTemplates [level+1] ; 
		}
		String [] fieldNames= new String [numOfCols] ; 
		fieldNames [0] = template.getName() ;
		for (int i  = 0 ; i <  numOfVLists ;   i ++ ) { 
			fieldNames [i+1] = template.getValueLists().getName(i) ; 
		}
		if (level < numOfTemplates.length -1 ) { 
			for (int i = 0 ; i < numOfTemplates [level+1] ; i ++ ) {
				fieldNames [i+1+numOfVLists ] = "T"+i ; 
			}				
		}			 

		ExWriteTable slotsXls  = new ExWriteTable (SheetNames) ; 
		slotsXls.setSheetFieldNames(null, fieldNames) ;
		for (int templateId = 0 ; templateId < numOfTemplates [level] ; templateId ++ ) { 
			ExRow row = slotsXls.addNewRowToSheet(null) ; 
			row.setCell (0, templateId ) ; 
			for (int valueId = 0 ; valueId < numOfVLists ; valueId ++ )  { 
				row.setCell(1+valueId , ValueProb2String (template , multFeatures , templateId , valueId))  ; 
			}
			if (level < numOfTemplates.length -1 ) { 
				for (int templateId1 = 0 ; templateId1 <  numOfTemplates [level+1] ; templateId1 ++ ) { 
					row.setCell (1+numOfVLists + templateId1 , multLevel [templateId][templateId1])  ; 
				}								
			}			
		}
		slotsXls.writeTable (excelFile) ;
	}
	private static String ValueProb2String(Template template ,  ArrayList<double[][]> multFeatures, int templateId, int valueId) {
		int numOfValues = template.getNumOfValues(valueId) ; 
		ValueProb [] valueProb = new ValueProb [numOfValues] ; 
		for (int i = 0 ; i < numOfValues ;  i++ ) { 
			valueProb [i] = new ValueProb (i , template.getValueLists().getValueList(valueId).getText(i),
										   multFeatures.get(valueId)[templateId][i])  ;  
		}
		Arrays.sort (valueProb , new CompareValueProb()) ; 
		final int maxVal = 50 ; 
		String r ; 
		r = "" ; 
		for (int i = 0 ; i <  Math.min(valueProb.length , maxVal) ;  i++ ) { 

			r += valueProb[i].toString()+"; " ;  
		}
		return r ; 	
	}
}
