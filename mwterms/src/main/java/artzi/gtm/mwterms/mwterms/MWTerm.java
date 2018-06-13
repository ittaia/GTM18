package artzi.gtm.mwterms.mwterms;

import artzi.gtm.utils.elog.EL;

public class MWTerm {

	String [] terms ; 
	int len ; 
	double cVal = -1 ; 
	int totCount ;         /** f(a) ***/
	int nestedCount ;      /** t(a) ***/ 
	int upperTermsCount ;  /** c(b) ***/
	public MWTerm (String [] terms  ){
		this.terms = terms ; 
		len = terms.length ; 
		totCount = 0 ; 
		nestedCount = 0 ; 
		upperTermsCount = 0 ; 
	}
	public void addCount () { 
		this.totCount ++ ; 
	}
	public int getLen() {
		return len ; 
	}
	public double getCVal() {
		final double log2 = Math.log(2) ; 
		if (upperTermsCount == 0 ) { 
			cVal = (Math.log(len+MWParms.len1Delta )/log2) * totCount  ; 
		}
		else { 
			cVal = (Math.log(len)/log2) * (totCount - (nestedCount/upperTermsCount))    ; 			
		}
		return cVal ; 
	}
	public void updateNestedCount (int delta) { 
		nestedCount += delta ; 
	}
	public void addUpperTermsCount () { 
		upperTermsCount ++ ; 
	}
	public String [] getTerms() {
		return terms ; 
	}
	
	public int getTotCount() {
		return totCount;
	}
	public int getNestedCount() {
		return nestedCount;
	}
	public int getUpperTermsCount() {
		return upperTermsCount;
	}
	public void print() {
		String s = len + " - " ; 
		for (String term:terms) s += term + " " ; 
		s += "Cval:" + getCVal()+ " Count:" + totCount + " NestedCount:" + nestedCount + " Upper:" + upperTermsCount ; 
		EL.W(s) ;  
	}
	public String getCsv() {
		String s = len+"," ;  
		for (String term:terms) s += term + " " ;  
		s+= "," + getCVal()+ "," +  totCount + "," + nestedCount + "," + upperTermsCount ; 
		return (s) ; 
		 
	}		 
}