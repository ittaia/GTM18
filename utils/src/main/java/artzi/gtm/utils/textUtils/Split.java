/*
 * 
 */
package artzi.gtm.utils.textUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * split text to words / tokens
 * words = only words that are not stop-words and are real words
 * tokens = everything regardless of contents
 *
 */
public class Split {
	
	/**
	 * split text to an array of words - only real words and not stop-words
	 * if word ends with punctuation marks ",.;:" remove these characters
	 * @param str = text to split
	 * @return = array list of words
	 * @throws Exception 
	 */
	public static ArrayList <String> Str2WordList (String str)  { 
		ArrayList <String> rList = new ArrayList <String> () ; 
		ArrayList <String> w1 =  split(str) ; 
		for (String w : w1) {
			/*
			w = w.trim();
			char c = w.charAt(w.length() - 1);
			if (c == ',' || c == '.' || c == ';' || c == ':'){
				w = w.substring(0, w.length() - 1);
			}
			*/
			if ((!StopWords.isStopWord(w)) & (StopWords.isWord(w))) {
				rList.add(w) ; 
			}
		}
		return rList ; 
	}

	/**
	 * split text to an array of tokens and return everything
	 * 
	 * @param str = text to split
	 * @return = array list of tokens
	 */
	public static ArrayList <String> Str2Tokens (String str) { 
		ArrayList <String> rList = split(str) ; 
 
		return rList ; 
	}
	
	/**
	 * split text using regex
	 * 
	 * @param str = text to split
	 * @return = array list of tokens
	 */
	public static ArrayList <String> split (String str) { 
		ArrayList <String> rList = new ArrayList <String> () ; 

		final String token  = "[^\\s][^\\s]*[\\s]" ; 		 
		final Pattern ps = Pattern.compile(token) ; 
		
		Matcher m = ps.matcher(str+" ") ; 
		while (m.find() ) { 
			String g = m.group() ; 
			String t = g.substring(0,g.length()-1) ; 
			rList.add(t) ; 			
		}
		return rList ; 
		
	}

}
