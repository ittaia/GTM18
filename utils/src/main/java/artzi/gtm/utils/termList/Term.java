package artzi.gtm.utils.termList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * word object
 * 
 * text = word
 * count = total count of word in corpus
 * docCount = number of documents containing this word
 * docID = last document ID containing this word
 * indx = index of this term in full TermList or in ActiveTermList
 *
 */
public class Term {
	private String text ; 
	private int count ; 
	private int docCount ; 
	private int docID ;
	private int indx  ;

 /**
  * initial constructor
  * 
  * @param _text = word
  * @param _docId = ID of current document
  * @param _indx = index of current term
  */
 public Term(String _text, int _docId, int _indx){
		this.docID = _docId ; 
		this.text = _text ; 
		this.count = 1 ; 
		this.docCount = 1 ; 
		this.indx = _indx ; 
 }
 /**
  * constructor from a json element
  * @param je = json
  */
 public Term(JsonElement je){
	 JsonObject jo = je.getAsJsonObject();
		this.docID = jo.get("docID").getAsInt(); 
		this.text = jo.get("text").getAsString() ; 
		this.count = jo.get("count").getAsInt() ; 
		this.docCount = jo.get("docCount").getAsInt() ; 
		this.indx = jo.get("indx").getAsInt() ; 
 }
 
 /**
  * constructor for active terms
  * @param _oldterm = term from full term list
  * @param _newindex = new index for active term list
  */
 public Term(Term _oldterm, int _newindex){
		this.text = _oldterm.text ; 
		this.count = _oldterm.count ; 
		this.docCount = _oldterm.docCount ; 
		this.docID = _oldterm.docID ;
		this.indx = _newindex  ;

 }
 /**
  * manage term counters
  * add to total number in corpus
  * if parameter docID no equal last docID in term, add to number of documents
  * 
  * @param docID = document ID of new term
  */
	public void addCount (int docID) {
		this.count ++ ;
		if (docID  != this.docID) {docCount ++ ; }
		this.docID = docID ; 
	}
	
	public String getText () {return this.text ; }
	public int getCount () {return this.count ; }
	public int getDocID () {return this.docID ; }
	public int getDocCount() { return this.docCount ; } 
	public int getIndx ( ) {return this.indx ; } 
	
	/**
	 * get json element of this term
	 * 
	 * @return = JsonElement
	 */
	public JsonElement toJson(){
		JsonObject jo = new JsonObject();
		jo.addProperty("text", this.text);
		jo.addProperty("count", this.count);
		jo.addProperty("docCount", this.docCount);
		jo.addProperty("docID", this.docID);
		jo.addProperty("indx", this.indx);
		
		JsonElement je = jo;
		return je;
	}

	@Override
	public String toString() {
		String str = this.text + ", count="+this.count + ", doc count="+this.docCount + ", index="+this.indx;
		return str  ;

	}
}