package artzi.gtm.utils.textUtils;

import java.util.ArrayList;


public class NameHead {
	public static String getHead(String val) {
		ArrayList <String> tokens = Split.split (val) ; 
		int retToken = tokens.size() -1 ; 
		for (int i = 1 ; i < tokens.size () ; i ++  ) { 
			if (prepWord (tokens.get(i))) { 
				retToken = i-1 ; 
				break ; 
			}			
		}
		 
		if (badWord (tokens.get(retToken)) && (retToken > 0)) { 				
			retToken -- ; 
			// EL.WE (1122 , " Fix head " + val + " --> " +  tokens.get (retToken)) ; 
		}
		return tokens.get (retToken) ;  
	}

	private static boolean badWord(String token) {
		if (token.length() <=1) return true ;  
		if (token.startsWith ("'")) return true ; 
		return false;
	}

	private static boolean prepWord(String token) {
		if (token.equalsIgnoreCase("of") ) return true ; 
		return false;
	}
}
