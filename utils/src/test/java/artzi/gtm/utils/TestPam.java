package artzi.gtm.utils;

import artzi.gtm.utils.clustering.PAM;
import artzi.gtm.utils.clustering.PAMResult;

public class TestPam {

	public static void main(String[] args) {
		double [][] dis = { { 0 , 1 , 1 , 2 , 2 } ,
				 			{ 1 , 0 , 1 , 2 , 2 } ,
				 			{ 1 , 1 , 0 , 2 , 2 } ,
				 			{ 2 , 2 , 2 , 0 , 1 } ,
				 			{ 2 , 2 , 2 , 1 , 0 } } ; 
		
		PAM p = new PAM (dis , 2)  ; 
		PAMResult r = p.getClusters() ; 
		r.print () ; 
	}
}