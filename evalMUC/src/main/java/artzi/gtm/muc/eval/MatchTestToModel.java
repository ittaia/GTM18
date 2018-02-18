package artzi.gtm.muc.eval;

import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class MatchTestToModel {
	TestDocs testDocs ; 
	ModelDocs modelDocs ; 
	MatchTestClusterToModelCluster  [] bestModelClusterMatch ; 
	MatchTestSlotToModelClusters  [] slotMatch ; 
	MatchTestSlotToModelCluster  [] directSlotMatch ;  
	
	double slotRecall , slotPrecision , slotF1Score ; 
	double directSlotRecall , directSlotPrecision , directSlotF1Score ; 
	
	double recall , precision , F1Score ; 
	double minProb ; 
	int numOfTestClusters = TemplateNames.numOfTemplates * SlotNames.numOfSlots ; 
	int numOfTemplatest , numOfSlots ; 

	public MatchTestToModel (int numOfTemplates, int numOfSlots, TestDocs testDocs , ModelDocs modelDocs, double minProb) throws Exception { 
		this.numOfTemplatest = numOfTemplates ; 
		this.numOfSlots = numOfSlots ; 
		this.testDocs = testDocs ; 
		this.modelDocs = modelDocs ; 
		this.minProb = minProb; 
		bestModelClusterMatch = new MatchTestClusterToModelCluster [numOfTestClusters];  
		slotMatch = new MatchTestSlotToModelClusters [SlotNames.numOfSlots];  
		directSlotMatch = new MatchTestSlotToModelCluster [SlotNames.numOfSlots];  
		match () ; 	
		matchSlots () ; 
		matchDirectSlots () ; 
	}
	
	private void match () throws Exception { 
		double correct = 0 , missing = 0 , correctExtraction = 0 , falseExtraction = 0 ; 
		for (int testClusterIndx = 0 ; testClusterIndx <  numOfTestClusters ; testClusterIndx ++ )  { 
			MatchTestClusterToModelCluster bestMatch = null ;   
			double maxF1Score = -999 ; 
			for (int modelClusterIndx = 0 ; modelClusterIndx < numOfSlots  ; modelClusterIndx ++ ) { 
				MatchTestClusterToModelCluster match = 
						new MatchTestClusterToModelCluster (testClusterIndx , modelClusterIndx , testDocs , modelDocs , minProb) ; 
				if (match.getF1Score() > maxF1Score) { 
					bestMatch = match ; 
					maxF1Score = match.getF1Score() ; 
				}
			}
			bestModelClusterMatch [testClusterIndx] = bestMatch   ; 
			if (bestMatch != null & (  testClusterIndx / SlotNames.numOfSlots) < TemplateNames.templateOther)  {
				correct += bestMatch.getCorrect () ; 
				missing += bestMatch.getMissing () ; 
				correctExtraction += bestMatch.getCorrectExtraction() ; 
				falseExtraction += bestMatch.getFalseExtraction() ; 
			}
		}
		recall =  correct / (correct+missing) ; 
		precision = correctExtraction/(correctExtraction + falseExtraction) ; 
		if ((recall + precision) <= 0 ) { 
			F1Score = 0 ; 
		}
		else { 
			F1Score = 2 * (recall*precision)/(recall+precision) ; 
		}		
	}
	private void matchSlots() throws Exception {
		double correct = 0 , missing = 0 , correctExtraction = 0 , falseExtraction = 0 ; 
		for (int testSlot  = 0 ; testSlot <  SlotNames.numOfSlots ; testSlot  ++ )  { 
			ArrayList <Integer> modelClusters = new ArrayList <Integer> () ; 
			for (int template  = 0 ; template  < TemplateNames.numOfTemplates-1 ; template  ++ ) {
				int testClusterIndx = (template * SlotNames.numOfSlots) + testSlot ;  
				if (bestModelClusterMatch [testClusterIndx] != null) {  
					if (bestModelClusterMatch [testClusterIndx].getF1Score() > Parms.minTestClusterF1) { 
						int bestModelCluster = bestModelClusterMatch [testClusterIndx].getModelCluster() ; 
						boolean dup = false ; 
						for (int c : modelClusters) { 
							if (c== bestModelCluster ) { 
								dup = true ; 
								break ; 
							}
						}
						if (!dup) modelClusters.add(bestModelCluster) ;
					}
				}
			}


			slotMatch [testSlot] = new MatchTestSlotToModelClusters (testSlot , modelClusters , testDocs , modelDocs , minProb) ; 		

			correct += slotMatch [testSlot].getCorrect () ; 
			missing += slotMatch [testSlot].getMissing () ; 
			correctExtraction += slotMatch [testSlot].getCorrectExtraction() ; 
			falseExtraction += slotMatch [testSlot].getFalseExtraction() ; 			 
		}
		slotRecall =  correct / (correct+missing) ; 
		slotPrecision = correctExtraction/(correctExtraction + falseExtraction) ; 
		slotF1Score = 2 * (slotRecall*slotPrecision)/(slotRecall+slotPrecision) ; 		
	}
	public void printBestMatch  () throws Exception { 
		for (int testClusterIndx = 0 ; testClusterIndx <  numOfTestClusters ; testClusterIndx ++ )  { 
			MatchTestClusterToModelCluster bestMatch = bestModelClusterMatch [testClusterIndx] ; 
			bestMatch.printDocs () ; 
		}

	}
	private void matchDirectSlots () throws Exception { 
		double correct = 0 , missing = 0 , correctExtraction = 0 , falseExtraction = 0 ; 
		for (int testSlotIndx = 0 ; testSlotIndx <  SlotNames.numOfSlots; testSlotIndx ++ )  { 
			MatchTestSlotToModelCluster bestMatch = null ;   
			double maxF1Score = -999 ; 
			for (int modelClusterIndx = 0 ; modelClusterIndx < numOfSlots  ; modelClusterIndx ++ ) { 
				MatchTestSlotToModelCluster match = 
						new MatchTestSlotToModelCluster (testSlotIndx , modelClusterIndx , testDocs , modelDocs , minProb) ; 
				if (match.getF1Score() > maxF1Score) { 
					bestMatch = match ; 
					maxF1Score = match.getF1Score() ; 
				}
			}
			directSlotMatch [testSlotIndx] = bestMatch   ; 
			if (bestMatch != null) {
				correct += bestMatch.getCorrect () ; 
				missing += bestMatch.getMissing () ; 
				correctExtraction += bestMatch.getCorrectExtraction() ; 
				falseExtraction += bestMatch.getFalseExtraction() ; 
			}
		}
		directSlotRecall =  correct / (correct+missing) ; 
		directSlotPrecision = correctExtraction/(correctExtraction + falseExtraction) ; 
		directSlotF1Score = 2 * (directSlotRecall*directSlotPrecision)/(directSlotRecall+directSlotPrecision) ; 
		
	}

	public void print  () { 
		for (int testClusterIndx = 0 ; testClusterIndx <  numOfTestClusters ; testClusterIndx ++ )  {
			EL.W(" Test Cluster " + clusterName(testClusterIndx)) ; 
			if (bestModelClusterMatch [testClusterIndx] != null) { 
				bestModelClusterMatch [testClusterIndx].print () ;
			}
			else { 
				EL.W("   No matching model cluster"  )  ; 
			}
		}
		EL.W(" Total      "  + " Recall " + recall + "precision " + precision + " F1 " + F1Score );
		EL.W( "******  Merged Slot Match *****") ; 
		for (int testSlot = 0 ; testSlot <  SlotNames.numOfSlots ; testSlot ++ )  {
			EL.W( " Test Slot " + SlotNames.names [testSlot])  ;  
			slotMatch [testSlot].print () ; 
		}
		EL.W(" Total Slot      "  + " Recall " + slotRecall + "precision " + slotPrecision + " F1 " + slotF1Score );
		
		EL.W( "******  Direct Slot Match *****") ; 
		for (int testSlot = 0 ; testSlot <  SlotNames.numOfSlots ; testSlot ++ )  {
			EL.W( " Test Slot " + SlotNames.names [testSlot])  ;  
			directSlotMatch [testSlot].print () ; 
		}

		EL.W(" Total Slot      "  + " Recall " + directSlotRecall + "precision " + directSlotPrecision + " F1 " + directSlotF1Score );
	}
	private String clusterName(int testCluster) {
		int template = testCluster/SlotNames.numOfSlots ; 
		int slot = testCluster % SlotNames.numOfSlots ; 
		String name = " ** " + template + "-"+slot + TemplateNames.names[template]+ SlotNames.names[slot] ; 
		return name ; 
	}

}
