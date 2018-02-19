package artzi.gtm.muc.eval;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.JsonIO;

public class EvalMainN {
	public static void main(String[] args) throws Exception {  	
		String path = "C:\\TestDir\\MUCUIE" ;  ; 
		Config config = Config.getInstance(path) ; 
		TestDocs tl = new TestDocs (config.getPath("eval")) ; 
		ModelDocs ml = new ModelDocs () ;  
		JsonElement je = JsonIO.read(config.getPath("ran"));
		JsonObject jo = je.getAsJsonObject();
		JsonArray numOfMixesArray = jo.get("numOfMixes").getAsJsonArray() ;  
		int numOfTemplates, numOfSlots ; 
		numOfTemplates = numOfMixesArray.get(0).getAsInt() ; 
		numOfSlots = numOfMixesArray.get(1).getAsInt() ; 
		System.out.println ("Templates: " + numOfTemplates + "Slots "+ numOfSlots ) ; 
		for (TestDoc t : tl.getList())  { 
			t.print () ; 
			ml.getDoc(t.getID()).print () ; 
		}
		for (int i = 0 ; i < 5; i ++ ) { 
			double minProb = Parms.minProb + (i*0.2) ; 
			System.out.println(minProb) ; 
			EL.W(" ----- Match ----" + minProb );
			MatchTestToModel match = new MatchTestToModel (numOfTemplates , numOfSlots , tl , ml , minProb ) ; 
			match.print();
		}
		double minProb = -999 ; 
		System.out.println(minProb) ; 
		EL.W(" ----- Match ----" + minProb );
		MatchTestToModel match = new MatchTestToModel (numOfTemplates , numOfSlots , tl , ml , minProb ) ; 
		match.print();				
		EL.ELClose();
	}
}