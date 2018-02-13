package artzi.gtm.utils;

import artzi.gtm.utils.termList.Term2Id;

public class T2 {

	public static void main(String[] args) {
		String [] t = {"a" ,"b" , "a" , "x" ,"b" ,"u"} ; 
		Term2Id t2i = new Term2Id () ; 
		for (String term : t) {
			System.out.println (term + " " + t2i.addTerm(term)) ; 
		}
		String term = "xx" ; 
		System.out.println (term + " "+  t2i.addTerm(term)) ; 
		term = "a" ; 
		System.out.println (term +  " "+  t2i.addTerm(term)) ; 		
	}
}