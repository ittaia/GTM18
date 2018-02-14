package artzi.gtm.dmlhdp.dhdp;

import java.util.ArrayList;

import artzi.gtm.topicModelInfra.counters.Counters;
import artzi.gtm.topicModelInfra.counters.DCounters;
import artzi.gtm.topicModelInfra.dataObjects.ObservedComponent;

public class MixCountersCopy {

	public int numOfObjects ; 
	public int numOfNextLevelContentObjects ; 
	public Counters observedComponentsCounter ; 
	public ArrayList <Counters> featuresCounters ; 
	public DCounters nextLevelMixObjectsCounter  ; 
	
	public MixCountersCopy (ArrayList <ObservedComponent> observedComponents) { 
		featuresCounters = new ArrayList <Counters> () ; 
		observedComponentsCounter = new Counters (observedComponents.size()) ;
		for (int componentId = 0 ; componentId <  observedComponents.size()  ;  componentId ++) {  
			featuresCounters.add(new Counters (observedComponents.get(componentId).getNumOfFeatures())) ; 
		}
		nextLevelMixObjectsCounter = new DCounters (1) ;   
	}
}
