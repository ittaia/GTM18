package artzi.gtm.lda.toxlsx;
import artzi.gtm.utils.xlsx.ExWriteTable ;
import artzi.gtm.utils.format.FormatNum;
import artzi.gtm.utils.gen.DocData;
import artzi.gtm.utils.hebrew.Hebrew;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;

import java.util.ArrayList;
import java.util.HashMap;

import artzi.gtm.topicModelInfra.trainedModel.TermProb;
import artzi.gtm.utils.xlsx.ExRow ; 
 
public class ToXlsxTopicDocs {
	ExWriteTable xlsTopics = null ; 
	static int maxTopicDocs = 10 ; 
	HashMap <String , Integer> docLines ; 
	public ToXlsxTopicDocs () { 
		docLines = new HashMap<>() ;  
		String [] SheetNames = {"Sheet1"} ;  
		String [] fieldNames = {"TopicId" ,   "Terms" ,/* "DocId",*/ "Prob", "DocName", "Header" , "Text" } ;  
		xlsTopics = new artzi.gtm.utils.xlsx.ExWriteTable (SheetNames) ; 
		xlsTopics.setSheetFieldNames(null, fieldNames) ;
		xlsTopics .setColumnWidth(null, 0, 4) ; 
		xlsTopics .setColumnWidth(null, 1, 30) ; 
		xlsTopics .setColumnWidth(null, 2, 6) ; 
		xlsTopics .setColumnWidth(null, 3, 4) ; 
		xlsTopics .setColumnWidth(null, 4, 30) ; 
		xlsTopics .setColumnWidth(null, 5, 100) ; 	
	}
		
	public void editModel(TrainedMLModel trainedModel, ArrayList<DocData> docs, ArrayList<ArrayList <IndxProb>> topicDocProb , boolean heb  ) { 
		int numOfTopics = trainedModel.getNumOfTopics() ; 
		for (int topicId = 0 ; topicId < numOfTopics; topicId ++  ) {		 
			TermProb [] topTerms = trainedModel.getTopTerms(topicId) ; 		 
			String strTerms = "" ; 
			for (TermProb  termProb : topTerms) { 
				strTerms += termProb.getTerm()+"-"+/*termProb.getTermId()+"-"+*/FormatNum.format0(termProb.getProb())+ ";  " ; 
			}
			ExRow row = xlsTopics.addNewRowToSheet(null) ;
			row.setCell (0, topicId) ;
			if (heb) { 
				row.setCellWrap (1, Hebrew.tr2heb(strTerms));
			}
			else {
				row.setCellWrap (1, strTerms);
			}
			ArrayList <IndxProb> docProb = topicDocProb.get(topicId) ; 
			docProb.sort( (ip1,ip2) -> Double.compare(ip2.getProb() ,  ip1.getProb())  ) ; 
			
						 
			int dcount = 0 ; 
			for (int i = 0 ; i < Math.min(docProb.size(), maxTopicDocs); i ++) { 
				IndxProb indxProb = docProb.get(i) ; 				
				dcount +=1 ; 
				if (dcount > 1) { 
					row = xlsTopics.addNewRowToSheet(null) ;  
				}
				
				DocData doc = docs.get(indxProb.getIndx()) ; 
				row.setCell (2,FormatNum.format0(indxProb.getProb())) ;
				row.setCell(3, doc.getFile_id());
				if (heb) {
					row.setCell(4, Hebrew.tr2heb(doc.getTitle()));	
					row.setCellWrap(5, Hebrew.tr2heb(doc.getText()));	
				}
				else {
					row.setCell(4, doc.getTitle());	
					row.setCellWrap(5, doc.getText());	
				}
			}			
		}		
	}
	
	public  void writeXlsx(String path ) {
		xlsTopics.writeTable (path) ;
	}
}
