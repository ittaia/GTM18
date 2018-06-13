package artzi.gtm.mwterms.mwterms;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import com.google.gson.Gson;

public class MWTerms {	 

	ArrayList <MWTerm> mwTermList ;
	Hashtable <String  , Integer> mwTermHash ; 
	Hashtable <String , HashSet <Integer> > tokenKeyHash ; 
	ArrayList <ArrayList <Integer> > termLenLists ;
	ArrayList <Integer> candidateTermList ; 
	
	public MWTerms () { 
		mwTermList = new ArrayList <MWTerm> () ; 
		mwTermHash = new Hashtable <String  , Integer> () ; 
		tokenKeyHash = new Hashtable <String , HashSet <Integer> > () ; 
		termLenLists = new 	ArrayList <ArrayList <Integer> > () ; 
		for (int l = 0 ; l <= MWParms.maxTermLen ; l ++ ) {
			termLenLists.add(new ArrayList <Integer> ()) ; 			
		}
		candidateTermList = new ArrayList <Integer> () ; 
	}
	public void addWordList (ArrayList <String> wordList) { 
		String [] words = new String [wordList.size()] ; 
		for (int i= 0 ; i < words.length ; i ++  ) words[i] = wordList.get(i) ; 
		ArrayList <String []> subTerms = MWTerms.getSubStrings(words, MWParms.minTermLen) ; 
		if (words.length < MWParms.maxTermLen) 	addMWTerm(words , null) ;
		for (String [] subTerm: subTerms) { 
			addMWTerm(subTerm , null) ;			
		}	
	}
	
	
	public int addMWTerm (String [] termTokens ,  String [] tags) { 
		int termKey ; 
		String sKey = "" ; 
		for (String t: termTokens) sKey+= t+" " ; 
		Integer key = mwTermHash.get(sKey) ; 
		if (key != null) {
			termKey = key ; 
		}
		else { 
			termKey = addNewMWTerm (termTokens , sKey) ; 
		}
		mwTermList.get(termKey).addCount() ;  
		return termKey ; 
	}
			
	private int addNewMWTerm (String[] termTokens, String sKey) {
		MWTerm mwTerm = new MWTerm (termTokens) ; 
		mwTermList.add(mwTerm) ; 
		int termKey  = mwTermList.size()-1 ; 
		mwTermHash.put(sKey , termKey ) ; 
		termLenLists.get (mwTerm.getLen()).add(termKey) ;  
		
		return termKey ; 
	}
	public void initCandidateTerms () { 
		for (int termLen = MWParms.maxTermLen ; termLen >= MWParms.minTermLen ; termLen --) { 
			for (int termKey : termLenLists.get(termLen))  { 
				MWTerm mwTerm = mwTermList.get(termKey) ; 
				if (mwTerm.getCVal () > MWParms.minCVal ) { 
					candidateTermList.add(termKey) ;
					reviseSubStrings (mwTerm) ; 
				}			 
			}
		}
		candidateTermList.sort ((Integer i1, Integer i2) -> 
								(int) Math.signum(mwTermList.get(i2).getCVal() - mwTermList.get(i1).getCVal() ) ) ; 
	}
		
	private void reviseSubStrings(MWTerm mwTerm) {
		ArrayList <String []> subStrings = getSubStrings (mwTerm.getTerms () , MWParms.minTermLen) ; 
		for (String [] termTokens : subStrings) { 
			String sKey = "" ; 
			for (String t: termTokens) sKey+= t+" " ; 
			Integer key = mwTermHash.get(sKey) ; 
			if (key != null) {
				MWTerm subTerm = mwTermList.get(key) ; 
				subTerm.addUpperTermsCount() ; 
				subTerm.updateNestedCount (mwTerm.getTotCount() - mwTerm.getNestedCount()) ; 				
			}			
		}
	} 

	public static ArrayList<String[]> getSubStrings( String [] terms,  int minLen) {
		ArrayList <String [] > rList = new ArrayList <String [] > () ; 		
		for (int len = minLen ; len  < Math.min (terms.length , MWParms.maxTermLen)  ; len ++) { 
			addSubStrings (rList , terms ,     len) ;
		}
		return rList ; 
	}		

	private static void addSubStrings(ArrayList <String [] > rList,   String[] terms,     int len) {
		for (int start = 0 ; start < terms.length -len + 1 ; start++) {  
			String [] subStringTerms = new String [len] ; 
			for (int i = 0 ; i < len ; i ++ ) { 
				subStringTerms [i] = terms [start+i] ;   
			}
			rList.add(subStringTerms) ; 			 				
		}
	}
	public ArrayList <Integer> getTerms () { 
		return candidateTermList ; 		
	}
	public void toJson (String file) {
		Gson gson = new Gson () ; 
		FileWriter fstream ; 
		BufferedWriter out ;
		try {
			fstream = new FileWriter(file);
			out = new BufferedWriter(fstream); 
			
			for (int termKey : candidateTermList) { 
				String s  = gson.toJson(mwTermList.get(termKey)) ;  
				out.write(s) ; 
				out.newLine() ; 
			}
			out.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}
	public MWTerm  getTerm(int termKey) {
		return mwTermList.get (termKey) ;  
	}
	public String  get(int termKey) {
		String [] terms = mwTermList.get(termKey).getTerms() ; 
		String s = "" ;  
		for (String term:terms) s += term + " " ;  
		return s ; 
	}
}