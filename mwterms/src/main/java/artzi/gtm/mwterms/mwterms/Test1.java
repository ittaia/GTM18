package artzi.gtm.mwterms.mwterms;

import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class Test1 {

	static MWTerms mwTerms ; 
	public static void main(String[] args) {
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
		//mwTerms.print () ;
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