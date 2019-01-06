package artzi.gtm.lda.toxlsx;
import artzi.gtm.utils.xlsx.ExWriteTable ;
import artzi.gtm.utils.format.FormatNum;
import artzi.gtm.utils.hebrew.Hebrew;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.topicModelInfra.trainedModel.TermProb;
import artzi.gtm.utils.xlsx.ExRow ; 
 
public class ToXlsx {
	ExWriteTable xlsTopics = null ; 
	public ToXlsx () { 
		String [] SheetNames = {"Sheet1"} ;  
		String [] fieldNames = {"TopicId" ,  "Header" ,  "Terms"  } ;  
		xlsTopics = new artzi.gtm.utils.xlsx.ExWriteTable (SheetNames) ; 
		xlsTopics.setSheetFieldNames(null, fieldNames) ;
		xlsTopics .setColumnWidth(null, 0, 6) ; 
		xlsTopics .setColumnWidth(null, 1, 30) ; 
		xlsTopics .setColumnWidth(null, 2, 100) ; 
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
			row.setCell (1, trainedModel.getHeader(topicId));
			row.setCellWrap (2, str);
		}		
	}
	public void editModelHeb(TrainedMLModel trainedModel ) { 
		int numOfTopics = trainedModel.getNumOfTopics() ; 
		for (int topicId = 0 ; topicId < numOfTopics; topicId ++  ) {		 
			TermProb [] topTerms = trainedModel.getTopTerms(topicId) ; 		 
			String str = "" ; 
			for (TermProb  termProb : topTerms) { 
				str += termProb.getTerm()+"-"+/*termProb.getTermId()+"-"+*/FormatNum.format0(termProb.getProb())+ ";  " ; 
			}
			ExRow row = xlsTopics.addNewRowToSheet(null) ;
			row.setCell (0, topicId) ;
			row.setCell (1, Hebrew.tr2heb(trainedModel.getHeader(topicId)));
			row.setCellWrap (2, Hebrew.tr2heb(str));
		}		
	}
	
	public  void writeXlsx(String path ) {
		xlsTopics.writeTable (path) ;
	}
}
