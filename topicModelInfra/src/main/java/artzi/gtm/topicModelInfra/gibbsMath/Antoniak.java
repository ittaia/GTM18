package artzi.gtm.topicModelInfra.gibbsMath;

import artzi.gtm.utils.aMath.Mult;

public class Antoniak {

	public static int getAntoniak (int N, double alpha )  { 
		int sumBernoulli ; 
		sumBernoulli = 0 ; 
		for (int l = 1 ; l <= N ; l ++  ) { 
			sumBernoulli += Mult.bernoulliTrial(alpha / (alpha + l -1)) ; 
		}	
		return sumBernoulli;
	}
}