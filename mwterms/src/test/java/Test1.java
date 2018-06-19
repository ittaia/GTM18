

import java.io.IOException;
import java.util.ArrayList;

import artzi.gtm.mwterms.mwterms.MWParms;
import artzi.gtm.mwterms.mwterms.MWTerms;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;

public class Test1 {
	
	static String path = "C:\\TestDir\\MWTerms" ;  

	static MWTerms mwTerms ; 
	static Config config ;
	public static void main(String[] args) throws IOException {
		config = Config.getInstance(path) ; 
		mwTerms = new MWTerms() ; 
		EL.W("Start") ; 
		 
		String [] s1 = {"a" , "b" , "c" } ; 
		String []s2 = {"a" , "b" , "d" } ; 		  
		String []s3 = {"a" , "b" , "e" } ; 
		for (int i = 0 ; i < 10 ; i ++ ) { 
			add (s1) ; 
			add(s2) ; 
			add(s3) ;
		}
		mwTerms.initCandidateTerms() ; 
		
		String jsonPath = config.getPath("Json") ; 
		mwTerms.toJson (jsonPath) ;
		EL.ELClose() ; 
	}		 

	private static void add(String[] s1) {
		ArrayList <String []> subTerms = MWTerms.getSubStrings(s1, MWParms.minTermLen) ; 
		mwTerms.addMWTerm(s1 , null) ;
		for (String [] subTerm: subTerms) { 
			mwTerms.addMWTerm(subTerm , null) ;			
		}		
	}
}