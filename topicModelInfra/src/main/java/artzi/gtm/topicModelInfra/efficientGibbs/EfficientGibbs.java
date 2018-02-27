package artzi.gtm.topicModelInfra.efficientGibbs;

import java.util.Arrays;
import java.util.Random;

import artzi.gtm.topicModelInfra.counters.DCounters;

public class EfficientGibbs {
	int level ; 
	DCounters docMixCounters ; 
	DCounters mixVTermCounters ; 
	DCounters mixVTermSum ;
	int numOfMixs ; 
	int numOfVTerms ; 
	double  alpha0 , lambda , lambdaNumOfVTerms ; 
	double[] upperLevelAlpha0StickBreakingWeights ; 
	double s,r ; 
	double [] sArray , rArray , qFactorArray ; 
	Integer [] sortMix ; 
	Random generator ;	
	
	public EfficientGibbs (int level, DCounters docMixCounters , DCounters mixVTermCounters, DCounters mixVTermSum) { 
		this.level = level ; 
		this.docMixCounters = docMixCounters ; 
		this.mixVTermCounters = mixVTermCounters ; 
		this.mixVTermSum = mixVTermSum ; 
		generator = new Random () ; 
	}
	public void initIteration (int numOfMixs, int numOfVTerms, double alpha0, double lambda, 
			double lambdaNumOfVTerms,  double[] upperLevelAlpha0StickBreakingWeights) throws Exception { 
		this.numOfMixs = numOfMixs ; 
		this.numOfVTerms = numOfVTerms ; 
		this.alpha0 = alpha0 ; 
		this.lambda = lambda ; 
		this.lambdaNumOfVTerms = lambdaNumOfVTerms ; 
		this.upperLevelAlpha0StickBreakingWeights = upperLevelAlpha0StickBreakingWeights ; 	
		initS() ; 
	}
	public void initDoc (int docIndx) throws Exception { 
		initR  (docIndx) ; 
		initQFactor (docIndx) ;
		sortMix = new Integer [numOfMixs+1] ; 
		for (int m = 0 ; m <= numOfMixs ;  m ++ ) sortMix [m] = m ; 
		Arrays.sort(sortMix , (Integer i1, Integer i2) ->
		{try {
			return Integer.signum (docMixCounters.get(docIndx,i2) -docMixCounters.get(docIndx,i1)) ;
		} catch (Exception e) {
			return 0 ; 
		} } ) ;
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
	public void updateCounters (int docIndx , int mixId , int vtermId  ) throws Exception { 
		updateS (mixId ) ; 
		int docMixCounter =  docMixCounters.get(docIndx , mixId) ; 
		updateR (mixId , docMixCounter) ; 
		updateQFactor (mixId , docMixCounter) ;
		int end = numOfMixs ; 
		while (end > 0) { 
			boolean replace = false ; 
			for (int m = 0 ; m < end ; m ++ ) { 			
				if (docMixCounters.get(docIndx,sortMix[m]) < docMixCounters.get(docIndx,sortMix[m+1])) { 
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
		double [] rNew = new double[numOfMixs] ; 
		double [] qNew = new double[numOfMixs] ; 
		Integer [] sortNew = new Integer [numOfMixs+1] ; 
		for (int mixId = 0 ; mixId < oldNumOfMixs ; mixId ++ ) { 
					
			rNew [mixId] = rArray[mixId] ; 
			qNew [mixId] = qFactorArray [mixId] ; 
			
		}
		for (int mixId = 0 ; mixId <= oldNumOfMixs ; mixId ++ ) { 
			sNew [mixId] = sArray [mixId] ; 
			sortNew [mixId] = sortMix [mixId] ; 
		}		 
		sArray = sNew ; 
		rArray = rNew ; 
		qFactorArray = qNew ; 
		sortMix = sortNew ; 		
		for (int mixId = oldNumOfMixs  ; mixId < numOfMixs ; mixId ++ ) { 			 
			rArray [mixId] = 0 ; 
			qFactorArray [mixId] = 0 ; 			 
		}
		for (int mixId = oldNumOfMixs +1 ; mixId <= numOfMixs ; mixId ++ ) { 
			sArray [mixId] = 0 ;		
			updateS(mixId) ; 
			sortMix [mixId] = mixId ;
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

	private void initR(int docIndx) throws Exception {
		rArray = new double [numOfMixs] ; 
		r = 0 ; 
		for (int mixId = 0 ;mixId  < numOfMixs ; mixId ++ ) { 
			rArray[mixId] = (docMixCounters.get(docIndx , mixId) *lambda) / (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
			r += rArray[mixId] ; 
		}		
	}
	private void updateR(int mixId ,  int docMixCount ) throws Exception {
		r -= rArray[mixId] ; 
		rArray[mixId] = (docMixCount *lambda) / (lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
		r += rArray[mixId] ; 		
	}
	private void initQFactor (int docIndx) throws Exception { 
		qFactorArray = new double [numOfMixs] ; 
		for (int mixId = 0 ; mixId < numOfMixs ; mixId ++ ) { 
			qFactorArray [mixId] = 
					(upperLevelAlpha0StickBreakingWeights [mixId]+ docMixCounters.get(docIndx , mixId) )
					/(lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
		}
	}
	private void updateQFactor (int mixId , int docMixCount) throws Exception { 
		qFactorArray [mixId] = (upperLevelAlpha0StickBreakingWeights [mixId]+ docMixCount  )/(lambdaNumOfVTerms +  mixVTermSum.get(mixId)) ; 
	}
}