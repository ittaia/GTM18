package artzi.gtm.muc.eval;

import artzi.gtm.dmlhdp.appInterface.ModelDoc;
import artzi.gtm.utils.elog.EL;

public class MatchTestSlotToModelCluster {
	int modelCluster ; 
	int testSlot ; 
	double correct = 0 , missing = 0 , correctExtraction = 0 , falseExtraction = 0 ; 
	double recall , precision , F1Score ; 
	double minProb ; 
	TestDocs testDocs ; 
	ModelDocs modelDocs ; 
	
	public MatchTestSlotToModelCluster 
	       (int testSlot, int modelCluster , TestDocs testDocs ,  ModelDocs modelDocs, double minProb ) throws Exception {
		super();
		this.modelCluster = modelCluster;
		this.testSlot = testSlot;
		this.testDocs = testDocs ; 
		this.modelDocs = modelDocs ; 
		this.minProb = minProb ; 
		matchTestDocsToModelDocs (testDocs , modelDocs) ; 
	}
	private void matchTestDocsToModelDocs(TestDocs testDocs, ModelDocs modelDocs) throws Exception {
		for (TestDoc testDoc : testDocs.getList()) { 
			ModelDoc modelDoc = modelDocs.getDoc (testDoc.getID()) ; 
			boolean bySlot = true ;  
			MatchTestDocToModelDoc match = 
					new MatchTestDocToModelDoc (testDoc.getID(), bySlot , testSlot , 
							modelCluster , testDoc , modelDoc , false , minProb) ; 
			correct += match.getCorrect() ; 
			missing+= match.getMissing() ; 
			correctExtraction+= match.getCorrectExtraction() ; 
			falseExtraction += match.getFalseExtraction() ; 			
		}
		recall =  correct / (correct+missing) ; 
		precision = correctExtraction/(correctExtraction + falseExtraction) ; 
		F1Score = 2 * (recall*precision)/(recall+precision) ; 
	}
	
	public double getF1Score() {
		return F1Score;
	}
	public void print() {
		EL.W(" Model Cluster " + modelCluster + " Recall " + recall + "precision " + precision + " F1 " + F1Score );
		
	}
	public double getCorrect() {
		return this.correct  ; 
	}
	public double getMissing() {
		return missing;
	}
	public double getCorrectExtraction() {
		return correctExtraction;
	}
	public double getFalseExtraction() {
		return falseExtraction;
	}
	public void printDocs() throws Exception {
		for (TestDoc testDoc : testDocs.getList()) { 
			ModelDoc modelDoc = modelDocs.getDoc (testDoc.getID()) ; 
			new MatchTestDocToModelDoc (testDoc.getID(), testSlot , modelCluster , testDoc , modelDoc , true , minProb ) ; 
		}
	} 	
}