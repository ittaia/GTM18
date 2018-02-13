package artzi.gtm.topicModelInfra.gibbsMath;

public class testBeta {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int [][] counters = { 
				{1,0,1,1,1 } , 
				{0,1,1,1,0} ,  
				{0,5,0,0,0} 
				} ; 
		System.out.println(LDAHyperParms.sampleBeta(counters, 1.0)) ; 
	}
}
