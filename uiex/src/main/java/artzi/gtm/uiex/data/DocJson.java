package artzi.gtm.uiex.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

import Interface.DocFeatures;
import artzi.gtm.dmlhdp.appInterface.NPParms;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.io.SaveObject;


public class  DocJson{
	static final  String _S = ("\\") ; 
	static String path = "C:\\Corpuses\\MUC\\MACUIE" ;  
	static int docId = 0 ;
	static int docCnt = 0 ;	 
	static String projectPath = "MUIEPath" ; 
	static final String predicates = "Predicates" ; 
	static final String dependencies = "Dependencies" ; 
	static final String entities = "Entities" ; 	
	static final String semCodes = "SemCodes" ; 
	static File mainPath ; 
	static ArrayList <DocFeatures> evalDocList ; 
	static Gson gson = new Gson () ; 
	 
				
	public static void main(String[] args) throws Exception {  
		Config c = Config.getInstance(path) ; 
		processDocs () ; 
	}
	
	private static void  processDocs  () throws Exception { 
		
		String docsDir =  new File (path , "DocsFeatures").getPath () ; 
		String jsonFile =  new File (path , "DocsFeaturesJson.json").getPath () ; 
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile)));		
		
		System.out.println ("Load docs - Docs Dir" + docsDir); 
		ArrayList <String> textFiles = getTextFiles (docsDir) ; 
		evalDocList = new ArrayList <DocFeatures> () ; 
		int id ; 
		int cout =  0 ; 
		for (id = 0 ; id <    textFiles.size () & id < NPParms.maxDocs ;  id ++ ) {
			if (id > NPParms.startDoc)  { 
				String fullName = textFiles.get(id) ; 
				System.out.println (id + " - " + fullName) ; 
				DocFeatures docFeatures = (DocFeatures) SaveObject.read(fullName) ; 
				String jsonstr = gson.toJson(docFeatures) ; 
				out.write(jsonstr);
				out.newLine();
				cout += 1 ; 
			}
		}
		out.close(); 
		System.out.println ("Docs - " + id  + " Out - "+ cout ) ; 	
	}		
	
	private static ArrayList<String> getTextFiles(String dirName ) {
		ArrayList <String> rFileList = new ArrayList <String> () ; 
		File dirFile = new File(dirName); 			
		String[] files = dirFile.list();		 
		for(int i=0; i<files.length ;   ++i) {				 
			String fileName  = new File (dirName   , files [i] ).getPath()  ; 
			if (fileName.endsWith(".txt") ) {
				rFileList .add (fileName) ; 
			}
			else {
				rFileList.addAll(getTextFiles (fileName)) ;  					
			}
		}
		return rFileList ; 
	}	
}
