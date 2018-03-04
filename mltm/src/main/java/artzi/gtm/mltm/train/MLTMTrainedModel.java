package artzi.gtm.mltm.train;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import artzi.gtm.topicModelInfra.dataObjects.DocWords;
import artzi.gtm.topicModelInfra.logProportions.LogProportions;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.utils.termList.Term;
import artzi.gtm.utils.termList.TermList;

public class MLTMTrainedModel {
	private TermList termList ; 
	private double alpha ; 
	private double alphaTopics ; 
	private int K$numOfTopics ; 
	private int V$numOfTerms ;
	private int numOfLevels;
	private double [][] topicTermProb  ; 
	private ProbMat[] probMat;
	
	public MLTMTrainedModel   (TermList termList ,  int k$numOfTopics, int levels,  double alpha, ProbMat[] probMat , 
			double[][] topicTermProb) {
		this.termList  = termList ; 
		this.K$numOfTopics = k$numOfTopics;
		this.alpha = alpha ; 
		this.alphaTopics = alpha * K$numOfTopics ; 
		this.V$numOfTerms = termList.getSize ();
		this.probMat = new ProbMat[levels];
		this.numOfLevels = levels;
		this.probMat = probMat;
		this.topicTermProb = topicTermProb ; 
	} 
	
	public MLTMTrainedModel (JsonElement _je)
	{
		JsonObject jo = _je.getAsJsonObject();
		JsonElement trm = jo.get("termList");
		
		this.termList = TermList.GetInstance(trm);
		this.alpha = jo.get("alpha").getAsDouble();
		this.alphaTopics = jo.get("alphaTopics").getAsDouble();
		this.K$numOfTopics = jo.get("K$numOfTopics").getAsInt();
		this.V$numOfTerms = jo.get("V$numOfTerms").getAsInt();
		
		JsonArray arr = jo.get("probMat").getAsJsonArray();
		this.probMat = new ProbMat[arr.size()];
		for (int i = 0; i < arr.size(); i++){
			ProbMat pm = new ProbMat(arr.get(i));
			this.probMat[i] = pm;
		}
//		double[][] tmptop = new double[arr.size()][];
//		for (int i = 0; i < arr.size(); i++)
//		{
//			JsonArray arr2 = arr.get(i).getAsJsonArray();
//			tmptop[i] = new double[arr2.size()];
//			for (int j = 0; j < arr2.size(); j++)
//			{
//				tmptop[i][j] = arr2.get(j).getAsDouble();
//			}
//		}
//		this.topicTermProb = tmptop;
		
	}
	
	public MLTMTrainedModel(String _path) throws IOException {
//		JsonObject jo = new JsonObject();
		JsonReader reader = new JsonReader(new FileReader(_path));
		reader.setLenient(true);
		Gson json = new Gson();
		Type type = new TypeToken <JsonObject>(){}.getType();
		JsonToken token;
		String name = "";
		int trm = 0;
		int top = 0;
		int nm = 0;
		int level = -1;
		
		while(reader.hasNext()){
			token = reader.peek();
			if (token.equals(JsonToken.END_DOCUMENT)){
				break;
			}
			if (token.equals(JsonToken.BEGIN_OBJECT)){
				reader.beginObject();
				continue;
			}
			if (token.equals(JsonToken.END_OBJECT)){
				reader.endObject();
				continue;
			}
			if (token.equals(JsonToken.NAME)){
				name = reader.nextName();
				if (name.equals("alpha")){
					this.alpha = reader.nextDouble();
					continue;
				}
				if (name.equals("alphaTopics")){
					this.alphaTopics = reader.nextDouble();
					continue;
				}
				if (name.equals("K$numOfTopics")){
					this.K$numOfTopics = reader.nextInt();
					continue;
				}
				if (name.equals("V$numOfTerms")){
					this.V$numOfTerms = reader.nextInt();
					continue;
				}
				/**
				 * ignore term list inside results
				 */
				if (name.equals("termList")){
					@SuppressWarnings("unused")
					JsonObject trmob = json.fromJson(reader,type) ;
					continue;
				}
				/**
				 * number of levels for probability matrix
				 */
				if (name.equals("numOfLevels")){
					this.numOfLevels = reader.nextInt();
					this.probMat = new ProbMat[this.numOfLevels];
					continue;
				}
				if (name.equals("probMat")){
					top = 0;
					double[][] pm = new double[K$numOfTopics][V$numOfTerms];
					reader.beginArray();
					while (reader.hasNext()){
						reader.beginArray();
						trm = 0;
						while (reader.hasNext()){
							pm[top][trm] = reader.nextDouble();
							trm++;
						}
						reader.endArray();
						top++;
					}
					reader.endArray();
					level++;
					this.probMat[level] = new ProbMat(pm);
					continue;
				}
				
				if (name.equals("topicName")){
					String[] tn = new String[K$numOfTopics];
					reader.beginArray();
					while(reader.hasNext()){
						tn[nm] = reader.nextString();
						nm++;
					}
					reader.endArray();
					this.probMat[level].setNames(tn);
					continue;
				}
					
			}
				
		} // reader.has next
		reader.close();
	}
	
	public ProbMat[] getProbMat() {
		return probMat;
	}

	public boolean setProbMat(ProbMat probMat, int level) {
		if (level < 0){
			return false;
		}
		if (level > this.numOfLevels){
			return false;
		}
		this.probMat[level] = probMat;
		return true;
	}

	/**
	 * print lowest level (topics X terms)
	 * 
	 * @param sec = secondary log
	 */
	public void print (boolean sec) {
		String [] topicTerm0 = new String [K$numOfTopics] ;  
		IndxProb [] termProbArray  = new IndxProb [V$numOfTerms] ; 
		ProbMat Lm1 = this.probMat[this.probMat.length - 1];
		EL.W (" Topic - Term" ); 
		for (int topicIndx = 0 ; topicIndx <K$numOfTopics  ; topicIndx ++) { 
			EL.W( "******    Topic - " + topicIndx) ; 
			for (int termIndx = 0 ; termIndx < V$numOfTerms ; termIndx ++ ) {
				termProbArray [termIndx] = new IndxProb (termIndx,Lm1.getProbMat() [topicIndx][termIndx]) ;
			}			
			Arrays.sort ( termProbArray  ,  new CompareProb()) ; 
			topicTerm0 [topicIndx] = termList.getTerm(termProbArray[0].getIndx()) ; 
			for (int i = 0 ; i < V$numOfTerms; i ++ ) {
				int termIndx = termProbArray[i].getIndx() ; 
				double p = termProbArray[i].getProb() ; 
				if (p > 0.001)
					EL.W ( " Term - " + termIndx +"-"+ termList.getTerm(termIndx)+ " - " +  p) ; 
			}
		}	
	}
	public IndxProb [] getTopicTerms (int topicIndx) { 
		IndxProb [] termProbArray  = new IndxProb [V$numOfTerms] ; 
		ProbMat Lm1 = this.probMat[this.probMat.length - 1];
		for (int termIndx = 0 ; termIndx < V$numOfTerms ; termIndx ++ ) {
			termProbArray [termIndx] = new IndxProb (termIndx,Lm1.getProbMat() [topicIndx][termIndx]) ;
		}			
		Arrays.sort ( termProbArray  ,  new CompareProb()) ;
		return termProbArray ; 
	}
	public double [] classify (DocWords doc) { 
		Random generator = new Random () ; 
		
		int [] wordArray = doc.getWordArray() ; 
		int nWords = wordArray.length ; 
		int [] wordTopic = new int [nWords] ; 
		int [] topicCount = new int [K$numOfTopics] ; 
		for (int topic = 0 ; topic < K$numOfTopics ; topic ++ ) { 
			topicCount [topic] = 0 ;   
		}
		for (int wordIndx = 0 ; wordIndx < nWords ; wordIndx ++ ) { 
			int topic = generator.nextInt () ; 
			wordTopic [wordIndx] = topic ; 
			topicCount [topic] ++ ;   
		}
		for (int iter = 0 ; iter < 50 ; iter ++ ) { 
			for (int wordIndx = 0 ; wordIndx < nWords ; wordIndx ++ ) { 
				topicCount [wordTopic[wordIndx]] -- ;
				LogProportions logProportions = new LogProportions () ;  
				for (int topic = 0 ; topic < K$numOfTopics ; topic ++ ) { 					
					double logProp = Math.log ( topicTermProb[topic][wordArray[wordIndx]])   
								   + Math.log( topicCount [topic] + alpha) ; 
					logProportions.add (topic , logProp) ; 
				}
				int topic = logProportions.sample () ; 
				wordTopic[wordIndx] = topic ; 
				topicCount [wordTopic[wordIndx]] ++ ;
			}
		}
		double [] topicProb = new double [K$numOfTopics] ; 
		for (int topic = 0 ; topic < K$numOfTopics ; topic ++ ) { 
			topicProb [topic] = (topicCount [topic] + alpha)  / (nWords + alphaTopics) ; 
		}
		return topicProb ; 
	}
	public double computePerplexity (ArrayList <DocWords> docList     ) { 
		int M$numOfDocuments = docList.size () ; 	
		double [][] docTopicProb  = new double [M$numOfDocuments][K$numOfTopics] ; 
		for  (int docIndx= 0 ; docIndx < M$numOfDocuments  ; docIndx++ ) { 
			docTopicProb [docIndx] = classify (docList.get(docIndx)) ; 
		}
		return computePerplexity (docList ,docTopicProb) ; 
	}
	public double computePerplexity (ArrayList <DocWords> docList , double [][] docTopicProb    ) { 
		double [][] docTermProb ; 
		int numTokens = 0 ; 
		int M$numOfDocuments = docList.size () ; 
		docTermProb = new double[M$numOfDocuments][V$numOfTerms] ; 
		for (int docIndx= 0 ; docIndx < M$numOfDocuments  ; docIndx++ ) {
			for (int termIndx = 0 ; termIndx < V$numOfTerms ; termIndx ++ ) {
				double prob = 0 ; 
				for (int topicIndx = 0 ; topicIndx < K$numOfTopics ; topicIndx ++ ) { 
					prob += topicTermProb [topicIndx][termIndx] * docTopicProb[docIndx] [topicIndx] ;   
				}
				docTermProb [docIndx][termIndx] = prob ; 
			}
		}
		double pp ; 
		double sumLogProb ; 
		sumLogProb = 0 ; 
		for (int docIndx = 0 ; docIndx <  M$numOfDocuments ; docIndx ++ ) { 
			int [] wordArray = docList.get(docIndx).getWordArray() ;
			for (int wordIndx = 0 ; wordIndx < wordArray.length ; wordIndx ++ ) { 
				numTokens ++ ; 
				int termIndx = wordArray[wordIndx] ; 
				sumLogProb += Math.log (docTermProb[docIndx][termIndx]/Math.log(2)) ;  				
			}			
		}
		EL.W("Log Prob" + sumLogProb) ; 
		Double t1 = -1.0 ; 
		double t2 = t1/numTokens ; 
		
		pp = Math.pow (2 , t2 *sumLogProb) ; 
		EL.W("Perplexity " + pp) ; 
		EL.W("Tokens " + numTokens + " Perplexity " + pp ) ; 
		return pp ; 		
	}

	public TermList getTermList() {
		return termList;
	}

	public double getAlpha() {
		return alpha;
	}

	public int getNumOfTopics() {
		return K$numOfTopics;
	}

	public int getNumOfTerms() {
		return V$numOfTerms;
	}

//	public double[][] getTopicTermProb() {
//		return topicTermProb;
//	}
	
	public double getAlphaTopics() {
		return alphaTopics;
	}

	public int getK$numOfTopics() {
		return K$numOfTopics;
	}

	public int getV$numOfTerms() {
		return V$numOfTerms;
	}

	public JsonElement toJson()
	{
		JsonObject job = new JsonObject();
		job.addProperty("alpha", this.alpha);
		job.addProperty("alphaTopics", this.alphaTopics);
		job.addProperty("K$numOfTopics", this.K$numOfTopics);
		job.addProperty("V$numOfTerms", this.V$numOfTerms);
		job.addProperty("numOfLevels", this.numOfLevels);

		JsonElement jterm = this.termList.toJson();
		job.add("termList", jterm);
		
		JsonArray jarr = new JsonArray();
		for (ProbMat pm : this.probMat){
			jarr.add(pm.toJson());
		}
		job.add("probMat", jarr);
		
//		for (double[] top : this.topicTermProb)
//		{
//			JsonArray tmp = new JsonArray();
//			for (double d : top){
//				tmp.add(d);
//			}
//			jarr.add(tmp);
//		}
		
		return job;
	}

	/**
	 * write this object to a json file
	 * 
	 * @param _path = full path of output file (including file name)
	 * @throws IOException
	 */
	public void Persist(String _path) throws IOException{
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(_path));
		JsonWriter writer = new JsonWriter(out);
		writer.setLenient(true);
			
		writer.beginObject();
		writer.name("alpha").value(this.alpha);
		writer.name("alphaTopics").value(this.alphaTopics);
		writer.name("K$numOfTopics").value(this.K$numOfTopics);
		writer.name("V$numOfTerms").value(this.V$numOfTerms);
		writer.name("numOfLevels").value(this.numOfLevels);
		
		writer.name("termList");
		writer.beginObject();
		
		writer.name("maxTerm").value(this.termList.getMaxTerm());
		writer.name("termArray");
		writer.beginArray();
		for (Term term : this.termList.getTermArray()){
			
			writer.beginObject();
			writer.name("text").value(term.getText());
			writer.name("count").value(term.getCount());
			writer.name("docCount").value(term.getDocCount());
			writer.name("docID").value(term.getDocID());
			writer.name("indx").value(term.getIndx());
			writer.endObject();
		}
		writer.endArray();
		writer.endObject();
		
		writer.name("probMats");
		writer.beginArray();
		for (ProbMat pm : this.probMat){
			pm.Persist(writer);
		}
//		for (double[] tops : this.topicTermProb){
//			writer.beginArray();
//			for (double trms : tops){
//				writer.value(trms);
//			}
//			writer.endArray();
//		}
		writer.endArray();

		writer.endObject();
		writer.close();

	}
	
	/**
	 * write this object to a json file
	 * 
	 * @param _file = File object of the containing directory
	 * @param _name = output file name 
	 * @throws IOException
	 */
	public void Persist(File _file, String _name) throws IOException
	{
		String path = _file.getAbsolutePath()+File.separatorChar+_name;
		Persist(path);
	}

}
