package artzi.gtm.lda.model;

import cc.mallet.types.*; 
import cc.mallet.topics.*;

import java.util.*;

import artzi.gtm.lda.malletutils.MInstanceList;
import artzi.gtm.utils.elog.EL;

import java.io.*;

public class LDAModel {
	LDAParms parms ; 
	ParallelTopicModel model ; 
	int numTopics , numDocs , numTerms ; 
	InstanceList instanceList ; 
	double [][] docTopicProb ; 

	double [][] topicTermProb ; 
	double alpha_t , beta_w ; 

	public LDAModel (MInstanceList mInstanceList , int numTopics , double alpha , double beta    ) { 
		parms = LDAParms.getInstance() ; 

		// Create a model with numTopics topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is the parameter for a single dimension of the Dirichlet prior.
		this.instanceList = mInstanceList.getInstanceList () ; 
		this.numTopics = numTopics ; 
		beta_w = beta ; 
		alpha_t = numTopics * alpha ; 
		numDocs = instanceList.size () ; 
		numTerms = instanceList.getDataAlphabet().size(); 
		EL.W ("Topics:" + numTopics + " docs:"+numDocs + " Terms:"+numTerms) ; 
		EL.W ("Alpha: " + alpha_t + " beta:"+beta_w ) ; 

		model = new ParallelTopicModel(numTopics, alpha_t, beta_w);

		model.addInstances(instanceList);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(parms.numOfThreads);

		// Run the model for 50 iterations and stop (this is for testing only, 
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(parms.iterations);  
		model.setOptimizeInterval(parms.optimezeInterval) ; 
		model.setBurninPeriod(parms.burninIters) ;  
		try {
			model.estimate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initMats () ; 
	}
	private void initMats () { 
		docTopicProb = new double [numDocs][numTopics]  ; 
		for (int doc = 0 ; doc < numDocs ; doc ++ ) { 
			docTopicProb [doc] = model.getTopicProbabilities(doc);				
		}
		topicTermProb = new double [numTopics][numTerms] ; 		
		double numTermsInTopic ; 
		IDSorter idCountPair ;
		ArrayList<TreeSet<IDSorter>> topicSortedTerms = model.getSortedWords();
		for (int topic = 0; topic < numTopics; topic++) {
			numTermsInTopic = 0 ; 
			Iterator<IDSorter> iterator = topicSortedTerms.get(topic).iterator();
			while (iterator.hasNext() ) {
				idCountPair = iterator.next () ;  
				numTermsInTopic += idCountPair.getWeight()  ; 
			}
			for (int j = 0 ; j < numTerms ; j ++ )
				topicTermProb[topic][j] = 
				  beta_w  /
				(numTermsInTopic + beta_w * numTerms) ; 
			iterator = topicSortedTerms.get(topic).iterator();
			while (iterator.hasNext() ) {
				idCountPair = iterator.next();
				topicTermProb [topic][idCountPair.getID()] = 
						(idCountPair.getWeight() + beta_w) /
						(numTermsInTopic + beta_w * numTerms) ; 
			}
		}		
	}
	public double[][] getDocTopicProb() {
		return docTopicProb;
	}
	public double[][] getTopicTermProb() {
		return topicTermProb;
	}
	public TopicInferencer getTopicInferencer() {
		return model.getInferencer() ; 
	}
	public double [] classify (Instance instance) { 
		TopicInferencer topicI = model.getInferencer() ; 
		return topicI.getSampledDistribution(instance, 50 , 5 , 20) ; 
	}
	public double getBeta () { 
		return model.beta ; 
	}
	public int [][] getTopicTermCount () { 
		int [][] typeTopicCount = model.typeTopicCounts ;  		
		int [][] topicTermCount = new int [parms.numOfLDATopics][numTerms]  ; 
		for (int topicId = 0 ; topicId < parms.numOfLDATopics ; topicId ++) { 
			for (int termId = 0 ; termId < numTerms ; termId ++) { 
				topicTermCount [topicId][termId] = 0 ; 
			}
		}
		for (int termId = 0 ; termId < numTerms ; termId ++  ) {
			for (int counter : typeTopicCount[termId])  { 	
				if (counter > 0)  {
					int currentTopic = counter & model.topicMask;
					int currentValue = counter >> model.topicBits;
					topicTermCount [currentTopic][termId] = currentValue ; 
				}
			}			
		}
		for (int topicId = 0 ; topicId < parms.numOfLDATopics ; topicId ++) { 
			int sumt = 0 ; 		 
			for (int termId = 0 ; termId < numTerms ; termId ++  ) {		
				sumt += topicTermCount [topicId][termId] ; 
			}		 
			if (sumt != model.tokensPerTopic[topicId] ) System.out.println ("bad topic counter " + topicId + " - " +sumt) ;  
		}
	 
		return topicTermCount ;  
	}
	public int[] getTopicCount() {
		return model.tokensPerTopic ;		
	}	
}
