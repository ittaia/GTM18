package artzi.gtm.utils.termList;

import java.util.ArrayList;
import java.util.HashMap;

public class Term2Id {
	HashMap <String,Integer> termMap ; 
	ArrayList <String> terms ; 
	public Term2Id () { 
		termMap = new HashMap <> () ; 
		terms = new ArrayList <> () ; 
	}
	public int addTerm (String term) {
		Integer termId = termMap.get(term) ; 
		if (termId == null) { 
			termId = termMap.size(); 
			termMap.put(term, termId) ; 
			terms.add(term) ; 
		}
		return termId ; 		
	}
	public int getTermId (String term) {
		Integer termId = termMap.get(term) ; 
		if (termId != null) return termId ; 
		else return -1 ; 			 		
	}
	public int getSize() {
		return termMap.size(); 
	}
	
	public String getTerm (int termId) { 
		return terms.get (termId) ; 
	}
}