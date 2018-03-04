package artzi.gtm.mltm.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import artzi.gtm.mltm.mlshdp.MLSHDPModel;
import artzi.gtm.mltm.mlshdp.MLSHDPParms;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.topicModelInfra.dataObjects.LDADoc;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.termList.TermList;


public class MLTMTrain {
	MLSHDPParms parms ; 
	ArrayList <LDADoc> ldaDocList ; 
	ArrayList <DocWords> docWordsList ; 
	MLSHDPModel mlhdpModel ; 
	TermList terms , activeTerms  ;
	public MLTMTrain (String parmPath) throws IOException { 
		parms = MLSHDPParms.getInstance(parmPath) ; 
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
		activeTerms = terms.initActive (parms.minWordCount , parms.maxDf) ; 
		for (LDADoc ldaDoc : ldaDocList ) { 
			ldaDoc.initWordVector(activeTerms) ; 
			if (ldaDoc.isEmpty() ) { 
				EL.WE( 777 ,  "Empty doc "  + ldaDoc.getName()     ) ; 
			}
			DocWords docWords = new DocWords (ldaDoc.getDocID() , ldaDoc.getWordArray()) ; 
			docWordsList.add(docWords) ; 
		}	
		
		
		mlhdpModel = new MLSHDPModel (docWordsList ,  activeTerms.getSize()) ; 
		mlhdpModel.infer(parms.iters) ; 
		EL.W ("End MLTM Train") ;
	}

	public int[] getNumOfTopics() {
		return mlhdpModel.getNumOfMixs () ; 
	}

	public TermList getActiveTermList() {
		return this.activeTerms ; 
	}

	public ArrayList <double [][]> getMultinomilas() {
		return mlhdpModel.getMultinomials() ; 
	}
	public double getLambda () { 
		return mlhdpModel.getLambda() ; 
	}	
	public void save(String path) throws IOException {
		String termPath = new File (path, "terms.json").getPath() ; 
		activeTerms.toFile(termPath);
		MLTMTrainedModel tmodel = MLTMTrainedModel.getInstance (parms.levels , mlhdpModel.getNumOfMixs() , mlhdpModel.getMultinomials() , 
				mlhdpModel.getMixVtermsCounters() , mlhdpModel.getMixVtermsSum() , 
				mlhdpModel.getAlpha0() , mlhdpModel.getMixWeights()) ;  
		String tmodelPath = new File (path, "tmodel.json").getPath() ; 
		tmodel.toFile(tmodelPath);
		
		//tmodel.print();		
	}
}