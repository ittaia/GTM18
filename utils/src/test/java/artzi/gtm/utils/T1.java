package artzi.gtm.utils;

import com.google.gson.Gson;

import artzi.gtm.utils.format.FormatJson;

public class T1 {
	public static void main(String[] args) throws Exception {
		String s = "{\"minWordCount\":8,\"maxDF\":0.3}" ; 
		System.out.println (s) ; 
		System.out.println (FormatJson.setNewLines(s)) ;
		Gson gson = new Gson () ; 
		Tj tj = new Tj () ;
		System.out.println (gson.toJson(tj , Tj.class)) ; 
				 
	}

}
