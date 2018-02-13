package artzi.gtm.utils.sortProb;

import java.util.Comparator;

public class CompareProb implements Comparator<Object> {
	 
	public int compare  (Object a1 , Object a2) {
		IndxProb ip1 = (IndxProb) a1 ;
		IndxProb ip2 = (IndxProb) a2 ;
		 
		if ( ip1.getProb()   <  ip2.getProb()  ) {
			return 1 ; 
		}
		else if  ( ip1.getProb()   >  ip2.getProb()  ) {
			return -1 ; 
		}
		else return 0 ; 	 
	}	
}