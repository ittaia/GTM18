package artzi.gtm.lda.train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import artzi.gtm.topicModelInfra.dataObjects.LDADoc;
import artzi.gtm.topicModelInfra.logProportions.LogProportions;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.utils.termList.TermList;

public class LDAResults implements Serializable {	
	private static final long serialVersionUID = 1L;
	TermList termList ; 
	double alpha ; 
	double alphaTopics ; 
	double beta ; 
	int K$numOfTopics ; 
	int V$numOfTerms ;
	double [][] topicTermProb  ; 
	int [] topicCount ; 
	int [][] topicTermCount ; 
	
	public LDAResults   (TermList termList ,  int k$numOfTopics,  double alpha , double beta , 
			double[][] topicTermProb , int [] topicCount , int [][] topicTermCount ) {
		this.termList  = termList ; 
		this.K$numOfTopics = k$numOfTopics;
		this.alpha = alpha ; 
		this.beta = beta ; 
		this.alphaTopics = alpha * K$numOfTopics ; 
		this.V$numOfTerms = termList.getSize ();
		this.topicTermProb = topicTermProb ; 
		this.topicCount = topicCount ; 
		this.topicTermCount = topicTermCount ; 
	} 
	 
	public void print () {
		EL.W(" NumOfTopics " + K$numOfTopics) ; 
		EL.W(" NumOfTerms " + V$numOfTerms );
		EL.W("Alpha " + alpha + " beta "+ beta); ; 
		String [] topicTerm0 = new String [K$numOfTopics] ;  
		IndxProb [] termProbArray  = new IndxProb [V$numOfTerms] ; 
		EL.W (" Topic - Term" ); 
		for (int topicIndx = 0 ; topicIndx <K$numOfTopics  ; topicIndx ++) { 
			EL.W( "******    Topic - " + topicIndx) ; 
			for (int termIndx = 0 ; termIndx < V$numOfTerms ; termIndx ++ ) {
				termProbArray [termIndx] = new IndxProb (termIndx,topicTermProb [topicIndx][termIndx]) ;
			}			
			Arrays.sort ( termProbArray  ,  new CompareProb()) ; 
			topicTerm0 [topicIndx] = termList.getTerm(termProbArray[0].getIndx()) ; 
			for (int i = 0 ; i < V$numOfTerms; i ++ ) {
				int termIndx = termProbArray[i].getIndx() ; 
				double p = termProbArray[i].getProb() ; 
				if (p > 0.001)
					EL.W ( " Term - " + termIndx +"-"+ termList.getTerm(termIndx)+ " - " +  p ) ; 
			}
		}	
	}
	public IndxProb [] getTopicTerms (int topicIndx) { 
		IndxProb [] termProbArray  = new IndxProb [V$numOfTerms] ; 
		for (int termIndx = 0 ; termIndx < V$numOfTerms ; termIndx ++ ) {
			termProbArray [termIndx] = new IndxProb (termIndx,topicTermProb [topicIndx][termIndx]) ;
		}			
		Arrays.sort ( termProbArray  ,  new CompareProb()) ;
		return termProbArray ; 
	}
	public double [] classify (LDADoc doc) { 
		Random generator = new Random () ; 
		
		int [] wordArray = doc.getWordArray() ; 
		int nWords = wordArray.length ; 
		int [] wordTopic = new int [nWords] ; 
		int [] topicCount = new int [K$numOfTopics] ; 
		for (int topic = 0 ; topic < K$numOfTopics ; topic ++ ) { 
			topicCount [topic] = 0 ;   
		}
		for (int wordIndx = 0 ; wordIndx < nWords ; wordIndx ++ ) { 
			int topic = generator.nextInt (K$numOfTopics) ; 
			wordTopic [wordIndx] = topic ; 
			topicCount [topic] ++ ;   
		}
		for (int iter = 0 ; iter < 50 ; iter ++ ) { 
			for (int wordIndx = 0 ; wordIndx < nWords ; wordIndx ++ ) { 
				topicCount [wordTopic[wordIndx]] -- ;
				LogProportions logProportions = new LogProportions () ;  
				for (int topic = 0 ; topic < K$numOfTopics ; topic ++ ) { 					
					double logProp = Math.log ( topicTermProb[topic][wordArray[wordIndx]] )   
								   + Math.log( topicCount [topic] +  alpha ) ; 
					logProportions.add (topic , logProp) ; 
				}
				int topic = logProportions.sample () ; 
				wordTopic[wordIndx] = topic ; 
				topicCount [wordTopic[wordIndx]] ++ ;
			}
		}
		double [] topicProb = new double [K$numOfTopics] ; 
		for (int topic = 0 ; topic < K$numOfTopics ; topic ++ ) { 
			topicProb [topic] = (topicCount [topic] + alpha)  / (nWords + alphaTopics) ; 
		}
		return topicProb ; 
	}
	public void classifyPrint(LDADoc doc) {
				
	}
	public double computePerplexity (ArrayList <LDADoc> docList     ) { 
		int M$numOfDocuments = docList.size () ; 	
		double [][] docTopicProb  = new double [M$numOfDocuments][K$numOfTopics] ; 
		for  (int docIndx= 0 ; docIndx < M$numOfDocuments  ; docIndx++ ) { 
			docTopicProb [docIndx] = classify (docList.get(docIndx)) ; 
		}
		return computePerplexity (docList ,docTopicProb) ; 
	}
	public double computePerplexity (ArrayList <LDADoc> docList , double [][] docTopicProb    ) { 
		double [][] docTermProb ; 
		int numTokens = 0 ; 
		int M$numOfDocuments = docList.size () ; 
		docTermProb = new double[M$numOfDocuments][V$numOfTerms] ; 
		for (int docIndx= 0 ; docIndx < M$numOfDocuments  ; docIndx++ ) {
			for (int termIndx = 0 ; termIndx < V$numOfTerms ; termIndx ++ ) {
				double prob = 0 ; 
				for (int topicIndx = 0 ; topicIndx < K$numOfTopics ; topicIndx ++ ) { 
					prob += topicTermProb [topicIndx][termIndx] * docTopicProb[docIndx] [topicIndx] ;   
				}
				docTermProb [docIndx][termIndx] = prob ; 
			}
		}
		double pp ; 
		double sumLogProb ; 
		sumLogProb = 0 ; 
		for (int docIndx = 0 ; docIndx <  M$numOfDocuments ; docIndx ++ ) { 
			int [] wordArray = docList.get(docIndx).getWordArray() ;
			for (int wordIndx = 0 ; wordIndx < wordArray.length ; wordIndx ++ ) { 
				numTokens ++ ; 
				int termIndx = wordArray[wordIndx] ; 
				sumLogProb += Math.log (docTermProb[docIndx][termIndx]/Math.log(2)) ;  				
			}			
		}
		EL.W("Log Prob" + sumLogProb) ; 
		Double t1 = -1.0 ; 
		double t2 = t1/numTokens ; 
		
		pp = Math.pow (2 , t2 *sumLogProb) ; 
		EL.W("Perplexity " + pp ) ; 
		EL.W("Tokens " + numTokens + " Perplexity " + pp ) ; 
		return pp ; 		
	}

	public TermList getTermList() {
		return termList;
	}

	public double getAlpha() {
		return alpha;
	}

	public int getNumOfTopics() {
		return K$numOfTopics;
	}

	public int getNumOfTerms() {
		return V$numOfTerms;
	}

	public double[][] getTopicTermProb() {
		return topicTermProb;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public double getAlphaTopics() {
		return alphaTopics;
	}

	public double getBeta() {
		return beta;
	}

	public int getK$numOfTopics() {
		return K$numOfTopics;
	}

	public int getV$numOfTerms() {
		return V$numOfTerms;
	}
	
	public int [] getTopicCount () { 
		return this.topicCount ; 
	}

	public int[][] getTopicTermCount() {
		return topicTermCount;
	}		
}
