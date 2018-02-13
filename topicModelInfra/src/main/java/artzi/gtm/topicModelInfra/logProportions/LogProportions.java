package artzi.gtm.topicModelInfra.logProportions;

import java.util.ArrayList;

import artzi.gtm.utils.aMath.Distributions;
import artzi.gtm.utils.elog.EL;

public class LogProportions {
	
	ArrayList <Double> logProportions ; 
	ArrayList <Integer> indexes ; 
	double [] proportions ; 
	
	public LogProportions () { 
		logProportions = new ArrayList <Double> () ; 
		indexes = new ArrayList <Integer> () ; 
	}
	public void add (int index , double logProp ) { 
		logProportions.add(logProp) ; 
		indexes.add(index) ; 		
	}
	public int sample () { 
		int numOfObjects = logProportions.size () ; 
		proportions = new double [numOfObjects] ; 
		double max = Double.NEGATIVE_INFINITY ;  
		for (double x:logProportions) { 
			if (x>max) { max = x ; } 
		}
		for (int i = 0 ; i < numOfObjects ; i ++ ) { 
			proportions [i] = Math.exp(logProportions.get(i) -max) ; 
			if (proportions [i] <= 0 ) ; // EL.WE(7777 ,  "Bad prpo " + i);   
		}
		int objectI   = Distributions.MultSampleUnNorm (proportions) ; 
		int m = 0 ; 
		for (int i = 1 ; i < proportions.length ; i ++ ) { 
			if (proportions [i] > proportions [m]  ) m = i ;  
		}
		if (m != objectI ) ; //EL.WE(8877 , " Not Max !!!! " ) ; 
		return   indexes.get(objectI) ;  		
	}
	public void printP() {
		String s = " prop " ;  
		for (int i = 0 ; i < proportions.length ; i ++ ) { 
			s += " " + i + " Index " + indexes.get (i) + "-"+ proportions[i] ; 
		}
		EL.W( s) ; 		
	}
}
