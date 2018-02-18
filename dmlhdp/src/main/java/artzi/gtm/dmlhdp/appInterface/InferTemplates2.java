package artzi.gtm.dmlhdp.appInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

import artzi.gtm.dmlhdp.appInterface.String2IntegerFeatures;
import artzi.gtm.dmlhdp.appInterface.Template;
import artzi.gtm.dmlhdp.appInterface.Template2Excel;
import artzi.gtm.dmlhdp.mlhdp.MLHDPData;
import artzi.gtm.dmlhdp.mlhdp.MLHDPModel;
import artzi.gtm.dmlhdp.mlhdp.MLHDPParms;
import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.topicModelInfra.dataObjects.InstanceTemplate;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;

public class InferTemplates2 {
	static String path = "C:\\TestDir\\MUCUIE" ;  
	static int docId = 0 ;
	static int docCnt = 0 ;	 
	
	static MLHDPModel mlhdp ; 
	static MLHDPData mlhdpData ;
	static Template [] templates; 
	
	static ArrayList <DocFeatures2> evalDocList ; 
	static String parmsPath ; 
	static MLHDPParms parms = null ;  
	static Config config ; 
	static Gson gson = new Gson () ; 
						
	public static void main(String[] args) throws Exception {  
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		parmsPath = config.getPath("MLHDPParms") ; 
		parms = MLHDPParms.getInstance (parmsPath) ; 
		parms.print () ;		 
		
				
		initUIENP () ; 
		processDocs  () ; 
		inferTemplates () ; 		
		
	}
	private static void initUIENP() {
		
		mlhdpData = new MLHDPData (parms.levels) ; 
		templates = new Template [parms.levels] ; 
		for (int level = 0 ; level < parms.levels ; level ++) {
			templates[level]  = new Template (level , parms.templates[level]) ; 
			templates[level].addValueList(parms.valueLists[level]);
		}		 
	}

	private static void  processDocs  () throws Exception { 
		String docsjson =  config.getPath ("DocsFeatures") ; 
		System.out.println ("Load docs - json " + docsjson); 
		evalDocList = new ArrayList <DocFeatures2> () ; 
		BufferedReader in  = new BufferedReader(new FileReader(docsjson));
		int id = -1 ;  
		String jsons ; 
		while ((jsons = in.readLine()) != null) { 
			id += 1 ; 
			if (id > NPParms.startDoc)  { 
				DocFeatures2 docFeatures = gson.fromJson(jsons , DocFeatures2.class) ; 
				System.out.println (id + " - " + docFeatures.getDocName()) ; 
				if (docFeatures.isEvalList()) { 
					evalDocList.add(docFeatures) ;  
				}
				addDoc(docFeatures) ; 
			} 
		}
		in.close(); 
		System.out.println ("Docs - " + id ) ; 	
		mlhdpData.print () ; 
	}	
	

	private static void addDoc(DocFeatures2 docFeatures) {
		int docIndx = mlhdpData.addInstance (0 , docFeatures.getDocId() , -1 ,new ArrayList<ComponentFeatures>()) ;  
		for (InstanceFeatures instance : docFeatures.getInstances()) { 
			ArrayList<ComponentFeatures> featureLists = String2IntegerFeatures.get (instance.getFeatures() , templates[1]) ; 
			ArrayList<ComponentFeatures> inverseFeatureLists = null ;
			if (instance.getInverseFeatures() != null) { 
				inverseFeatureLists = String2IntegerFeatures.get (instance.getInverseFeatures() , templates[1]) ; 
			}
			int indx = mlhdpData.addInstance (1 , instance.getInstanceId() , docIndx , featureLists , inverseFeatureLists ) ;
			instance.setInstanceId(indx) ; 
		}
	} 

	
	
	private static void inferTemplates () throws Exception {
		
		for (int templateId = 0 ; templateId < templates[1].getNumOfLists(); templateId ++) {
				 
			mlhdpData.addComponent(1, templateId , templates[1].getNumOfValues(templateId)  , parms.beta[1][templateId]) ; 
		}
		
		mlhdp = new MLHDPModel (mlhdpData , parmsPath) ; 		
		mlhdp.infer () ; 
		String objectPath = config.getPath ("obj") ; 
		mlhdp.save(objectPath) ; 
		String ranPath = config.getPath ("ran") ; 
		mlhdp.saveRan(ranPath) ; 
		
		saveModelDocs () ; 
		String xlPath =   config.getPath("xls") ; 
		Template2Excel.createExcel (1 , templates[1] , mlhdp , xlPath) ; 
	}	
	
	private static void saveModelDocs() throws IOException {
		String modelDocsPath = config.getPath("ModelDocs") ; 
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelDocsPath)));		
		int [] numOfTemplates = mlhdp.getNumOfTemplates () ; 
		InstanceTemplate [] instanceTemplates = mlhdp.getInstanceTempaltes().get (1) ; 
		for (DocFeatures2   docFeatures : evalDocList ) { 
			ModelDoc modelDoc = new ModelDoc (docFeatures.getDocName() , numOfTemplates[1]+1 ) ; 
			
			for (InstanceFeatures instance : docFeatures.getInstances() ) { 
				String name = instance.getNameText() ; 
				InstanceTemplate instanceTemplate =  instanceTemplates  [instance.getInstanceId()] ; 
				ModelVal val = new ModelVal (name , instanceTemplate.getProbability()) ; 
				modelDoc.addVal(instanceTemplate.getTemplate()  , val)  ;  				 
			}
			System.out.println(modelDoc.getID()) ; 
			String jsons = gson.toJson(modelDoc) ;
			out.write(jsons);
			out.newLine();			
		}
		out.close(); 		
	}
}