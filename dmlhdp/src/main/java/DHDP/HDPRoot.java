package DHDP;

import java.util.ArrayList;

import MLHDP.MLHDPLevelData;
import MLHDP.MLHDPParms;
import artzi.gtm.topicModelInfra.counters.Counters;
import artzi.gtm.topicModelInfra.dataObjects.ObservedComponent;
import artzi.gtm.topicModelInfra.gibbsMath.HDPHyperParms;
import artzi.gtm.topicModelInfra.gibbsMath.LDAHyperParms;
import artzi.gtm.topicModelInfra.hdpInfra.MapMixtureComponents;
import artzi.gtm.topicModelInfra.hdpInfra.StickBreakingWeights;
import artzi.gtm.utils.elog.EL;

public class HDPRoot {

	HDPState state ; 
	MLHDPLevelData data ; 
	MapMixtureComponents [] mapMixs ; 
	StickBreakingWeights stickBreakingWeights ;
	MLHDPParms parms ; 
	public HDPRoot (int level , int levels , double gammaTop , double gamma , double alpha0 ,  MLHDPLevelData data) { 
		parms = MLHDPParms.getInstance() ; 
		state = new HDPState (level , levels ,  gammaTop , gamma , alpha0 ) ; 
		this.data = data ; 
		mapMixs = new MapMixtureComponents [parms.numOfThreads] ; 
		for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
			mapMixs [threadId] = 	new MapMixtureComponents () ;  
		}
		stickBreakingWeights = new  StickBreakingWeights () ; 
	}
	
	public void extendMixs(int maxMixsInLevel) {
		for (int mixId = state.mixs.size () ; mixId < maxMixsInLevel ; mixId++ )   { 
			MixtureComponent mix = new MixtureComponent (mixId , state.level , data.getObservedComponents()) ; 
			state.mixs.add(mix) ; 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				mapMixs[threadId].mapMixs(mixId, mixId) ; 					 
			}
		}
		state.numOfMixs = maxMixsInLevel ; 	
	}
	public void extendNextLevelMixs (int numOfNextLevelMixs) { 
		state.numOfNextLevelMixs = numOfNextLevelMixs ; 
		stickBreakingWeights.extendMixs(state.numOfNextLevelMixs) ; 
	}
	public MapMixtureComponents  getMapMixs (int threadId) { 
		return this.mapMixs [threadId] ; 
	}
	public int getNumOfContentObjects () { 
		return state.numOfContentObjects ; 
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
	public int getObservedComponentCount (int mixId , int componentId) { 
		return state.mixs.get(mixId).getObservedComponentCount(componentId) ; 
	}	
	public Counters getFeatureCounters (int mixId , int componentId) { 
		return state.mixs.get(mixId).getFeatureCounters(componentId) ;  
	}	
	public ArrayList <ObservedComponent> getObservedComponents () { 
		return this.data.getObservedComponents () ; 
	}
	public int getNumOfMixs() {
		return state.mixs.size () ; 
	}	

	public void validateCounters(HDPRoot [] hdps ) {
		int totObjects = 0 ; 
		int totNextLevelObjects = 0 ; 
		int [] nextLevelMixObjects = null ; 
		if (state.level < state.levels-1) { 
			nextLevelMixObjects = new int [hdps [state.level+1].getNumOfMixs()] ; 
			for (int m1 = 0 ; m1 < hdps [state.level+1].getNumOfMixs(); m1 ++) { 
				nextLevelMixObjects [m1] = 0 ; 
			}
		}
		
		for (int mixId = 0 ; mixId < state.mixs.size () ; mixId++) {
			MixtureComponent mix = state.mixs.get(mixId) ; 
			totObjects += mix.getNumOfContentObjects() ; 
			totNextLevelObjects += mix.getNumOfNextLevelContentObjects() ; 
			
			if (state.level < state.levels-1) { 
				for (int m1 = 0 ; m1 < hdps [state.level+1].getNumOfMixs(); m1 ++) { 
					nextLevelMixObjects [m1] += mix.getNumOfNextLevelContentObjects(m1) ; 					
				}				
			}
		}
		if (totObjects != state.numOfContentObjects) { 
			EL.WE(8877 , " Bad Object count " + state.level ) ; 
		}
		
		if (state.level < state.levels-1) { 
			if (totNextLevelObjects != hdps [state.level+1].getNumOfContentObjects() ) { 
				EL.WE(8877 , " Bad Tot Next Level Object count " + state.level + " - " + totNextLevelObjects + " - " + 
						hdps [state.level+1].getNumOfContentObjects()) ; 
			}
			int sum1 = 0 , sum2 = 0; 
			for (int m1 = 0 ;m1 < hdps [state.level+1].getNumOfMixs(); m1 ++) { 
				sum1 += nextLevelMixObjects [m1] ; 
				sum2 += hdps [state.level+1].getNumOfContentObjects(m1) ; 
				if ( nextLevelMixObjects [m1] != hdps [state.level+1].getNumOfContentObjects(m1) ) { 
					EL.WE(8877 , " Bad Next Level Customer count " + m1 + " - " + state.level + " - " +  nextLevelMixObjects [m1] + " - " 
						+ 	hdps [state.level+1].getNumOfContentObjects(m1))  ; 
				}
			}
			EL.WE (8877 ,"sum 1 " + sum1 + " sum 2 " +  sum2  ) ;  
		}
	}
	public void printCounters () { 
		 
		for (MixtureComponent mix :state.mixs ) { 
			mix.printCounters() ; 
		}		
	}
	
	public MixtureComponent getMix (int mixId) { 
		return state.mixs.get(mixId) ; 
	}
	
	public void saveCounters() {
		for (MixtureComponent mix : state.mixs) { 
			mix.saveCounters() ; 
		}		
	}

	public void sumDelta( HDPThread hdpThread, MapMixtureComponents mapMixs , MapMixtureComponents nextLevelMapMixs) {		
		for (MixtureComponent mix : state.mixs) { 
			int threadMixId = mapMixs.getThreadMix( mix.getId())  ; 			 
			mix.sumDelta (hdpThread.getMix(threadMixId) , mapMixs , nextLevelMapMixs ) ; 
		}		
	}
	
	public void setNumOfObjects(int totObjects) {
		state.numOfContentObjects = totObjects ; 		
	}
	public void sampleParms (int iter) {
		int activeMixs = 0 ; 
		for (int t = 0 ; t < state.mixs.size () ; t ++ ) { 
			if (state.mixs.get(t).getNumOfContentObjects() > 0) activeMixs ++ ; 
		}
		EL.WE(98765 , "Sample Parms - Level " + state.level + " Mixs " +state.mixs.size () + " Active " + activeMixs +
				" content objects: " +  state.numOfContentObjects) ; 
		
		/*******  Sample Gamma Top **********/
		if (state.level == 0) { 
			double gammaTopNew = HDPHyperParms.sampleGamma(state.gammaTop, activeMixs, state.numOfContentObjects, 
					parms.aGammaTop , parms.bGammaTop ) ;			
			EL.WE( 98765 , " Sample gamma Top. Old " + state.gammaTop+ " New: " + gammaTopNew ) ; 
			state.gammaTop = gammaTopNew ; 
		}
		/*******  stick breaking weights  **********/
		if (state.level < state.levels-1) { 
			
			int [][] mixNextLevelMixObjectCount = new int  [state.numOfMixs][state.numOfNextLevelMixs]  ; 
			int [] mixObjectCount = new int [state.numOfMixs] ;  
			int [] nextLevelMixObjectCount = new int [state.numOfNextLevelMixs] ; 
			for (int i = 0 ; i < state.numOfNextLevelMixs ; i ++) nextLevelMixObjectCount [i] = 0 ; 
			for (int  mixId = 0 ; mixId< state.numOfMixs ; mixId ++ ) { 
				mixObjectCount[mixId] = 0 ; 
				for (int nextLevelMixId = 0 ; nextLevelMixId < state.numOfNextLevelMixs ; nextLevelMixId ++ ) { 
					mixNextLevelMixObjectCount [mixId][nextLevelMixId] = getNumOfNextLevelContentObjects(mixId, nextLevelMixId) ; 
					mixObjectCount[mixId]  += mixNextLevelMixObjectCount [mixId][nextLevelMixId] ; 
					nextLevelMixObjectCount [nextLevelMixId] += mixNextLevelMixObjectCount [mixId][nextLevelMixId] ; 
				}
			}
			int activeNextLevelMixs = 0 ; 
			for (int nextLevelMixId = 0 ; nextLevelMixId < state.numOfNextLevelMixs ; nextLevelMixId ++ ) { 
				if (nextLevelMixObjectCount [nextLevelMixId] > 0) activeNextLevelMixs ++ ; 				
			}
			stickBreakingWeights.updateWeights(state.numOfMixs , state.numOfNextLevelMixs, mixNextLevelMixObjectCount,
					state.alpha0, state.gamma) ; 
			int nextLevelObjects = stickBreakingWeights.getTotMixTables() ; 	
			//String s = "Stick Breaking Weights " ; 
			//for (int i = 0 ; i < state.numOfNextLevelMixs ;  i ++) s += i + ":"+stickBreakingWeights.getWeight(i) + " " ; 
			//s += " new:" + stickBreakingWeights.getWeightNew() ; 
			//EL.WE(7788 , s) ; 
					
			double gammaNew = HDPHyperParms.sampleGamma(state.gamma,  activeNextLevelMixs , nextLevelObjects, 
					parms.aGamma[state.level] , parms.bGamma[state.level] ) ; 
			EL.WE( 98765 , " Sample gamma.   Old Gamma: " + state.gamma +
					" New: " + gammaNew + "  active next level mixes: "  +activeNextLevelMixs + " NextLevelObjects:" + nextLevelObjects) ; 
			state.gamma = gammaNew ; 
			String s = " Mix Object count" ; 
			for (int c : mixObjectCount ) s +=  c + " " ; 
			double alpha0New = HDPHyperParms.sampleAlpha0 (state.alpha0,  stickBreakingWeights.getTotMixTables() , 
					state.numOfMixs , mixObjectCount  ,parms.aAlpha0[state.level] , parms.bAlpha0[state.level]) ; 
			EL.WE( 98765 , " Sample Alpha0  - Old: " + state.alpha0  + " new: "   + alpha0New  + s) ; 
			state.alpha0  = alpha0New ; 
		}
		if ((iter > 0) & (iter % parms.updateBeta == 0)) estimateBeta (); 
	}
	private void estimateBeta() {
		for (int componentId = 0 ; componentId< data.getObservedComponents().size() ; componentId++  ) { 
			ObservedComponent  observedComponent = data.getObservedComponents().get(componentId) ; 
			
			int  [][] mixValue = new  int [state.mixs.size()][observedComponent.getNumOfFeatures()] ; 
			for (int mixId = 0 ;  mixId < state.mixs.size() ; mixId ++ ) { 
				MixtureComponent mixComp = state.mixs.get(mixId) ; 
				Counters featureCounters = mixComp.getFeatureCounters(componentId) ;  
				for (int valueIndx = 0 ; valueIndx < observedComponent.getNumOfFeatures() ;  valueIndx ++ ) { 
					mixValue [mixId][valueIndx] = featureCounters.get(valueIndx) ; 
				}
			}		 
			double newBeta = LDAHyperParms.sampleBeta(mixValue, observedComponent.getBeta()) ; 
			EL.WE(4444, "Sample Beta - Component " + observedComponent.getComponentId() + " Old " +  observedComponent.getBeta() + " new " + newBeta) ; 
			observedComponent.updateBeta (newBeta) ; 
		}
	}
	
	public double getGammaTop() {
		return state.gammaTop ; 
	}

	public double getGamma() {
		return state.gamma ; 
	}
	public double getAlpha0() {
		return state.alpha0 ; 
	}		

	public StickBreakingWeights getStickBreakingWeights() {
		return stickBreakingWeights;
	}

	public void resetStickBreakingWeights() {
		stickBreakingWeights.resetWeights () ; 		
	}

	public void sumStickBreakingWeights(HDPThread hdpThread, MapMixtureComponents mapMixtureComponents, int numOfThreads) {
		stickBreakingWeights.sumWeights (hdpThread.getStickBreakingWeights() , mapMixtureComponents , numOfThreads ) ; 
	}
}