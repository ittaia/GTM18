package artzi.gtm.lda.train;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import artzi.gtm.lda.malletutils.MInstanceList;
import artzi.gtm.lda.malletutils.Malphabet;
import artzi.gtm.lda.model.LDAModel;
import artzi.gtm.lda.model.LDAParms;
import artzi.gtm.topicModelInfra.dataObjects.DocHeader;
import artzi.gtm.topicModelInfra.dataObjects.LDADoc;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.SaveObject;
import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.utils.termList.TermList;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;

public class LDATrain {

	ArrayList <LDADoc> docList ; 
	ArrayList <String> textList ; 
	LDAModel ldaModel ; 
	LDAResults results ; 
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
		for (LDADoc doc : docList ) { 
			doc.initWordVector(terms) ; 
			if (doc.isEmpty()) { 
				EL.W(   "Empty doc "  + doc.getName()     ) ; 
			}
			else activeDocs.add(doc) ; 
		}	
		docList = activeDocs ; 
		mAlphabet = new Malphabet() ; 
		mAlphabet.init (activeTerms) ; 
		MInstanceList mInstanceList = new MInstanceList (mAlphabet) ; 
		for (LDADoc doc : docList) { 
			DocHeader docHeader = new DocHeader (doc.getDocID() , doc.getName() , doc.getHeader ())  ; 
			mInstanceList.add (docHeader , doc.getWordArray()) ; 
		}
		ldaModel = new LDAModel (mInstanceList ,parms.numOfLDATopics , parms.alpha , parms.beta ) ; 
		
		results = new LDAResults (activeTerms ,  parms.numOfLDATopics, parms.alpha , ldaModel.getBeta() ,
				ldaModel.getTopicTermProb() , ldaModel.getTopicCount () , ldaModel.getTopicTermCount ()) ; 
		results.print () ; 	
		ldaDocs = new LDADocs (parms.numOfLDATopics, mInstanceList , ldaModel.getDocTopicProb()) ; 
		EL.W ("End LDA Train") ;
	}
	public void save (String modelPath) { 
		String file = new File (modelPath , "results").getAbsolutePath() ; 
		SaveObject.write (file , results ) ; 
		file =  new File (modelPath , "ldaDocs").getAbsolutePath() ; 
		SaveObject.write (file ,  ldaDocs) ; 
		file =  new File (modelPath , "topicInferencer").getAbsolutePath() ; 
		SaveObject.write (file , ldaModel.getTopicInferencer()) ; 
	}
	public void getPerplexity() {
		double pp = results.computePerplexity(docList , ldaModel.getDocTopicProb() ) ; 
		EL.W(" perplexity " + pp ) ; 		
	}
	public void printDocs () {
		int numOfTopics = results.getNumOfTopics() ; 	 
		String [] topicTerm0 = new String [numOfTopics] ;  
		for (int topicIndx = 0 ; topicIndx <numOfTopics  ; topicIndx ++) { 
			topicTerm0 [topicIndx] = getTopicHeader (topicIndx) ;  					 
		}
		EL.W (" --- Doc  Topic ----- " ); 
		IndxProb [] topicProbArray  = new IndxProb [numOfTopics] ;
		for (int docIndx = 0 ; docIndx < docList.size()  ; docIndx ++) { 
			EL.W(" DOC -" + docIndx + " -" + docList.get(docIndx).getName()) ; 
			for (int topicIndx = 0 ; topicIndx < numOfTopics ; topicIndx ++ ) {
				topicProbArray [topicIndx] = new IndxProb (topicIndx, ldaDocs.getDocTopicProb()[docIndx][topicIndx]   ) ;
			}
			Arrays.sort ( topicProbArray  ,  new CompareProb()) ; 
			for (int i = 0 ; i < Math.min(numOfTopics,10) ; i ++ ) { 
				int topicIndx = topicProbArray [i].getIndx() ; 
				double p = topicProbArray[i].getProb() ; 
				if (p > 0.001)
					EL.W ( " Topic -" + topicIndx + " Topic Prob - " +   p + " W0-" + topicTerm0 [topicIndx]    ) ; 
			}	
			printText (textList.get(docIndx)) ; 
		}				 
	}
	public String getTopicHeader (int topicIndx) { 
		int numOfTerms = mAlphabet.getSize() ; 
		IndxProb [] termProbArray  = new IndxProb [numOfTerms] ; 
		for (int termIndx = 0 ; termIndx < numOfTerms ; termIndx ++ ) {
			termProbArray [termIndx] = new IndxProb (termIndx,results.getTopicTermProb() [topicIndx][termIndx]) ;
		}			
		Arrays.sort ( termProbArray  ,  new CompareProb()) ; 
		String s = "" ; 
		for (int i = 0 ; i < 10 ; i ++ ) { 
			s  += mAlphabet.getText(termProbArray[i].getIndx()) ; 
			if (i < 9 ) s += "," ;  
		}
		return s ; 	
	}
	public TermList getTerms() {
		return terms ; 
	}
	
	public void printSubjectDocs (String text) throws Exception { 
		LDADoc doc = new LDADoc (0 , ""  ,"" , text , terms) ;
		doc.initWordVector(terms) ; 
		if (doc.isEmpty() ) { 
			EL.WE( 777 ,  "Empty doc "  + doc.getName()     ) ; 
		}
		else { 
			int numOfTopics = results.getNumOfTopics() ; 
			FeatureSequence wordFeatureSequence = new FeatureSequence(mAlphabet.getAlphabet() , doc.getWordArray()) ; 			  
			Instance instance = new Instance (wordFeatureSequence  , null , null , null); 
			double [] topicProb =  ldaModel.classify (instance) ;				 
			IndxProb [] topicProbArray  = new IndxProb [numOfTopics] ;
			for (int topicIndx = 0 ; topicIndx < results.getNumOfTopics() ; topicIndx ++ ) {
				topicProbArray [topicIndx] = new IndxProb (topicIndx, topicProb[topicIndx]   ) ;
			}
			Arrays.sort ( topicProbArray  ,  new CompareProb()) ; 
			EL.W (" --- Text topics   Topic ----- " ); 			 
			for (int i = 0 ; i < Math.min(topicProb.length,10) ; i ++ ) { 
				int topicIndx = topicProbArray[i].getIndx() ; 
				double p = topicProbArray[i].getProb() ; 
				if (p > 0.01)
					EL.W ( " Topic -" + topicIndx + " Topic Prob - " +   p + " W0-" + getTopicHeader (topicIndx)     ) ; 
			}			

			for (int docIndx = 0 ; docIndx < docList.size()  ; docIndx ++) { 
				double sumProb = 0 ; 
				for (int i = 0 ; i < Math.min(topicProb.length,10) ; i ++ ) {  
					double subjectProb = topicProbArray[i].getProb() ; 
					int topicIndx = topicProbArray[i].getIndx() ;
					double docTopicProb = ldaDocs.getDocTopicProb()[docIndx][topicIndx] ; 
					sumProb += docTopicProb * subjectProb ; 
				}
				if (sumProb  >  0.12  ) { 
					printDoc (docIndx , sumProb) ; 					 
				}					
			}
		}
	}

	private void printDoc(int docIndx, double sumProb) {
		EL.W(" ------------  DOC -" + docIndx + " - " + docList.get(docIndx).getName() ) ;
		EL.W (" Score =====> "+ sumProb) ; 
		int numOfTopics = results.getNumOfTopics() ; 
		IndxProb [] topicProbArray  = new IndxProb [numOfTopics] ;
		for (int topicIndx = 0 ; topicIndx < numOfTopics ; topicIndx ++ ) {
			topicProbArray [topicIndx] = new IndxProb (topicIndx, ldaDocs.getDocTopicProb()[docIndx][topicIndx]   ) ;
		}
		Arrays.sort ( topicProbArray  ,  new CompareProb()) ; 
		for (int i = 0 ; i < Math.min(numOfTopics,10) ; i ++ ) { 
			int topicIndx = topicProbArray [i].getIndx() ; 
			double p = topicProbArray[i].getProb() ; 
			if (p >= 0.1) EL.W ( " Topic -" + topicIndx + " Topic Prob - " +   p + " W0-" + getTopicHeader (topicIndx)    ) ; 
		}	
		printText (textList.get(docIndx)) ;  		 
	}
	private void printText(String string) {
		String [] va = string.split(" ") ; 
		String s = "" ; 
		for (String v : va) { 
			s += v + " " ; 
			if (s.length () > 100) { 
				EL.W(s) ; 
				s = "" ; 
			}
		}
		EL.W(s) ; 
		 
		
	}
	public void classify (String text) { 
		LDADoc doc = new LDADoc (0 , ""  ,"" , text , terms) ;
		doc.initWordVector(terms) ; 
		if (doc.isEmpty()) { 
			EL.WE( 777 ,  "Empty doc "  + doc.getName()     ) ; 
		}
		else { 
			int numOfTopics = results.getNumOfTopics() ; 
			FeatureSequence wordFeatureSequence = new FeatureSequence(mAlphabet.getAlphabet() , doc.getWordArray()) ; 			  
			Instance instance = new Instance (wordFeatureSequence  , null , null , null); 
			double [] topicProb =  ldaModel.classify (instance) ;				 
			IndxProb [] topicProbArray  = new IndxProb [numOfTopics] ;
			for (int topicIndx = 0 ; topicIndx < results.getNumOfTopics() ; topicIndx ++ ) {
				topicProbArray [topicIndx] = new IndxProb (topicIndx, topicProb[topicIndx]   ) ;
			}
			Arrays.sort ( topicProbArray  ,  new CompareProb()) ; 
			EL.W (" --- Text topics   Topic ----- " ); 			 
			for (int i = 0 ; i < Math.min(topicProb.length,10) ; i ++ ) { 
				int topicIndx = topicProbArray[i].getIndx() ; 
				double p = topicProbArray[i].getProb() ; 
				if (p > 0.001)
					EL.W ( " Topic -" + topicIndx + " Topic Prob - " +   p + " W0-" + getTopicHeader (topicIndx)     ) ; 
			}			
		} 	 

	}

	public void classify1 (String text) { 
		LDADoc doc = new LDADoc (0 , ""  ,"" , text , terms) ; 
		doc.initWordVector(terms) ; 
		if (doc.isEmpty() ) { 
			EL.WE( 777 ,  "Empty doc "  + doc.getName()     ) ; 
		}
		else { 
			int numOfTopics = results.getNumOfTopics() ; 
			double [] topicProb =  results.classify (doc) ;	
			IndxProb [] topicProbArray  = new IndxProb [numOfTopics] ;
			for (int topicIndx = 0 ; topicIndx < results.getNumOfTopics() ; topicIndx ++ ) {
				topicProbArray [topicIndx] = new IndxProb (topicIndx, topicProb[topicIndx]   ) ;
			}
			Arrays.sort ( topicProbArray  ,  new CompareProb()) ; 
			EL.W (" --- Text topics   Topic ----- " ); 			 
			for (int i = 0 ; i < Math.min(topicProb.length,10) ; i ++ ) { 
				int topicIndx = topicProbArray[i].getIndx() ; 
				double p = topicProbArray[i].getProb() ; 
				if (p > 0.001)
					EL.W ( " Topic -" + topicIndx + " Topic Prob - " +   p + " W0-" + getTopicHeader (topicIndx)     ) ; 
			}			
		} 	 	
	}
}
