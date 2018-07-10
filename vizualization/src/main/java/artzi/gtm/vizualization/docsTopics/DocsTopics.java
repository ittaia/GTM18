package artzi.gtm.vizualization.docsTopics;

import java.util.ArrayList;

import artzi.gtm.lda.train.LDADocs;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.vizualization.VParms;

public class DocsTopics {
	
	int numOfDocs ; 
	int numOfTopics ; 
	ArrayList <ArrayList <IndxProb>> docsTopics ; 
	ArrayList <ArrayList <IndxProb>> topicsDocs ; 
	
	public DocsTopics (LDADocs ldaDocs) {
		numOfDocs = ldaDocs.getM$numOfDocs() ; 
		numOfTopics = ldaDocs.getK$numOfTopics() ; 
		docsTopics = new ArrayList <> () ; 
		topicsDocs = new ArrayList <> () ; 	
		initLists (ldaDocs) ; 
	}

	private void initLists(LDADocs ldaDocs) {

		double [][]docTopicProb = ldaDocs.getDocTopicProb() ; 

		for (int docId = 0 ; docId < numOfDocs ;  docId ++) { 
			ArrayList <IndxProb> docTopics = new ArrayList <> () ; 
			for (int topicId = 0 ; topicId < numOfTopics ; topicId ++) {
				if (docTopicProb [docId][topicId] > VParms.minDocTopicProb) { 
					IndxProb ip = new IndxProb (topicId , docTopicProb [docId][topicId]) ; 
					docTopics.add(ip) ; 
				}
			}
			docTopics.sort( (t2,t1) ->   Double.compare(t1.getProb(), t2.getProb()));
			docsTopics.add(docTopics) ; 
		}
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++) {
			ArrayList <IndxProb> topicDocs = new ArrayList <> () ; 
			for (int docId = 0 ; docId < numOfDocs ;  docId ++) { 
				if (docTopicProb [docId][topicId] > VParms.minDocTopicProb) { 
					IndxProb ip = new IndxProb (docId , docTopicProb [docId][topicId]) ; 
					topicDocs.add(ip) ; 
				}
			}
			topicDocs.sort( (t2,t1) ->   Double.compare(t1.getProb(), t2.getProb()));
			topicsDocs.add(topicDocs) ; 
			if (topicId == 79) { 
				for (IndxProb   d : topicDocs) {
					if (d.getProb() > 0.5) System.out.println (d.getIndx() + " - "+ d.getProb()) ; 
				}
					
			}
		}		
	}

	public void printCounts() {
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++) {
			System.out.println (topicId + " numOfDocs - " + topicsDocs.get(topicId).size() ) ; 
		}		
	}
}