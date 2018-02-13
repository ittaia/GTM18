package artzi.gtm.topicModelInfra.gibbsMath;

import artzi.gtm.utils.aMath.Distributions;

public class TestGamma {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double a = 1.0 , b = 5.0 ; 
		for (int  i = 0 ; i < 10 ; i ++ ) { 
			System.out.println(Distributions.GammaSampleShapeScale(a,1/b))  ; 
			
		}
	}

}
