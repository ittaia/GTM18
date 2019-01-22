package artzi.gtm.lda.shut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import artzi.gtm.lda.classify.TopicClassifier;
import artzi.gtm.lda.toxlsx.ToXlsxTopicDocs;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.gen.DocData;
import artzi.gtm.utils.io.Dirs;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.utils.termList.TermList;

public class LDAShutClassify {
	
	static String path = "C:\\TestDir\\LDAShut" ;  
		
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	static TrainedMLModel model ; 
	static TopicClassifier classifier ; 
	static TermList termList ;
	static int numOfTopics ; 
	static ArrayList <DocData> highProbDocs ; 
	static ArrayList<ArrayList <IndxProb>> topicDocProb ; 
			
	public static void main(String[] args) throws Exception {
		highProbDocs = new ArrayList <> () ; 
		topicDocProb = new ArrayList <>() ; 
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		String modelPath = config.getPath("Model") ; 
		String trainedModelPath = modelPath + "\\trainedmodel" ; 
		model = TrainedMLModel.getInstance(trainedModelPath) ;
		numOfTopics = model.getNumOfTopics() ; 
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			topicDocProb.add(new ArrayList <IndxProb> ()) ; 
		}
		termList = model.getTermList () ; 
		classifier = new TopicClassifier(modelPath, termList  ) ; 
		loadData () ;	
		System.out.println ("Tot " + totFiles + " High Prob " + highProbDocs.size() ) ; 
		ToXlsxTopicDocs xls = new ToXlsxTopicDocs() ; 
		xls.editModel(model, highProbDocs, topicDocProb, true);
		String xlsPath = config.getPath("Xls") ; 
		xls.writeXlsx(xlsPath);
	}
	private static void loadData () throws JsonSyntaxException, IOException, Exception { 
		String dataPath =  config.getValue ("Data") ;
		System.out.println ("Load data " + dataPath);
		String [] filter = {"json"} ; 
		ArrayList <File> datafiles = Dirs.FilesInDir(dataPath , filter) ; 
		for (File file : datafiles) { 			
			loadDocs (file) ;
		}
	}
	private static void loadDocs (File file) throws JsonSyntaxException, IOException, Exception { 
		//System.out.println ("Load docs - json " + file.getAbsolutePath());
		BufferedReader in  = new BufferedReader(new FileReader(file));
		String line ; 
		while ((line = in.readLine()) != null) { 
			DocData docData = gson.fromJson(line, DocData.class) ; 			
			totFiles ++ ; 
			if (totFiles % 1000 == 0 ) {
				System.out.println ("load : "+ totFiles) ; 				
			}
			getTopic (docData) ; 
		}
		in.close(); 
		System.out.println (file.getAbsolutePath() + "files " + totFiles  ) ; 		 
	}
	
	private static ArrayList <String> split (String str) { 
		ArrayList <String> rList = new ArrayList <String> () ; 

		final String token  = "[^\\s][^\\s]*[\\s]" ; 		 
		final Pattern ps = Pattern.compile(token) ; 
		
		Matcher m = ps.matcher(str+" ") ; 
		while (m.find() ) { 
			String g = m.group() ; 
			String t = g.substring(0,g.length()-1) ; 
			rList.add(t) ; 			
		}
		return rList ; 		
	}
	
	private static void getTopic (DocData docData)  { 
		String text = docData.getText() ; 
		ArrayList<String> tokens = split(text) ; 
		ArrayList <Integer>  termIds = new ArrayList <> () ; 
		for (String w:tokens) {
			int termId = termList.getTermIndx(w) ; 
			if (termId > -1) termIds.add(termId) ; 				
		}
		int []wordArray = new int [termIds.size()] ; 
		for (int i = 0 ; i < termIds.size() ; i ++ ) wordArray[i] = termIds.get(i) ; 			
		double [] topicProb  = classifier.classify(wordArray) ; 
		int maxTopic = 0 ; 
		for (int topicId = 1 ; topicId < topicProb.length ; topicId ++) { 
			if (topicProb[maxTopic] < topicProb[topicId]) maxTopic = topicId ; 			
		}
		if (topicProb[maxTopic] > 0.76) { 
			highProbDocs.add(docData) ; 			
			IndxProb indxProb = new IndxProb(highProbDocs.size()-1 , topicProb[maxTopic] ) ; 
			topicDocProb.get(maxTopic).add(indxProb) ; 
		}		
	}
}