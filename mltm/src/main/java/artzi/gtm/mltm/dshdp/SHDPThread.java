package artzi.gtm.mltm.dshdp;

import java.util.ArrayList;
import java.util.Random;

import artzi.gtm.mltm.mlshdp.MLSHDPParms;
import artzi.gtm.topicModelInfra.counters.DCounters;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.topicModelInfra.efficientGibbs.EfficientGibbs;
import artzi.gtm.topicModelInfra.hdpInfra.MapMixtureComponents;
import artzi.gtm.topicModelInfra.hdpInfra.StickBreakingWeights;
import artzi.gtm.topicModelInfra.logProportions.Proportions;
import artzi.gtm.utils.elog.EL;


public class SHDPThread extends Thread{
	MLSHDPParms parms ; 
	int level ;	
	int threadId ; 
	/******  documents ********/
	int numOfRootDocs ; 
	ArrayList <DocWords> docList ; 
	int numOfThreadDocs , numOfThreadWords ;  
	int [] rootDocIndx  ; 
	ArrayList <int []> docWordsMixList ; 
	/****   mixes ,virtual terms *****/ 
	int numOfMixs ; 
	int numOfVTerms ; 
	MapMixtureComponents mapMixs ; 	
	/****  Mix Counters  *******/ 
	DCounters mixVTermCounters ; 
	DCounters mixVTermSum ; 
	/******  model parameters  ******/ 
	StickBreakingWeights stickBreakingWeights ;
	double  alpha0 , lambda , lambdaNumOfVTerms ; 
	double [] alpha0StickBreakingWeights , upperLevelAlpha0StickBreakingWeights ; 
	EfficientGibbs efficientGibbs ; 

	public SHDPThread (int threadId , int level ,  int numOfThreadWords , ArrayList <DocWords> docList , int[] rootDocIndx ,
			int numOfThreadDocs, int numOfMixs , int numOfVTerms ) { 
		parms = MLSHDPParms.getInstance() ; 
		this.threadId = threadId ; 
		this.level = level ; 
		this.numOfThreadWords = numOfThreadWords ; 
		this.docList = docList ; 
		this.rootDocIndx = rootDocIndx ; 
		this.numOfThreadDocs = numOfThreadDocs ; 

		/****   mixes , terms.   in level == 0 - numOfMixs = numOfThreadDocs *****/
		this.numOfMixs = numOfMixs ;    
		this.numOfVTerms = numOfVTerms ; 
		mixVTermCounters = new DCounters (numOfMixs, numOfVTerms) ; 
		mixVTermSum = new DCounters (numOfMixs) ;  

		/******  model parameters  ******/ 	
		if (level == parms.modelLevels) { 
			lambda = parms.lambda ; 	
			lambdaNumOfVTerms = lambda * numOfVTerms ; 
		}
		else { 
			alpha0 = parms.alpha0[level] ;  
			stickBreakingWeights = new  StickBreakingWeights () ; 
		}
		EL.W(  "init thread " + threadId + " level " + level + " docs " + numOfThreadDocs ) ;   
	}

	public void initMixs(SHDPThread upperLevelHDP) throws Exception {
		Random random = new Random () ; 
		docWordsMixList = new ArrayList <int []> () ; 
		for (int docIndx= 0 ; docIndx < numOfThreadDocs ; docIndx++ ) { 
			int numOfDocWords = docList.get(rootDocIndx[docIndx]).getNumOfWords() ; 
			int [] wordMix = new int [numOfDocWords] ; 
			docWordsMixList.add (wordMix) ;
			DocWords doc = null ;  
			if (level == parms.modelLevels) { 
				doc = docList.get (rootDocIndx[docIndx]) ;  
			}
			for (int wordIndx = 0 ; wordIndx < numOfDocWords ; wordIndx ++) { 
				int newMixId = random.nextInt(numOfMixs) ; 
				docWordsMixList.get(docIndx)[wordIndx] = newMixId ; 
				upperLevelHDP.add1VMix(docIndx , wordIndx , newMixId) ; 					 
				if (level ==parms.modelLevels) {					
					int vtermId = doc.getWordArray()[wordIndx] ;  				
					mixVTermCounters.add1(newMixId , vtermId) ; 
					mixVTermSum.add1(newMixId) ; 
				}				
			}
		}
		if (useEfficientGibbs())  { 
			efficientGibbs = new EfficientGibbs (level , upperLevelHDP.getMixVTermCounters() , mixVTermCounters , mixVTermSum) ; 
		}
	}

	public void extendMixs(int newMaxMixs) throws Exception {
		if (newMaxMixs > numOfMixs) { 
			numOfMixs = newMaxMixs ; 			
		}		
	}
	public void extendVMixs(int  maxMixsInV) {
		numOfVTerms = maxMixsInV ; 
		if (level == parms.modelLevels) { 
			lambdaNumOfVTerms = lambda * numOfVTerms ; 
		}
		if (level < parms.modelLevels) { 
			stickBreakingWeights.extendMixs(numOfVTerms) ; 	
		}
		if (useEfficientGibbs() & level ==1) efficientGibbs.extendVTerms(numOfVTerms);
	}

	public void copyCounters(SHDPRoot hdpRoot, SHDPRoot nextLevelHdp) throws Exception {
		if (level > 0 ) { 
			numOfMixs = hdpRoot.getNumOfMixs() ; 
			mapMixs = hdpRoot.getMapMixs(threadId) ; 
			for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ )  { 
				int rootMixId ; 
				rootMixId = mapMixs.getRootMix(mixId) ; 
				mixVTermSum.copyCell(mixId , hdpRoot.getMixVTermSum() , rootMixId) ; 
				for (int vtermId = 0 ; vtermId < numOfVTerms ; vtermId ++) { 				 
					int rootVTermId ; 
					if (nextLevelHdp == null ) 	rootVTermId = vtermId ; 
					else  						rootVTermId =  nextLevelHdp.getMapMixs(threadId).getRootMix(vtermId) ; 
					mixVTermCounters.copyCell(mixId , vtermId , hdpRoot.getMixVTermCounters()  , rootMixId , rootVTermId)   ; 
				}
			}
			if (level == parms.modelLevels) { 
				this.lambda = hdpRoot.getLambda () ; 		 
				this.lambdaNumOfVTerms = lambda * numOfVTerms ; 
			}
			else {  
				stickBreakingWeights.copy (hdpRoot.getStickBreakingWeights () , nextLevelHdp.getMapMixs(threadId)) ; 
				this.alpha0 = hdpRoot.getAlpha0 () ; 			
			}
		}
	}

	public int getNumOfMixs() {
		return this.numOfMixs ; 
	}
	public void inferMixs(SHDPThread[] hdps) throws Exception {
		long start = System.currentTimeMillis() ;
		if (level < parms.modelLevels) { 
			alpha0StickBreakingWeights =  getAlpha0StickBreakingWeights() ; 
		}
		
		upperLevelAlpha0StickBreakingWeights = hdps [level-1].getAlpha0StickBreakingWeights() ; 
		if (useEfficientGibbs()) { 
			if (level == 1) { 				
				efficientGibbs.initIteration
				(numOfMixs, numOfVTerms, alpha0, lambda, lambdaNumOfVTerms, upperLevelAlpha0StickBreakingWeights);
			}
			else { 				
				int numOfUpperLevelMixs = hdps[level-1].getNumOfMixs() ; 
				efficientGibbs.initIteration
				(numOfUpperLevelMixs , numOfMixs, numOfVTerms, alpha0, lambda, lambdaNumOfVTerms, upperLevelAlpha0StickBreakingWeights);
			}
		}
		for (int docIndx = 0 ; docIndx < numOfThreadDocs  ; docIndx ++ ) { 
			int [] wordMixArray = docWordsMixList.get(docIndx) ;  
			int [] VTermArray ; 
			if (level == parms.levels-1) { 
				VTermArray = docList.get(rootDocIndx[docIndx]).getWordArray() ; 
			}
			else {  
				VTermArray = hdps[level+1].getWordMixArray(docIndx) ; 
			}
			int numOfDocWords = VTermArray.length ;  
			if (useEfficientGibbs() &  level==1) efficientGibbs.initDoc(docIndx) ; 
			for (int wordIndx = 0 ; wordIndx < numOfDocWords ; wordIndx ++) { 
				int vtermId = VTermArray[wordIndx] ; 
				int lastMixId = wordMixArray[wordIndx] ; 
				mixVTermCounters.dec1(lastMixId , vtermId) ; 
				mixVTermSum.dec1(lastMixId) ;
				hdps[level-1].dec1VMix(docIndx , wordIndx ,  lastMixId) ;
				int newMixId ; 
				if (useEfficientGibbs() ) { 
					if (level == 1) { 
						efficientGibbs.updateCounters(docIndx, lastMixId, vtermId);
					}
					else { 
						int upperLevelMixId = hdps[level-1].getMixId(docIndx , wordIndx) ; 
						efficientGibbs.updateCounters(upperLevelMixId, lastMixId, vtermId);
					}
				}				
				newMixId = inferNewMix (docIndx , wordIndx , vtermId ,  hdps[level-1]) ;				 
				wordMixArray[wordIndx] = newMixId ; 
				mixVTermCounters.add1(newMixId , vtermId) ; 
				mixVTermSum.add1(newMixId) ; 
				hdps[level-1].add1VMix(docIndx , wordIndx ,  newMixId) ; 
				if (useEfficientGibbs()) { 
					if (level == 1) { 
						efficientGibbs.updateCounters(docIndx, lastMixId, vtermId);
					}
					else { 
						int upperLevelMixId = hdps[level-1].getMixId(docIndx , wordIndx) ; 
						efficientGibbs.updateCounters(upperLevelMixId, lastMixId, vtermId);
					}
				}	
			}			
		}
		long time = System.currentTimeMillis() - start ; 
		System.out.println("level: "+ level +  "Thread time "+ time);
	}	

	private void add1VMix(int docIndx, int wordIndx , int nextLevelMixId) throws Exception {
		int mixId = getMixId (docIndx , wordIndx) ;  		
		mixVTermCounters.add1(mixId , nextLevelMixId) ; 
		mixVTermSum.add1(mixId) ;		
	}

	private void dec1VMix(int docIndx, int wordIndx ,  int nextLevelMixId) throws Exception {
		int mixId = getMixId (docIndx , wordIndx) ;  		
		mixVTermCounters.dec1(mixId , nextLevelMixId) ; 
		mixVTermSum.dec1(mixId) ;		
	}	 

	private int getMixId(int docIndx , int wordIndx) {
		int mixId ; 
		if 	(level == 0) mixId = docIndx ; 
		else  			 mixId = docWordsMixList.get(docIndx)[wordIndx] ;  
		return mixId ; 
	}

	private int inferNewMix(int docIndx, int wordIndx, int vtermId,  SHDPThread upperLevelHdp ) throws Exception {
		int newMixId = -1 ,  sampleMixId = -1 ; 		 
		if (useEfficientGibbs() ) {
			if (level == 1) { 
				sampleMixId = efficientGibbs.inferNewMix(vtermId) ; 
			}
			else { 
				int upperLevelMixId =upperLevelHdp.getMixId(docIndx , wordIndx) ; 
				sampleMixId = efficientGibbs.inferNewMix(upperLevelMixId , vtermId) ; 
			}
		}
		else { 
			Proportions proportions = new Proportions () ; 			
			for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
				if (mixVTermSum.get(mixId)  > 0) { 
					int upperLevelMixId = upperLevelHdp.getMixId(docIndx, wordIndx) ; 
					double prop = upperLevelHdp.getMixVTermCounters().get(upperLevelMixId, mixId) 
							      + upperLevelAlpha0StickBreakingWeights[mixId] ; 
					if (level == parms.modelLevels) { 			 
						prop  = prop * (mixVTermCounters.get(mixId , vtermId) + lambda) ; 
						prop  = prop / (mixVTermSum.get(mixId) + lambdaNumOfVTerms) ; 	
					}
					else { 
						prop  = prop *  (mixVTermCounters.get(mixId , vtermId) + (alpha0StickBreakingWeights[vtermId] )) ; 
						prop =  prop /   (mixVTermSum.get(mixId) + alpha0) ; 
					}
					proportions.add(mixId, prop) ; 
				}
			}
			double newMixProp = upperLevelAlpha0StickBreakingWeights[numOfMixs]  ; 
			if (level == parms.modelLevels) { 	
				newMixProp = newMixProp /numOfVTerms  ; 
			}
			else { 
				newMixProp  = newMixProp * stickBreakingWeights.getWeight(vtermId); 	
			}
			proportions.add(numOfMixs , newMixProp) ;			
			sampleMixId = proportions.sample() ;
			/*
			 
			if (level ==1 & sampleMixId >= numOfMixs) {
				proportions.printP1();
				EL.W("Sampled Mix " +sampleMixId );
			}	
			*/ 	 
		}
		
		if (sampleMixId < numOfMixs) { 	 
			newMixId = sampleMixId ; 
		}  
		else { 
			newMixId = openNewMix (upperLevelHdp) ; 
		}		
		return newMixId ; 
	}	

	public double [] getAlpha0StickBreakingWeights () { 
		double [] alpha0StickBreakingWeights = new double [numOfVTerms+1] ; 
		for (int vtermId = 0 ; vtermId < numOfVTerms ; vtermId ++ ) { 
			alpha0StickBreakingWeights [vtermId] = alpha0 * stickBreakingWeights.getWeight(vtermId) ;  
		}		
		alpha0StickBreakingWeights [numOfVTerms] = alpha0 * stickBreakingWeights.getWeightNew() ; 
		return alpha0StickBreakingWeights ; 		
	}

	private int openNewMix(SHDPThread upperLevelHdp) throws Exception {
		int newMixId = -1 ;   	 
		for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
			if (mixVTermSum.get(mixId) == 0) { 
				newMixId = mixId ; 
				upperLevelHdp.getStickBreakingWeights().reUseMix(newMixId) ; 			 
				break ; 
			}
		}
		if (newMixId < 0) { 
			newMixId = numOfMixs ; 	
			numOfMixs ++ ;					
			upperLevelHdp.extendVMixs(numOfMixs) ; 
			upperLevelAlpha0StickBreakingWeights = upperLevelHdp.getAlpha0StickBreakingWeights() ; 
			if ( useEfficientGibbs() ) {
				efficientGibbs.extendMixs(upperLevelAlpha0StickBreakingWeights, numOfMixs);
			}
		}
		return newMixId ; 		 
	}	

	public void validateCounters () throws Exception { 
		EL.W ( " Validate Counters. Level:" + level  + " Thread " + threadId + " numOfMixs:  " + numOfMixs + " vterms: " + numOfVTerms );
		for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
			int sum = 0 ; 
			for (int vtermId= 0 ;  vtermId < numOfVTerms ; vtermId ++ ) { 
				sum += mixVTermCounters.get(mixId , vtermId) ; 				
			}
			if (mixVTermSum.get(mixId) != sum) { 
				EL.WF(2234 , "level: " + level +  " Bad mix Term Counter. mix: " + mixId +  " sumCounter: "  + mixVTermSum.get(mixId) + " sum: " + sum) ; 
			}
		}
	}
	
	private boolean useEfficientGibbs () { 
		return (parms.efficientGibbs & level == parms.modelLevels ) ;  
	}

	public DCounters getMixVTermCounters() {
		return mixVTermCounters ;
	}	
	public DCounters getMixVTermSum() {
		return mixVTermSum ;
	}
	public StickBreakingWeights getStickBreakingWeights() {
		return stickBreakingWeights ; 
	}
	public int [] getWordMixArray (int docIndx) { 
		return docWordsMixList.get(docIndx) ; 
	}

	public int[] getRootDocIndx() {
		return this.rootDocIndx;
	}
}