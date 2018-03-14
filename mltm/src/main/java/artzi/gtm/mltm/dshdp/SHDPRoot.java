package artzi.gtm.mltm.dshdp;

import java.util.ArrayList;

import artzi.gtm.mltm.mlshdp.MLSHDPParms;
import artzi.gtm.topicModelInfra.counters.DCounters;
import artzi.gtm.topicModelInfra.counters.SumDCounters;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.topicModelInfra.gibbsMath.HDPHyperParms;
import artzi.gtm.topicModelInfra.gibbsMath.LDAHyperParms;
import artzi.gtm.topicModelInfra.hdpInfra.MapMixtureComponents;
import artzi.gtm.topicModelInfra.hdpInfra.StickBreakingWeights;
import artzi.gtm.utils.elog.EL;


/**
 * 
 * @author Ittai Artzi
 *
 */
public class SHDPRoot {
	MLSHDPParms parms ; 
	int level ; 	
	/******  documents ********/
	ArrayList <DocWords> docList ;
	int numOfDocs  , numOfWords ; 
	int [] numOfDocWords ; 	
	int [] docThread ; 	
	int [] docThreadId ; 
	int [] numOfThreadDocs  ; 
	/****   mixes , virtual terms *****/ 
	int numOfMixs, numOfVTerms ; 
	MapMixtureComponents [] mapMixs ;	
	/****  Mix Counters  *******/ 
	DCounters mixVTermCounters ; 
	DCounters mixVTermSum ; 
	DCounters saveMixVTermCounters ; 
	DCounters saveMixVTermSum ; 
	SumDCounters sumMixVTermCounters , sumMixVTermSum ; 
	double [][] mixVTermProb ; 
	
	/******  model parameters  ******/ 
	double gamma , alpha0 ; 
	StickBreakingWeights stickBreakingWeights ;
	double [] vtermWeights ; 
	double lambda , lambdaNumOfVTerms; 	
			
	public SHDPRoot (int level , int numOfWords , ArrayList <DocWords> docList , int[] numOfDocWords, int numOfMixs, int numOfVTerms) throws Exception { 
		parms = MLSHDPParms.getInstance() ; 
		this.level = level ; 
		/******  documents ********/
		this.numOfWords = numOfWords ; 
		this.docList = docList ; 
		this.numOfDocs = docList.size () ; 
		this.numOfDocWords = numOfDocWords ; 	
		/****   mixes , terms *****/ 
		this.numOfMixs = numOfMixs ; 
		this.numOfVTerms = numOfVTerms ;
		if (level > 0) { 
			mapMixs = new MapMixtureComponents [parms.numOfThreads] ; 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				mapMixs [threadId] = new MapMixtureComponents() ; 
			}
			for (int mixId = 1 ; mixId < numOfMixs ; mixId++ )   { 
				for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
					mapMixs[threadId].mapMixs(mixId, mixId) ; 					 
				}
			}
		}
		/******  mix counters *******/ 
		mixVTermCounters = new DCounters (numOfMixs , numOfVTerms) ; 
		mixVTermSum = new DCounters (numOfMixs ) ; 
		sumMixVTermCounters = null ; 
		sumMixVTermSum = null ; 
		if (level == 0) { 
			for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
				mixVTermSum.addZ (mixId , numOfDocWords[mixId]) ; 
			}
		}
		if (level > 0) { 
			saveMixVTermCounters = new DCounters (numOfMixs , numOfVTerms) ; 
			saveMixVTermSum = new DCounters (numOfMixs ) ; 
		}
		/******  model parameters  ******/ 			
		if (level == parms.modelLevels) { 
			lambda = parms.lambda ; 	
			lambdaNumOfVTerms = lambda * numOfVTerms ; 
		}
		else { 
			gamma = parms.gamma[level]   ;  
			alpha0 = parms.alpha0[level] ;  
			stickBreakingWeights = new  StickBreakingWeights (parms.gamma[level]) ; 
		}
	}	
	
	public void extendMixs(int maxMixs) {
		if (maxMixs > numOfMixs) { 
			for (int mixId = numOfMixs ; mixId < maxMixs ; mixId++ )   { 
				for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
					mapMixs[threadId].mapMixs(mixId, mixId) ; 					 
				}
			}
			numOfMixs = maxMixs ; 				
		}
	}
	public void extendVTerms (int  maxTermsInV) {
		numOfVTerms = maxTermsInV ; 
		if (level <  parms.modelLevels) { 
			stickBreakingWeights.extendMixs(numOfVTerms) ; 
		}
	}
		 
	public void aggregateCounters (int threadId , SHDPThread hdpThread, MapMixtureComponents mapNextLevelMixs) throws Exception{ 
		if (level == 0) { 			
			int [] rootDocIndx = hdpThread.getRootDocIndx () ; 
			for (int threadMixId = 0 ; threadMixId  <rootDocIndx.length ; threadMixId  ++ ) { 
				int rootMixId = rootDocIndx[threadMixId] ; 
				for (int nextLevelTermId = 0 ; nextLevelTermId < numOfVTerms ; nextLevelTermId ++) {
					int threadVTermId ; 
					threadVTermId = mapNextLevelMixs.getThreadMix(nextLevelTermId ) ; 			 
					mixVTermCounters.copyCell (rootMixId , nextLevelTermId , hdpThread.getMixVTermCounters() , threadMixId , threadVTermId) ; 
				}
			}
		}
		else { 		 	
			for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
				int threadMixId = mapMixs[threadId].getThreadMix(mixId) ; 
				int delta ; 
				delta = hdpThread.getMixVTermSum().get(threadMixId) - saveMixVTermSum.get(mixId) ; 
				mixVTermSum.addZ (mixId, delta);
				for (int nextLevelTermId = 0 ; nextLevelTermId < numOfVTerms ; nextLevelTermId ++) {
					int threadVTermId ; 
					if (mapNextLevelMixs == null) threadVTermId = nextLevelTermId ; 
					else  							threadVTermId = mapNextLevelMixs.getThreadMix(nextLevelTermId ) ; 			 
					delta = hdpThread.getMixVTermCounters().get(threadMixId , threadVTermId) -
							saveMixVTermCounters.get(mixId , nextLevelTermId) ; 
					mixVTermCounters.addZ (mixId , nextLevelTermId , delta) ; 
				}
			}
		}		 
		if (level < parms.modelLevels) { 
			stickBreakingWeights.sumWeights(hdpThread.getStickBreakingWeights(), mapNextLevelMixs, parms.numOfThreads) ; 	
		}
	}
	
	public void copmuteStickBreakingWeights() {
		stickBreakingWeights.updateWeights(numOfVTerms, numOfMixs , mixVTermCounters.getMat() ,  alpha0 , gamma) ; 
		double [] w = stickBreakingWeights.getWeights() ; 
		String s = "Level " + level +  " Stick Breaking Weights " ; 
		for (int i = 0 ; i < w.length ; i ++ ) { 
			s += " " + i +":"+ w [i] ; 
		}
		EL.W(s) ; 

	}
	public void resetStickBreakingWeights() {
		if (level < parms.modelLevels) stickBreakingWeights.resetWeights () ; 			 
	}
	public void sampleParms (int iter) { 
		/******  sample  model parameters *******/
		if (level == parms.modelLevels) {  
			if (iter > parms.burninIters & iter % parms.sampleParmsIters == 0 ) { 
				double lambdaNew = LDAHyperParms.sampleBeta(mixVTermCounters.getMat () , lambda ) ; 
				EL.W(" sample lambda - old "  + lambda + " new - " + lambdaNew ) ; 
				lambda = lambdaNew ; 
				lambdaNumOfVTerms = lambda * numOfVTerms ; 
			}
		}
		else { 
			if  ( iter < 20  |iter % parms.sampleParmsIters == 0 ) { 
				EL.W("Level:" + level + " VTerms " +numOfVTerms  + " Tot mix tables  " +  stickBreakingWeights.getTotMixTables()) ;  
				double gammaNew = HDPHyperParms.sampleGamma(gamma, numOfVTerms , stickBreakingWeights.getTotMixTables() , 
						parms.aGamma[level] , parms.bGamma[level]  ) ; 
				EL.W(" Sample Gamma  - Old :  " + gamma  + " new - "   + gammaNew ) ; 
				gamma  = gammaNew ; 
				double alpha0New = HDPHyperParms.sampleAlpha0 (alpha0,   stickBreakingWeights.getTotMixTables() , numOfMixs ,mixVTermSum.getMat()[0] , 
						parms.aAlpha0[level] ,parms.bAlpha0[level]) ; 
				EL.W(" Sample Alpha0  - Old :  " + alpha0  + " new - "   + alpha0New ) ; 
				alpha0  = alpha0New ;
			}
		}
	}	 
	public StickBreakingWeights getStickBreakingWeights() {
		return stickBreakingWeights;
	}
	public void sumCounters() throws Exception {
		if (sumMixVTermCounters == null ) { 
			sumMixVTermCounters = new SumDCounters (mixVTermCounters.getMaxX() ,mixVTermCounters.getMaxY() ) ;  
		}
		if (sumMixVTermSum == null ) { 
			sumMixVTermSum = new SumDCounters (mixVTermSum.getMaxY() ) ; 
		}
		sumMixVTermCounters.sum(mixVTermCounters);
		sumMixVTermSum.sum(mixVTermSum);		
	}	
	
	public double [][] computeMultinomials () throws Exception { 		
		mixVTermProb = new double [numOfMixs][numOfVTerms] ;
		if (level < parms.modelLevels) { 
			vtermWeights = new double [numOfVTerms]  ; 
			double [] stb= stickBreakingWeights.getWeights () ; 
			double sumVTermWeights = 1-stb[numOfVTerms] ; 
			for  (int vtermIndx = 0 ; vtermIndx < numOfVTerms; vtermIndx ++  ) { 
				vtermWeights [vtermIndx] = stb[vtermIndx] + stb[numOfVTerms]*(stb[vtermIndx]/sumVTermWeights) ;  
			}
		}
		if (sumMixVTermCounters == null) { 
			for (int  mixIndx = 0 ; mixIndx <  numOfMixs ; mixIndx ++ ) { 
				for (int vtermIndx = 0 ; vtermIndx < numOfVTerms; vtermIndx ++  ) { 
					if (level ==  parms.modelLevels) { 
						mixVTermProb [mixIndx][vtermIndx] = 
								(1.0*mixVTermCounters.get (mixIndx,vtermIndx) + lambda ) /
								(1.0*mixVTermSum.get(mixIndx) + lambdaNumOfVTerms) ; 
					}
					else { 					
						mixVTermProb [mixIndx][vtermIndx] = 
								(1.0*mixVTermCounters.get (mixIndx,vtermIndx) + alpha0*vtermWeights[vtermIndx] ) /
								(1.0*mixVTermSum.get(mixIndx) + alpha0) ; 
					}
				}
			}
		}
		else { 
			for (int  mixIndx = 0 ; mixIndx <  numOfMixs ; mixIndx ++ ) { 
				for (int vtermIndx = 0 ; vtermIndx < numOfVTerms; vtermIndx ++  ) { 
					if (level ==  parms.modelLevels) { 
						mixVTermProb [mixIndx][vtermIndx] = 
								(sumMixVTermCounters.getAverage (mixIndx,vtermIndx) + lambda ) /
								(sumMixVTermSum.getAverage(mixIndx) + lambdaNumOfVTerms) ; 
					}
					else { 
						mixVTermProb [mixIndx][vtermIndx] = 
								(sumMixVTermCounters.getAverage(mixIndx,vtermIndx) + alpha0*vtermWeights[vtermIndx] ) /
								(sumMixVTermSum.getAverage(mixIndx) + alpha0) ; 
					}
				}
			}
		}
		return mixVTermProb ; 
	}
				
	public void validateCounters () throws Exception { 
		int sumWords = 0 ; 
		for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
			int sum = 0 ; 
			for (int vtermId = 0 ;  vtermId < numOfVTerms ; vtermId ++ ) { 
				sum += mixVTermCounters.get(mixId , vtermId) ; 				
			}
			if (mixVTermSum.get(mixId) != sum) { 
				EL.WF(2234 ,  " Bad mix VTerm Counter + mix: " + mixId +  " sumCounter: " +  
						mixVTermSum.get(mixId) + " sum: " + sum) ; 
			}
			sumWords += sum ; 
		}
		if (sumWords != numOfWords) {
			EL.WF(22341 ,  " Level:" + level +  " bad tot word counter: " + " num Of Words:" + numOfWords + " sum Words:" + sumWords  );
		}
	}

	public void printCounters() {
		// TODO Auto-generated method stub		
	}
		
	private void printMult () { 
		printMat (" Mix " , " VTerm " , mixVTermProb ) ;  ;  
	}
	
	private void printMat (String h1 , String  h2 , double [][] mat) { 
		int m1 = mat.length   ; 
		int m2 = mat[0].length ; 
		System.out.println ("LLL " + m2) ; 
		EL.W("******************* " + h1) ; 
		for (int i = 0 ; i < m1 ; i ++ ) { 
			String s = h2 + " " +  i + " - "; 
			for (int j = 0 ; j < m2 ; j ++ ) { 
				s += " " + j + ": " + mat [i][j]  ; 
				
			}
			EL.W(s) ; 
		}
	}
	
	private void printP(double[] p) {
		String s = " prop " ;  
		for (int i = 0 ; i < p.length ; i ++ ) { 
			s += " " + i + "-"+ p[i] ; 
		}
		EL.W(s) ; 		
	}
	public int getNumOfMixs () { 
		return this.numOfMixs ; 
	}
	
	public double[][] getMixVTermProb() {
		return this.mixVTermProb ; 		
	}

	public MapMixtureComponents  getMapMixs (int threadId) { 
		return this.mapMixs [threadId] ; 
	}	

	public void saveCounters() throws Exception {
		saveMixVTermCounters.copy(mixVTermCounters) ; 
		saveMixVTermSum.copy(mixVTermSum) ; 			
	}

	public DCounters getMixVTermSum() {
		return mixVTermSum ; 
	}

	public DCounters getMixVTermCounters() {
		return mixVTermCounters ; 
	}

	public double getAlpha0() {
		return alpha0 ; 
	}
	
	public double [] getVTermWeights () {
		return this.vtermWeights ; 
	}

	public double getLambda() {
		return lambda ; 
	}

	
}