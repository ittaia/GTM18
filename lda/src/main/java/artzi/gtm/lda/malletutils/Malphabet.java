package artzi.gtm.lda.malletutils;

import java.io.Serializable;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.termList.Term;
import artzi.gtm.utils.termList.TermList;
import cc.mallet.types.Alphabet;

public class Malphabet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Alphabet alphabet ; 
	public Malphabet () { 
		alphabet = new Alphabet () ; 
	}
	public void init (TermList terms) throws Exception { 
		for (Term term :terms.getTermArray()) { 
			int i = alphabet.lookupIndex(term.getText(), true) ; 
			if (i != term.getIndx()) { 
				EL.WF(1 , "Error in init alphabet" );  
			}
		}
	}
	public int add (String word) { 
		int i = alphabet.lookupIndex(word, true) ; 
		return i ; 
	}
	public int getKey (String word) { 
		int key = alphabet.lookupIndex(word, false) ; 
		return (key) ; 
	}	
	public int getSize () { 
		return alphabet.size () ; 
	}
	public String getText(int key) {
		return alphabet.lookupObject(key).toString() ; 
	}
	public Alphabet getAlphabet() {
		return this.alphabet ; 
	}
	public String [] getTermArray () { 
		String []  termArray = new String [alphabet.size()] ;
		Object [] array = alphabet.toArray() ; 
		for (int i = 0 ; i < alphabet.size() ; i ++ ) { 
			termArray [i] = (String) array [i] ; 
		}
		return termArray ; 
		
	}
}
