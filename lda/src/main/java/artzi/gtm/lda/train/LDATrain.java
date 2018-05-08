package artzi.gtm.lda.train;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import artzi.gtm.lda.malletutils.MInstanceList;
import artzi.gtm.lda.malletutils.Malphabet;
import artzi.gtm.lda.model.LDAModel;
import artzi.gtm.lda.model.LDAParms;
import artzi.gtm.topicModelInfra.dataObjects.DocHeader;
import artzi.gtm.topicModelInfra.dataObjects.LDADoc;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.SaveObject;
import artzi.gtm.utils.termList.TermList;

public class LDATrain {

	ArrayList <LDADoc> docList ; 
	ArrayList <String> textList ; 
	LDAModel ldaModel ; 
	TrainedMLModel trainedModel ; 
	LDADocs ldaDocs ; 
	Malphabet mAlphabet ; 
	LDAParms parms ; 
	TermList terms ,activeTerms ; 
	public LDATrain (String parmsPath) throws IOException { 
		parms = LDAParms.getInstance(parmsPath) ; 
		terms = TermList.GetInstance() ; 
		docList = new ArrayList <LDADoc> () ; 
		textList = new ArrayList <String> () ; 
	}
	public void addDoc (String name , String header , String text) throws Exception { 
		int docID = docList.size() ; 
		LDADoc ldaDoc = new LDADoc (docID , name  , header , text , terms) ; 
		docList.add(ldaDoc) ; 
		textList.add(text) ; 
	}
	public void trainModel () throws Exception  { 
		activeTerms = terms.initActive(parms.minWordCount, parms.maxDf) ; 
		EL.W( " Number of active terms: "+ activeTerms.getSize());
		ArrayList <LDADoc> activeDocs = new ArrayList <> () ; 
		int totTokens = 0 ; 
		for (LDADoc doc : docList ) { 
			doc.initWordVector(terms) ; 
			if (doc.isEmpty()) { 
				EL.W(   "Empty doc "  + doc.getName()     ) ; 
			}
			else { 
				activeDocs.add(doc) ; 
				totTokens += doc.getNumOfWorsd() ; 
			}
		}
		EL.W(" number of token: " + totTokens ); 
		docList = activeDocs ; 
		mAlphabet = new Malphabet() ; 
		mAlphabet.init (activeTerms) ; 
		MInstanceList mInstanceList = new MInstanceList (mAlphabet) ; 
		for (LDADoc doc : docList) { 
			DocHeader docHeader = new DocHeader (doc.getDocID() , doc.getName() , doc.getHeader ())  ; 
			mInstanceList.add (docHeader , doc.getWordArray()) ; 
		}
		ldaModel = new LDAModel (mInstanceList ,parms.numOfLDATopics , parms.alpha , parms.beta ) ; 
		
		trainedModel = getTrainedModel () ;  
		trainedModel.printTopics () ; 
		ldaDocs = new LDADocs (parms.numOfLDATopics, mInstanceList , ldaModel.getDocTopicProb()) ; 
		EL.W ("End LDA Train") ;
	}
	private TrainedMLModel getTrainedModel () { 
		int levels = 1 ; 
		int [] numOfMixs = new int [1] ; 
		numOfMixs [0] = parms.numOfLDATopics ; 
		ArrayList <double [][]> multinomials = new ArrayList <> () ; 
		multinomials.add (ldaModel.getTopicTermProb()) ; 
		ArrayList <int [][]> mixVTermCounters = new ArrayList <> () ; 
		mixVTermCounters.add (ldaModel.getTopicTermCount()) ; 
		ArrayList <int []> mixVTermSum = new ArrayList <> () ; 
		mixVTermSum.add (ldaModel.getTopicCount()) ; 
		double [] alpha0 = new double [1]  ; 
		alpha0 [0] = parms.alpha * parms.numOfLDATopics ; 
		 
		ArrayList <double []>  mixWeights = new ArrayList <> () ;
		double [] wts = new double [parms.numOfLDATopics] ;
		double w = 1.0/parms.numOfLDATopics ;  
		for (int t = 0 ; t < parms.numOfLDATopics ;t ++) wts [t] = w ; 	
		mixWeights.add (wts) ; 
		TrainedMLModel tmodel =TrainedMLModel.getInstance  (activeTerms , levels , numOfMixs , multinomials ,  
				mixVTermCounters , mixVTermSum , alpha0 , mixWeights ) ; 
		return tmodel ; 		
	}
	public void save (String modelPath) throws IOException { 
		String file = new File (modelPath , "trainedModel").getAbsolutePath() ; 
		trainedModel.toFile(file);
		file =  new File (modelPath , "ldaDocs").getAbsolutePath() ; 
		SaveObject.write (file ,  ldaDocs) ; 
		file =  new File (modelPath , "topicInferencer").getAbsolutePath() ; 
		SaveObject.write (file , ldaModel.getTopicInferencer()) ; 
	}
}
	