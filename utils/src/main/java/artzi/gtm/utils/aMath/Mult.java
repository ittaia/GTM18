package artzi.gtm.utils.aMath;

import java.util.Random;

public class Mult {
	static Random randomGenerator = null ; 
	public static int MultSampleUnNorm (double [] p) { 
		double sump = 0 ; 
		for (double pi : p ) { 
			sump += pi ; 			
		}
		return sample(p,sump) ; 
	}
	
	public static int sample (double [] p) { 
		if (randomGenerator == null) randomGenerator = new Random () ; 
		double random01 = randomGenerator.nextDouble() ; 
		double sum = 0 ; 
		for (int i = 0 ; i < p.length ; i ++) { 
			sum += p[i] ; 
			if (sum > random01) return i ;  
		}
		return p.length ; 		
	}
	
	public static int sample (double [] p , double sump) { 
		if (randomGenerator == null) randomGenerator = new Random () ; 
		double random01 = randomGenerator.nextDouble() ; 
		double random = random01 * sump ; 
		double sum = 0 ; 
		for (int i = 0 ; i < p.length ; i ++) { 
			sum += p[i] ; 
			if (sum > random) return i ;  
		}
		return p.length ; 		
	}
	
	public static int bernoulliTrial (double  p) { 
		double [] vp = new double [2]  ; 
		double p1 ; 
		if (p <= 1) p1 = p ; 
		else p1 = 1 ; 
		vp [0] = p1 ; 
		vp [1] = 1-p ; 	
		int bernoulliResult  ; 
		bernoulliResult = sample(vp) ; 
		if (bernoulliResult == 0 ) return  1 ; 
		else return 0 ; 
	}	
}