package artzi.gtm.utils.termList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import com.google.gson.Gson;
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
 */

public class TermList {
	private static TermList termList;	
	private Hashtable<String, Integer> termHash;
	private ArrayList<Term> termArray;
	private int maxTerm;
	private int lastDocId;
	private float totalDocCount;
	private  TermList activeTermList = null;

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
		int newTermId = maxTerm ; 

		Term trm = new Term(term, docID, newTermId);
		termHash.put(term, newTermId);
		termArray.add(trm);
		maxTerm++;
		return newTermId ;  
	}


	/**
	 * create active term list from full term list	 * 
	 * @param _minCount = minimum count of term in corpus
	 * @param _maxDocFrequency = maximum frequency (fraction) of documents containing this word	 * 
	 * @return = active term list
	 */


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
			

			int activeTermIndx =  this.activeTermList.maxTerm ; 
			this.activeTermList.termHash.put(term.getText(), activeTermIndx);
			Term acTerm = new Term(term, activeTermIndx);
			term.setActiveTermIndx(activeTermIndx);			
			this.activeTermList.maxTerm++;
			this.activeTermList.termArray.add(acTerm);
		}
		EL.W( "Terms: " + termArray.size() +"  Active: " + activeTermList.getSize());
		return this.activeTermList;
	}	

	/**
	 * save  term list  as json file 
	 * 
	 * @param _path = full path of file
	 * @throws IOException   
	 */
	public void   toFile(String _path) throws IOException{
		Gson gson = new Gson () ; 
		String str = gson.toJson (this , this.getClass()) ; 
		JsonIO.write(_path , str);
	}	

	public String getTerm(int _index){
		return termArray.get(_index).getText();
	}
	public double  getTermDC(int _index){
		return termArray.get(_index).getDocCount() ; 
	}
	public int getActiveTermIndx(int _index){
		return termArray.get(_index).getActiveTermIndx() ; 
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