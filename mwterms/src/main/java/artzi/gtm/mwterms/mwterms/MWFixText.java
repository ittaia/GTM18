package artzi.gtm.mwterms.mwterms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import artzi.gtm.utils.elog.EL;

public class MWFixText {

	static MWFixText mwFixText ; 
	MWTermDictionary dictionary ; 
	private MWFixText (String path) throws IOException { 
		dictionary = new MWTermDictionary () ; 
		loadJson (path) ; 
	}
	private void loadJson(String path) throws IOException {
		Gson gson = new Gson () ; 
		BufferedReader in = new BufferedReader(new FileReader(path)); 
		String rec ;
		while ((rec = in.readLine())!= null) {
			MWTerm term = gson.fromJson(rec , MWTerm.class) ;  
			if (term.getCVal() >= MWParms.minCValDic) dictionary.add(term.getTerms());				 
		}
		in.close() ;		
	} 
	
	public static MWFixText getInstance (String path) throws IOException { 
		if (mwFixText != null) return mwFixText ; 
		mwFixText = new MWFixText (path) ; 
		return mwFixText ; 
	}
	
	public ArrayList <String> fix (ArrayList <String> tokens ) { 
		ArrayList <String> rTokens = new ArrayList <> () ; 
		int startToken = 0 ; 
		while (startToken < tokens.size()  ) { 
			int lenMatch = dictionary.findMaxLengthMatch(tokens, startToken) ; 
			if (lenMatch > 0) { 				
				String matchTerm = tokens.get (startToken) ; 
				for (int t = startToken + 1 ; t < startToken + lenMatch ; t ++ ) matchTerm += "_" + tokens.get(t) ; 
				rTokens.add(matchTerm) ; 
				startToken += lenMatch ; 
				
			}
			else { 
				rTokens.add(tokens.get(startToken)) ; 
				startToken += 1 ; 
			}
		}
		return rTokens ;			
	}
}