package artzi.gtm.dmlhdp.mlhdp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import artzi.gtm.dmlhdp.dhdp.HDPRoot;
import artzi.gtm.topicModelInfra.counters.FeatureCount;
import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.topicModelInfra.dataObjects.GInstance;
import artzi.gtm.topicModelInfra.dataObjects.InstanceTemplate;
import artzi.gtm.topicModelInfra.dataObjects.ObservedComponent;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.gen.GetHeapSpace;
import artzi.gtm.utils.io.SaveObject;

public class MLHDPModel {

	HDPRoot [] hdps ; 
	MLHDPData data ; 
	MLHDPResults results ; 
	MLHDPRun run ; 
	
	MLHDPThread [] threads ; 
	BlockingQueue<Integer> queue ; 
	MLHDPParms parms = null ; 	
	public MLHDPModel (MLHDPData data , String parmsPath ) throws Exception { 
		parms = MLHDPParms.getInstance () ;   
				
		this.data = data ; 
		this.data.initObjectComponentCount() ; 
		run = new MLHDPRun (data.levels) ; 
		threads = new  MLHDPThread [parms.numOfThreads] ; 
		queue = new ArrayBlockingQueue<Integer> (parms.numOfThreads) ; 
		hdps = new HDPRoot [data.levels] ; 
		for (int level = 0 ; level  < data.levels ; level ++ ) { 
			hdps [level] = new HDPRoot(level , data.levels , parms.gammaTop , parms.gamma [level], 
					parms.alpha0[level],data.getLevelData(level)) ; 
		}
		for (int instanceIndx = 0 ; instanceIndx < data.getLevelData (0).getInstanceList().size () ; instanceIndx ++) { 
			int threadId = instanceIndx % parms.numOfThreads ; 
			data.getLevelData (0).getInstanceList().get(instanceIndx).setThread(threadId) ;  
		}
		for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++) { 
			threads [threadId] = new MLHDPThread (threadId , data , queue   ) ; 
		}
		for  (int level = 0 ; level  < data.levels ; level ++ ) { 
			int totObjects = 0 ; 
			for  (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++) { 
				totObjects += threads [threadId].getHDP(level).getNumOfContentObjects() ; 
			}
			hdps[level].setNumOfObjects (totObjects) ; 
		}
		
		aggregateThreads (0) ; 
		validateCounters () ; 
		printCounters () ; 
		results = new MLHDPResults () ;  
	}		

	public void infer () throws Exception { 	
		EL.WE(7777,  " Start Infer ;  Iters " + parms.maxIters) ;  
		for  (int threadId = 0 ; threadId < parms.numOfThreads ;  threadId ++) { 
			threads[threadId].start() ; 
		}		
		for (int iter = 0 ; iter <  parms.maxIters;  iter++ ) { 
			System.out.println ("iter - "+ iter) ; 
			EL.WE (99, " Start Iter-" + iter  + " HS " + GetHeapSpace.HS()/1000000 ) ; 			
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				threads [threadId].copyCounters (hdps ) ;  				
				threads [threadId].getQueue().put (iter) ;   
			}
			
			for (int level = 0 ; level < data.levels ; level ++ ) { 
				hdps[level].saveCounters () ; 
			}
			int numOfThreadMsg = 0 ; 
			while   (numOfThreadMsg < parms.numOfThreads ) { 
				int threadID = queue.take() ; 	
				EL.WE (99, "Take " + " ID - " + threadID + "Iter-" + iter  ) ; 
				numOfThreadMsg ++ ; 
			}
			aggregateThreads (iter) ; 

			EL.WE (99, " After Iter-" + iter  ) ; 
			
			if ((iter< parms.checkPoint) | (iter % parms.checkPoint == 0))  {
				validateCounters () ; 	
				printCounters () ;

				if (iter % parms.likelihood == 0) { 
					computeMultinomials  () ; 
					double likelihood = computeLogLikelihood () ; 
					EL.WE (990, " Iter-" + iter + " likelihood " +  likelihood  ) ; 
					System.out.println ("Likelihood "+ likelihood + 
							" Mixes 0 " + hdps[0].getNumOfMixs() + " Mixes 1 " + hdps[1].getNumOfMixs()) ; 
					run.addLikelihood(likelihood) ; 
				}								
			}
			printCounters () ; 
		}
		for (int threadIndx = 0 ; threadIndx < parms.numOfThreads ; threadIndx ++ ) {
			threads[threadIndx].getQueue().put (-1) ; 
		}
		setResults () ; 	
		//printMult () ; 
	}	

	private void aggregateThreads(int iter) {

		for (int level = 0 ; level < data.levels ; level++) { 
			int maxMixsInLevel = hdps[level].getNumOfMixs() ; 		 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				if (threads[threadId].getHDP (level).getNumOfMixs() > maxMixsInLevel) { 
					maxMixsInLevel = threads[threadId].getHDP (level).getNumOfMixs() ; 
				}
			}
			hdps[level].extendMixs (maxMixsInLevel) ; 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				threads[threadId].getHDP(level).extendMixs (maxMixsInLevel) ; 
			}
			hdps[level].resetStickBreakingWeights () ; 
			if (level > 0)   { 
				hdps[level-1].extendNextLevelMixs (maxMixsInLevel) ; 
				for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
					threads[threadId].getHDP(level-1).extendNextLevelMixs (maxMixsInLevel) ; 
				}			
			}
		}
		for (int level = 0 ; level < data.levels ; level++) { 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				if (level < data.levels-1) {
					hdps[level].sumDelta
					( threads[threadId].getHDP(level) , hdps[level].getMapMixs(threadId), hdps[level+1].getMapMixs(threadId)) ; 
					hdps[level].sumStickBreakingWeights
					(threads[threadId].getHDP(level) ,hdps[level+1].getMapMixs(threadId), parms.numOfThreads  ) ; 
				}
				else { 
					hdps[level].sumDelta 
					( threads[threadId].getHDP(level) , hdps[level].getMapMixs(threadId), null) ; 
				}
				
			}
		}
		for (int level = 0 ; level < data.levels ; level ++ ) { 
			hdps[level].sampleParms (iter) ; 			 
		}
		/*
		for (int level = 1 ; level < data.levels ; level++) {
			hdps[level].copmuteStickBreakingWeights(hdps[level-1]) ;  
		}
		for (int level = 0 ; level < data.levels-1 ; level++) {
			hdps[level].sampleGammaAlpha0 (hdps[level+1].getStickBreakingWeights()) ;  
		}
		hdps[data.levels-1].sampleGammaAlpha0(null) ; 
		*/
	}

	private void computeMultinomials () { 

		results.multL0 = new double [hdps[0].getNumOfMixs()] ; 
		for (int m = 0 ; m < hdps[0].getNumOfMixs() ; m ++ ) { 
			results.multL0 [m] = (1.0*hdps[0].getNumOfContentObjects(m) + (hdps[0].getGammaTop()/hdps[0].getNumOfMixs()) ) /
					  			 (1.0*hdps[0].getNumOfContentObjects() + hdps[0].getGammaTop()) ; 
		}
		results.multLevels = new ArrayList <double [][]> () ; 
		for (int level = 0 ; level < data.levels-1 ; level ++ ) { 
			double [][] mult = new double [hdps[level].getNumOfMixs()][hdps[level+1].getNumOfMixs()] ; 
			for (int m = 0 ; m < hdps[level].getNumOfMixs() ; m ++ ) { 
				//if (hdps[level].getNumOfNextLevelContentObjects(m) > 0 ) { 
					for (int m1 = 0 ; m1 < hdps[level+1].getNumOfMixs() ; m1 ++ ) { 
						mult [m][m1] = (1.0*hdps[level].getNumOfNextLevelContentObjects (m,m1) +
									   (hdps[level].getGamma()/hdps[level+1].getNumOfMixs()))
									/  (1.0*hdps[level].getNumOfNextLevelContentObjects(m) + hdps[level].getGamma()) ; 
					}
				}
			/*
				else {
					for (int m1 = 0 ; m1 < hdps[level+1].getNumOfMixs() ; m1 ++ ) { 
						mult [m][m1] = 0 ; 
					}
				}
			}
			*/
			results.multLevels.add(mult) ; 
		}
		results.multFeatures = new ArrayList<ArrayList <double [][]>> () ; 
		for  (int level = 0 ; level < data.levels ; level ++ ) { 
			results.multFeatures.add( new ArrayList <double [][]>()) ;  
			for (int componentId = 0 ; componentId < data.getLevelData (level).getObservedComponents().size() ; componentId ++) { 
				ObservedComponent component = data.getLevelData (level).getObservedComponents().get(componentId) ; 		 
				double [][] mult = new double [hdps[level].getNumOfMixs()][component.getNumOfFeatures()] ; 
				for (int m = 0 ; m < hdps[level].getNumOfMixs() ; m ++ ) { 
					for (int f = 0 ; f <component.getNumOfFeatures() ; f ++  ) { 
						mult [m][f] = ((1.0*hdps[level].getFeatureCounters (m,componentId).get(f)) +  component.getBeta())
								/((1.0*hdps[level].getObservedComponentCount(m, componentId)) + component.getNumOfFeaturesBeta()) ;  
					}
				}
				results.multFeatures.get (level).add(mult) ; 
			}
		}
	}
	
	private double  computeLogLikelihood() {
		computeMultinomials () ; 
		results.printMult() ; 
		double logLikelihood = 0 ;  
		for (int instanceIndx = 0 ; instanceIndx < data.getLevelData(0).getInstanceList().size () ; instanceIndx ++ ) { 				
			logLikelihood += instanceLogLikelihood (0 , -1 ,  instanceIndx   )  ; 
		}		
		return logLikelihood ; 
	}
	
	private double instanceLogLikelihood(int level, int ownerTemplate ,  int instanceIndx ) {
		double instanceLogLikelihood ; 
		GInstance instance = data.getLevelData(level).getInstanceList().get(instanceIndx) ; 
		int instanceTemplate = getInstanceTemplate (level , instance) ; 
		if (level  == 0 ) { 
			instanceLogLikelihood = Math.log(results.multL0[instanceTemplate]) ;  
		}
		else { 
			instanceLogLikelihood = Math.log(results.multLevels.get(level-1)[ownerTemplate][instanceTemplate]) ; 
		}
		for (ComponentFeatures  componentFeatures : instance.getFeatureLists()  ) { 
			double [][] multFeatures = results.multFeatures.get(level).get (componentFeatures.getComponentId()) ; 
			for (FeatureCount  featureCount : componentFeatures.getFeaturesList()) { 
				instanceLogLikelihood += featureCount.getCount () * Math.log(multFeatures[instanceTemplate][featureCount.getFeature()]) ; 
			}
		}
		if (level < data.levels-1 ) { 
			for (int instanceIndx1 = instance.getFromMember() ; instanceIndx1 <= instance.getToMember() ; instanceIndx1++) { 
				instanceLogLikelihood += instanceLogLikelihood (level+1 , instanceTemplate , instanceIndx1 ) ; 
			}
		}
		return instanceLogLikelihood ; 
	}			 

	private int getInstanceTemplate(int level , GInstance instance) {
		int threadId = instance.getThread() ; 
		int threadMix = threads [threadId].getHDP(level).getContentObject(instance.getContentObjectIndx()).getMix() ; 
		int rootMix = hdps[level].getMapMixs(threadId).getRootMix( threadMix) ; 
		return rootMix ; 		 
	}

	private double computeInstanceLogProbability(GInstance instance , int instanceTemplate , int level) {
		double prob = 0 ; 
		 
		for (int componentIndx = 0 ; componentIndx < data.getLevelData(level).getObservedComponents().size () ; componentIndx ++) { 
			double [][] tempalteFeatureMult = results.multFeatures.get(level).get (componentIndx) ; 
			ComponentFeatures componentFeatures = instance.getFeatureLists().get(componentIndx) ; 
			ArrayList <FeatureCount> featureList = componentFeatures.getFeaturesList() ; 
			for (FeatureCount featureCount : featureList) { 
				prob += Math.log(tempalteFeatureMult [instanceTemplate][featureCount.getFeature()])/componentFeatures.getNumOfFeatures () ;    					
			}
		}		 	
		return prob;
	}
	private void setResults () {
		results.numOfTemplates = new int [data.levels]; 
		for (int level = 0 ; level < data.levels ; level ++ )  { 
			results.numOfTemplates [level] = hdps [level].getNumOfMixs() ; 
		}
		computeMultinomials () ; 
		results.instanceTemplates = new ArrayList <InstanceTemplate[]> () ; 
		for (int level = 0 ; level < data.levels ; level ++) { 
			results.instanceTemplates.add (new InstanceTemplate [ data.getLevelData(level).getInstanceList().size()]) ; 				
		}
		for (int instanceIndx = 0 ; instanceIndx < data.getLevelData(0).getInstanceList().size () ; instanceIndx ++ ) { 
			setInstanceTemplates (0 , instanceIndx ) ; 
		}		
	}
	
	private void setInstanceTemplates(int level, int instanceIndx ) {
		GInstance instance =  data.getLevelData(level).getInstanceList().get(instanceIndx) ; 
		int instanceTemplate  = getInstanceTemplate (level , instance) ; 
		
		double prob = 0 ; 
		if (level ==  data.levels-1) { 
			prob = computeInstanceLogProbability (instance ,instanceTemplate  , level) ; 
		}
		results.instanceTemplates.get(level) [instanceIndx] = new InstanceTemplate (level , instanceIndx , prob , instanceTemplate) ;  
		if (level < data.levels-1) {  
			for (int indx1 = instance.getFromMember() ; indx1 <= instance.getToMember() ;  indx1 ++) { 
				setInstanceTemplates (level+1 , indx1 )  ;  
			}
		}
	}	
	public void save (String dir) throws IOException { 
		String fileName  = new File (dir   , "data" ).getPath()  ; 
		SaveObject.write(fileName , data ) ;  
		fileName  = new File (dir   , "result" ).getPath()  ; 
		SaveObject.write(fileName , results ) ; 
	}
	public void saveRun (String path) throws IOException { 
		run.setNumOfMixes(results.getNumOfTemplates()) ; 
		run.setLikelihood (computeLogLikelihood()) ; 
		run.save(path) ; 
	}
	
	public void load (String dir) { 
		String fileName  = new File (dir   , "data" ).getPath()  ; 
		data = (MLHDPData) SaveObject.read(fileName  ) ;  
		fileName  = new File (dir   , "result" ).getPath()  ; 
		results = (MLHDPResults) SaveObject.read(fileName ) ;  
	}

	public int[] getNumOfTemplates() {
		return results.numOfTemplates ; 		 
	}

	public ArrayList<InstanceTemplate[]> getInstanceTempaltes() {
		return results.instanceTemplates ; 
	}

	public ArrayList <double [][]> getMultLevels () { 
		return results.multLevels ; 
	}

	public ArrayList <ArrayList<double[][]>> getMultFeatures() {
		return results.multFeatures ; 	
	}

	private void validateCounters () throws Exception { 
		for (int level = 0 ; level < data.levels ; level++ ) { 
			hdps[level].validateCounters (hdps) ; 
		}
	}
	private void printCounters() {
		
		 /* 
		for (int level = 0 ; level < data.levels ; level++ ) { 
			hdps[level].printCounters() ;  
		}
		*/
		 
		
	}
	/*
	private void printMult () { 
		for (int componentId = 0 ; componentId < data.getLevelData (1).getObservedComponents().size() ; componentId ++) { 
			printMat (" x " , " Y " , results.multFeatures.get (1).get(componentId)) ;  ;  
		}
	}
	 
	private void printMat (String h1 , String  h2 , double [][] mat) { 
		int m1 = mat.length   ; 
		int m2 = mat[0].length ; 
		System.out.println ("LLL " + m2) ; 
		EL.W("******************* " + h1) ; 
		for (int i = 0 ; i < m1 ; i ++ ) { 
			String s = h2 + " " +  i + " - "; 
			for (int j = 0 ; j < m2 ; j ++ ) { 
				s += " " + j + ": " + mat [i][j]  ; 
				
			}
			EL.W(s) ; 
		}
	}
	private void printP(double[] p) {
		String s = " prop " ;  
		for (int i = 0 ; i < p.length ; i ++ ) { 
			s += " " + i + "-"+ p[i] ; 
		}
		EL.W(s) ; 		
	}
	*/
}
