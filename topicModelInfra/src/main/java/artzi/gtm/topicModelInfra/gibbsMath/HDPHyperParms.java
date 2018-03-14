package artzi.gtm.topicModelInfra.gibbsMath;

import artzi.gtm.utils.aMath.Distributions;
import artzi.gtm.utils.aMath.Mult;

/*************************************************************************************/
/***  sample HDP gamma and  alpha 0 - according to:                           		**/
/***     Heinrich "infinite LDA" - Hyper parameters sampling -  page 9   			**/
/***     gamma -  upper level DP concentration parameter                            **/
/**      alpha0 - second level DP concentration parameter                           **/    														    
/*************************************************************************************/  

public class HDPHyperParms {

	public static double sampleGamma  (double gammaOld , int numOfMixs , int numOfNextLevelMixs ,double aGamma , double bGamma ) { 
		
		/***** a - shape ; b - scale *****/
		final int iters = 30 , burnin = 10 ; 		
		double gammaNew = gammaOld ; 
		double K = numOfMixs ; 
		double T = numOfNextLevelMixs ;  
		double sumGammaNew = 0 ; 
		int samples = 0 ; 
		for (int step = 0; step < iters; step++) {
			int    u  =  Mult.bernoulliTrial(T / ( T + gammaNew )) ; 	
			double v = Distributions.BetaSample (gammaNew+ 1, T ) ;  					 
			gammaNew = Distributions.GammaSampleShapeScale(aGamma + K - 1 +u  , 1./(bGamma - Math.log(v)));
			if (step > burnin) { 
				samples++ ; 
				sumGammaNew += gammaNew ; 
			}
		}
		gammaNew = sumGammaNew / samples ; 
		return gammaNew ; 	 
	}

	public static double sampleAlpha0  (double alpha0Old ,  int numOfNextLevelMixs , 
			int numOfDocs , int [] numOfDocWords , double aAlpha0 , double bAlpha0 ) { 
		/***** a - shape ; b - scale *****/
		final int iters = 5 , burnin = 2; 		
		double alpha0New = alpha0Old ; 
		double T = numOfNextLevelMixs ;  
		double sumAlpha0New = 0 ; 
		int samples = 0 ; 
		
		for (int step = 0; step < iters; step++) {	
			double sumu,sumLogv ; 		
			sumu = 0 ; 
			sumLogv = 0 ; 		 
			for (int m = 0 ; m < numOfDocs ; m++) { 
				double  um  =  Mult.bernoulliTrial(numOfDocWords[m] / ( numOfDocWords[m] + alpha0New )) ; 
				sumu += um ; 
				if (numOfDocWords [m] > 0 ) { 
					double vm = Distributions.BetaSample (alpha0New+ 1, numOfDocWords[m] ) ;  
					sumLogv += Math.log(vm) ;
				}
			}
			alpha0New = Distributions.GammaSampleShapeScale(aAlpha0 + T - sumu  , 1./(bAlpha0 - sumLogv));
			if (step > burnin) { 
				samples++ ; 
				sumAlpha0New += alpha0New ; 
			}
		}
		alpha0New = sumAlpha0New / samples ; 
		return alpha0New ; 	 
	}
}
