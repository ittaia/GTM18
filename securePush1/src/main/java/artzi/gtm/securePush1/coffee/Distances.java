package artzi.gtm.securePush1.coffee;

import artzi.gtm.utils.clustering.Kmean;

public class Distances {
	
	double [] distance ; 
	int window = 20 ; 
	int numOfDots ; 
	
	double [] movingAverage ; 
	public Distances (Kmean kmean , int window) { 
		double sumWindow = 0 ; 
		this.window = window ; 
		this.numOfDots = kmean.getNumDots() ; 
		this.distance = new double [this.numOfDots] ; 
		this.movingAverage = new double [this.numOfDots] ; 
		for (int i = 0 ; i < this.numOfDots ; i ++ ) this.movingAverage [i] = 0 ; 
		for (int i = 0 ; i < this.numOfDots ; i ++) { 
			distance[i] = getNpormDist(kmean.getCenterList().get(kmean.getDotCenter()[i]),
					kmean.getDotList().get(i)) ; 
			sumWindow += distance[i] ; 
			if (i > window) sumWindow -= distance[i-window] ; 
			if (i >= window) movingAverage[i] = sumWindow/window ; 
		}		
	}
	private double getNpormDist(double[] center, double[] dot) {
		double distance2 = 0 ; 
		for (int i = 0; i < center.length; i ++) { 
			distance2 += Math.pow((100*(center[i]-dot[i])/center[i]),2) ; 
		}
		return Math.sqrt(distance2);
	}
	double [] getDistance () { 
		return  this.distance ; 
	}
	double [] getMovingAverage() { 
		return this.movingAverage ; 
	}
}
