package artzi.gtm.mltm.train;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * probability matrix for all levels
 * 
 * base level = Topic X Term probability matrix
 * any other level = (current level Topic) X (lower level Topic) probability
 * 
 * probMat = double X double probabilities
 * topicName = array of names for current level topics
 *
 */
public class ProbMat {

	private double [][] probMat;
	private String[] topicName;

	/**
	 * constructor with probabilities, without names
	 * (builds empty name array)
	 * 
	 * @param _pm = probability matrix
	 */
	public ProbMat(double[][] _pm){
		this.probMat = _pm;
		this.topicName = new String[_pm.length];
	}
	
	/**
	 * constructor with probabilities and names
	 * 
	 * @param _pm = probability matrix
	 * @param _names = string array of topic names
	 */
	public ProbMat(double[][] _pm, String[] _names){
		this.probMat = _pm;
		this.topicName = _names;
	}
	
	/**
	 * constructor for empty matrix and empty names array
	 * 
	 * @param _tops = number of topics
	 * @param _terms = number of terms
	 */
	public ProbMat(int _tops, int _terms){
		this.probMat = new double[_tops] [_terms];
		this.topicName = new String[_tops];
	}
	
	/**
	 * constructor from a JsonObject
	 * 
	 * @param _job = JsonObject containing matrix and names
	 */
	public ProbMat(JsonElement _je){
		JsonObject job = _je.getAsJsonObject();
		JsonArray tarr = job.get("probMat").getAsJsonArray();
		JsonArray narr = job.get("topicName").getAsJsonArray();
		
		this.probMat = new double[tarr.size()][];
		this.topicName = new String[narr.size()];
		
		for (int i = 0; i < tarr.size(); i++){
			JsonArray jarr = tarr.get(i).getAsJsonArray();
			this.probMat[i] = new double[jarr.size()];
			for (int j = 0; j < jarr.size(); j++){
				this.probMat[i][j] = jarr.get(j).getAsDouble();
			}
		}
		
		for (int n = 0; n < narr.size(); n++){
			this.topicName[n] = narr.get(n).getAsString();
		}
	}
	
	public void setNames(String[] _names){
		if (this.topicName == null){
			this.topicName = new String[_names.length];
		}
		if (this.topicName.length == _names.length){
			this.topicName = _names;
		}
	}
	
	public double[][] getProbMat() {
		return probMat;
	}
	
	
	public String[] getTopicName() {
		return topicName;
	}

	/**
	 * persist probabilities and names 
	 * 
	 * @param writer = JsonWriter to persist on
	 * @throws IOException
	 */
	public void Persist(JsonWriter writer) throws IOException{
		
		writer.name("probMat");
		writer.beginArray();
		
		for (double[] tops : this.probMat){
			writer.beginArray();
			for (double trms : tops){
				writer.value(trms);
			}
			writer.endArray();
		}
		writer.endArray();
		
		writer.name("topicName");
		writer.beginArray();
		for (String name : this.topicName){
			writer.value(name);
		}
		writer.endArray();
	}
	
	/**
	 * build JsonElement from this level
	 * 
	 * @return = JsonElement containing all probabilities and names
	 */
	public JsonElement toJson(){
		JsonObject job = new JsonObject();
		
		JsonArray tarr = new JsonArray();
		for (double[] tops : this.probMat)
		{
			JsonArray tmp = new JsonArray();
			for (double d : tops){
				tmp.add(d);
			}
			tarr.add(tmp);
		}
		
		JsonArray narr = new JsonArray();
		for (String name : this.topicName){
			narr.add(name);
		}

		job.add("probMat", tarr);
		job.add("topicName", narr);
		return job;
	}
}
