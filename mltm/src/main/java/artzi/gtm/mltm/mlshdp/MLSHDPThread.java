package artzi.gtm.mltm.mlshdp;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import artzi.gtm.mltm.dshdp.SHDPRoot;
import artzi.gtm.mltm.dshdp.SHDPThread;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.utils.elog.EL;

public class MLSHDPThread extends Thread{
	 
	MLSHDPParms parms = null ; 	
	/******  documents ********/
	ArrayList <DocWords> docList ; 
	int numOfRootDocs ; 	
	int numOfThreadDocs ; 
	int [] rootDocIndx  ; 
	int numOfThreadWords ; 
	/****   mixes , virtual terms *****/ 
	int [] numOfMixs ; 
	int [] numOfVTerms ; 
	/**** threads , HDPs *****/
	SHDPThread [] hdps ; 
	BlockingQueue<Integer> rootQueue ; 
	BlockingQueue<Integer> threadQueue ;
	int threadId ; 	
	
	public MLSHDPThread(int threadId ,  ArrayList <DocWords> docList , int[] docThread, int [] docThreadId , int numOfThreadDocs,
			int [] numOfMixs , int[] numOfVTerms, BlockingQueue<Integer> rootQueue) throws Exception { 
		parms = MLSHDPParms.getInstance() ; 
		this.threadId = threadId ; 
		this.rootQueue = rootQueue ; 
		threadQueue = new ArrayBlockingQueue<Integer> (1) ; 
		/*****  documents *****/
		this.numOfThreadDocs = numOfThreadDocs ; 
		rootDocIndx = new int [numOfThreadDocs] ; 
		this.docList = docList ; 
		numOfRootDocs = docList.size () ; 
		/******  documents ********/
		int threadDocIndx = -1 ; 
		numOfThreadWords = 0 ; 
		for (int docRIndx= 0 ; docRIndx< numOfRootDocs ; docRIndx++ ) { 
			if (docThread [docRIndx] == threadId) { 
				threadDocIndx ++ ; 
				rootDocIndx [threadDocIndx] = docRIndx; 
				if (docThreadId [docRIndx] != threadDocIndx) { 
					EL.WF(5544 , " bad doc Indx - thread: " + threadId) ; 
				}
				numOfThreadWords += docList.get(docRIndx).getNumOfWords()  ; 
			}
		}
		EL.W(  " Init thread " + threadId + " docs " + numOfThreadDocs + " words " + numOfThreadWords  ) ;   
		/****** mixes , virtual terms ******/
		this.numOfMixs = numOfMixs ; 
		this.numOfVTerms = numOfVTerms ; 
		/********  HDPs *******/
		hdps = new SHDPThread [parms.levels] ; 
		int numOfThreadMixs ; 
		for (int level = 0 ; level  < parms.levels ; level ++ ) { 
			if (level == 0) 
				numOfThreadMixs = numOfThreadDocs ; 
			else 
				numOfThreadMixs = numOfMixs[level] ; 
			hdps [level] = new SHDPThread (threadId , level, numOfThreadWords ,  docList, rootDocIndx, numOfThreadDocs, numOfThreadMixs , numOfVTerms[level] ) ; 
			if (level > 0) { 
				hdps[level].initMixs(hdps[level-1]);
			}
		}
		validateCounters () ; 		
	}	
	
	public void copyCounters (SHDPRoot [] rootHdps ) throws Exception {   
		for (int level = 1 ; level < parms.levels-1; level ++) {   
			hdps[level].copyCounters(rootHdps[level] , rootHdps [level+1]); 
		}
		hdps[parms.levels-1].copyCounters(rootHdps[parms.levels-1] , null); 
		validateCounters () ; 
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
		//System.out.println ("Start Iters " + threadId) ;  
		iter = threadQueue.take () ;
		while (iter > -1) { 
			EL.W( "Start Iter - " + iter + " Thread- " + threadId ) ; 
			for (int level = 1 ; level < parms.levels; level++) { 				
				hdps[level].inferMixs (hdps) ; 
			}
			validateCounters () ; 
			EL.W(  "After Thread Iter - " + iter  + " Thread- " + threadId ) ; 
			rootQueue.put(threadId ) ;
			iter = threadQueue.take () ;
		}
	}
	
	
	private void validateCounters() throws Exception {
		for (int level = 0 ; level < parms.levels ; level++) { 
			hdps[level].validateCounters () ; 
		}	
	}

	public BlockingQueue<Integer> getQueue() {
		return this.threadQueue ; 
	}
	
	public SHDPThread getHDP(int level) {
		return hdps [level] ; 
	}
}
