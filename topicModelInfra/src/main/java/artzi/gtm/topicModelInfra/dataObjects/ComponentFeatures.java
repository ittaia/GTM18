package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;
import artzi.gtm.topicModelInfra.counters.FeatureCount;
import artzi.gtm.utils.elog.EL;

public class ComponentFeatures implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int componentId ; 
	ArrayList<FeatureCount> featuresList ; 	
	int numOfFeatures ; 
	public ComponentFeatures (int componentId) { 
		this.componentId = componentId ; 
		featuresList = new ArrayList <FeatureCount> () ;  
		numOfFeatures = 0 ; 
	}
	public int getComponentId() {
		return componentId;
	}
	public void addFeature(int feature) {
		boolean found = false ; 
		for (FeatureCount featureCount : featuresList ) { 
			if (featureCount.getFeature() == feature) { 
				found = true ; 
				featureCount.add1() ; 			
				break ; 
			}
		}
		if (!found) { 
			FeatureCount  featureCount = new FeatureCount (feature) ; 
			featuresList.add (featureCount) ; 
		}
		numOfFeatures ++ ; 
	}
	public void decFeature(int feature) {
		boolean found = false ; 
		for (FeatureCount featureCount : featuresList ) { 
			if (featureCount.getFeature() == feature) { 
				found = true ; 
				featureCount.dec1() ; 			
				break ; 
			}
		}
		if (!found) { 
			EL.WE(12346 , " Feature not found " + feature ) ; 
		}
		numOfFeatures -- ; 
	}	
	public ArrayList<FeatureCount> getFeaturesList() {
		return featuresList;
	}
	public int getSize () { 
		return featuresList.size () ; 
	}
	public double getNumOfFeatures() {
		return numOfFeatures ; 
	}
	public int getNumOfFeaturesN() {
		return numOfFeatures ; 
	}
	public int get(int feature) {
		for (FeatureCount featureCount : featuresList ) { 
			if (featureCount.getFeature() == feature) { 
				return featureCount.getCount() ; 
			}
		}
		return 0 ; 
	}
}
