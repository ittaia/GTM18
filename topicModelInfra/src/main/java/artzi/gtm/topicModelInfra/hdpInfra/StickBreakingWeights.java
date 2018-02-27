package artzi.gtm.topicModelInfra.hdpInfra;

import artzi.gtm.utils.aMath.DirichletSample;
import artzi.gtm.utils.aMath.Mult;
import artzi.gtm.utils.elog.EL;

public class StickBreakingWeights {

	int numOfMixs ; 
	int [] mixTables ; 
	double [] weights ; 	
	double gamma; 
	public  StickBreakingWeights () { 
		this.numOfMixs = 1 ; 
		this.mixTables = new int [1] ; 
		this.mixTables [0] = 1 ; 
		this.gamma = 1 ; 		
		this.weights = new double [1] ; 
		this.weights [0] = 1. ; 		
	}
	public  StickBreakingWeights (double gamma) { 
		this.numOfMixs = 1 ; 
		this.mixTables = new int [1] ; 
		this.mixTables [0] = 1 ; 
		this.gamma = gamma ; 		
		this.weights = new double [1] ; 
		this.weights [0] = 1. ; 		
	}
	
	public void updateWeights (int numOfMixs , int numOfUpperLevelMixs , int [][] mixCount , double alpha0 , double gamma ) { 
		double sw = 0 ; 
		for (double w : weights) sw+= w ; 
		EL.W(" sum weights:" + sw) ; 
		this.numOfMixs = numOfMixs ; 
		this.gamma = gamma ; 
		mixTables = new int [numOfMixs] ; 
		for (int m = 0 ; m < numOfMixs ; m ++ ) { 
			mixTables[m] = 0 ; 
			
			for (int  m0 = 0 ; m0 < numOfUpperLevelMixs ; m0 ++  ) { 
				//System.out.println(" m0 - " + m0   + " m " + m + " - " +  mixCount [m0][m] )  ; 
				mixTables [m] += antoniak (mixCount [m0][m] , alpha0 * weights [m]  ) ; 
			}
			//EL.WE(1111 , " Mix " + m + " count "  + mixTables [m] ) ; 
		}
		//String s = " Tables : " + numOfMixs ; 
		//for (int i = 0 ; i < numOfMixs ; i ++ ) s +=  " " +  i+"-"+mixTables [i] ; 
		//EL.WE (7777 , s ) ; 
		sampleWeights () ; 
	}
	
	public void extendMixs (int newNumOfMixs ) { 
		int [] newMixTables =  new int [newNumOfMixs] ; 
		for (int m = 0 ; m <  newNumOfMixs ; m ++ ) { 
			if (m < this.numOfMixs) { 
				newMixTables [m] = this.mixTables [m] ; 
			}
			else { 
				newMixTables [m] = 1 ; 
			}			 
		}
		this.numOfMixs = newNumOfMixs ; 
		this.mixTables = newMixTables ; 
		sampleWeights () ; 		
	}
	public void reUseMix(int mixId) {
		mixTables [mixId] = 1 ; 
		sampleWeights () ; 
	}
	
	
	private static int antoniak (int N, double alpha0Weight )  { 
		int sumBernoulli ; 
		sumBernoulli = 0 ; 
		for (int l = 1 ; l <= N ; l ++  ) { 
			sumBernoulli += Mult.bernoulliTrial(alpha0Weight / (alpha0Weight + l -1)) ; 
		}	
		return sumBernoulli;
	}
	private void sampleWeights () { 
		
		double [] dirichletParms = new double [numOfMixs+1] ;  
		for (int m = 0 ; m < numOfMixs ; m ++ ) { 
			dirichletParms [m] = mixTables [m] ; 
			if (dirichletParms [m] <= 0 )   dirichletParms [m] = 0.0001 ;  
		}
		dirichletParms [numOfMixs] = gamma ; 
		DirichletSample dirichlet = new DirichletSample (dirichletParms) ; 			
		weights  = dirichlet.nextSample() ; 	 
	}
	public void copy(StickBreakingWeights rootStickBreakingWeights, MapMixtureComponents mapMixs) {
		int newNumOfMixs = rootStickBreakingWeights.getNumOfMixs() ; 
		EL.WE(8888 ,  " ThreadSTB Mixes "+ this.numOfMixs) ; 
		if (newNumOfMixs > this.numOfMixs) { 
			this.numOfMixs = newNumOfMixs ; 			
			int [] newMixTables =  new int [newNumOfMixs] ; 			
			this.mixTables = newMixTables ;
		}		
		this.gamma = rootStickBreakingWeights.getGamma() ; 
		for (int threadMixId = 0 ; threadMixId < numOfMixs ; threadMixId ++ ) { 
			int rootMixId = mapMixs.getRootMix(threadMixId) ; 
			mixTables [threadMixId] = rootStickBreakingWeights.getMixTables()[rootMixId] ; 
		}
		sampleWeights () ; 			
	}
	public int getNumOfMixs() {
		return numOfMixs;
	}
	public double[] getWeights() {
		return weights;
	}
	public double getWeight(int mixId) {
		 return this.weights [mixId] ; 
	}
	public double getWeightNew() {
		 return this.weights [numOfMixs] ; 
	}
	public int[] getMixTables() {
		return mixTables;
	}
	public double getGamma() {
		return gamma;
	}
	public int getTotMixTables() {
		int tot = 0 ; 
		for (int i = 0 ; i < numOfMixs ; i ++ ) { 
			tot += mixTables [i] ; 
		}
		return tot ; 
	}
	public void resetWeights() {
		for (int i = 0 ; i <= numOfMixs ; i ++ ) { 
			weights [i] = 0 ; 
		}		
	}
	public void sumWeights(StickBreakingWeights threadStickBreakingWeights, MapMixtureComponents mapMixs , int numOfThreads) {
		for (int rootMixId = 0 ; rootMixId < numOfMixs ; rootMixId ++ ) { 
			int threadMixId = mapMixs.getRootMix(rootMixId) ; 
			weights [rootMixId] += threadStickBreakingWeights.getWeights()[threadMixId]/numOfThreads ; 
		}
		weights [numOfMixs] += threadStickBreakingWeights.getWeights()[numOfMixs]/numOfThreads ; 
	}
	
	
}
