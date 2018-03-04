package artzi.gtm.mltm.train;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

//import sortProb.CompareProb;
//import sortProb.IndxProb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;


/**
 * 
 * persist probabilities after training model
 *
 */
public class DocTopicProb {
	private double [][] docTopicProb ;
	private int M$numOfDocs ; 
	private int K$numOfTopics ; 
	
	/**
	 * initial constructor after training 
	 * 
	 * @param _docTopicProb = result document-topics probabilities
	 * (save documents number and topics number for json constructor)
	 */
	public DocTopicProb(double [][] _docTopicProb){
		this.docTopicProb = _docTopicProb;
		this.M$numOfDocs = this.docTopicProb.length;
		this.K$numOfTopics = this.docTopicProb[0].length;
	}
	
	/**
	 * constructor from a json element
	 * 
	 * @param _je = json element from persisted file
	 */
	public DocTopicProb(JsonElement _je){
		JsonObject jo = _je.getAsJsonObject();
		this.M$numOfDocs = jo.get("M$numOfDocs").getAsInt();
		this.K$numOfTopics = jo.get("K$numOfTopics").getAsInt();
		this.docTopicProb = new double[M$numOfDocs][K$numOfTopics];
		JsonArray jart = jo.get("docTopicProb").getAsJsonArray();
		for (int t = 0; t < jart.size(); t++){
			JsonArray jarp = jart.get(t).getAsJsonArray();
			for (int p = 0; p < jarp.size(); p++){
				this.docTopicProb[t][p] = jarp.get(p).getAsDouble();
			}
		}
	}
	/**
	 * constructor from a json file
	 * 
	 * @param _path = full path of input json file
	 * @throws IOException 
	 */
	public DocTopicProb(String _path) throws IOException{
		JsonReader reader = new JsonReader(new FileReader(_path));
		reader.setLenient(true);
		JsonToken token;
		while(reader.hasNext()){
			token = reader.peek();
			String name="";
			if (token.equals(JsonToken.END_DOCUMENT)){
				break;
			}
			if (token.equals(JsonToken.BEGIN_OBJECT)){
				reader.beginObject();
				continue;
			}
			if (token.equals(JsonToken.NAME)){
				name = reader.nextName(); 
				if (name.equals("M$numOfDocs")){
					this.M$numOfDocs = reader.nextInt();
					continue;
				}
				if (name.equals("K$numOfTopics")){
					this.K$numOfTopics = reader.nextInt();
					continue;
				}
				if (name.equals("docTopicProb")){
					this.docTopicProb = new double[this.M$numOfDocs][this.K$numOfTopics];
					reader.beginArray();
					int docNum = 0;
					while (reader.hasNext()){
						reader.beginArray();
						int topNum = 0;
						while (reader.hasNext()){
							if (docNum <= this.M$numOfDocs && topNum <= this.K$numOfTopics){
								this.docTopicProb[docNum][topNum] = reader.nextDouble();
							}
							topNum++;
						}
						reader.endArray();
						docNum++;
					}
					reader.endArray();
					continue;
				}

			} // main object

		} // has next
		reader.close();
	}
	
	public IndxProb [] getTopicDocs (int topicIndx) {
		IndxProb [] termProbArray  = new IndxProb [this.M$numOfDocs] ; 
		for (int docIndx = 0 ; docIndx < this.M$numOfDocs ; docIndx ++ ) {
			termProbArray [docIndx] = new IndxProb (docIndx,docTopicProb [docIndx][topicIndx]) ;   
		}			
		Arrays.sort ( termProbArray  ,  new CompareProb()) ;
		return termProbArray ; 
	}

	/**
	 * save current object in a json file
	 * 
	 * @param _path = full path of file
	 * @throws IOException
	 */
	public void Persist(String _path) throws IOException{
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(_path));
		JsonWriter writer = new JsonWriter(out);
		writer.setLenient(true);
		writer.beginObject();
		
		writer.name("M$numOfDocs").value(this.M$numOfDocs);
		writer.name("K$numOfTopics").value(this.K$numOfTopics);
		
		writer.name("docTopicProb");
		writer.beginArray();

		for (double[] dt : this.docTopicProb){
			writer.beginArray();
			for (double dp : dt){
				writer.value(dp);
			}
			writer.endArray();
		}
		writer.endArray();
		writer.endObject();
		writer.close();
	}
	
	/**
	 * save current object in a json file
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

	/**
	 * static method to convert DocTopicProb object to a json element
	 * 
	 * @param _dtp = double[][] from DocTopicProb object
	 * @return = json element
	 */
	public static JsonElement toJson(double[][] _dtp){
		JsonObject jo = new JsonObject();
		jo.addProperty("M$numOfDocs", _dtp.length);
		jo.addProperty("K$numOfTopics", _dtp[0].length);
		JsonArray jart = new JsonArray();
		JsonArray jarp = new JsonArray();
		for (double[] dt : _dtp){
			for (double dp : dt){
				jarp.add(dp);
			}
			jart.add(jarp);
		}
		jo.add("docTopicProb", jart);
		return jo;
	}

	public double[][] getDocTopicProb() {
		return docTopicProb;
	}

	public int getM$numOfDocs() {
		return M$numOfDocs;
	}

	public int getK$numOfTopics() {
		return K$numOfTopics;
	}
	
	
}
