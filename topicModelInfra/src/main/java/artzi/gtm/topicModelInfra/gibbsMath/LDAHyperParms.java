package artzi.gtm.topicModelInfra.gibbsMath;

import artzi.gtm.utils.aMath.Functions;

/****** Hienrich eq 84 *****/

public class LDAHyperParms {

	public static double sampleBeta (int [][] counters , double oldBeta) { 
		final int iters = 10 ; 
		double sum1 , sum2 ; 
		double beta = oldBeta ; 
		int sizeX = counters.length ; 
		int sizeY = counters[0].length ; 
		for (int i = 0 ; i < iters ; i ++ ) { 
			double digammaBeta = Functions.digamma (beta) ; 
			sum1 = 0 ; 
			for (int x = 0 ; x < sizeX ; x ++ ) { 
				for (int y = 0 ; y < sizeY ; y ++) { 			  
					sum1 += (Functions.digamma(counters[x][y] + beta)  - digammaBeta) ; 				
				}
			}
			sum2 = 0 ; 
			double digammaSizeYBeta = Functions.digamma (sizeY * beta) ; 
			for (int x = 0 ; x < sizeX ; x ++ ) { 
				int Nx = 0 ; 			
				for (int y = 0 ; y < sizeY ; y ++) { 	
					Nx += counters [x][y] ; 
				}
				sum2 += (Functions.digamma(Nx + sizeY*beta)  - digammaSizeYBeta) ; 				
			}
			beta = (beta*sum1) / ( sizeY*sum2) ; 
		}
		return beta ; 	
	}
}
