package artzi.gtm.dmlhdp.appInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class MixFeatures1 implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String componentName ; 
	ArrayList<String> features ;
	public MixFeatures1(String componentName, ArrayList<String> features) {
		super();
		this.componentName = componentName;
		this.features = features;
	}
	public MixFeatures1(String componentName) {
		super () ; 
		this.componentName = componentName ; 
		this.features = new ArrayList <String> () ; 
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public ArrayList<String> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<String> features) {
		this.features = features;
	}
	public void add(String feature) {
		features.add(feature) ; 
		
	} 
}
