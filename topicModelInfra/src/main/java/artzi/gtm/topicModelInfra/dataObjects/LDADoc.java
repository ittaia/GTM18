package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.termList.TermList;
import artzi.gtm.utils.textUtils.Split;


public class LDADoc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int docID ; 
	String header ; 
	int numOfWords ; 
	int [] wordArray ; 
	ArrayList <String> textWords ; 
	String name ; 
	public LDADoc (int docID , String name , String header ,  String words , TermList terms) throws Exception  { 
		this.docID= docID ; 
		this.name = name ; 
		this.header = header ; 
		textWords = Split.Str2WordList (words) ; 		
		for (String w:textWords) {
			terms.addTerm (w ,docID ) ; 
		}		 
	}
	public void initWordVector (TermList terms) { 
		int [] wordVecTemp = new int [textWords.size()]  ; 
		numOfWords = 0 ; 
		for (String word : textWords) { 
			int termIndx = terms.getTermIndx(word) ; 
			if (termIndx > -1) { 
				wordVecTemp[numOfWords] = termIndx ; 
				numOfWords ++ ; 
			}
		}
		if (numOfWords > 0) {  
			wordArray = new int [numOfWords]  ; 
			
			for (int i = 0 ; i < numOfWords ; i ++  ) { 
				wordArray [i] = wordVecTemp [i] ;  
			}
		}
	}
	
	public String getHeader () { 
		return this.header ; 
	}
	public String getName() {
		return name;
	}
	public int getDocID() {
		return docID ; 
	}
	public int [] getWordArray () { 
		
		return this.wordArray ; 
	}
	public void print(TermList terms) { 
		EL.W ("DOC Name " + name) ; 
		String s = "" ;   
		for (int i = 0 ; i < numOfWords ; i ++ ) {
			s += " " +  i + "- Term -" + wordArray [i] +  terms.getTerm(wordArray[i]) ; 
		}
		EL.W (s) ; 
	}
	public boolean isEmpty () { 
		return (numOfWords == 0) ; 					
	}
} 