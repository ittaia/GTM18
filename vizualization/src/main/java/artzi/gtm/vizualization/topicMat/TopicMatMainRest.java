package artzi.gtm.vizualization.topicMat;

import java.io.File;
import com.google.gson.Gson;

import artzi.gtm.utils.config.Config;

public class TopicMatMainRest {
	
	static String path = "C:\\TestDir\\LDARest" ;  
		
	static TopicMat topicMat ; 
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 
		String modelPath = config.getPath("Model") ; 
		String trainedModelPath = new File (modelPath , "trainedModel").getAbsolutePath() ; 
		topicMat = new TopicMat (trainedModelPath) ; 
		String treePath = new File (modelPath , "tree.json").getAbsolutePath() ; 
		topicMat.writeTrees(treePath); 	
		String treehPath = new File (modelPath , "treeh.json").getAbsolutePath() ; 
		topicMat.writeHierarchy(treehPath) ; 
		topicMat.printTopicClusters();
		topicMat.printCloseTopics();
	}
}	