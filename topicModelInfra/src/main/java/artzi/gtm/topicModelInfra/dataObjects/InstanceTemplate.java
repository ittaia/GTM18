package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;

public class InstanceTemplate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int level ; 
	int instanceIndx ; 
	double probability ; 
	int instanceTemplate ; 
	public InstanceTemplate (int level ,int instanceIndx, double probability, int instanceTemplate   ) {
		super();
		this.level = level ; 
		this.instanceIndx = instanceIndx;
		this.probability = probability;
		this.instanceTemplate = instanceTemplate ; 
	}
	public int getLevel  () { 
		return this.level ; 
	}
	public int getInstanceIndx() {
		return instanceIndx;
	}
	public double getProbability() {
		return probability;
	}
	public  int   getTemplate()  {
		return this.instanceTemplate ; 
	}
}
