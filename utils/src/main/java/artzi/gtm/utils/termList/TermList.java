/*
 * 
 */
package artzi.gtm.utils.termList;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.JsonIO;



/**
 * collection of terms
 * 
 * full term list = collection of all terms in corpus
 * 
 * active term list = only terms that have these two conditions 
 * 		minimum counter = don't add terms with a counter smaller than this
 * 		maximum document counter = don't add terms with a document counter higher than this (DF)
 * 
 * 		lastDocId = keeps ID of last document
 * 		totalDocCount = increases when docId changes
 *
 */
public class TermList {
	private static TermList termList;
	private Hashtable<String, Integer> termHash;
	private ArrayList<Term> termArray;
	private int maxTerm;
	
	private int lastDocId;
	private float totalDocCount;

	private static TermList activeTermList;

	/**
	 * get singleton of term list
	 *  
	 * @return = this term list 
	 */
	public static TermList GetInstance(){
		if (termList == null){
			termList = new TermList();
		}
		return termList;
	}

	/**
	 * initialize term list from json element
	 * 
	 * @param _element = json element
	 * @return = term list
	 */
	public static TermList GetInstance(JsonElement _element){
		fromJson(_element);
		return termList;
	}

	/**
	 * initialize term list from a json file
	 * @param _path
	 * @return
	 * @throws IOException 
	 */
	public static TermList GetInstance(String _path) throws IOException{
		fromFile(_path);
		return termList;
	}
	
	/**
	 * initialize term list from an array of terms
	 * 
	 * @param _terms = array list of terms
	 * @return
	 */
	public static TermList GetInstance(ArrayList<Term> _terms){
		fromArrayList(_terms);
		return termList;
	}
	/**
	 * create active term list from full term list
	 * 
	 * @param _minCount = minimum count of term in corpus
	 * @param _maxDocFrequency = maximum frequency (fraction) of documents containing this word
	 * 
	 * @return = active term list
	 */
	@SuppressWarnings("static-access")
	public TermList initActive(double _minCount, double _maxDocFrequency){
		if (this.activeTermList != null){
			return this.activeTermList;
		}
		this.activeTermList = new TermList();
		
		for (Term term : this.termArray){
			
			float termDocCount = term.getDocCount();
			float termdf = (termDocCount / totalDocCount);
			
			if (term.getCount() < _minCount || termdf > _maxDocFrequency){
				continue;
			}
			this.activeTermList.termHash.put(term.getText(), this.activeTermList.maxTerm);
			Term acTerm = new Term(term, this.activeTermList.maxTerm);
			this.activeTermList.maxTerm++;
			this.activeTermList.termArray.add(acTerm);
		}
		EL.W( "Terms: " + termArray.size() +"  Active: " + activeTermList.getSize());
		return this.activeTermList;
	}

	/**
	 * add a term to full term list
	 * 
	 * if docId changes - add 1 to total doc count
	 * 
	 * if term list contains this word - add to counters in term and return term's index
	 * if a new term - add a new term using maxTerm counter
	 * 
	 * @param term = term to add
	 * @param docID = current document ID
	 * 
	 * @return = index of term in term list
	 */
	public int addTerm (String term , int docID )  {

		if (this.lastDocId != docID){
			this.lastDocId = docID;
			this.totalDocCount++;
		}
		
		if (termHash.containsKey(term)){
			int termIndx = termHash.get(term);
			Term trm = termArray.get(termIndx);
			trm.addCount(docID);
			return termHash.get(term);
		}

		Term trm = new Term(term, docID, maxTerm);
		termHash.put(term, maxTerm);
		termArray.add(trm);
		maxTerm++;
		return maxTerm  ; 
	}

	/**
	 * create a json element from this term list
	 * 
	 * @return = json element
	 */
	public JsonElement toJson(){
		JsonObject jo = new JsonObject();
		jo.addProperty("maxTerm", this.maxTerm);
		jo.addProperty("lastDocId", this.lastDocId);
		jo.addProperty("totalDocCount", this.totalDocCount);

		JsonArray jt = new JsonArray();
		for (Term term : this.termArray){
			jt.add(term.toJson());
		}
		jo.add("termArray", jt);
		return jo;

	}
	/**
	 * save  term list  as json file 
	 * 
	 * @param _path = full path of file
	 * @throws IOException 
	 *  
	 */
	public void   toFile(String _path) throws IOException{
		JsonElement jo = this.toJson() ; 
		JsonIO.write(_path , jo);
	}

	/**
	 * initialize a new term list
	 */
	private TermList(){
		termHash = new Hashtable<String, Integer>();
		termArray = new ArrayList<Term>();
		maxTerm = 0;
		lastDocId = -1;
		totalDocCount = 0;
	}

	/**
	 * create a term list from a json element
	 * 
	 * @param _element = json element
	 */
	private static void fromJson(JsonElement _element){
		termList = new TermList();

		JsonObject jo = _element.getAsJsonObject();
		JsonArray tarr = jo.get("termArray").getAsJsonArray();
		for (int i = 0; i < tarr.size(); i++){
			Term term = new Term(tarr.get(i));
			termList.termArray.add(term);
			termList.termHash.put(term.getText(), term.getIndx());
		}

		termList.maxTerm = jo.get("maxTerm").getAsInt();
		termList.lastDocId = jo.get("lastDocId").getAsInt();
		termList.totalDocCount = jo.get("totalDocCount").getAsFloat();
	}

	/**
	 * create a term list from an array of terms
	 * 
	 * @param _terms = array list of terms
	 */
	private static void fromArrayList(ArrayList<Term> _terms){
		
		termList = new TermList();
		
		termList.lastDocId = -1;
		termList.totalDocCount = 0;
		
		for (Term term : _terms){
			termList.termArray.add(term);
			termList.termHash.put(term.getText(), term.getIndx());
			
			if (term.getDocID() != termList.lastDocId){
				termList.lastDocId = term.getDocID();
				termList.totalDocCount++;
			}
		}
		termList.maxTerm = _terms.size() - 1;
	}
	
	/**
	 * create term list from a json file
	 * 
	 * @param _path = full path of file
	 * @throws IOException 
	 */
	private static void fromFile(String _path) throws IOException{
		termList = new TermList();
		String name = "";
		JsonReader reader = new JsonReader(new FileReader(_path));
		reader.setLenient(true);
		JsonToken token;
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
				if (name.equals("maxTerm")){
					termList.maxTerm = reader.nextInt();
					continue;
				}
				if (name.equals("lastDocId")){
					termList.lastDocId = reader.nextInt();
					continue;
				}
				if (name.equals("totalDocCount")){
					termList.totalDocCount = (float) reader.nextDouble();
				}
				
				if (name.equals("termArray")){
					JsonObject jo = null;
					token = reader.peek();
					if (token.equals(JsonToken.BEGIN_ARRAY)){
						reader.beginArray();

						while (reader.hasNext()){
							
							token = reader.peek();
							if (token.equals(JsonToken.END_ARRAY)){
								reader.endArray();
								break;
							}
							if (token.equals(JsonToken.BEGIN_OBJECT)){
								jo = new JsonObject();
								reader.beginObject();
								continue;
							}
//							if (token.equals(JsonToken.END_OBJECT)){
//								Term trm = new Term(jo);
//								termList.termArray.add(trm);
//								termList.termHash.put(trm.getText(), trm.getIndx());
//								reader.endObject();
//								continue;
//							}

							if (token.equals(JsonToken.NAME)){
								name = reader.nextName();
								if (name.equals("text")){
									jo.addProperty(name, reader.nextString());
									continue;
								}
								if (name.equals("count")){
									jo.addProperty(name, reader.nextInt());
									continue;
								}
								if (name.equals("docCount")){
									jo.addProperty(name, reader.nextInt());
									continue;
								}
								if (name.equals("docID")){
									jo.addProperty(name, reader.nextInt());
									continue;
								}
								if (name.equals("indx")){
									jo.addProperty(name, reader.nextInt());
									Term trm = new Term(jo);
									termList.termArray.add(trm);
									termList.termHash.put(trm.getText(), trm.getIndx());
									reader.endObject();
									continue;
								}
							} // name in term
						} //has next term array
						
					} // begin array
				} // name = "termArray"
			} // token name
		} //has next
		reader.close();
	}

	public String getTerm(int _index){
		return termArray.get(_index).getText();
	}
	public int getTermIndx (String _term) { 
		Integer termIndx  = termHash.get(_term);
		if (termIndx == null )
			return -1 ;   
		else
			return termIndx ; 
	}
	public Hashtable<String, Integer> getTermHash() {
		return termHash;
	}
	public ArrayList<Term> getTermArray() {
		return termArray;
	}
	public int getMaxTerm() {
		return maxTerm;
	}
	public int getSize(){
		return termArray.size();
	}

	public float getTotalDocCount() {
		return totalDocCount;
	}
	
}
