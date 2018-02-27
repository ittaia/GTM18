package artzi.gtm.mltm.mlshdp;

import java.util.Arrays;
import java.util.Random;

import artzi.gtm.utils.termList.TermList;

public class FastLDAClassifier {
	int numOfTopics ; 
	int numOfTerms ; 
	TermList termList ; 
	int [] topicCount ; 
	int [][] topicTermCount ; 
	double [] alphaTopic ; 
	double alpha , beta , betaNumOfTerms ; 
	double s,r ; 
	double [] sArray , rArray , qFactorArray ; 
	Random generator ; 
	MLSHDPParms parms ; 	
	
	public FastLDAClassifier 
	(int numOfTopics, int numOfTerms, int[] topicCount, int [][] topicTermCount , double []  alphaTopic ,
			double beta ) {
		super();
		parms = MLSHDPParms.getInstance() ; 
		this.numOfTopics = numOfTopics;
		this.numOfTerms = numOfTerms;
		this.topicCount = topicCount;
		this.topicTermCount = topicTermCount ; 
		this.alphaTopic = alphaTopic ; 
		this.beta = beta ; 
		this.betaNumOfTerms = beta * numOfTerms ; 
		this.sArray = new double [numOfTopics]  ; 
		this.rArray = new double [numOfTopics]  ;
		this.qFactorArray = new double [numOfTopics]  ;
		initS () ; 
	}	


	public  double [] classify (int[] wordArray ) {
		int nWords = wordArray.length ; 	 
		int [] wordTopic = new int [nWords] ; 
		int [] docTopicCount = new int [numOfTopics] ; 
		int [] topicCountSum = new int [numOfTopics] ; 	
		int sumCount = 0 ;		
		for (int topicId= 0 ; topicId < numOfTopics ; topicId ++ ) { 
			docTopicCount[topicId] = 0 ;  
			topicCountSum [topicId] = 0 ;   
		}
		for (int wordIndx = 0 ; wordIndx < nWords ; wordIndx ++ ) { 
			int topicId = generator.nextInt(numOfTopics) ; 
			wordTopic [wordIndx] = topicId ; 
			docTopicCount[topicId] ++ ;     
		}
		for (int iter = 0 ; iter < parms.classifyIters ; iter ++) { 
			initR  (docTopicCount) ; 
			initQFactor (docTopicCount) ;
			Integer [] sortTopic = new Integer [numOfTopics] ; 
			for (int t = 0 ; t < numOfTopics ;  t ++ ) sortTopic [t] = t ; 
			Arrays.sort(sortTopic , (Integer i1, Integer i2) ->
			{ return Integer.signum (docTopicCount[i2] -docTopicCount[i1]) ; }  );				
			for (int wordIndx = 0 ; wordIndx < wordArray.length ; wordIndx ++ ) { 
				int term = wordArray [wordIndx] ; 
				int oldTopicId = wordTopic  [wordIndx] ;
				//topicCount[oldTopicId] -- ;  
				//updateS (oldTopicId ) ; 
				docTopicCount[oldTopicId] -- ;	
				//topicTermCount [oldTopicId][term] -- ; 
				updateR (oldTopicId , docTopicCount[oldTopicId]  ) ; 
				updateQFactor (oldTopicId , docTopicCount[oldTopicId]) ; 
				int newTopicId = getNewTopic (sortTopic , term) ; 
				wordTopic [wordIndx] = newTopicId ; 
				//topicCount[newTopicId] ++ ;  
				updateS (newTopicId ) ; 
				docTopicCount[newTopicId] ++ ;	
				//topicTermCount [newTopicId][term] ++ ; 
				updateR (newTopicId , docTopicCount[newTopicId]  ) ; 
				updateQFactor (newTopicId , docTopicCount[newTopicId]) ; 
			}
			if (iter > parms.classifyBurnInIters) { 
				sumCount ++ ; 		  
				for (int topicId = 0 ; topicId <  numOfTopics; topicId ++) { 
					topicCountSum[topicId] += docTopicCount[topicId] ; 
				}
			}
		}

		double [] topicProb = new double [numOfTopics] ; 
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			topicProb [topicId] = ((1.0*topicCountSum [topicId])/sumCount +alpha)  / (nWords + alpha*numOfTopics) ; 
		}
		return topicProb ; 			
	}		

	private void initS() {
		s = 0 ; 
		for (int topicId  = 0 ; topicId < numOfTopics ; topicId ++   ) { 
			sArray [topicId] = (alphaTopic[topicId] * beta) / (betaNumOfTerms + topicCount[topicId]) ;  
			s+= sArray[topicId] ; 			
		}		
	}


	private void updateS(int topicId ) {
		s -= sArray [topicId] ; 
		sArray [topicId] = (alphaTopic[topicId] * beta) / (betaNumOfTerms + topicCount[topicId]) ;  
		s+= sArray[topicId] ; 		
	}

	private void initR(int[] docTopicCount) {
		r = 0 ; 
		for (int topicId = 0 ;topicId  < numOfTopics ; topicId ++ ) { 
			rArray[topicId] = (docTopicCount[topicId] *beta) / (betaNumOfTerms + topicCount[topicId]) ; 
			r += rArray[topicId] ; 
		}		
	}
	private void updateR(int topicId , int docTopicCount ) {
		r -= rArray[topicId] ; 
		rArray[topicId] = (docTopicCount *beta) / (betaNumOfTerms + topicCount[topicId]) ; 
		r += rArray[topicId] ; 		
	}
	private void initQFactor (int [] docTopicCount) { 
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			qFactorArray [topicId] = 
					(alphaTopic[topicId]+ docTopicCount [topicId] )/(betaNumOfTerms + topicCount[topicId]) ; 
		}
	}
	private void updateQFactor (int topicId , int docTopicCount) { 
		qFactorArray [topicId] = (alphaTopic[topicId]+ docTopicCount  )/(betaNumOfTerms + topicCount[topicId]) ; 
	}
	private int getNewTopic (Integer [] sortTopic , int term) { 
		double qTerm = 0 ; 
		for (int t = 0 ; t <numOfTopics ; t ++) 
			if (topicTermCount [t][term] > 0 ) qTerm += qFactorArray[t]*topicTermCount [t][term] ;  
		Random generator = new Random () ; 
		double u = generator.nextDouble ()*(r+s+qTerm) ;  ; 
		int newTopicId = -1 ; 
		if (u<s) {
			double sums = 0 ; 
			for (int t = 0 ; t < numOfTopics ; t ++ ) { 
				sums+= sArray[sortTopic[t]] ; 
				if (sums >= u | t== numOfTopics-1) { 
					newTopicId = sortTopic[t] ; 
					break ; 
				}
			}
		}
		else {
			u -= s ; 
			if (u < r) {
				double sumr = 0 ; 
				for (int t = 0 ; t < numOfTopics ; t ++ ) { 
					sumr+= rArray[sortTopic[t]] ; 
					if (sumr >= u | t== numOfTopics-1) { 
						newTopicId = sortTopic[t] ; 
						break ; 
					}
				}
			}
			else {
				u -= r ; 			 
				double sumq = 0 ; 
				for (int t = 0 ; t < numOfTopics ; t ++ ) { 
					if (topicTermCount [sortTopic[t]][term] > 0 ) {
						sumq += qFactorArray[sortTopic[t]] * topicTermCount [sortTopic[t]][term] ; 
						if (sumq >= u | t== numOfTopics-1) { 
							newTopicId = sortTopic[t] ; 
							break ; 
						}
					}				
				}			
			}
		}
		return newTopicId ; 
	}
}