package artzi.gtm.topicModelInfra.gibbsMath;

public class LogGammaFraction {

	/**
	 * @param args
	 */
	public static double get (double x , int n) { 
		double l = 0 ; 
		for (int i = 1 ; i <= n ; i ++ ) { 
			l += Math.log(x+i-1)  ; 
		}
		return l ; 
	}
}
