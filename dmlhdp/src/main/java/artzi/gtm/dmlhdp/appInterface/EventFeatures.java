package artzi.gtm.dmlhdp.appInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class EventFeatures implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int eventId ; 
	String nameText ; 
	ArrayList<MixFeatures1> features ;
	ArrayList <InstanceFeatures> instances ; 	
	public EventFeatures(int eventId, String nameText , ArrayList<MixFeatures1> features) {
		super();
		this.nameText = nameText ; 
		this.eventId = eventId;
		this.features = features;
		instances = new ArrayList <InstanceFeatures> () ; 
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getNameText () { 
		return this.nameText  ; 
	}
	public ArrayList<MixFeatures1> getFeatures() {
		return features;
	}
	public void addInstance(InstanceFeatures instanceFeatures) {
		instances.add(instanceFeatures) ; 		
	}
	public 	ArrayList <InstanceFeatures> getInstances () { 
		return this.instances ; 
	}
}
