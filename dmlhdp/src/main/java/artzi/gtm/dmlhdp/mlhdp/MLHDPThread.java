package artzi.gtm.dmlhdp.mlhdp;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import artzi.gtm.dmlhdp.dhdp.HDPRoot;
import artzi.gtm.dmlhdp.dhdp.HDPThread;
import artzi.gtm.topicModelInfra.dataObjects.ContentObject;
import artzi.gtm.topicModelInfra.dataObjects.GInstance;
import artzi.gtm.topicModelInfra.logProportions.LogProportion;
import artzi.gtm.topicModelInfra.logProportions.LogProportions;
import artzi.gtm.utils.elog.EL;

public class MLHDPThread extends Thread{
	BlockingQueue<Integer> rootQueue ; 
	BlockingQueue<Integer> threadQueue ; 
	HDPThread [] hdps ; 
	MLHDPData data ; 
	int threadId ; 
	MLHDPParms parms = null ; 	
	public MLHDPThread(int threadId, MLHDPData data ,  BlockingQueue<Integer> rootQueue) throws Exception { 
		parms = MLHDPParms.getInstance() ; 
		this.threadId = threadId ; 
		this.data = data ; 
		this.rootQueue = rootQueue ; 
		threadQueue = new ArrayBlockingQueue<Integer> (1) ; 
		hdps = new HDPThread [data.levels] ; 
		for (int level = 0 ; level  < data.levels ; level ++ ) { 
			hdps [level] = new HDPThread (threadId  , level , data.levels , parms.gammaTop , parms.gamma [level],
					parms.alpha0[level] ,
					data.getLevelData(level).getObservedComponents()) ; 
		}
		for (int instanceIndx = 0 ; instanceIndx < data.getLevelData (0).getInstanceList().size () ; instanceIndx ++) { 
			GInstance instance = data.getLevelData(0).getInstanceList().get(instanceIndx) ; 
			if (instance.getThread () == threadId ) {  
				initContentObjects  (0, instance , -1) ; 
			}
		}
		for (int level = data.levels-1 ; level >= 0  ; level --) { 
			for (int contentObjectIndx = 0 ; contentObjectIndx < hdps [level].getNumOfContentObjects() ; contentObjectIndx ++ ) { 
				inferMixture(level , contentObjectIndx) ; 
			}
		}
		EL.WE( 889977 , "thread " + threadId +    " num of thread ContentObjects " + hdps[0].getNumOfContentObjects()  ) ; 
	}
	private void initContentObjects (int level , GInstance instance , int ownerIndx){
		int contentObjectIndx = hdps[level].addContentObject (instance , ownerIndx ) ; 
		instance.setContentObjectIndx (contentObjectIndx) ; 
		if (ownerIndx != -1) { 
			hdps[level-1].getContentObject (ownerIndx).setMember (contentObjectIndx) ;  			
		}
		if (level < data.levels-1 ) { 
			for (int indx1 = instance.getFromMember() ; indx1 <= instance.getToMember() ; indx1 ++ ) { 	
				GInstance instance1 = data.getLevelData(level+1).getInstanceList().get(indx1) ; 
				instance1.setThread (threadId) ; 
				initContentObjects (level+1 , instance1 , contentObjectIndx ) ; 				
			}
		}
	}
	public BlockingQueue<Integer> getQueue() {
		return this.threadQueue ; 
	}
	public void copyCounters(HDPRoot[] rootHdps ) throws Exception {
		for (int level  = 0 ; level < data.levels; level ++) { 
			if (level < data.levels-1) {
				hdps [level].copyCounters(rootHdps [level] , rootHdps [level+1] ) ;			 
			}
			else { 
				hdps [level].copyCounters(rootHdps [level] , null ) ; 
			}
		}
		 
		
	}
	public void run (){ 
		try {
			iterations () ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}	

	private void iterations ()   throws Exception{ 
		int iter ;  
		System.out.println ("Start Iters " + threadId) ;  
		iter = threadQueue.take () ;
		int level ; 
		while (iter > -1) { 
			EL.WE( 4321 ,  "Start Iter - " + iter + " Thread- " + threadId ) ; 
			//for (HDPThread hdp : hdps) { 
				// EL.W(" HDP Num Of ContentObjects - " + hdp.getNumOfContentObjects()) ;  
				//hdp.printCounters() ; 
				//hdp.sampleAlpha () ; 
			//}
			validateCounters () ; 
			for (level = 0 ; level < data.levels ; level++ ) { 
				for (int contentObjectIndx = 0 ; contentObjectIndx < hdps [level].getNumOfContentObjects() ; contentObjectIndx ++ ) { 					
					inferMixture(level , contentObjectIndx) ; 
				}
			}
			EL.WE( 4321 ,  "After Thread Iter - " + iter  + " Thread- " + threadId ) ; 
			printCounters () ; 
			validateCounters () ; 
			rootQueue.put(threadId ) ;
			iter = threadQueue.take () ;
		}
	}
	private void inferMixture ( int level, int contentObjectIndx ) throws Exception {	
		hdps[level].unAssignContentObject (contentObjectIndx ,hdps  ) ; 
		LogProportions logProportions = new LogProportions () ;  
		setLogProportions (level , contentObjectIndx , logProportions) ; 
		int tableIndx  = logProportions.sample () ;

		//if (level == 0 ) { 
		//	EL.W ("*** Level " + level + " ContentObject " + contentObjectIndx + " Table " + tableIndx  ) ; 
		//	logProportions.printP() ; 
		//}

		hdps[level].assignContentObject (contentObjectIndx, tableIndx , hdps   ) ; 	 
	}

	private void setLogProportions(int level , int  contentObjectIndx  , LogProportions logProportions) throws Exception {

		ContentObject contentObject = hdps [level].getContentObject(contentObjectIndx) ;
		int ownerIndx = contentObject.getOwnerIndx() ; 
		int ownerMixId= -1 ; 
		if (ownerIndx > -1) { 
			ownerMixId = hdps[level-1].getContentObject(ownerIndx).getMix() ; 
		}
		for (int mixId = 0 ; mixId <= hdps[level].getNumOfMixs() ;  mixId ++) {
			LogProportion logProp ; 
			if (ownerMixId < 0 ) { 			
				logProp  = hdps[level].getDPLogProportion (mixId , contentObjectIndx , hdps ) ; 
			}
			else { 
				logProp  = hdps[level].getHDPLogProportion (ownerMixId , mixId , contentObjectIndx , hdps ) ; 
			}
			if ((!logProp.isZero())) { 
				//EL.W (" Add log prop ContentObject - " + contentObjectIndx + " Level " + level + " Table - " +  tableIndx  + " - " + logProp3) ; 
				logProportions.add(mixId, logProp.getLogProp()  ) ; 
			}
		}
	}

	public HDPThread getHDP(int level) {
		return hdps[level] ; 
	}
	private void printCounters() {
		/*
		for (int level = 0 ; level < data.levels ; level++ ) { 
			hdps[level].printCounters() ;  
		}
		*/		
	}
	private void validateCounters () throws Exception { 
		for (int level = 0 ; level < data.levels ; level++ ) { 
			hdps[level].validateCounters (hdps) ; 
		}
	}	
}