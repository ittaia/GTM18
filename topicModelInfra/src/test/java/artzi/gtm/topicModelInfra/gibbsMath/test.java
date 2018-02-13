package artzi.gtm.topicModelInfra.gibbsMath;

import artzi.gtm.utils.aMath.Functions;

public class test {
	public static void main(String[] args) {  		

	/**
	 * @param args
	 */
	double x = 3.6 ; 
	int n = 3 ; 
	double f3 = Functions.logGamma(x + n) ; 
	double f4 = Functions.logGamma(x) ; 
	double l1  = (f3-f4) ;
	System.out.println (l1) ; 
	System.out.println (LogGammaFraction.get(x, n) ) ;   
	}

}
