package artzi.gtm.topicModelInfra.gibbsMath;

public class TestH {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double a = 1; 
		double b = 1 ; 
		int nx = 100000 ; 
		//double g1 = HDPHyperParms.sampleGamma(g, n, nx, a, b) ; 
		//System.out.println (g1) ; 
		
		double al = 3.0 ; 
		int [] nw = {25000  , 25000  ,  25000  , 25000  ,25000  , 25000  } ; 
		double al1 = al ;  
		for (int i = 0 ; i < 100 ; i ++) { 
			al1 = HDPHyperParms.sampleAlpha0(al1 , nx , 6 , nw , a, b) ; 
			System.out.println(al1) ;  
		}

	}

}
