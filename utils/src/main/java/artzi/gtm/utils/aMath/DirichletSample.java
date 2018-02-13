         /************************************************************************/
         /*** Sample Dirichlet (alpha;x) according to wikipedia and Mallet     ***/
         /************************************************************************/

package artzi.gtm.utils.aMath;

public class DirichletSample {
	double [] alpha ; 
	public DirichletSample (double [] alpha) { 
		this.alpha = alpha ; 
	}
	public double [] nextSample () { 
		double [] y = new double[alpha.length] ;
		double sum = 0 ; 
		for (int i = 0 ; i < alpha.length ; i ++ ) { 
			y[i] = Distributions.GammaSampleShapeScale(alpha[i], 1) ; 
			if (y[i] < 0.00001)  y[i] = 0.00001 ; 
			sum += y[i] ; 
		}
		double [] x = new double [alpha.length] ; 
		for  (int i = 0 ; i < alpha.length ; i ++ ) { 
			x[i] = y[i]/sum ; 
		}
		return x ; 
	}
}