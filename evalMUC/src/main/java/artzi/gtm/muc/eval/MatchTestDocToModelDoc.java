package artzi.gtm.muc.eval;

import java.util.ArrayList;

import artzi.gtm.dmlhdp.appInterface.ModelDoc;
import artzi.gtm.dmlhdp.appInterface.ModelVal;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.textUtils.NameHead;

public class MatchTestDocToModelDoc {
	String ID ; 
	int modelCluster ; 
	ArrayList <Integer> modelClusters ; 	
	int testCluster ;
	int testSlot ; 
	double minProb ; 
	ModelDoc modelDoc ; 
	TestDoc testDoc ;
	double correct = 0 ; 
	double missing = 0 ; 
	double correctExtraction = 0 ; 
	double falseExtraction = 0 ; 	
	
	public MatchTestDocToModelDoc(String iD, int testCluster,int modelCluster,
			TestDoc testDoc , ModelDoc modelDoc , boolean print, double minProb) {
		super();
		ID = iD;
		this.modelCluster = modelCluster;
		this.modelClusters = null ; 
		this.testCluster = testCluster;
		this.modelDoc = modelDoc;
		this.testDoc = testDoc;	 
		this.minProb = minProb ; 
		match1 (print) ;    
	}
	public MatchTestDocToModelDoc(String iD, int testSlot, ArrayList <Integer> modelClusters, 
			TestDoc testDoc , ModelDoc modelDoc , boolean print, double minProb) {
		super();
		ID = iD;
		this.testSlot = testSlot;
		this.modelClusters = modelClusters;		
		this.modelDoc = modelDoc;
		this.testDoc = testDoc;	 
		this.minProb = minProb ; 
		match2 (print) ;    
	}
	
	public MatchTestDocToModelDoc(String iD, boolean bySlot , int testSlot, int modelCluster, 
			TestDoc testDoc , ModelDoc modelDoc , boolean print, double minProb) {
		super();
		ID = iD;
		this.testSlot = testSlot;
		this.modelCluster  = modelCluster;
		this.modelClusters = null ; 
		this.modelDoc = modelDoc;
		this.testDoc = testDoc;	 
		this.minProb = minProb ; 
		match3 (print) ;    
	}
	private void match1 (boolean print) { 		 
		ArrayList <ORVals>   testNames = testDoc.getClusterVals().get(testCluster) ; 
		ArrayList <ModelVal> modelNames = modelDoc.getClustersVals().get(modelCluster) ;   
		if (print)  { 
			EL.W(" Doc " + ID + " Test Cluster " +  clusterName (testCluster) + " ModelCluster " + modelCluster ) ; 
			for (ORVals testVal : testNames) { 
				testVal.print () ; 
			}
			for (ModelVal modelName : modelNames ) { 
				EL.W("Model Name-" + modelName.getName());   
			}
		}
		matchNames (testNames , modelNames ,print) ; 
	}
	private void match2 (boolean print) { 		 
		ArrayList <ORVals>   testNames = new ArrayList <ORVals>() ; 
		for (int template = 0 ; template < TemplateNames.numOfTemplates ; template++) { 
			int cluster = (template * SlotNames.numOfSlots) + testSlot ; 
			testNames.addAll  ( testDoc.getClusterVals().get(cluster)) ; 
		}
		
		ArrayList <ModelVal> modelNames = new ArrayList <ModelVal> () ; 
		for (int cluster : modelClusters ) { 
				addNoDup (modelNames  , modelDoc.getClustersVals().get(cluster)) ; 
		}
		if (print)  { 
			EL.W(" Doc " + ID + " Test Slot " +  SlotNames.names [testSlot] + " ModelClusters " + modelClusters ) ; 
			for (ORVals testVal : testNames) { 
				testVal.print () ; 
			}
			for (ModelVal modelName : modelNames ) { 
				EL.W("Model Name-" + modelName.getName());   
			}
		}
		matchNames (testNames , modelNames, print) ; 
	}
	
	
	private void match3 (boolean print) { 		 
		ArrayList <ORVals>   testNames = testDoc.getSlotVals().get(testSlot) ; 
		ArrayList <ModelVal> modelNames = modelDoc.getClustersVals().get(modelCluster) ;  
		if (print)  { 
			EL.W(" Doc " + ID + " Test Slot " +  SlotNames.names [testSlot] + " ModelCluster " + modelCluster ) ; 
			for (ORVals testVal : testNames) { 
				testVal.print () ; 
			}
			for (ModelVal modelName : modelNames ) { 
				EL.W("Model Name-" + modelName.getName());   
			}
		}
		matchNames (testNames , modelNames, print) ; 
	}
	private void addNoDup(ArrayList<ModelVal> modelNames, ArrayList<ModelVal> clusterVals) {
		for (ModelVal newVal : clusterVals ) { 
			boolean dup = false ; 
			for (ModelVal val : modelNames) { 
				if (val.getName().equals(newVal.getName()) ) {  
					dup = true ; 
					val.setMaxProb (newVal.getProb()) ; 
					break ; 
				}
			}
			if (!dup )  modelNames.add(newVal) ;	
		}
		
	}
		
	private void matchNames( ArrayList <ORVals>   testNames  ,ArrayList <ModelVal> modelNames, boolean print)  { 		
		for (ORVals testVal : testNames) { 
			if ( testVal.isOptional()) continue ; 	
			if (testVal.isEmpty()) continue ; 
			boolean found = false ; 
			for (ModelVal modelName : modelNames ) { 
				if (modelName.getProb() >= minProb)  { 
					if (matchHead (testVal , modelName.getName()))  { 
						if (print ) EL.W(" Match 1 " + modelName + " test " + testVal.getString () )   ;  
						found = true ;  
						break ; 
					}
				}
			}
			if (found) correct ++ ; 
			else       { 
				missing++ ; 
				if (print) EL.W(" Missing 1 "  + testVal.getString () )   ;  
				found = true ;  
			}
		}		
		for (ModelVal modelName : modelNames ) { 
			if (modelName.getProb() >= minProb)  { 
				boolean found = false ;  
				for (ORVals testVal : testNames) { 
					if (matchHead (testVal , modelName.getName()))  { 
						if (print)  EL.W(" Match 2 " + modelName + " test " + testVal.getString () )   ;  
						found = true ; 
						break ; 
					}
				}
				if (found) correctExtraction ++ ; 
				else       { 
					falseExtraction ++ ; 
					if (print ) EL.W(" Missing 2  " + modelName    )   ; 
				}
			}
		}		
	}
	
	private String clusterName(int testCluster) {
		int template = testCluster/SlotNames.numOfSlots ; 
		int slot = testCluster % SlotNames.numOfSlots ; 
		String name = " ** " + template + "-"+slot + TemplateNames.names[template]+ SlotNames.names[slot] ; 
		return name ; 
	}
	private boolean matchHead(ORVals testVal , String modelName) {
		 boolean match = false ; 
		 for (String val  :testVal.getVals() ) { 
			 if  (NameHead.getHead(val).equals(NameHead.getHead (modelName)) ) { 
				match = true ; 
				break ; 
			 }			 
		 }
		 return match ; 
	}
		
	public String getID() {
		return ID;
	}
	public int getModelCluster() {
		return modelCluster;
	}
	public int getTestCluster() {
		return testCluster;
	}
	public ModelDoc getModelDoc() {
		return modelDoc;
	}
	public TestDoc getTestDoc() {
		return testDoc;
	}
	public double getCorrect() {
		return correct;
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
	
}
