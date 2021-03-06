package artzi.gtm.dmlhdp.mlhdp;

import java.io.IOException;
import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.JsonIO;

public class MLHDPParms implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int levels = 2 ; 
	public String [] templates = {"Template" , "Slot"} ; 
	public String [][] valueLists = {{} , {"Predicates" , "Entities" , "SemCodes"}}  ; 
	
	public double gammaTop = 0; 
	public double [] gamma = {0 , 0 , 0} ;  
	public double [] alpha0 = {0 , 0, 0} ;  
	public double aGammaTop = 0; 
	public double bGammaTop = 0 ;  
	public double [] aGamma = {0 , 0 , 0}  ; 
	public double [] bGamma = {0, 0 , 0} ; 
	public double [] aAlpha0 = {0 , 0 ,0}; 
	public double [] bAlpha0 = {0 , 0 , 0} ; 
	public int maxIters = 0 ; 
	public int burnIn = 0 ; 
	public int skipIters = 0 ; 
	public int numOfThreads = 0;
	public int checkPoint = 0 ;
	public int likelihood = 0; 
	public double minEditProb = 0 ;
	public double [][] beta ; 
	public int updateBeta = 0 ;
	public boolean inverse = false ;	
	public double minInverseLogProportion = 1  ; 
	public double inverseMatchPenalty = 1   ;
	
	static MLHDPParms parmsInstance = null ; 
	static Gson gson = null ; 	 
	
	public static MLHDPParms getInstance (String path) throws IOException { 
		if (parmsInstance == null)  { 
			if (gson ==null) gson = new Gson();
			JsonElement je = JsonIO.read(path);
			parmsInstance 	= gson.fromJson(je,MLHDPParms.class) ; 
		}
		return parmsInstance ; 
	}
	public static MLHDPParms getInstance() {
		return parmsInstance ;  
	}		
	
	public  void print ()   { 
		if (gson ==null) gson = new Gson();
		EL.W( gson.toJson(parmsInstance)) ; 
	}	
}
