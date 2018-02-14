package artzi.gtm.dmlhdp.driver;


public class F1Match {
	int goldClass ; 
	int modelCluster ; 
	double correctMatch , miss , falsePos ;  
	double recall , precision , f1 ; 
	
 
	public F1Match (int goldClass , int modelCluster , double [][] assignmentMat  ) { 
		this.goldClass = goldClass ; 
		this.modelCluster = modelCluster ; 		
		correctMatch = assignmentMat [goldClass][modelCluster] ; 
		
		miss = 0 ; 
		for (int i = 0 ; i < assignmentMat[0].length ; i ++ ) { 
			if (i != modelCluster) {  
				miss += assignmentMat [goldClass][i]  ; 
			}
		}
		recall = correctMatch / (correctMatch + miss) ; 	
		
		falsePos= 0 ; 
		for (int i = 0 ; i < assignmentMat.length ; i ++ ) { 
			if (i != goldClass) { 
				falsePos+= assignmentMat [i][modelCluster]  ; 
			}
		}
		precision = correctMatch/(correctMatch + falsePos) ; 
		
		if ((recall + precision) <= 0.0) { 
			f1 = 0 ; 
		}
		else { 
			f1 = 2 * (recall*precision)/(recall+precision) ; 
		}
	}


	public int getGoldClass() {
		return goldClass;
	}


	public int getModelCluster() {
		return modelCluster;
	}


	public double getCorrectMatch() {
		return correctMatch;
	}


	public double getMiss() {
		return miss;
	}


	public double getFalsePos() {
		return falsePos;
	}


	public double getRecall() {
		return recall;
	}


	public double getPrecision() {
		return precision;
	}


	public double getF1() {
		return f1;
	}
	
}