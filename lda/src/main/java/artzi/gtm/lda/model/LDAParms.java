package artzi.gtm.lda.model;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.JsonIO;
public class LDAParms {
	public int numOfLDATopics = 100 ;
	public double minWordCount =  8;
	public double alpha = 0.01 ; 
	public double beta = 0.01 ; 
	public double maxDF = 0.3 ;
	public int iterations = 2000 ;
	public int optimezeInterval = 20 ;
	public int burninIters = 1500 ;  
	public int numOfThreads = 2 ; 
	static LDAParms parmsInstance = null ; 
	static Gson gson = null ; 	 

	public static LDAParms getInstance (String path) throws IOException { 
		if (parmsInstance == null)  { 
			if (gson ==null) gson = new Gson();
			JsonElement je = JsonIO.read(path);
			parmsInstance 	= gson.fromJson(je,LDAParms.class) ; 
		}
		return parmsInstance ; 
	}
	public static LDAParms getInstance() {
		return parmsInstance ;  
	}		

	public  void print ()   { 
		if (gson ==null) gson = new Gson();
		EL.W( gson.toJson(parmsInstance)) ; 
	}
}
