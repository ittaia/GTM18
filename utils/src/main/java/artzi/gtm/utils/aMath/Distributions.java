package artzi.gtm.utils.aMath;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

public class Distributions {

	public static int MultSample (double [] p) { 
		return Mult.sample(p) ; 
	}
	public static int MultSampleUnNorm (double [] p) { 
		double sump = 0 ; 
		for (double pi : p ) { 
			sump += pi ; 			
		}
		return Mult.sample(p,sump) ; 
	}
	
	public static double  BetaSample (double alpha , double beta)  {
		BetaDistribution bd = new BetaDistribution (alpha , beta) ; 
		return bd.sample () ; 
	}
	public static double  GammaSampleShapeScale (double shape , double  scale)  {
		GammaDistribution gd = new GammaDistribution (shape , scale) ; 
		return gd.sample () ; 
	}
	public static double NormalSample (double min , double var) { 
		NormalDistribution nd = new NormalDistribution (min,var) ; 
		return nd.sample () ; 
	}
	public static int PoissoSample (double mean) { 
		PoissonDistribution ps = new PoissonDistribution (mean) ; 
		return ps.sample () ; 
	}	
}