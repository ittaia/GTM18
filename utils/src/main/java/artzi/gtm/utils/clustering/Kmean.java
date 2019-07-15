package artzi.gtm.utils.clustering;

import java.util.ArrayList;
import java.util.Random;

import artzi.gtm.utils.elog.EL;

public class Kmean {
	
	ArrayList<double []> dotList ; 
	int numCenters ; 
	int dimention ; 
	ArrayList<double[]> centerList ; 
	int numDots ; 
	int[] dotCenter ; 
	int[] centerDotCount ; 

	int epochs = 0 ; 
	static int maxEpoches = 1000 ; 
	
	public  Kmean(ArrayList<double []> dotList, int dimention, int numCenters){
		this.dotList = dotList ; 	
		this.dimention = dimention ; 
		this.numCenters = numCenters ; 
		this.centerList = new ArrayList<>() ; 
		this.numDots = dotList.size(); 
		this.dotCenter = new int[this.numDots] ; 
		this.centerDotCount = new int[this.numCenters] ; 
		for (int i = 0; i < this.numCenters; i ++) this.centerDotCount[i] = 0 ; 
		for (int j = 0; j < this.numDots; j ++) this.dotCenter[j] = -1 ; 
		initKmean() ;
		//printCenters() ; 
		boolean change = true;
		while (change & (epochs < maxEpoches)) {
			epochs += 1 ; 
			//EL.W("Epoch" + epochs);
			updateCenter();
			//printCenters() ; 
			change = assignDots();
		}	
		if (change) {
			EL.WE (999, " Kmean loop !!!!!!") ; 
		}
		EL.W("end kmean. num Epochs: "+ epochs);
	}
	private  void initKmean(){
		Random random = new Random();
		int j = random.nextInt(numDots) ;
		centerList.add(dotList.get(j).clone()) ; 
		while (centerList.size() < numCenters) {
			int maxIndx = -1;
			double maxDistance = 0 ; 
			for (int dotIndx = 0; dotIndx < numDots; dotIndx++) {
				double minDistanceCandidate = -1 ; 
				double [] candidateCenter = dotList.get(dotIndx) ; 
				for (double [] selectedCenter: centerList) { 
					double distance = getDistance(candidateCenter, selectedCenter) ; 
					if (distance < minDistanceCandidate| minDistanceCandidate < 0) {
						minDistanceCandidate = distance ; 						
					}
				}
				if (minDistanceCandidate > maxDistance) {
					maxIndx = dotIndx ; 
					maxDistance = minDistanceCandidate ; 
				}				 		
			}
			centerList.add(dotList.get(maxIndx).clone()) ; 			 
		}
		assignDots() ;	
	}
	public double getDistance (double[] center, double[] dot){
		double distance2 = 0 ; 
		for (int i = 0; i < this.dimention; i ++) { 
			distance2 += Math.pow((center[i]-dot[i]),2) ; 
		}
		return Math.sqrt(distance2);
	}
	
	public void updateCenter(){
		ArrayList<double[]> dimSumList = new ArrayList<>(); 
		for (int i = 0; i < numCenters; i++) {
			double [] dimSum = new double[dimention] ; 
			for (int j = 0 ; j < dimention ; j++) { 
				dimSum[j] = 0 ; 			
			}
			dimSumList.add(dimSum) ; 
		}	
		for (int i = 0; i < numDots; i ++) {			
			double [] dot = dotList.get(i) ; 
			double [] dimSum =dimSumList.get(dotCenter[i]) ; 
			for (int j = 0 ; j < dimention ; j++) { 
				dimSum[j] += dot[j] ;   		
			}  
		}
		for (int i = 0; i < numCenters; i++) {
			if (epochs > maxEpoches-10)  
				EL.W("dot count"+ i +"-" + centerDotCount[i]);
			if (centerDotCount[i] > 0) {
				double [] center = centerList.get(i) ; 
				double [] dimSum =dimSumList.get(i) ; 
				for (int j = 0 ; j < dimention ; j++) { 
					center[j] = dimSum[j]/centerDotCount[i] ;  			
				}
			}			 
		}		
	}
	
	public boolean assignDots(){
		boolean change = false;		 
		double distance;
		double minDistance ; 
		int minCenter ; 
		double [] dot ; 
		for (int dotIndx = 0; dotIndx < numDots; dotIndx++){
			dot = this.dotList.get(dotIndx) ; 
		 	minCenter = -2 ;
		 	minDistance = -1 ; 
			for (int i = 0; i <numCenters; i++) {	
				distance = getDistance(dot, centerList.get(i)) ; 			 
				if (distance < minDistance | minDistance < 0) {
						minDistance = distance;
						minCenter = i;					 
				}				
			}
			if (dotCenter[dotIndx] != minCenter) {
				change = true ; 
				if (dotCenter[dotIndx] >= 0) centerDotCount[dotCenter[dotIndx]] -=1 ; 
				if (epochs > maxEpoches) { 
					System.out.println (dotIndx + "- " + dotCenter[dotIndx] + " - "+ minCenter ) ; 
					printDot(" change in dot:" + dotIndx, dot) ; 
					printCenters() ; 
				}
				dotCenter[dotIndx] = minCenter ; 
				centerDotCount[dotCenter[dotIndx]] +=1 ; 
			}
		} 
		return change ; 		 
	}
	public void printCenters() {
		
		for (int i = 0; i < numCenters; i ++) { 
			printDot ("center"+ i + " num dots: " + centerDotCount[i], centerList.get(i)) ;
		}
		
	}
	private void printDot(String header, double[] dot) {
		String s = header + ": "; 
		for (int j = 0; j < dimention; j++) { 
			s += j + "-" + dot[j] ; 
		}
		EL.W(s);
		
	}
	public ArrayList<double[]> getDotList() {
		return dotList;
	}
	public int getNumCenters() {
		return numCenters;
	}
	public int getDimention() {
		return dimention;
	}
	public ArrayList<double[]> getCenterList() {
		return centerList;
	}
	public int getNumDots() {
		return numDots;
	}
	public int[] getDotCenter() {
		return dotCenter;
	}
	public void printAllPoints() {
		for (int i = 0; i < numDots; i ++) {
			printDot(" Dot: "+ i, dotList.get(i)) ; 
		}
	}
}