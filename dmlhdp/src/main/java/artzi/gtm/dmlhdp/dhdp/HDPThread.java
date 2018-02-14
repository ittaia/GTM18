
package artzi.gtm.dmlhdp.dhdp;

import java.util.ArrayList;

import artzi.gtm.dmlhdp.mlhdp.MLHDPParms;
import artzi.gtm.topicModelInfra.counters.Counters;
import artzi.gtm.topicModelInfra.counters.FeatureCount;
import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.topicModelInfra.dataObjects.ContentObject;
import artzi.gtm.topicModelInfra.dataObjects.GInstance;
import artzi.gtm.topicModelInfra.dataObjects.ObservedComponent;
import artzi.gtm.topicModelInfra.gibbsMath.DirichletMultLogProp;
import artzi.gtm.topicModelInfra.hdpInfra.StickBreakingWeights;
import artzi.gtm.topicModelInfra.logProportions.LogProportion;
import artzi.gtm.utils.elog.EL;

public class HDPThread {

	int threadId ; 
	int numOfRootContentObjects ; 
	HDPState state ; 	
	ArrayList <ObservedComponent> observedComponents ; 
	StickBreakingWeights stickBreakingWeights ;
	MLHDPParms parms = null ;  
	public HDPThread(int threadId , int level , int levels , double gammaTop , double gamma , double alpha0 , 
			ArrayList <ObservedComponent> observedComponents) { 
		parms = MLHDPParms.getInstance() ;  
		this.threadId = threadId ; 
		numOfRootContentObjects = 0 ; 
		state = new HDPState (level , levels , gammaTop , gamma , alpha0  ) ; 	
		this.observedComponents = observedComponents ; 
		stickBreakingWeights = new  StickBreakingWeights () ; 
	}
	public void extendMixs(int maxMixsInLevel) {
		for (int mixId = state.mixs.size () ; mixId < maxMixsInLevel ; mixId++ )   { 
			MixtureComponent mix = new MixtureComponent (mixId , state.level , observedComponents) ; 
			state.mixs.add(mix) ; 		
		}
		state.numOfMixs = state.mixs.size () ; 
	}
	public void extendNextLevelMixs (int numOfNextLevelMixs) { 
		state.numOfNextLevelMixs = numOfNextLevelMixs ; 
		stickBreakingWeights.extendMixs(state.numOfNextLevelMixs) ; 
	}
	public int addContentObject(GInstance instance, int ownerIndx ) {
		int indx = state.contentObjects.size () ; 
		ContentObject  contentObject = new ContentObject (instance , indx , ownerIndx) ; 
		state.contentObjects.add(contentObject) ; 
		state.numOfContentObjects ++ ; 
		return indx ; 		
	}
	
	public void copyCounters (HDPRoot rootHdp ,  HDPRoot nextLevelHdp  ) { 
			 
		numOfRootContentObjects = rootHdp.getNumOfContentObjects() ; 
		state.gammaTop = rootHdp.getGammaTop() ; 
		state.gamma = rootHdp.getGamma() ; 
		state.alpha0 = rootHdp.getAlpha0() ; 
		for (int threadmixId = 0 ; threadmixId < state.mixs.size(); threadmixId ++  ) { 
			MixtureComponent threadMix = state.mixs.get(threadmixId) ; 
			int rootmixId = rootHdp.getMapMixs(threadId).getRootMix( threadmixId) ; 
			if (nextLevelHdp != null ) { 
				threadMix.copyCounters (rootHdp.getMix(rootmixId) , rootHdp.getMapMixs (threadId) , nextLevelHdp.getMapMixs(threadId)) ; 	
			}
			else { 
				threadMix.copyCounters (rootHdp.getMix(rootmixId) , rootHdp.getMapMixs (threadId) , null) ; 	
			}
		}
		if (state.level < state.levels-1 ) { 
			stickBreakingWeights.copy (rootHdp.getStickBreakingWeights () , nextLevelHdp.getMapMixs (threadId)) ; 
		}
	}

	public ContentObject getContentObject(int contentObjectIndx) {
		return state.contentObjects.get(contentObjectIndx) ;
	}
	public int getNumOfContentObjects () { 
		return state.numOfContentObjects ; 
	}
	public int getNumOfRootContentObjects () { 
		return this.numOfRootContentObjects ; 
	}
	public int getNumOfContentObjects (int mixId) { 
		return state.mixs.get(mixId).getNumOfContentObjects() ; 		
	}
	public int getNumOfNextLevelContentObjects(int mixId) {
		return state.mixs.get(mixId).getNumOfNextLevelContentObjects();
	}
	public int getNumOfNextLevelContentObjects(int mixId, int nextLevelmixId) {
		return state.mixs.get(mixId).getNumOfNextLevelContentObjects(nextLevelmixId); 
	}
	public int getComponentCount (int mixId , int componentId) { 
		return state.mixs.get(mixId).getObservedComponentCount(componentId) ; 
	}
	public Counters getFeatureCounters (int mixId , int componentId) { 
		return state.mixs.get(mixId).getFeatureCounters(componentId) ;  
	}
	public ArrayList <ObservedComponent> getObservedComponents () { 
		return this.observedComponents ; 
	}

	public int getNumOfMixs() {
		return state.mixs.size () ; 
	}
	public MixtureComponent getMix (int mixId) { 
		return state.mixs.get(mixId) ; 
	}

	public LogProportion getDPLogProportion(int mixId, int  contentObjectIndx , HDPThread[] hdps) {
		double logPropVal ;  
		boolean zero ; 
		if (mixId < state.mixs.size ()) { 
			MixtureComponent mix = state.mixs.get(mixId) ; 
			if (mix.getNumOfContentObjects() == 0) { 
				zero = true ; 
				logPropVal = 0 ; 
			}
			else { 
				LogProportion logProportionObject = contentObjectLogProportion(mix , contentObjectIndx ) ; 			 
				zero =  logProportionObject.isZero() ; 	
				logPropVal  = Math.log(mix.getNumOfContentObjects()) - Math.log(numOfRootContentObjects + state.gammaTop ) + 
						logProportionObject.getLogProp() ; 

			}					 
		}
		else { 
			LogProportion logProportionObject = contentObjectLogProportionNewMix (contentObjectIndx ) ; 
			zero = false ;  
			logPropVal  = Math.log(state.gammaTop) - Math.log (numOfRootContentObjects + state.gammaTop )
					+logProportionObject.getLogProp() ; 
		}
		LogProportion  logProportion = new LogProportion (logPropVal , zero); 
		return logProportion ; 
	}

	public LogProportion getHDPLogProportion(int ownermixId, int mixId , int  contentObjectIndx , HDPThread[] hdps) {
		double logPropVal ;  
		boolean zero ; 
		if (mixId < state.mixs.size ()) { 
			MixtureComponent mix = state.mixs.get(mixId) ; 
			if (mix.getNumOfContentObjects() == 0) { 
				zero = true ; 
				logPropVal = 0 ; 
			}
			else { 
				LogProportion logProportionObject = contentObjectLogProportion(mix , contentObjectIndx ) ; 			 
				zero =  logProportionObject.isZero() ; 	
				logPropVal  = Math.log (hdps[state.level-1].getNumOfNextLevelContentObjects(ownermixId,mixId) + 
									   (hdps[state.level-1].getAlpha0()*
										hdps[state.level-1].getStickBreakingWeights().getWeight(mixId) ) )
							- Math.log(hdps[state.level-1].getNumOfNextLevelContentObjects(ownermixId) + hdps[state.level-1].getAlpha0()) ; 
				logPropVal +=  logProportionObject.getLogProp() ; 
			}					 
		}
		else { 
			LogProportion logProportionObject = contentObjectLogProportionNewMix( contentObjectIndx ) ; 
			zero = false ;  
			logPropVal  = Math.log(hdps[state.level-1].getAlpha0()*hdps[state.level-1].getStickBreakingWeights().getWeightNew()) 
					- Math.log(hdps[state.level-1].getNumOfNextLevelContentObjects(ownermixId) + hdps[state.level-1].getAlpha0()) ; 
			logPropVal +=  logProportionObject.getLogProp() ; 
		}
		LogProportion  logProportion = new LogProportion (logPropVal , zero); 
		return logProportion ; 
	}
	
	private double getAlpha0() {
		return state.alpha0 ; 
	}
	private LogProportion contentObjectLogProportion(MixtureComponent mix , int contentObjectIndx  ) {
		double logPropVal = 0 ; 
		boolean zero = false ; 
		ContentObject contentObject = state.contentObjects.get(contentObjectIndx) ; 
		GInstance instance = contentObject.getInstance() ; 
		
		if (state.level < state.levels-1) {		
			ComponentFeatures nextLevelMixs = contentObject.getMixMemberCounters() ;  
			logPropVal += DirichletMultLogProp.contentObjectComponentMixLogProportion
					(nextLevelMixs,mix.getNumOfNextLevelContentObjects(), mix.getNextLevelMixObjectsCounter(),
							stickBreakingWeights.getWeights() , state.alpha0) ; 
		}
		if (instance.getFeatureLists().size () > 0 )  { 
			double basicFeaturesLogProportion = 0 ; 
			for (ComponentFeatures componentFeatures : instance.getFeatureLists()) { 
				int componentId = componentFeatures.getComponentId() ; 
				ObservedComponent observedComponent =  observedComponents.get(componentId) ; 
				Counters mixValueCounters = mix.getFeatureCounters (componentId) ;
				int mixComponentCount  = mix.getObservedComponentCount (componentId) ;
				basicFeaturesLogProportion += DirichletMultLogProp.contentObjectComponentMixLogProportion
						(componentFeatures, mixComponentCount, mixValueCounters, observedComponent.getBeta (), 
								observedComponent.getNumOfFeaturesBeta()) ; 
			} 
			logPropVal += basicFeaturesLogProportion ; 
			if (parms.inverse & instance.hasInverseFeatures ()) { 
				double inverseFeaturesLogProportions = 0 ; 
				for (ComponentFeatures componentFeatures : instance.getInverseFeatureLists()) { 
					int componentId = componentFeatures.getComponentId() ; 
					ObservedComponent observedComponent =  observedComponents.get(componentId) ; 
					Counters mixValueCounters = mix.getFeatureCounters (componentId) ;
					int mixComponentCount  = mix.getObservedComponentCount (componentId) ;
					inverseFeaturesLogProportions += DirichletMultLogProp.contentObjectComponentMixLogProportion
							(componentFeatures, mixComponentCount, mixValueCounters, observedComponent.getBeta (), 
									observedComponent.getNumOfFeaturesBeta()) ; 
				}
				if (inverseFeaturesLogProportions > parms.minInverseLogProportion & 
				    inverseFeaturesLogProportions > basicFeaturesLogProportion) { 
					logPropVal += parms.inverseMatchPenalty ; 
					EL.WE (4352 , "Penalty" + " - " + mix.getId() + " inverse- " + inverseFeaturesLogProportions  
							+ " Basic-" + basicFeaturesLogProportion); 
				}
			}
		}
		
		/*
		
		if (state.level == 0 ) { 
			EL.W( "Object log prop : Mix " + mix.getId() + " contentObject " + contentObjectIndx + " - " + logPropVal ) ;   
		}
		*/
		
		LogProportion logProportion = new LogProportion (logPropVal , zero) ;  
		return logProportion ; 
	}
	
	
	private LogProportion contentObjectLogProportionNewMix(int contentObjectIndx ) {
		double logPropVal = 0 ; 
		boolean zero = false ; 
		ContentObject contentObject = state.contentObjects.get(contentObjectIndx) ; 
		GInstance instance = contentObject.getInstance() ; 
		if (state.level < state.levels-1) { 
			logPropVal += DirichletMultLogProp.contentObjectComponentNewMixLogProportion
					(contentObject.getMixMemberCounters(), stickBreakingWeights.getWeights(), 
							state.alpha0) ; 
		}
		if (instance.getFeatureLists().size () > 0 )  { 
			for (ComponentFeatures componentFeatures : instance.getFeatureLists()) { 
				int componentId = componentFeatures.getComponentId() ; 
				ObservedComponent observedComponent =  observedComponents.get(componentId) ; 
				logPropVal += DirichletMultLogProp.contentObjectComponentNewMixLogProportion
						(componentFeatures,  observedComponent.getBeta (), 
								observedComponent.getNumOfFeaturesBeta()) ; 				
			}
		}
		
		/*

		if ( state.level ==0 )  { 
			EL.W( "Object log prop : New Mix "  +  contentObjectIndx + " - " + logPropVal ) ;   
		}
		 */
		LogProportion logProportion = new LogProportion (logPropVal , zero) ;  
		return logProportion ; 
	}

	public void assignContentObject(int contentObjectIndx, int mixId , HDPThread[] hdps) {
		ContentObject contentObject = state.contentObjects.get (contentObjectIndx) ; 
		HDPThread ownerLevelHDP = null , memberLevelHDP = null ; 
		if (state.level > 0 ) ownerLevelHDP = hdps[state.level-1] ; 
		if (state.level < state.levels-1 ) memberLevelHDP = hdps[state.level+1] ; 
		if (mixId == state.mixs.size ()) { 
			//System.out.println(" New Mix" + "Level " +   state.level + " CI "   +contentObjectIndx  + "  ID " + mixId ) ; 
			mixId = openNewMix (ownerLevelHDP) ; 			
		}
		contentObject.setMix (mixId) ; 
		MixtureComponent mix = state.mixs.get(mixId) ; 
		mix.add1ContentObject() ;   
		for (ComponentFeatures  componentfeatures : contentObject.getInstance().getFeatureLists()) { 
			int componentId = componentfeatures.getComponentId() ;

			for (FeatureCount featureCount :  componentfeatures.getFeaturesList()) { 
				for (int m = 0 ; m < featureCount.getCount() ; m ++) { 
					mix.add1Component (componentId) ;
					mix.add1Feature(componentId, featureCount.getFeature()) ; 
				}
			}
		}
		if (state.level  < state.levels-1) { 
			
			for (int memberIndx = contentObject.getFromMember() ; memberIndx <= contentObject.getToMember() ; memberIndx ++ ) { 
				int nextLevelMixIndx = memberLevelHDP.getContentObject(memberIndx).getMix()  ; 				
				if (nextLevelMixIndx > -1) { 
					mix.add1NextLevelContentObject (nextLevelMixIndx) ;   					 
				}
			}
		}
		if (state.level > 0 ) { 
			HDPThread ownerLevelCrp = hdps[state.level-1] ; 		 
			int ownerIndx = contentObject.getOwnerIndx() ; 
			ownerLevelCrp.add1NextLevelObject (ownerIndx ,  mixId) ; 
		}
	}

	private void add1NextLevelObject (int contentObjectIndx , int memberMixIndx ) {
		ContentObject contentObject = state.contentObjects.get(contentObjectIndx) ; 
		contentObject.addMemberMix (memberMixIndx) ; 
		int mixId = contentObject.getMix() ; 
		if (mixId > -1) { 
			MixtureComponent mix = state.mixs.get(mixId) ; 
			mix.add1NextLevelContentObject(memberMixIndx) ; 
		}
	}
	private void decNextLevelObject (int contentObjectIndx, int memberMixIndx) throws Exception {
		ContentObject contentObject = state.contentObjects.get(contentObjectIndx) ; 
		contentObject.decMemberMix (memberMixIndx) ; 
		int mixId = contentObject.getMix() ; 
		if (mixId > -1) { 
			MixtureComponent mix = state.mixs.get(mixId) ; 
			mix.dec1NextLevelContentObject(memberMixIndx) ; 
		}
	}

	private int openNewMix(HDPThread ownerLevelHDP) {
		int mixId = -1 ; 	
		for (MixtureComponent mix: state.mixs) {  
			if (mix.getNumOfContentObjects() == 0) { 
				mixId = mix.getId() ;
				if (state.level > 0) {  
					ownerLevelHDP.getStickBreakingWeights().reUseMix(mixId) ; 
				}
				break ; 
			}
		}
		if (mixId< 0 ) { 
			mixId = state.mixs.size () ;  
			MixtureComponent mix = new MixtureComponent (mixId, state.level ,  observedComponents ) ; 
			state.mixs.add(mix) ; 
			if (state.level > 0) { 
				ownerLevelHDP.getStickBreakingWeights().extendMixs(mixId) ; 
			}
		} 
		return mixId ; 
	}

	public void unAssignContentObject(int contentObjectIndx, HDPThread [] hdps ) throws Exception {
		ContentObject contentObject = state.contentObjects.get (contentObjectIndx) ; 
		int mixId = contentObject.getMix() ; 
		if (mixId > -1) { 
			contentObject.setMix (-1) ; 
			MixtureComponent mix = state.mixs.get(mixId) ; 
			mix.dec1ContentObject() ;   
			for (ComponentFeatures  componentfeatures : contentObject.getInstance().getFeatureLists()) { 
				int componentId = componentfeatures.getComponentId() ;
				for (FeatureCount featureCount : componentfeatures.getFeaturesList()) {
					for (int m = 0 ; m < featureCount.getCount() ; m ++) { 
						mix.dec1Component(componentId) ; 
						mix.dec1Feature(componentId,featureCount.getFeature()) ; 
					}
				}
			}
			if (state.level < state.levels-1) { 
				HDPThread memberLevelCrp = hdps[state.level+1] ; 

				for (int memberIndx = contentObject.getFromMember() ; memberIndx <= contentObject.getToMember() ; memberIndx ++ ) { 
					int nextLevelMixIndx = memberLevelCrp.getContentObject(memberIndx).getMix()  ; 				
					if (nextLevelMixIndx > -1) { 
						mix.dec1NextLevelContentObject (nextLevelMixIndx) ;   					 
					}
				}
			}
			if (state.level > 0 ) { 
				HDPThread ownerLevelCrp = hdps[state.level-1] ; 		 
				int ownerIndx = contentObject.getOwnerIndx() ; 
				ownerLevelCrp.decNextLevelObject (ownerIndx , mixId) ; 
			}
		}
	}
	
	public void validateCounters(HDPThread [] hdps ) {
		int totObjects = 0 ; 
		
		if (state.level < state.levels-1) { 
			int [] numOfNextLevelContentObjects = new int [hdps [state.level+1].getNumOfMixs()]  ; 
			for (ContentObject contentObject : state.contentObjects) { 
				int totMembers = 0 ; 
				for (int t1 = 0 ; t1 < hdps [state.level+1].getNumOfMixs(); t1 ++) { 
					totMembers += contentObject.getMixMemberCounters().get(t1) ;  
				}
				if (contentObject.getNumOfMembers() != totMembers) { 
					EL.WE (9988 , "bad Object - Level "   + state.level +   " contentObject " + contentObject.getIndx()) ;  
				}			
			}
			for (int t1 = 0 ; t1 < hdps [state.level+1].getNumOfMixs(); t1 ++) { 
				numOfNextLevelContentObjects [t1] = 0 ; 			 
				for (int mixId = 0 ; mixId < state.mixs.size () ; mixId++) {
					numOfNextLevelContentObjects [t1] += hdps[state.level].getNumOfNextLevelContentObjects(mixId, t1) ; 
				}			 
				if (numOfNextLevelContentObjects [t1] != hdps [state.level+1].getNumOfContentObjects(t1)  ) {
					EL.WE(567833 ,  "Bad Next Level contentObject counters - level "  + state.level + " Mix " + t1 + " - " +  
							hdps[state.level].getNumOfNextLevelContentObjects(t1)  + " - " + hdps [state.level+1].getNumOfContentObjects(t1))  ; 
				}				
			}					
		}
		for (int mixId = 0 ; mixId < state.mixs.size () ; mixId++) {
			MixtureComponent mix = state.mixs.get(mixId) ; 
			totObjects += mix.getNumOfContentObjects() ; 
			 
		}
		EL.WE(5678 ,  " Tot Objects " + totObjects ) ; 
		if (totObjects != this.getNumOfRootContentObjects() ) {
			EL.WE(56781 ,  "Bad contentObject counters - level "  + state.level + " - " + totObjects + " - " 
						+ this.getNumOfRootContentObjects());  
		}
	}
	public void printCounters () { 
		EL.W(" Print HDP   " + state.level + " Mixs " + state.mixs.size () ) ; 
		for (MixtureComponent mix :state.mixs ) { 
			mix.printCounters() ; 
		}		
	}
	
	public StickBreakingWeights getStickBreakingWeights() {		 
		return stickBreakingWeights ; 
	}
}
