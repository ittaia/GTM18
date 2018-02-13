package artzi.gtm.utils.aMath;

import org.apache.commons.math3.special.Gamma;

public class Functions {

	public static double gamma (double x) { 
		return Gamma.gamma (x) ;  
	}
	public static double logGamma (double x) { 
		return Gamma.logGamma (x) ;  
	}
	public static double digamma (double x) { 
		return Gamma.digamma(x) ; 
	}

}
