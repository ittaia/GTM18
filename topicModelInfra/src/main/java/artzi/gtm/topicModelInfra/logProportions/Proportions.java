package artzi.gtm.topicModelInfra.logProportions;

import java.util.ArrayList;

import artzi.gtm.utils.aMath.Distributions;
import artzi.gtm.utils.elog.EL;

public class Proportions {
	
	ArrayList <Double> Proportions ; 
	ArrayList <Integer> indexes ; 
	double [] proportions ; 
	
	public Proportions () { 
		Proportions = new ArrayList <Double> () ; 
		indexes = new ArrayList <Integer> () ; 
	}
	public void add (int index , double Prop ) { 
		Proportions.add(Prop) ; 
		indexes.add(index) ; 		
	}
	public int sample () { 
		int numOfObjects = Proportions.size () ; 
		proportions = new double [numOfObjects] ; 
		for (int i = 0 ; i < numOfObjects ; i ++ ) { 
			proportions [i] = Proportions.get(i) ;  
			if (proportions [i] <= 0 ) ; // EL.WE(7777 ,  "Bad prpo " + i);   
		}
		int objectI   = Distributions.MultSampleUnNorm (proportions) ; 
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
