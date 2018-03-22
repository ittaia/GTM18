package artzi.gtm.utils.aMath;

public class KLDivergence {
	public static double get (double [] p , double [] q ) { 
		double kldiv = 0 ;
		for (int i = 0 ; i < p.length ; i ++ ) { 
			kldiv += p[i] * Math.log(p[i]/q[i]) ; 
		}
		return kldiv ; 		
	}
	public static double getsym (double [] p , double [] q ) {
		double kldivsym = get(p,q) + get (q,p) ;
		return kldivsym ; 
	}
}