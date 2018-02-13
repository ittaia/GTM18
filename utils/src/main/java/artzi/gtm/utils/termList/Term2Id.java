package artzi.gtm.utils.termList;

import java.util.HashMap;

public class Term2Id {
	HashMap <String,Integer> termMap ; 
	public Term2Id () { 
		termMap = new HashMap <> () ; 
	}
	public int addTerm (String term) {
		Integer termId = termMap.get(term) ; 
		if (termId == null) { 
			termId = termMap.size(); 
			termMap.put(term, termId) ; 
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
}