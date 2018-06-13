package artzi.gtm.mwterms.mwterms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class  MWTermDictionary{
	ArrayList <String [] > termDic ; 
	Hashtable <String , HashSet <Integer> > tokenKeyHash ; 
	
	public MWTermDictionary () { 
		termDic = new ArrayList <String []> () ; 
		tokenKeyHash = new Hashtable <String , HashSet <Integer> > () ; 
	}
	
	public void add (String [] termTokens ) { 
		termDic.add(termTokens); 
		int key = termDic.size()-1 ; 
		for (int i = 0 ; i < termTokens.length ; i ++ ) { 
			String tokenKey = getKey (i, termTokens [i]) ; 
			HashSet<Integer> hash = tokenKeyHash.get(tokenKey) ; 
			if (hash == null ) {
				hash = new HashSet  <Integer> () ; 				
			}
			hash.add(key) ; 
			tokenKeyHash.put(tokenKey, hash) ; 			
		}		
	}

	private String getKey(int i, String string) {
		String r = i + string ; 
		return r ; 
	}
	public int findMaxLengthMatch (String [] termTokens) { 
		int tokenIndx = 0 ; 
		String tokenKey = getKey (0, termTokens [0]) ; 
		HashSet<Integer> hash = tokenKeyHash.get(tokenKey) ;
		ArrayList < HashSet<Integer> > matchSets= new ArrayList <HashSet<Integer>> () ; 
		if (hash != null ) {
			HashSet<Integer> match = (HashSet<Integer>) hash.clone () ; 
			matchSets .add (match) ; 
			for (tokenIndx = 1 ; tokenIndx <  termTokens.length ; tokenIndx ++ ) { 
				tokenKey = getKey (tokenIndx, termTokens [tokenIndx]) ; 
				hash = tokenKeyHash.get(tokenKey) ;
				if (hash == null) break ; 
				HashSet<Integer> match1 = (HashSet<Integer>)matchSets.get(matchSets.size()-1).clone () ; 
				match1.retainAll(hash) ; 
				if (match1.size() == 0 ) break ; 
				matchSets.add(match1) ; 
				 
			}
		}
		boolean foundMatch = false ; 
		int matchLength = 0; 
		matchLength: 		 
		for (int matchIndx = matchSets.size ()-1  ; matchIndx > 0 ; matchIndx -- ) { 
			Iterator<Integer> keys= matchSets.get(matchIndx).iterator() ; 
			matchLength = matchIndx + 1 ; 
			while (keys.hasNext() ) { 
				int key = keys.next() ;  
				if (termDic.get(key).length ==  matchLength ) { 
					foundMatch = true ; 
					break matchLength ; 
				}
			}
		}
		if (foundMatch) return matchLength ;
		else return 0 ; 		 
	}

	public void print() {
		for (String [] s  : termDic) { 
			String p = "" ; 
			for (String a:s) {
				p += a ;
			}
			System.out.println(p) ; 
		}		
	}
}