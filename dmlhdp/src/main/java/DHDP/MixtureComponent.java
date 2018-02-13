package DHDP;

import java.util.ArrayList;

import artzi.gtm.topicModelInfra.counters.Counters;
import artzi.gtm.topicModelInfra.counters.DCounters;
import artzi.gtm.topicModelInfra.dataObjects.ObservedComponent;
import artzi.gtm.topicModelInfra.hdpInfra.MapMixtureComponents;
import artzi.gtm.utils.elog.EL;

public class MixtureComponent {
	int mixId ; 
	int level ; 
	int thread ; 	
	int numOfContentObjects ; 
	int numOfNextLevelContentObjects ; 
	Counters observedComponentsCounter ; 
	ArrayList <Counters> featuresCounters ; 
	DCounters nextLevelMixObjectsCounter  ; 
	
	MixCountersCopy countersCopy ; 	 
	
	public MixtureComponent (int mixId , int level , ArrayList <ObservedComponent> observedComponents  )   { 
		this.mixId = mixId ; 
		numOfContentObjects = 0 ; 
		numOfNextLevelContentObjects = 0 ; 
		featuresCounters = new ArrayList <Counters> () ; 
		observedComponentsCounter = new Counters (observedComponents.size()) ;
		for (int componentId = 0 ; componentId <  observedComponents.size()  ;  componentId ++) {  
			featuresCounters.add(new Counters (observedComponents.get(componentId).getNumOfFeatures())) ; 
		}
		nextLevelMixObjectsCounter = new DCounters (1) ; 
		countersCopy = new MixCountersCopy ( observedComponents ) ; 
	}
	public int getNumOfContentObjects  () { 
		return this.numOfContentObjects  ; 
	}
	
	public int getId() {
		return this.mixId ; 
	}
	public Counters getFeatureCounters(int componentId) {
		return featuresCounters.get(componentId) ; 
	}
	public int getObservedComponentCount(int componentId) {
		return observedComponentsCounter.get(componentId) ; 
	}
	
	public int getNumOfNextLevelContentObjects ()  {
		return this.numOfNextLevelContentObjects ;   
	}
	public int getNumOfNextLevelContentObjects(int nextLevelMixId) {
		return nextLevelMixObjectsCounter.get (nextLevelMixId) ; 
	}
	public DCounters getNextLevelMixObjectsCounter() {
		return nextLevelMixObjectsCounter;
	}
	public void add1ContentObject() {  
		numOfContentObjects ++ ; 
	}
	public void dec1ContentObject() {  
		if (numOfContentObjects<= 0 ) { 
			EL.WE(9873,  " negative  object counter - Mixture " + mixId ) ; 
		}
		numOfContentObjects -- ; 
	}
	public void add1Component(int componentId) {
		observedComponentsCounter.add1(componentId) ; 		
	}	
	public void add1Feature(int componentId , int feature) {
		featuresCounters.get(componentId).add1 (feature) ;   		
	}
	
	public void add1NextLevelContentObject(int nextLevelMixId) {
		numOfNextLevelContentObjects ++ ; 
		nextLevelMixObjectsCounter.add1(nextLevelMixId) ; 
	}
	public void dec1NextLevelContentObject(int nextLevelMixId) throws Exception {
		if (numOfNextLevelContentObjects<= 0 ) { 
			EL.WE(9873,  " negative next level customer counter - Table " + mixId ) ; 
		}
		numOfNextLevelContentObjects -- ; 	
		nextLevelMixObjectsCounter.dec1(nextLevelMixId) ; 
	}
	public void dec1Component(int componentId) {
		observedComponentsCounter.dec1(componentId) ; 		
	}
	public void dec1Feature(int componentId, int feature) {
		featuresCounters.get(componentId).dec1(feature) ;   		
	}
	public void printCounters () { 
		EL.W(" Print Mixture " + mixId  + " Objects " + numOfContentObjects + " Next Level " +numOfNextLevelContentObjects) ; 
		observedComponentsCounter.print () ; 
		if (numOfNextLevelContentObjects > 0) nextLevelMixObjectsCounter.print () ; 
		//if (featuresCounters.size () > 0)featuresCounters. get(0).print() ; 
	
		
	}
	public void copyCounters(MixtureComponent rootMix , MapMixtureComponents mapMixs , 
			MapMixtureComponents nextLevelMapMixs) {
		numOfContentObjects = rootMix.getNumOfContentObjects(); 
		numOfNextLevelContentObjects = rootMix.getNumOfNextLevelContentObjects() ; 
		if (nextLevelMapMixs != null ) { 
			for (int rootNextLevelMixId = 0 ; rootNextLevelMixId < nextLevelMapMixs.getNumOfMixs() ;rootNextLevelMixId ++) { 
				int threadNextLevelTableId = nextLevelMapMixs.getThreadMix(rootNextLevelMixId) ; 
				nextLevelMixObjectsCounter.copyCell (threadNextLevelTableId, rootMix.getNextLevelMixObjectsCounter() , rootNextLevelMixId ) ; 
			}
		}
		observedComponentsCounter.copy (rootMix.getObservedComponentsCounter() ) ;  
		for (int componentId = 0 ; componentId <  featuresCounters.size()  ;  componentId ++) {  
			featuresCounters.get(componentId).copy (rootMix.getFeatureCounters(componentId)) ; 
		}		
	}
	
	public void saveCounters() {
		countersCopy.numOfObjects = this.numOfContentObjects ; 
		countersCopy.numOfNextLevelContentObjects = this.numOfNextLevelContentObjects ;  
		countersCopy.nextLevelMixObjectsCounter.copy (this.nextLevelMixObjectsCounter) ; 
		countersCopy.observedComponentsCounter.copy (this.observedComponentsCounter) ; 
		for (int componentId = 0 ; componentId <  featuresCounters.size()  ;  componentId ++) {  
			countersCopy.featuresCounters.get(componentId).copy (this.featuresCounters.get(componentId)) ; 
		}
	}

	public void sumDelta(MixtureComponent threadMix , MapMixtureComponents mapMixs ,MapMixtureComponents nextLevelMapMixs) {
		this.numOfContentObjects += threadMix.getNumOfContentObjects() - countersCopy.numOfObjects ; 
		this.numOfNextLevelContentObjects += threadMix.getNumOfNextLevelContentObjects() - countersCopy.numOfNextLevelContentObjects ; 	
		if (nextLevelMapMixs != null ) { 
			for (int rootNextLevelMixId = 0 ; rootNextLevelMixId < nextLevelMapMixs.getNumOfMixs() ;rootNextLevelMixId ++) { 
				int threadNextLevelMixId = nextLevelMapMixs.getThreadMix(rootNextLevelMixId) ; 
				int delta = threadMix.getNextLevelMixObjectsCounter().get (threadNextLevelMixId) - 
						countersCopy.nextLevelMixObjectsCounter.get (rootNextLevelMixId) ; 
				this.nextLevelMixObjectsCounter.addZ (rootNextLevelMixId, delta) ;   
			}
		}
		this.observedComponentsCounter.addDelta (threadMix.getObservedComponentsCounter() ,countersCopy.observedComponentsCounter ) ; 
		for (int componentId = 0 ; componentId <  featuresCounters.size()  ;  componentId ++) {  
			featuresCounters.get(componentId).addDelta 
			(threadMix.getFeatureCounters(componentId) , countersCopy.featuresCounters.get(componentId) ); 
		}		
	}
	
	private Counters getObservedComponentsCounter() {		 
		return this.observedComponentsCounter ; 
	}			 
}
