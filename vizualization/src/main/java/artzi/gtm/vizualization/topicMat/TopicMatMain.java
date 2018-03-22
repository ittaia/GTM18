package artzi.gtm.vizualization.topicMat;

import java.io.File;
import com.google.gson.Gson;

import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.sortProb.IndxProb;


public class TopicMatMain {
	
	static String path = "C:\\TestDir\\LDA" ;  
		
	static TopicMat topicMat ; 
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 
		String path = config.getPath("Model") ; 
		String modelPath = new File (path , "trainedModel").getAbsolutePath() ; 
		topicMat = new TopicMat (modelPath) ; 	
		IndxProb [] topics = topicMat.getCloseTopics(0) ; 
		for (int i = 0 ; i < 30 ; i ++) { 
			System.out.println (topics[i].getIndx() + " - " + topics [i].getProb()) ; 
		}
	}
}	