package artzi.gtm.utils.clustering;

import java.util.ArrayList;

public class TestKmean {
	public static void main(String[] args)  {
		ArrayList<double []> a ; 
		a = new ArrayList<>() ; 
		double[] dot1 = {1., 1.} ; 
		double[] dot2 = {1.1, 1.2} ; 
		double[] dot3 = {1.2, 1.3} ; 
		double[] dot4 = {4., 4.} ; 
		double[] dot5 = {4.1, 4.2} ; 
		double[] dot6 = {6., 1.} ; 
		double[] dot7 = {6., 1.} ; 
		double[] dot8 = {6.5, 7.} ; 
		
		a.add(dot1) ; 
		a.add(dot2) ; 
		a.add(dot3) ; 
		a.add(dot4) ; 
		a.add(dot5) ; 
		a.add(dot6) ; 
		a.add(dot7) ; 
		a.add(dot8) ; 
		Kmean km = new Kmean(a, 2, 3 ) ; 
		ArrayList <double []>cl = km.getCenterList() ; 
		for (double [] c:cl) {
			System.out.println(c[0]) ; 
			System.out.println(c[1]) ; 
			
		}
		
	}

}
