package artzi.gtm.mltm.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import artzi.gtm.mltm.mlshdp.MLSHDPModel;
import artzi.gtm.mltm.mlshdp.MLSHDPParms;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.topicModelInfra.dataObjects.LDADoc;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.termList.TermList;


public class MLTMTrain {
	MLSHDPParms parms ; 
	ArrayList <LDADoc> ldaDocList ; 
	ArrayList <DocWords> docWordsList ; 
	MLSHDPModel mlshdpModel ; 
	TermList terms , activeTerms  ;
	public MLTMTrain (String parmPath) throws IOException { 
		parms = MLSHDPParms.getInstance(parmPath) ; 
		parms.print(); 
		terms = TermList.GetInstance() ; 
		ldaDocList = new ArrayList <LDADoc> () ; 
		docWordsList = new ArrayList <DocWords> () ; 
	}
	
	public void addDoc (String name , String header , String text) throws Exception { 
		int docID = ldaDocList.size() ; 
		LDADoc ldaDoc = new LDADoc (docID , name  , header , text , terms) ; 
		ldaDocList.add(ldaDoc) ; 		 
	}
	
	public void trainModel () throws Exception  { 
		System.out.println ("Terms "+ terms.getMaxTerm()) ; 
		activeTerms = terms.initActive (parms.minWordCount , parms.maxDf) ;
		System.out.println ("Active Terms "+ activeTerms.getMaxTerm()) ; 
		for (LDADoc ldaDoc : ldaDocList ) { 
			ldaDoc.initWordVector(terms) ; 
			if (ldaDoc.isEmpty() ) { 
				EL.WE( 777 ,  "Empty doc "  + ldaDoc.getName()     ) ; 
			}
			else { 
				DocWords docWords = new DocWords (ldaDoc.getDocId() , ldaDoc.getWordArray()) ; 
				docWordsList.add(docWords) ; 
			}
		}		
		
		mlshdpModel = new MLSHDPModel (docWordsList ,  activeTerms.getSize()) ; 
		mlshdpModel.infer(parms.iters) ; 
		EL.W ("End MLTM Train") ;
	}

	public int[] getNumOfTopics() {
		return mlshdpModel.getNumOfMixs () ; 
	}

	public TermList getActiveTermList() {
		return this.activeTerms ; 
	}

	public ArrayList <double [][]> getMultinomilas() {
		return mlshdpModel.getMultinomials() ; 
	}
	public double getLambda () { 
		return mlshdpModel.getLambda() ; 
	}	
	public TrainedMLModel save(String path) throws IOException {
		String termPath = new File (path, "terms.json").getPath() ; 
		activeTerms.toFile(termPath);
		TrainedMLModel tmodel =TrainedMLModel.getInstance  (activeTerms , parms.levels , mlshdpModel.getNumOfMixs() , mlshdpModel.getMultinomials() , 
				mlshdpModel.getMixVtermsCounters() , mlshdpModel.getMixVtermsSum() , 
				mlshdpModel.getAlpha0() , mlshdpModel.getMixWeights()) ;  
		String tmodelPath = new File (path, "tmodel.json").getPath() ; 
		tmodel.toFile(tmodelPath);
		return tmodel ; 
	}
}