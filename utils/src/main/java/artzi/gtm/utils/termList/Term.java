package artzi.gtm.utils.termList;


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
	private int activeTermIndx ; 

 /**
  * initial constructor
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
		this.activeTermIndx = -1 ; 
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
		this.activeTermIndx = -1 ; 

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
	public int getActiveTermIndx () {return this.activeTermIndx ; }
	
	public void setActiveTermIndx (int activeTermIndx) { 
		this.activeTermIndx = activeTermIndx ; 
	}
	
	

	@Override
	public String toString() {
		String str = this.text + ", count="+this.count + ", doc count="+this.docCount + ", index="+this.indx  + 
				", activeIndx=" +  this.activeTermIndx ;
		return str  ;

	}
}