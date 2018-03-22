package artzi.gtm.vizualization.topicMat;

import java.io.IOException;
import java.util.Arrays;

import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.aMath.KLDivergence;
import artzi.gtm.utils.sortProb.IndxProb;

public class TopicMat {
	TrainedMLModel tmodel ; 
	
	int numOfTopics ; 
	int numOfTerms ; 
	double [][] topicMat ; 
	public TopicMat (String modelPath) throws IOException { 
		tmodel = TrainedMLModel.getInstance(modelPath) ; 
		initMat (modelPath) ; 
	}
	private void initMat(String modelPath) throws IOException {
		TrainedMLModel tmodel ; 
		tmodel = TrainedMLModel.getInstance(modelPath) ; 
		double [][] topicTermProb = tmodel.getMultinomials().get(tmodel.getLevels()-1) ; 
		numOfTopics = topicTermProb.length ; 
		numOfTerms = topicTermProb[0].length ; 
		topicMat = new double[numOfTopics][numOfTopics] ; 
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			topicMat [topicId][topicId] = 0 ;  
			for (int topic2 = topicId+1 ; topic2 < numOfTopics ; topic2 ++) { 
				topicMat [topicId][topic2] =  KLDivergence.getsym(topicTermProb[topicId] , topicTermProb[topic2]) ; 
				topicMat [topic2][topicId] = topicMat [topicId][topic2] ; 
			}
		}		
	}
	public IndxProb [] getCloseTopics (int topicId) { 
		IndxProb [] topics = new IndxProb [numOfTopics] ; 
		for (int topic2 = 0 ; topic2 < numOfTopics ; topic2 ++ ) { 
			topics [topic2] = new IndxProb (topic2 , topicMat [topicId][topic2]) ;  
		}
		Arrays.sort(topics , (t1,t2) ->   Double.compare(t2.getProb(), t1.getProb()));
		return topics ; 		
	}
}
