package Driver;

import artzi.gtm.utils.elog.EL;

public class ClusteringMeasure {

	int numOfClasses ; 
	int numOfClusters ; 
	String [] classes ; 
	String [] clusters ; 
	double [][] assignmentMat ; 
	int [] classSize ; 
	int [] clusterSize ; 
	double totElements = 0 ; 

	public ClusteringMeasure (String[] classes , String[] clusters) { 
		this.classes = classes ; 
		this.clusters = clusters ; 
		this.numOfClasses = classes.length ; 
		this.numOfClusters = clusters.length ;  
		assignmentMat = new double [numOfClasses ] [numOfClusters] ; 
		classSize = new int [numOfClasses] ; 
		clusterSize = new int [numOfClusters] ; 
		for (int c = 0 ; c < numOfClasses ; c ++) { 
			for (int k = 0 ; k < numOfClusters ;  k ++) { 
				assignmentMat [c][k] = 0 ; 				
			}
		}
	}

	public void add(int c  , int k) {
		assignmentMat [c][k] ++ ; 
		classSize [c] ++ ; 
		clusterSize [k] ++ ; 
		totElements ++ ;  
	}
	public void BestF1measure() {
		EL.W (" Tot elements " + totElements ); 
		F1Match [] matchClusters = new F1Match [numOfClasses] ; 

		for (int c =0 ; c < numOfClasses ; c++) { 
			F1Match maxF1Match = null ; 
			if (classSize [c] > 0 ) { 
				int kmax = -1 ; 
				for (int k = 0 ; k < numOfClusters ; k ++) { 
					if (clusterSize [k] > 0 ) { 
						F1Match f1Match = new F1Match (c , k , assignmentMat) ; 
						//EL.W(" F1 " + c + " - " + k  + " - "  + f1Match.getF1())   ; 
						if (kmax == -1) { 
							kmax = k ; 
							maxF1Match = f1Match ; 
						} 
						else 	if (f1Match.getF1() > maxF1Match.getF1()) { 
							maxF1Match = f1Match ; 
							kmax = k ;  					
						}
					}
				}
				matchClusters [c] = maxF1Match ; 

				EL.W ("Match " + c + " - " +  classes [c] + " ---> " + kmax + " - " 
						+ clusters [kmax]  + " F1 " + maxF1Match.getF1() + " Correct " + maxF1Match.getCorrectMatch()  
						+ " miss "+  maxF1Match.getMiss() +  " falsePos " + maxF1Match.getFalsePos() ) ; 
			}
		}
		double correct = 0 , falsePos = 0 , miss = 0 ; 
		for (int c =0 ; c < numOfClasses ; c++) { 
			if ( classSize [c] > 0 ) { 
				correct += matchClusters [c].getCorrectMatch() ; 
				falsePos += matchClusters [c].getFalsePos() ; 
				miss += matchClusters [c].getMiss() ; 
			}
		}
		double recall = correct/(correct + miss) ; 
		double precision = correct / (correct + falsePos) ; 
		double f1 = (recall * precision) / 2*(recall + precision) ; 
		EL.W(" Recall " + recall + " precsion  " + precision + " Tot F1  " + f1) ; 		
	}	

	public void ManyTo1measure() {
		EL.W (" Tot elements " + totElements ); 
		int [] matchClass = new int [numOfClusters] ; 
		int sumMatch = 0 ; 
		for (int k = 0 ; k < numOfClusters ; k ++) { 
			int cmax = 0 ; 
			for (int c =1 ; c < numOfClasses ; c++) { 
				if (assignmentMat [c][k] > assignmentMat [cmax][k])  { 
					cmax = c ; 
				}
			}
			sumMatch += assignmentMat [cmax][k] ; 
			EL.W ("Match " + k + " - " +  clusters [k] + " ---> " + cmax + " - " 
					+ classes [cmax]  + " Match elements " +assignmentMat [cmax][k] ) ; 
			matchClass [k] = cmax ; 
		}
		double accuracy = (100.0*sumMatch)/totElements ;  
		EL.W(" Accuracy " + accuracy) ; 		
	}
	public void VMeasure () { 
		double HC , HK , HCK , HKC ; 
		HC = 0 ; 
		for (int c = 0 ; c < numOfClasses ; c ++ ) { 
			double sumK = 0 ;  
			for (int k = 0 ; k < numOfClusters ; k ++)  { 
				sumK += assignmentMat [c][k] ; 
			}
			sumK = sumK / totElements; 
			if (sumK > 0 ) HC -= sumK * Math.log(sumK) ; 

		}
		HK = 0 ; 
		for (int k = 0 ; k < numOfClusters ; k ++)  { 
			double sumC = 0 ;  
			for (int c = 0 ; c < numOfClasses ; c ++ ) { 		
				sumC += assignmentMat [c][k] ; 
			}
			sumC = sumC / totElements ; 
			if (sumC > 0) HK -= sumC * Math.log(sumC) ; 
		}
		HCK = 0 ;

		for  (int k = 0 ; k < numOfClusters ; k ++ ) { 
			for (int c = 0 ; c < numOfClasses ; c ++) { 
				if (assignmentMat[c][k] > 0 ) { 
					double sumC = 0 ; 
					for (int c1 = 0 ; c1 < numOfClasses ; c1 ++ ) { 
						sumC += assignmentMat [c1][k] ; 					
					}
					HCK -=  (assignmentMat[c][k]/totElements) * Math.log(assignmentMat[c][k]/sumC) ;   
				}
			}
		}
		HKC = 0 ; 
		for  (int k = 0 ; k < numOfClusters ; k ++ ) { 
			for (int c = 0 ; c < numOfClasses ; c ++) { 			
				if (assignmentMat[c][k] > 0 ) { 
					double sumK = 0 ; 
					for (int k1 = 0 ; k1 < numOfClusters ; k1 ++ ) { 
						sumK += assignmentMat [c][k1] ; 					
					}
					HKC -=  (assignmentMat[c][k]/totElements) * Math.log(assignmentMat[c][k]/sumK) ;   
				}
			}
		}
		double V_h ; 
		if (HC == 0)  V_h = 1 ; 
		else   V_h = 1-(HCK/HC) ; 
		double V_c ; 
		if (HK==0 ) V_c = 1 ; 
		else V_c = 1-(HKC/HK) ; 

		double accuracy = 100.0* 2*V_h*V_c/(V_h + V_c) ; 
		EL.W("V - Accuracy  " + accuracy);  
		double NVIAccuracy ; 
		if (HC == 0)  NVIAccuracy = HK   ; 
		else          NVIAccuracy = (HCK + HKC) /HC ;
		EL.W("NVI - Accuracy  " + NVIAccuracy);  
	}
}


