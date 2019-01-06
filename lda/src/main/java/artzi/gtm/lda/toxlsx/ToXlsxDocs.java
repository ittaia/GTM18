package artzi.gtm.lda.toxlsx;
import artzi.gtm.utils.xlsx.ExWriteTable ;
import artzi.gtm.utils.format.FormatNum;
import artzi.gtm.utils.hebrew.Hebrew;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;

import java.util.ArrayList;
import java.util.HashMap;

import artzi.gtm.lda.train.LDADocs;
import artzi.gtm.topicModelInfra.dataObjects.DocHeader;
import artzi.gtm.topicModelInfra.trainedModel.TermProb;
import artzi.gtm.utils.xlsx.ExRow ; 
 
public class ToXlsxDocs {
	ExWriteTable xlsTopics = null ; 
	static int maxTopicDocs = 20 ; 
	static double minDocProb = 0.0 ; 
	HashMap <String , Integer> docLines ; 
	public ToXlsxDocs () { 
		docLines = new HashMap<>() ;  
		String [] SheetNames = {"Sheet1"} ;  
		String [] fieldNames = {"TopicId" ,  "Header" ,  "Terms" , "DocId", "Prob", "DocName", "Header" , "Text" } ;  
		xlsTopics = new artzi.gtm.utils.xlsx.ExWriteTable (SheetNames) ; 
		xlsTopics.setSheetFieldNames(null, fieldNames) ;
		xlsTopics .setColumnWidth(null, 0, 6) ; 
		xlsTopics .setColumnWidth(null, 1, 1) ; 
		xlsTopics .setColumnWidth(null, 2, 40) ; 
		xlsTopics .setColumnWidth(null, 3, 6) ; 
		xlsTopics .setColumnWidth(null, 4, 6) ; 
		xlsTopics .setColumnWidth(null, 5, 30) ; 
		xlsTopics .setColumnWidth(null, 6, 30) ; 
		xlsTopics .setColumnWidth(null, 7, 60) ; 	
	}
		
	public void editModel(TrainedMLModel trainedModel ) { 
		int numOfTopics = trainedModel.getNumOfTopics() ; 
		for (int topicId = 0 ; topicId < numOfTopics; topicId ++  ) {		 
			TermProb [] topTerms = trainedModel.getTopTerms(topicId) ; 		 
			String str = "" ; 
			for (TermProb  termProb : topTerms) { 
				str += termProb.getTermId()+"-"+termProb.getTerm()+"-"+FormatNum.format0(termProb.getProb())+ " " ; 
			}
			ExRow row = xlsTopics.addNewRowToSheet(null) ;
			row.setCell (0, topicId) ;
			row.setCellWrap (1, trainedModel.getHeader(topicId));
			row.setCellWrap (2, str);
			
			
		}		
	}
	public void editModelHeb(TrainedMLModel trainedModel, LDADocs ldaDocs ) { 
		int numOfTopics = trainedModel.getNumOfTopics() ; 
		 ArrayList<DocHeader> docHeaders = ldaDocs.getDocHeaders() ;  
		for (int topicId = 0 ; topicId < numOfTopics; topicId ++  ) {		 
			TermProb [] topTerms = trainedModel.getTopTerms(topicId) ; 		 
			String str = "" ; 
			for (TermProb  termProb : topTerms) { 
				str += termProb.getTerm()+"-"+/*termProb.getTermId()+"-"+*/FormatNum.format0(termProb.getProb())+ ";  " ; 
			}
			ExRow row = xlsTopics.addNewRowToSheet(null) ;
			row.setCell (0, topicId) ;
			//row.setCellWrap (1, Hebrew.tr2heb(trainedModel.getHeader(topicId)));
			row.setCellWrap (2, Hebrew.tr2heb(str));
			IndxProb [] termProbArray  = ldaDocs.getTopicDocs(topicId) ; 
			 
			int dcount = 0 ; 
			for (int i = 0 ; i < maxTopicDocs; i ++) { 
				if (termProbArray[i].getProb() < minDocProb  & dcount > 0) break ; 
				dcount +=1 ; 
				if (dcount > 1) { 
					row = xlsTopics.addNewRowToSheet(null) ;  
				}
				row.setCell (3,termProbArray[i].getIndx()) ; 
				row.setCell (4,FormatNum.format0(termProbArray[i].getProb())) ;
				DocHeader header = docHeaders.get(termProbArray[i].getIndx()) ; 
				docLines.put(header.getDocName(), row.getRowNum()) ; 
				row.setCell(5, header.getDocName());
				row.setCell(6, Hebrew.tr2heb(header.getHeader()));				
			}			
		}		
	}
	public int getLineId (String docName ) { 
		Integer lineNum= docLines.get(docName) ; 
		if (lineNum != null) return lineNum ; 
		return -1 ;
	}
	public void setDocText (int lineNum , String text) { 
		ExRow row = xlsTopics.ExGetRow(null, lineNum) ; 
		row.setCellWrap (7, Hebrew.tr2heb(text));		 
	}
	
	public  void writeXlsx(String path ) {
		xlsTopics.writeTable (path) ;
	}
}
