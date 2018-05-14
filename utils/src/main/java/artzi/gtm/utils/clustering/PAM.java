     /***** https://en.wikipedia.org/wiki/K-medoids ****/ 

package artzi.gtm.utils.clustering;

public class PAM {
	double [][] dismat ;   
	int numOfClusters ;  
	int numOfObjects ; 
	boolean [] ismed ; 
	int [] clusterAssignment ; 
	int [] saveClusters ; 
	int [] meds ; 
	
	public PAM (double [][] dismat  , int numOfClusters ) { 
		this.dismat = dismat ; 
		numOfObjects = dismat[0].length ; 
		this.numOfClusters = numOfClusters ; 
	}
	public PAMResult getClusterAssignment () { 
		ismed = new boolean [numOfObjects] ; 
		clusterAssignment = new int [numOfObjects] ; 
		saveClusters = new int [numOfObjects] ; 
		meds = new int [numOfClusters] ; 
		for (int i = 0 ; i < numOfObjects ; i ++)  {
			if (i < numOfClusters) { 
				ismed  [i] = true ; 
				meds [i] = i ; 
				clusterAssignment [i] = i ; 
			}
			else {
				ismed  [i] = false ; 
				clusterAssignment [i] = getBestCluster (i) ; 
			}
		}
		boolean change = true ; 
		while (change) { 
			change = false ; 
			for (int cluster = 0 ; cluster < numOfClusters ; cluster ++ ) { 
				for (int i = 0 ; i < numOfObjects ; i ++ ) { 
					if (!ismed[i]) { 
						change |= swap (i , cluster) ; 
					}
				}
			}		
		}
		return new PAMResult (clusterAssignment , meds) ; 		
	}
	
	private int getBestCluster(int i) {
		
		int minCluster = 0 ;  
		double mindis = dismat[i][meds[minCluster]] ; 
		for (int cluster = 1 ; cluster < numOfClusters ; cluster++ ) { 
			if (dismat[i][meds[cluster]] < mindis ) { 
				minCluster = cluster ; 
				mindis = dismat[i][meds[cluster]] ; 
			}
		}
		return minCluster;
	}
	
	private double getCost () {
		double cost = 0 ; 
		for  (int i = 0 ; i < numOfObjects ; i ++)  {
			cost += dismat[i][meds[clusterAssignment[i]]] ; 
		}
		return cost ; 		
	}
	
	private boolean swap(int swapi, int cluster) {
		boolean change; 
		double oldCost = getCost() ; 
		
		for (int i = 0 ; i < numOfObjects ; i ++) { 
			saveClusters [i] = clusterAssignment [i] ; 
		}
		 
		int swapMed = meds [cluster] ;
		meds [cluster] = swapi ; 
		ismed[swapi] = true ; 
		clusterAssignment[swapi] = cluster ; 
		ismed[swapMed] = false ; 
		
		
		for (int i = 0 ; i < numOfObjects ; i ++) { 
			if (!ismed[i]) { 
				clusterAssignment [i] = getBestCluster (i) ; 		
			}
		}	
		double newCost = getCost () ; 
		if (newCost < oldCost) { 
			change = true ; 
		}
		else { 
			change = false ; 
			ismed [swapi] = false ; 
			meds [cluster] = swapMed ; 
			ismed [swapMed] = true ; 
			for (int i = 0 ; i < numOfObjects ; i ++) { 
				clusterAssignment [i] = saveClusters [i] ; 
			}			
		}		
		return change ; 
	}
}