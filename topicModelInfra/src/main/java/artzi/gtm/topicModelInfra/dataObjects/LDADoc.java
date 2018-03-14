package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.termList.TermList;
import artzi.gtm.utils.textUtils.Split;

public class LDADoc implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int docID ; 
	String header ; 
	int numOfWords ; 
	int [] wordArray ; 
	ArrayList <Integer> termIds ; 
	String name ; 
	public LDADoc (int docID , String name , String header ,  String words , TermList terms)   { 
		this.docID= docID ; 
		this.name = name ; 
		this.header = header ; 
		ArrayList <String> textWords = Split.Str2WordList (words) ; 
		termIds = new ArrayList <> () ; 
		for (String w:textWords) {
			int termId = terms.addTerm (w ,docID ) ; 
			termIds.add(termId) ; 
		}		 
	}
	public void initWordVector (TermList termList) { 
		int [] wordVecTemp = new int [termIds.size()]  ; 
		numOfWords = 0 ; 
		for (int termId : termIds)  { 
			int activeTermIndx = termList.getActiveTermIndx(termId)  ; 
			if (activeTermIndx > -1) { 
				wordVecTemp[numOfWords] = activeTermIndx ; 
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
	public void print(TermList activeTerms) { 
		EL.W ("DOC Name " + name) ; 
		String s = "" ;   
		for (int i = 0 ; i < numOfWords ; i ++ ) {
			s += " " +  i + "- Term -" + wordArray [i] +  activeTerms.getTerm(wordArray[i]) ; 
		}
		EL.W (s) ; 
	}
	public boolean isEmpty () { 
		return (numOfWords == 0) ; 					
	}
} 