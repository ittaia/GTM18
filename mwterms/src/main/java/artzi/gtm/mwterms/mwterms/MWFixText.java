package artzi.gtm.mwterms.mwterms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class MWFixText {

	static MWFixText mwFixText ; 
	MWTermDictionary dictionary ; 
	private MWFixText (String path) throws IOException { 
		dictionary = new MWTermDictionary () ; 
		loadJson (path) ; 
	}
	private void loadJson(String file) throws IOException {
		Gson gson = new Gson () ; 
		BufferedReader in = new BufferedReader(new FileReader(file)); 
		String rec ;
		while ((rec = in.readLine())!= null) {
			MWTerm term = gson.fromJson(rec , MWTerm.class) ;  
			if (term.getCVal() >= MWParms.minCValDic) dictionary.add(term.getTerms());				 
		}
		in.close() ;
	}
}
