package artzi.gtm.topicModelInfra.efficientGibbs;

import java.util.Arrays;
import java.util.Random;

import artzi.gtm.topicModelInfra.counters.DCounters;

public class EfficientGibbs {
	int level ; 
	DCounters upperLevelMix_MixCounters ; 
	DCounters mixVTermCounters ; 
	DCounters mixVTermSum ;
	int numOfUpperLevelMixs ; 
	int numOfMixs ; 
	int numOfVTerms ; 
	double  alpha0 , lambda , lambdaNumOfVTerms ; 
	double[] upperLevelAlpha0StickBreakingWeights ; 
	double s,r ; 
	double [] sArray ; 
	double [] rUpperLevelMix ;
	double [][] qFactorMat , rMat ; 
	Integer [][] sortMixMat ; 
	double []   qFactorArray , rArray ;  
	Integer [] sortMix ; 
	Random generator ;	
	
	public EfficientGibbs (int level, DCounters upperLevelMix_MixCounters , DCounters mixVTermCounters, DCounters mixVTermSum) { 
		this.level = level ; 
		this.upperLevelMix_MixCounters = upperLevelMix_MixCounters ; 
		this.mixVTermCounters = mixVTermCounters ; 
		this.mixVTermSum = mixVTermSum ; 
		generator = new Random () ; 
	}
	
	public void initIteration (int numOfMixs, int numOfVTerms, double alpha0, double lambda, 
			double lambdaNumOfVTerms,  double[] upperLevelAlpha0StickBreakingWeights) throws Exception { 
		this.numOfUpperLevelMixs = -1 ; 
		this.numOfMixs = numOfMixs ; 
		this.numOfVTerms = numOfVTerms ; 
		this.alpha0 = alpha0 ; 
		this.lambda = lambda ; 
		System.out.println ( "Init iteration "+ level + "lambda "+ lambda ) ; 
		this.lambdaNumOfVTerms = lambdaNumOfVTerms ; 
		this.upperLevelAlpha0StickBreakingWeights = upperLevelAlpha0StickBreakingWeights ; 	
		initS() ; 
	}
	public void initIteration (int numOfUpperLevelMixs , int numOfMixs, int numOfVTerms, double alpha0, double lambda, 
			double lambdaNumOfVTerms,  double[] upperLevelAlpha0StickBreakingWeights) throws Exception { 
		this.numOfUpperLevelMixs = numOfUpperLevelMixs ; 
		this.numOfMixs = numOfMixs ; 
		this.numOfVTerms = numOfVTerms ; 
		this.alpha0 = alpha0 ; 
		this.lambda = lambda ; 
		this.lambdaNumOfVTerms = lambdaNumOfVTerms ; 
		this.upperLevelAlpha0StickBreakingWeights = upperLevelAlpha0StickBreakingWeights ; 	
		initS() ; 
		initRMat () ; 
		initQFactorMat () ; 
		initSortMat () ; 
	}
	public void initDoc (int upperLevelMixId) throws Exception { 		
		initR  (upperLevelMixId) ; 
		initQFactor (upperLevelMixId) ;
		sortMix = new Integer [numOfMixs+1] ; 
		initSortMix (upperLevelMixId , sortMix) ; 
	}
	public int inferNewMix ( int upperLevelMixId , int vtermId) throws Exception {
		rArray = rMat [upperLevelMixId] ; 
		qFactorArray = qFactorMat [upperLevelMixId] ; 
		sortMix = sortMixMat [upperLevelMixId] ; 
		r = rUpperLevelMix [upperLevelMixId ] ; 
		return inferNewMix (vtermId) ; 
	}
	public int inferNewMix ( int vtermId) throws Exception { 
		double qVTerm = 0 ; 
		for (int m = 0 ; m <numOfMixs ; m ++) 
			if (mixVTermCounters.get (m,vtermId) > 0 ) qVTerm += qFactorArray[m]*mixVTermCounters.get (m,vtermId) ;  		 
		double u = generator.nextDouble ()*(r+s+qVTerm) ;   
		int newMixId = -1 ; 
		if (u<s) {
			double sums = 0 ; 
			for (int m = 0 ; m <= numOfMixs ; m ++ ) { 
				sums+= sArray[sortMix[m]] ; 
				if (sums >= u | m== numOfMixs) { 
					newMixId = sortMix[m] ; 
					break ; 
				}
			}
		}
		else {
			u -= s ; 
			if (u < r) {
				double sumr = 0 ; 
				for (int m = 0 ; m < numOfMixs ; m ++ ) { 
					sumr+= rArray[sortMix[m]] ; 
					if (sumr >= u | m== numOfMixs-1) { 
						newMixId = sortMix[m] ; 
						break ; 
					}
				}
			}
			else {
				u -= r ; 			 
				double sumq = 0 ; 
				for (int m = 0 ; m < numOfMixs ; m ++ ) { 
					if (mixVTermCounters.get (sortMix[m],vtermId) > 0 ) {
						sumq += qFactorArray[sortMix[m]] * mixVTermCounters.get (sortMix[m],vtermId) ; 
						if (sumq >= u | m== numOfMixs-1) { 
							newMixId = sortMix[m] ; 
							break ; 
						}
					}				
				}			
			}
		}
		return newMixId ; 		
	}
	
	public void updateCounters (int upperLevelMixId , int mixId , int vtermId  ) throws Exception { 
		if (level > 1) { 
			rArray = rMat [upperLevelMixId] ; 
			qFactorArray = qFactorMat [upperLevelMixId] ; 
			sortMix = sortMixMat [upperLevelMixId] ; 
			r = rUpperLevelMix [upperLevelMixId] ; 
		}
		updateS (mixId ) ; 
		int upperLevelMix_MixCounter =  upperLevelMix_MixCounters.get(upperLevelMixId , mixId) ; 
		updateR (mixId , upperLevelMixId , upperLevelMix_MixCounter) ; 
		updateQFactor (mixId , upperLevelMix_MixCounter) ;
		int end = numOfMixs ; 
		while (end > 0) { 
			boolean replace = false ; 
			for (int m = 0 ; m < end ; m ++ ) { 
				if (upperLevelMix_MixCounters.get(upperLevelMixId,sortMix[m]) < upperLevelMix_MixCounters.get(upperLevelMixId,sortMix[m+1])) { 
					int save =  sortMix [m] ; 
					sortMix [m] = sortMix[m+1] ; 
					sortMix [m+1] = save ; 
					replace = true ; 					
				}				
			}
			if (!replace) break ; 
			end -- ; 
		}
	}
	public void extendMixs (double[] upperLevelAlpha0StickBreakingWeights, int newMaxMixs) throws Exception { 
		this.upperLevelAlpha0StickBreakingWeights = upperLevelAlpha0StickBreakingWeights ; 
		int oldNumOfMixs = numOfMixs ; 
		numOfMixs = newMaxMixs ;  
		double [] sNew = new double[numOfMixs+1] ; 	
			
		for (int mixId = 0 ; mixId <= oldNumOfMixs ; mixId ++ ) { 
			sNew [mixId] = sArray [mixId] ; 
			
		}		 
		sArray = sNew ; 		
		
		for (int mixId = oldNumOfMixs +1 ; mixId <= numOfMixs ; mixId ++ ) { 
			sArray [mixId] = 0 ;		
			updateS(mixId) ;		
		}
		if (level ==1 ) {
			double [] rNew = new double[numOfMixs] ; 
			double [] qNew = new double[numOfMixs] ; 
			Integer [] sortNew = new Integer [numOfMixs+1] ;		
			for (int mixId = 0 ; mixId < oldNumOfMixs ; mixId ++ ) { 					
				rNew [mixId] = rArray[mixId] ; 
				qNew [mixId] = qFactorArray [mixId] ; 
				sortNew [mixId] = sortMix [mixId] ; 
			}
			rArray = rNew ; 
			qFactorArray = qNew ; 
			sortMix = sortNew ;		
			for (int mixId = oldNumOfMixs  ; mixId < numOfMixs ; mixId ++ ) { 			 
				rArray [mixId] = 0 ; 
				qFactorArray [mixId] = 0 ; 	
				sortMix[mixId] = mixId ; 
			}
			sortMix[numOfMixs] = numOfMixs ; 
		}
		else { 
			double [][] rNewMat = new double [numOfUpperLevelMixs][numOfMixs] ; 
			double [][] qNewMat = new double [numOfUpperLevelMixs][numOfMixs] ; 
			Integer [][] sortNewMat = new Integer [numOfUpperLevelMixs][numOfMixs+1] ; 
			for (int upperLevelMixId = 0 ; upperLevelMixId < numOfUpperLevelMixs ; upperLevelMixId ++) { 
				for (int mixId = 0 ; mixId < oldNumOfMixs ; mixId ++ ) { 					
					rNewMat [upperLevelMixId][mixId] = rMat[upperLevelMixId][mixId] ; 
					qNewMat [upperLevelMixId][mixId]= qFactorMat [upperLevelMixId][mixId] ; 
					sortNewMat [upperLevelMixId][mixId] = sortMixMat [upperLevelMixId][mixId] ; 
				}
				for (int mixId = oldNumOfMixs  ; mixId < numOfMixs ; mixId ++ ) { 			 
					rNewMat [upperLevelMixId][mixId] = 0 ; 
					qNewMat [upperLevelMixId][mixId]= 0 ; 
					sortNewMat [upperLevelMixId][mixId] = mixId ; 
				}
				sortNewMat [upperLevelMixId][numOfMixs] = numOfMixs ; 
			}
			rMat = rNewMat ; 
			qFactorMat = qNewMat ; 
			sortMixMat = sortNewMat ; 
			rArray = null ; 
			qFactorArray = null ; 
			sortMix = null ; 
			r = -10 ; 
		}		 
	}
	public void extendVTerms (int newNumOfVTerms) { 
		this.numOfVTerms = newNumOfVTerms ; 
		this.lambdaNumOfVTerms = lambda*numOfVTerms ; 
	}
	 
	private void initS() throws Exception {
		sArray = new double [numOfMixs+1] ; 
		s = 0 ; 
		for (int mixId  = 0 ; mixId <= numOfMixs ; mixId ++   ) { 
			sArray [mixId] = (upperLevelAlpha0StickBreakingWeights [mixId] * lambda) / (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ;  
			s+= sArray[mixId] ; 			
		}		
	}

	private void updateS(int mixId ) throws Exception {
		s -= sArray [mixId] ; 
		sArray [mixId] = (upperLevelAlpha0StickBreakingWeights [mixId] * lambda) / (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ;  
		s+= sArray[mixId] ; 		
	}
	
	private void initRMat () throws Exception {
		rUpperLevelMix = new double [numOfUpperLevelMixs] ; 
		rMat = new double [numOfUpperLevelMixs][numOfMixs] ; 
		for (int upperLevelMixId = 0 ; upperLevelMixId < numOfUpperLevelMixs ; upperLevelMixId ++ ) { 
			rUpperLevelMix [upperLevelMixId] = 0 ; 
			for (int mixId = 0 ;mixId  < numOfMixs ; mixId ++ ) { 
				rMat[upperLevelMixId][mixId] = (upperLevelMix_MixCounters.get(upperLevelMixId , mixId) *lambda)
						/ (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
				rUpperLevelMix [upperLevelMixId] += rMat[upperLevelMixId][mixId] ; 
			}		
		}
		rArray = null ; 
	}
	

	private void initR(int upperLevelMixId) throws Exception {
		rArray = new double [numOfMixs] ; 
		r = 0 ; 
		for (int mixId = 0 ;mixId  < numOfMixs ; mixId ++ ) { 
			rArray[mixId] = (upperLevelMix_MixCounters.get(upperLevelMixId , mixId) *lambda)
					      / (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
			r += rArray[mixId] ; 
		}		
	}
	private void updateR(int mixId , int upperLevelMixId ,  int upperLevelMix_MixCount ) throws Exception {
		r -= rArray[mixId] ; 
		rArray[mixId] = (upperLevelMix_MixCount *lambda) / (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
		r += rArray[mixId] ; 
		if (level > 1) { 
			rUpperLevelMix [upperLevelMixId] = r ; 
		}
	}
	
	private void initQFactorMat () throws Exception { 
		qFactorMat = new double [numOfUpperLevelMixs][numOfMixs] ; 
		for (int upperLevelMixId = 0 ;  upperLevelMixId < numOfUpperLevelMixs ;  upperLevelMixId ++) {
			for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
				qFactorMat  [upperLevelMixId][mixId] = 
					(upperLevelAlpha0StickBreakingWeights [mixId]+ upperLevelMix_MixCounters.get(upperLevelMixId , mixId) )
					/(lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
			}
		}
		qFactorArray = null ; 
	}
	private void initQFactor (int upperLevelMixId) throws Exception { 
		qFactorArray = new double [numOfMixs] ; 
		for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
			qFactorArray [mixId] = 
					(upperLevelAlpha0StickBreakingWeights [mixId]+ upperLevelMix_MixCounters.get(upperLevelMixId , mixId) )
					/(lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
		}
	}
	private void updateQFactor (int mixId , int upperLevelMix_MixCount) throws Exception { 
		qFactorArray [mixId] = (upperLevelAlpha0StickBreakingWeights [mixId]+ upperLevelMix_MixCount  )
				/(lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
	}
	private void initSortMat () { 
		sortMixMat = new Integer[numOfUpperLevelMixs] [numOfMixs+1] ; 
		int upperLevelMixId ; 
		for ( upperLevelMixId = 0 ;  upperLevelMixId < numOfUpperLevelMixs ;  upperLevelMixId ++) {
			initSortMix (upperLevelMixId ,sortMixMat [upperLevelMixId]) ; 			
		}
	}
	private void initSortMix (int upperLevelMixId , Integer [] mixArray ) { 
		for (int mixId = 0 ; mixId <= numOfMixs ;  mixId ++ ) mixArray[mixId] = mixId ; 
		Arrays.sort(mixArray , (Integer i1, Integer i2) ->		
		{
			try {
				return Integer.signum ( upperLevelMix_MixCounters.get(upperLevelMixId,i2) 
						-upperLevelMix_MixCounters.get(upperLevelMixId,i1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
				) ;
	}

}