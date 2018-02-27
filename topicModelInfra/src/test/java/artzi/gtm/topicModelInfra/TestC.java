package artzi.gtm.topicModelInfra;

import artzi.gtm.topicModelInfra.counters.DCounters;

public class TestC {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DCounters s = new DCounters (1,1) ; 
		int g = s.get(5,5) ; 
		System.out.println(g) ; 
		s.add1(5,6) ;
		System.out.println(s.get(5,6) ) ; 
		s.add1(100, 200) ; 
		System.out.println(s.get(100,200) ) ; 
		
		s.addZ(100, 200 , 10) ; 
		System.out.println(s.get(100,200) ) ; 
		s.addZ(1000, 2000 , 10) ; 
		System.out.println(s.get(1000,2000) ) ; 

	}

}
