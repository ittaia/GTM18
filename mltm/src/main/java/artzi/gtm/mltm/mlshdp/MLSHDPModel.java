package artzi.gtm.mltm.mlshdp;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import artzi.gtm.mltm.dshdp.SHDPRoot;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.gen.GetHeapSpace;

public class MLSHDPModel {
	
	MLSHDPParms parms ; 
	/******  documents ********/
	ArrayList <DocWords> docList ;
	int numOfDocs , numOfTerms , numOfWords ; 
	int [] numOfDocWords ; 
	int [] docThread ; 	
	int [] docThreadId ; 
	int [] numOfThreadDocs  ; 
	/****   mixes , virtual terms *****/ 
	int [] numOfMixs , numOfVTerms ;  
	/*****  Threads , HDPs *******/ 
	BlockingQueue<Integer> queue ; 
	MLSHDPThread [] threads ; 
	SHDPRoot [] hdps  ; 	
	/*****  Multinomials *******/
	double [][] docMixLevel1Multinomials ; 
	ArrayList <double [][]> mixMultinomials ; 
	 
				
	public MLSHDPModel (ArrayList <DocWords> docList , int numOfTerms) throws Exception { 
		parms = MLSHDPParms.getInstance() ; 
		/******  documents ********/
		this.docList = docList ; 
		this.numOfDocs = docList.size () ; 
		this.numOfDocWords = new int [numOfDocs] ; 
		this.numOfWords = 0 ; 
		for (int docIndx = 0 ; docIndx < numOfDocs ; docIndx ++ ) { 
			numOfDocWords [docIndx] = docList.get(docIndx).getNumOfWords() ; 
			numOfWords += numOfDocWords [docIndx] ;  
		}
		this.numOfTerms = numOfTerms ; 
		EL.W(" MLHDP Docs: "+ this.numOfDocs + "Terms:" + this.numOfTerms + " words:"+this.numOfWords );
		/****   mixes , virtual terms *****/ 
		numOfMixs = new int [parms.levels] ; 
		numOfVTerms = new int [parms.levels] ; 		
		for (int level = 0 ; level < parms.levels ; level ++) { 
			if (level ==0) 
				numOfMixs [level] = numOfDocs ; 			 
			else 	
				numOfMixs [level] = parms.initialMixs[level] ; 
		}
		for (int level = 0 ; level < parms.levels ; level ++) { 
			if (level < parms.modelLevels) 
				numOfVTerms [level] = numOfMixs[level+1];
			else
				numOfVTerms [level] = numOfTerms ;  
		}	
		/****** Threads ********/
		threads = new  MLSHDPThread [parms.numOfThreads] ; 
		queue = new ArrayBlockingQueue<Integer> (parms.numOfThreads) ; 
		numOfThreadDocs = new int [parms.numOfThreads] ; 
		for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++) { 
			numOfThreadDocs [threadId] = 0 ; 
		}
		docThread = new int [numOfDocs] ; 
		docThreadId = new int [numOfDocs] ; 
		for (int docIndx = 0 ; docIndx < numOfDocs ;   docIndx ++) { 
			int threadId = docIndx % parms.numOfThreads ; 
			numOfThreadDocs [threadId] ++ ; 
			docThread [docIndx] = threadId ;  
			docThreadId [docIndx] = numOfThreadDocs [threadId]-1 ; 
		}
		/*******  HDPs ******/ 
		EL.W(  " Init HDPs. Docs" + numOfDocs + " Words:" + numOfWords + " Terms " + numOfTerms ) ;    
		hdps = new SHDPRoot [parms.levels] ; 
		for (int level = 0 ; level  < parms.levels ; level ++ ) { 
			hdps [level] = new SHDPRoot(level ,  numOfWords ,  docList , numOfDocWords ,  numOfMixs [level] , numOfVTerms[level]) ; 
		}
		EL.W(  " Init Threads "  ) ;    
		for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++) { 
			threads [threadId] = new MLSHDPThread (threadId, docList, docThread , docThreadId, numOfThreadDocs [threadId], numOfMixs, numOfVTerms ,queue) ; 
		}
		
		aggregateThreads (-1) ; 
		validateCounters () ; 
	}
	
	public void infer (int iters ) throws Exception { 
		EL.W(  " Start Infer ;  Iters " + parms.iters ) ;  
		for  (int threadId = 0 ; threadId < parms.numOfThreads ;  threadId ++) { 
			threads[threadId].start() ; 
		}		
		for (int iter = 0 ; iter <  parms.iters;  iter++ ) { 
			String mixs = " Mixes: " ; 
			for (int level = 1 ; level < parms.levels ; level ++ ) { 
				mixs += " " + level + "-" + hdps[level].getNumOfMixs()  ; 
			}
			System.out.println(" Start Iter-" + iter  + " HS " + GetHeapSpace.HS()/1000000  + mixs) ;  
			EL.W (" Start Iter-" + iter  + " HS " + GetHeapSpace.HS()/1000000 + mixs) ; 			
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				threads [threadId].copyCounters (hdps ) ;  				
				threads [threadId].getQueue().put (iter) ;   
			}

			for (int level = 1 ; level < parms.levels ; level ++ ) { 
				hdps[level].saveCounters () ; 
			}
			int numOfThreadMsg = 0 ; 
			while   (numOfThreadMsg < parms.numOfThreads ) { 
				int threadID = queue.take() ; 	
				EL.W ( "Take " + " ID - " + threadID + "Iter-" + iter  ) ; 
				numOfThreadMsg ++ ; 
			}
			aggregateThreads (iter) ; 
			if (iter > parms.burninIters & iter % parms.skipIters == 0 ) { 
				sumCounters () ; 
			}
			
			EL.W ( " After Iter-" + iter  ) ; 
			if ((iter< parms.printIters) | (iter % parms.printIters == 0))  {
				validateCounters () ; 	
				printCounters () ;
				if (iter % parms.printIters == 0) {
					computeMultinomials () ;  
					double likelihood = computeLogLikelihood (); 
					EL.W (  " Iter-" + iter + " loglikelihood/Words " +  likelihood/numOfWords  ) ; 
					System.out.println (  " Iter-" + iter + " loglikelihood/Words " +  likelihood/numOfWords  ) ; 
				}								
			}
		}
		for (int threadIndx = 0 ; threadIndx < parms.numOfThreads ; threadIndx ++ ) {
			threads[threadIndx].getQueue().put (-1) ; 
		}
		computeMultinomials () ; 
		double likelihood = computeLogLikelihood () ; 
		EL.W (  " End " +   " likelihood/Words " +  likelihood/numOfWords  ) ; 
	}

	private void validateCounters() throws Exception {
		for (int level = 0 ; level < parms.levels ; level++) { 
			hdps[level].validateCounters();
		}		
	}

	private void aggregateThreads(int iter) throws Exception {
		
		/*******   update number of mixes and virtual terms *******/ 
		for (int level = 1 ; level < parms.levels ; level++) { 
			int maxMixsInLevel = hdps[level].getNumOfMixs() ; 		 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				if (threads[threadId].getHDP (level).getNumOfMixs() > maxMixsInLevel) { 
					maxMixsInLevel = threads[threadId].getHDP (level).getNumOfMixs() ; 
				}
			}
			numOfMixs [level] = maxMixsInLevel ; 
			hdps[level].extendMixs (numOfMixs [level]) ; 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				threads[threadId].getHDP(level).extendMixs (numOfMixs [level]) ; 
			}	
			numOfVTerms [level-1] = numOfMixs [level] ; 
			hdps[level-1].extendVTerms (numOfVTerms [level-1]) ; 
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				threads[threadId].getHDP(level-1).extendVMixs (numOfVTerms [level-1]) ; 
			}
		}

		/********  aggregate counters **********/ 
		for (int level = 0 ; level < parms.levels ; level++) { 
			hdps[level].resetStickBreakingWeights();
			for (int threadId = 0 ; threadId < parms.numOfThreads ; threadId ++ ) { 
				if (level < parms.levels-1) { 
					hdps[level].aggregateCounters(threadId , threads [threadId].getHDP(level) , hdps[level+1].getMapMixs(threadId)) ; 	
				}
				else  { 
					hdps[level].aggregateCounters(threadId , threads [threadId].getHDP(level) , null) ; 	
				}
			}
		}
		/********  Compute Stick breaking weights ********/ 
		for (int level = 0 ; level < parms.modelLevels ; level ++ ) { 
			hdps[level].copmuteStickBreakingWeights();		 
		}
		/********  sample model parameters ********/ 
		for (int level = 0 ; level < parms.levels ; level ++ ) { 
			hdps[level].sampleParms (iter) ; 			 
		}
	}
	private void sumCounters () throws Exception { 
		for (int level = 0 ; level < parms.levels ; level ++) { 
			hdps[level].sumCounters () ;  
		}
	}
	
	private void computeMultinomials () throws Exception {
		docMixLevel1Multinomials = hdps[0].computeMultinomials() ; 
		mixMultinomials = new ArrayList <double[][]> () ; 
		mixMultinomials.add (new double [1][1]) ;  
		for (int level = 1 ; level < parms.levels ; level ++) { 
			mixMultinomials.add (hdps[level].computeMultinomials()) ; 
		}			
	}	 
		
	private double  computeLogLikelihood() throws Exception {			 
		double [][] mixVTermLikelihood = mixMultinomials.get(1) ; 
		for (int i = 2 ; i < parms.levels ; i++) { 
			mixVTermLikelihood = multMat (mixVTermLikelihood ,mixMultinomials.get(i)) ; 
		}
		double [][] mixLevel1TermLikelihood  = mixVTermLikelihood ; 
		double logLikelihood = 0 ;  
		for (int docIndx = 0 ; docIndx < numOfDocs ; docIndx ++) { 
			logLikelihood += docLogLikelihood ( docIndx , mixLevel1TermLikelihood ) ; 
		}
		return logLikelihood ; 
	}
	
	private double[][] multMat(double[][] m1 , double[][] m2) {
		double [][] rMat = new double [m1.length][m2[0].length]  ; 
		for (int i = 0 ; i < m1.length ; i ++ ) { 
			for (int j = 0 ; j < m2[0].length ; j ++ ) { 
				rMat [i][j] = 0 ; 
				for (int k = 0 ; k < m1[0].length ;  k++) { 
					rMat[i][j] += m1[i][k]*m2[k][j] ; 
				}				
			}			
		}
		return rMat ; 
	}

	private double docLogLikelihood(int docIndx, double[][] mixLevel1TermLikelihood ) {
		double docLogLikelihood = 0 ; 
		double [] docMixLevel1Prob= docMixLevel1Multinomials[docIndx] ;
		int [] words = docList.get(docIndx).getWordArray() ; 
		for (int wordIndx = 0 ; wordIndx < words.length ; wordIndx ++ ) { 
			int termIndx = words[wordIndx] ; 
			double termLikelihood = 0 ; 
			for (int mixIndx = 0 ; mixIndx < numOfMixs [1] ; mixIndx ++ )  { 
				termLikelihood += docMixLevel1Prob [mixIndx] * mixLevel1TermLikelihood [mixIndx][termIndx]  ; 
			}
			docLogLikelihood += Math.log(termLikelihood) ; 
		}
		return docLogLikelihood ;
	}
		
	private void printCounters() {
		for (int level = 0 ; level < parms.levels ; level++) { 
			hdps[level].printCounters() ; 
		}
	}

	public int[] getNumOfMixs() {
		return this.numOfMixs ; 		
	}
	public double[][]  getDocMixLevel1Multinomials () { 
		return this.docMixLevel1Multinomials ; 
	}
	public ArrayList <double[][]>  getMultinomials () { 
		return this.mixMultinomials ; 
	}
	public double getLambda () { 
		return hdps[parms.modelLevels].getLambda() ; 		
	}

	public double [] getAlpha0() {
		double [] alpha0= new double [parms.levels] ;  
		for (int level = 0 ; level < parms.levels ; level ++) { 
			alpha0[level] = hdps[level].getAlpha0() ; 
		}
		return alpha0 ; 
	}
	public ArrayList <double []> getMixWeights () { 
		ArrayList <double []> mixWeights = new ArrayList <double []> () ; 
		for (int level = 0 ; level < parms.levels ; level ++) { 
			mixWeights.add(hdps[level].getVTermWeights ()) ; 
		}
		return mixWeights ; 		
	}
	public ArrayList <int [][]> getMixVtermsCounters (){ 
		ArrayList <int [][]> counters = new ArrayList <> () ; 
		for (int level = 0 ; level < parms.levels ; level ++) {
			counters.add(hdps[level].getMixVTermCounters().getMat()) ; 
		}
		return counters ; 
	}
	public ArrayList <int []> getMixVtermsSum (){ 
		ArrayList <int []> counters = new ArrayList <> () ; 
		for (int level = 0 ; level < parms.levels ; level ++) {
			counters.add(hdps[level].getMixVTermSum().getMat()[0]) ; 
		}
		return counters ; 
	}
}