package artzi.gtm.utils.aMath;

public class LogMath {

	public static double [] logNormalize(double [] logArray) {

		final double logMax = 100.0; // the log(maximum in double precision), make sure it is large enough.
		int maxIndx = 0  ; 
		double maxVal ;  
		int aLen = logArray.length ; 
		for (int i  = 1 ; i < aLen ; i ++   ) { 
			if (logArray [i] > logArray [maxIndx]  ) maxIndx = i ; 			 
		}
		maxVal = logArray [maxIndx] ; 
		double logShift = logMax - Math.log(logArray.length + 1.0) - maxVal;
		double sum = 0.0;
		for (int i = 0; i < aLen ; i++) { 
			sum += Math.exp(logArray[i] + logShift); 
		}
		double logNorm = Math.log(sum) - logShift;
		double []  rArray = new double [aLen] ; 
		for (int i = 0; i < aLen ; i++) {
			rArray [i] = logArray[i] - logNorm;  
		}
		return rArray; 
	}



	public static double logSum (double[] xs) {
		if (xs.length == 1) return xs[0];
		double max = maximum(xs);
		double sum = 0.0;
		for (int i = 0; i < xs.length; ++i) {
			if (xs[i] == Double.NEGATIVE_INFINITY) { 
			}
			if (xs[i] != Double.NEGATIVE_INFINITY) { 
				sum += Math.exp(xs[i] - max);
			}
		}
		return max + Math.log(sum);
	}

	private static double maximum  (double [] xs) { 
		double max =Double.NEGATIVE_INFINITY ;  
		for (double x:xs ) { 
			if (x>max) { max = x ; } 
		}
		return max ; 
	}
}


