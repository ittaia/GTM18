package Interface;

import java.io.Serializable;
import java.util.ArrayList;

public class InstanceFeatures implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int instanceId ; 
	String nameText ; 
	ArrayList<MixFeatures1> features ;
	ArrayList<MixFeatures1> inverseFeatures ;
	public InstanceFeatures(int instanceId, String nameText , ArrayList<MixFeatures1> features) {
		super();
		this.nameText = nameText ; 
		this.instanceId = instanceId;
		this.features = features;
		this.inverseFeatures = null;
	}
	public InstanceFeatures(int instanceId, String nameText, 	ArrayList<MixFeatures1> features, ArrayList<MixFeatures1> inverseFeatures) {
		super();
		this.nameText = nameText ; 
		this.instanceId = instanceId;
		this.features = features;
		this.inverseFeatures = inverseFeatures;
	}
	public int getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}
	public String getNameText () { 
		return this.nameText  ; 
	}
	public ArrayList<MixFeatures1> getFeatures() {
		return features;
	}
	public ArrayList<MixFeatures1> getInverseFeatures() {
		return inverseFeatures;
	}
	
}
