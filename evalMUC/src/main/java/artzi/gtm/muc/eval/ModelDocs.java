package artzi.gtm.muc.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.Gson;

import artzi.gtm.dmlhdp.appInterface.ModelDoc;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;

public class ModelDocs {
	ArrayList <ModelDoc> modelDocList ; 
	static String path = "C:\\TestDir\\MUCUIE" ;    
	int docCnt = 0 ;	 
	
	int [] docTemplate ; 
	int [] instanceSlot ; 
	double [] instanceProb ;
	Config config ; 
	Gson gson = new Gson () ; 
		
	public ModelDocs () throws Exception { 
		modelDocList = new ArrayList <ModelDoc> () ; 
		config = Config.getInstance(path) ; 
		load() ; 
	}	
	private void  load () throws Exception { 
		String docsjson =  config.getPath ("ModelDocs") ; 
		System.out.println ("Load model docs - json " + docsjson); 
		BufferedReader in  = new BufferedReader(new FileReader(docsjson));
		String jsons ; 
		int cnt = 0 ; 
		while ((jsons = in.readLine()) != null) { 
			cnt += 1 ; 
			ModelDoc modelDoc = gson.fromJson(jsons , ModelDoc.class) ; 
			System.out.println (cnt + " - " + modelDoc.getID()) ; 
			modelDocList.add(modelDoc) ; 
			
		}
		in.close(); 
		System.out.println ("Docs - " + cnt ) ; 	
	}	
	
	public ArrayList<ModelDoc> getList() {
		return this.modelDocList ; 
	}
	public void print () {
		for (ModelDoc modelDoc :modelDocList) { 
			modelDoc.print () ; 
		}
	}
	public ModelDoc getDoc(String ID) throws Exception {
		for (ModelDoc   md : modelDocList   )  { 
			String ID1 = ID.replace("-0" , "0")+".txt" ;  
			if (md.getID().equals(ID1)) return md ; 
		}
		EL.WF (8976 , " ModelDoc Not Found " +ID) ; 
		return null ; 		 
	}	
}
