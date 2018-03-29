package artzi.gtm.vizualization.topicMat;

import java.io.IOException;
import java.util.Arrays;

import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.aMath.KLDivergence;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.sortProb.IndxProb;
import mdsj.MDSJ;

public class TopicMat {
	TrainedMLModel tmodel ; 
	
	int numOfTopics ; 
	int numOfTerms ; 
	double [][] topicMat ; 
	double[][] mdsMat ; 
	public TopicMat (String modelPath) throws IOException { 
		tmodel = TrainedMLModel.getInstance(modelPath) ; 
		System.out.println ( "Init Mat ")  ; 
		initMat (modelPath) ; 
		initMDS () ; 
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
	private void initMDS () { 
		mdsMat=MDSJ.classicalScaling(topicMat);
		/*
		for(int i=0; i<numOfTopics; i++) {  // output all coordinates
		    EL.W("topic "+ i + " coordinates: "+ mdsMat[0][i]+" "+mdsMat[1][i] + tmodel.getHeader (i)  );
		}
		*/
	}
	public void printCloseTopics() {
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			IndxProb [] closeTopics = getCloseTopics (topicId) ; 
			EL.W("close topics - " + topicId );
			tmodel.printTopic(topicId , 50);
			for (int i = 0 ; i < numOfTopics ;  i ++ ) { 
				if (closeTopics [i].getProb() > 15 ) break ; 
				int topic2 = closeTopics[i].getIndx() ; 
				EL.W("close topics - " + topicId  + "- "+   topic2 + " - "+ closeTopics [i].getProb() + 
						" coordinates: "+ mdsMat[0][topic2]+" "+mdsMat[1][topic2] + tmodel.getHeader (topic2));
				//tmodel.printTopic(closeTopics[i].getIndx() , 50);
			}
		}
		
	}
	public IndxProb [] getCloseTopics (int topicId) { 
		IndxProb [] topics = new IndxProb [numOfTopics] ; 
		for (int topic2 = 0 ; topic2 < numOfTopics ; topic2 ++ ) { 
			topics [topic2] = new IndxProb (topic2 , topicMat [topicId][topic2]) ;  
		}
		Arrays.sort(topics , (t1,t2) ->   Double.compare(t1.getProb(), t2.getProb()));
		return topics ; 		
	}
	
}
