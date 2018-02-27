package artzi.gtm.mltm.mlshdp;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.JsonIO;

public class MLSHDPParms  {
	public double minWordCount =  3;
	public double maxDf = 0.3 ;
	public int modelLevels = 2; 
	public int levels = 1+modelLevels ;  
	public int [] initialMixs = {-1 , 20 , 100} ;  
	public double [] aGamma = {1,1} ; 
	public double [] bGamma = {5,5} ; 
	public double [] aAlpha0 = {1,1} ; 
	public double [] bAlpha0 = {1,1} ; 	
	public double [] gamma = {1,1} ; 
	public double [] alpha0 = {1,1} ; 
	public double lambda = 0.01 ;
	public int numOfThreads = 2;
	public int iters = 400 ; 
	public int printIters = 10;	
	public int burninIters =300 ; 
	public int skipIters = 10 ; 
	public int sampleLambdaIters = 20 ;
	public int classifyIters = 50 ; 
	public int classifyBurnInIters = 5 ;
	public boolean efficientGibbs = false;  
	
	static MLSHDPParms parmsInstance = null ; 
	static Gson gson = null ; 	 
	
	public static MLSHDPParms getInstance (String path) throws IOException { 
		if (parmsInstance == null)  { 
			if (gson ==null) gson = new Gson();
			JsonElement je = JsonIO.read(path);
			parmsInstance 	= gson.fromJson(je,MLSHDPParms.class) ; 
		}
		return parmsInstance ; 
	}
	
	public static MLSHDPParms getInstance() {
		return parmsInstance ;  
	}		
	
	public  void print ()   { 
		if (gson ==null) gson = new Gson();
		EL.W( gson.toJson(parmsInstance)) ; 
	}	
}