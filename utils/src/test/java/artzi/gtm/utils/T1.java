package artzi.gtm.utils;

import artzi.gtm.utils.format.FormatJson;

public class T1 {
	public static void main(String[] args) throws Exception {
		String s = "{\"minWordCount\":8,\"maxDF\":0.3}" ; 
		System.out.println (s) ; 
		System.out.println (FormatJson.setNewLines(s)) ;
				 
	}

}
