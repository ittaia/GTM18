package artzi.gtm.topicModelInfra.trainedModel;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.utils.termList.TermList;

/**
 * Trained multi level model
 */

public class TrainedMLModel {
	
	private TermList termList ; 
	
	/**
	 * number of levels (starts at 0)
	 */	
	private int levels ;
	
	/**
	 * numOfMixs[0] = number of documents
	 * numOfMixs[x] = number of topics in level x
	 * size of array = levels ; 
	 */
	
	private int[] numOfMixs ;
	/**
	 * array of matrices = probabilities of terms in topics
	 * every line of the array corresponds to a level
	 */
	private ArrayList<double[][]> multinomials ;
	/**
	 * array of matrices = mix vTerm counters
	 * every line of the array corresponds to a level
	 */
	private ArrayList<int [][]> mixVTermCounters ;
	/**
	 * array of arrays = mix vTerm sum
	 * every line of the array corresponds to a level
	 */
	private ArrayList<int []> mixVTermSum ;
	/**
	 * array of double  = alpha0 [level]
	 * 
	 */
	private double[]  alpha0 ;
	
	/**
	 * list of double[]  = weights of mixes in next level 
	 * 
	 */
	ArrayList <double []> mixWeights ; 
	int numOfTopTerms = 50 ; 
	TermProb [][] topicTopTerms = null ; 
	
	String [] topicHeaders = null ;
	String [] topicHeaders1 = null ; 
	
			
	
	
	private static TrainedMLModel modelInstance = null ; 
	
	private TrainedMLModel(TermList termList , int levels, int[] numOfMixs, ArrayList<double[][]> multinomials , ArrayList <int [][]> mixVTermCounters , 
			ArrayList <int []> mixVTermSum , double [] alpha0, ArrayList <double []> mixWeights) {
		super();
		this.termList = termList ; 
		this.levels = levels;
		this.numOfMixs = numOfMixs;
		this.multinomials = multinomials;
		this.mixVTermCounters = mixVTermCounters ; 
		this.mixVTermSum = mixVTermSum ; 
		this.alpha0 = alpha0 ; 
		this.mixWeights = mixWeights ; 		
	}
	
	/**
	 * get instance using all fields
	 * 
	 * @param levels
	 * @param numOfMixs
	 * @param multinomials
	 * @param alpha0
	 * @param mixWeights
	 */
	
	public static TrainedMLModel  getInstance ( TermList termList , int levels, int[] numOfMixs, ArrayList<double[][]> multinomials , 
			ArrayList <int [][] > mixVTermCounters , ArrayList <int []> mixVTermSum , double [] alpha0,
							ArrayList <double []> mixWeights) {	
		modelInstance = new TrainedMLModel (termList , levels , numOfMixs , multinomials , mixVTermCounters , mixVTermSum , alpha0 , mixWeights)  ; 
		return modelInstance ; 		
	}
	

	/**
	 * get instance using a json file
	 * @param _path = full path to a json file
	 * @throws IOException 
	 */
	
	public static TrainedMLModel getInstance(String _path ) throws IOException {
		  
		Gson gson = new Gson () ; 
		JsonReader reader = new JsonReader (new InputStreamReader (new FileInputStream (_path))) ;  
		modelInstance =  gson.fromJson(reader,TrainedMLModel.class) ; 	
		return modelInstance ; 
	}
	
	/**
	 * get instance 
	 */
	
	public static TrainedMLModel getInstance () { 
		return modelInstance ; 
	}
	
		
	/**
	 * save current object in a json file
	 * @param _path = full path to json file output
	 * @throws IOException 
	 */
	public void toFile (String _path) throws IOException{
		Gson gson = new Gson () ; 
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_path)));
		out.newLine();
		String json = gson.toJson(modelInstance , TrainedMLModel.class);
		out.write(json);
		out.close(); 
		System.out.println("Save "+_path);
	}
	
	public int getLevels() {
		return levels;
	}
	
	public int getNumOfTopics () { 
		return numOfMixs[levels-1];
	}
	
	public int[] getNumOfMixs() {
		return numOfMixs;
	}

	public ArrayList<double[][]> getMultinomials() {
		return multinomials;
	}
	
	public double [] getAlpha0 () { 
		return alpha0 ; 
	}
	
	public ArrayList <double[]> getMixWeights () { 
		return mixWeights ; 
	}

	public ArrayList<int[][]> getMixVTermCounters() {
		return mixVTermCounters;
	}

	public ArrayList<int[]> getMixVTermSum() {
		return mixVTermSum;
	}
	public void setNumOfTopTerms (int numOfTopTerms) { 
		this.numOfTopTerms = numOfTopTerms ; 
	}
	public void printTopics  () {
		int numOfTopics = numOfMixs[levels-1] ;		
		 

		EL.W (" Topics " ); 
		 
		for (int topicId = 0 ; topicId < numOfTopics  ; topicId++) { 
			printTopic (topicId , 9999) ; 			 		
		} 	
	}
	public void printTopic  (int topicId, int maxTerms) {
		
		double [][] topicTermProb = multinomials.get(levels-1) ; 
		int numOfTerms = topicTermProb[0].length ;	
		IndxProb [] termProbArray  = new IndxProb [numOfTerms] ;
		EL.W (" Topic -" + topicId  ) ; 
		for (int termIndx = 0 ; termIndx < numOfTerms ; termIndx ++ ) {
			termProbArray [termIndx] = new IndxProb (termIndx, topicTermProb[topicId][termIndx]   ) ;
		}
		Arrays.sort ( termProbArray  ,  new CompareProb()) ; 
		for (int i = 0 ; i < Math.min(numOfTerms,100); i ++ ) { 
			int termIndx = termProbArray [i].getIndx() ; 
			double p = termProbArray[i].getProb() ; 
			if (p > 0.001) { 
				EL.W ( " term -" + termIndx + " prob - " +   p + " - "+ termList.getTerm(termIndx) + "- "+
						termList.getTermDC (termIndx) ) ; 
			}
			if (i > maxTerms ) break ; 
		}	
	}
	
	public void print2Levels  () {
		int numOfTopics = numOfMixs[levels-1] ;		
		int numOfMixs0 = numOfMixs[levels-2] ;			
		double [][] mix0TopicProb = multinomials.get(levels-2) ; 
		if (topicTopTerms == null) initTopTerms () ; 
		EL.W (" Topics level 0 --->  Topics level 1 " ); 
		IndxProb [] mixProbArray  = new IndxProb [numOfTopics] ;
		for (int mixIndx = 0 ; mixIndx < numOfMixs0  ; mixIndx ++) { 
			EL.W (" Mix -" + mixIndx  ) ; 
			for (int topicIndx = 0 ; topicIndx < numOfTopics ; topicIndx ++ ) {
				mixProbArray [topicIndx] = new IndxProb (topicIndx, mix0TopicProb[mixIndx][topicIndx]   ) ;
			}
			Arrays.sort ( mixProbArray  ,  new CompareProb()) ; 
			for (int i = 0 ; i < numOfTopics ; i ++ ) { 
				int topicIndx = mixProbArray [i].getIndx() ; 
				double p = mixProbArray[i].getProb() ; 
				if (p > 0.001) { 
					EL.W ( " topic -" + topicIndx + " Topic Prob - " +   p + "-" + topicTopTerms [topicIndx]    ) ; 
				}
			}		
		} 	
	}

	private void initTopTerms() {
		int numOfTopics = numOfMixs[levels-1] ;		
		double [][] topicTermProb = multinomials.get(levels-1) ; 
		int numOfTerms = topicTermProb[0].length ;	
		topicTopTerms = new TermProb [numOfTopics][numOfTopTerms] ; 
		topicHeaders = new String [numOfTopics] ; 
		topicHeaders1 = new String [numOfTopics] ; 
		IndxProb [] termProbArray  = new IndxProb [numOfTerms] ; 
		for (int topicIndx = 0 ; topicIndx <numOfTopics  ; topicIndx ++) { 
			for (int termIndx = 0 ; termIndx < numOfTerms ; termIndx ++ ) {
				termProbArray [termIndx] = new IndxProb (termIndx,topicTermProb [topicIndx][termIndx]) ;
			}			
			Arrays.sort ( termProbArray  ,  new CompareProb()) ;
			String topTerms = "" ; 	
			String topTerms1 = "" ;
			for (int i = 0 ; i < Math.min (numOfTerms,numOfTopTerms) ; i ++ ) {
				int termIndx = termProbArray[i].getIndx() ; 								
				topicTopTerms [topicIndx][i] = new TermProb 
						(termIndx , termList.getTerm(termProbArray[i].getIndx()) , termProbArray[i].getProb() ) ; 	
				if (i < 10) { 
					topTerms += " "+ termIndx+"-"+  termList.getTerm(termIndx) ; 
					if (topTerms1.length() < 15)
						topTerms1 += termList.getTerm(termIndx) + " "; 
				}
			}
			topicHeaders [topicIndx] = topTerms ; 
			topicHeaders1 [topicIndx] = topTerms1 ; 
		}		
	}
	public TermProb [] getTopTerms (int topicId) { 
		if (topicTopTerms == null) initTopTerms () ; 
		return topicTopTerms [topicId] ; 
	}

	public String getHeader(int topicId) {
		if (topicTopTerms == null) initTopTerms () ; 
		return topicHeaders [topicId] ; 
	}
	public String getHeader1(int topicId) {
		if (topicTopTerms == null) initTopTerms () ; 
		return topicHeaders1 [topicId] ; 
	}
	public String getTerm (int termIndx) { 
		return termList.getTerm(termIndx) ; 
	}
	
	public TermList getTermList () { 
		return termList ;		
	}
}