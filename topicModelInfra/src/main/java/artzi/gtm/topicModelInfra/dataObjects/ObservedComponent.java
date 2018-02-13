package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;

public class ObservedComponent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int componentId ; 
	int numOfFeatures ; 
	double beta ; 
	double numOfFeaturesBeta ; 
	public ObservedComponent (int componentId , int numOfFeatures, double beta) { 
		this.componentId = componentId ; 
		this.numOfFeatures = numOfFeatures ; 
		this.beta = beta ;
		this.numOfFeaturesBeta = numOfFeatures * beta ; 		
	}
	public int getComponentId() {
		return componentId;
	}
	public int getNumOfFeatures() {
		return numOfFeatures;
	}
	public double getBeta() {
		return beta;
	}
	public double getNumOfFeaturesBeta() {
		return numOfFeaturesBeta;
	}
	public void updateBeta (double beta) { 
		this.beta = beta ; 
		this.numOfFeaturesBeta = numOfFeatures * beta ; 	
	}
}
