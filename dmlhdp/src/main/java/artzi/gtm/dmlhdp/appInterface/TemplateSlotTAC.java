package artzi.gtm.dmlhdp.appInterface;

import java.io.File;
import java.util.ArrayList;

import artzi.gtm.dmlhdp.appInterface.String2IntegerFeatures;
import artzi.gtm.dmlhdp.appInterface.Template;
import artzi.gtm.dmlhdp.appInterface.Template2Excel;
import artzi.gtm.dmlhdp.mlhdp.MLHDPData;
import artzi.gtm.dmlhdp.mlhdp.MLHDPModel;
import artzi.gtm.dmlhdp.mlhdp.MLHDPParms;
import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.topicModelInfra.dataObjects.InstanceTemplate;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.SaveObject;

public class TemplateSlotTAC {
	static final  String _S = ("\\") ; 
	static String path = "" ;  
	static int docId = 0 ;
	static int docCnt = 0 ;	 
	static String projectPath = "MUIEPath" ; 
	static final String predicates = "Predicates" ; 
	static final String dependencies = "Dependencies" ; 
	static final String entities = "Entities" ; 	
	static final String semCodes = "SemCodes" ; 
	static MLHDPModel mlhdp ; 
	static MLHDPData mlhdpData ;
	static Template template0 ; 
	static Template template1 ; 
	static File mainPath ; 
	static ArrayList <DocFeatures> evalDocList ; 
	static ArrayList <ModelDoc> modelDocList ;  
	static MLHDPParms parms = null ;			
	public static void main(String[] args) throws Exception {  
		mainPath =  new File  (".")   ; // */ ("D:\\TestDir\\TestEC2NPTAC") ; 
		String parmPath = new File (mainPath , "parms.json").getPath () ; 
		parms = MLHDPParms.getInstance (parmPath) ; 
		System.out.println ("Work on Dir :"+ mainPath.getPath()) ; 		
		String logPath = new File (mainPath , "MLHDPLog.txt").getPath () ; 
		EL.ELFile (logPath) ;
		parms.print () ; 
		EL.W("Start read") ; 
		initUIENP () ; 
		processDocs  () ; 
		inferTemplates () ; 		
		EL.ELClose() ; 
	}
	
	private static void  processDocs  () throws Exception { 
		
		String docsDir =  new File (mainPath , "docsFeatures").getPath () ; 
		System.out.println ("Load docs - Docs Dir" + docsDir); 
		ArrayList <String> textFiles = getTextFiles (docsDir) ; 
		evalDocList = new ArrayList <DocFeatures> () ; 
		int id ; 
		for (id = 0 ; id <    textFiles.size () & id < NPParms.maxDocs   ; id ++ ) {
			if (id > NPParms.startDoc)  { 
				String fullName = textFiles.get(id) ; 
				System.out.println (id + " - " + fullName) ; 
				DocFeatures docFeatures = (DocFeatures) SaveObject.read(fullName) ; 
				if (docFeatures.isEvalList()) { 
					evalDocList.add(docFeatures) ;  
				}
				addDoc(docFeatures) ; 
			}
		}			
		System.out.println ("Docs - " + id ) ; 	
		mlhdpData.print () ; 
	}		
	

	private static void addDoc(DocFeatures docFeatures) {
		int docIndx = mlhdpData.addInstance (0 , docFeatures.getDocId() , -1 ,new ArrayList<ComponentFeatures>()) ;  
		for (InstanceFeatures instance : docFeatures.getInstances()) { 
			ArrayList<ComponentFeatures> featureLists = String2IntegerFeatures.get (instance.getFeatures() , template1) ; 
			int indx = mlhdpData.addInstance (1 , instance.getInstanceId() , docIndx , featureLists ) ;
			instance.setInstanceId(indx) ; 
		}
	} 

	private static void initUIENP() {
		
		mlhdpData = new MLHDPData (2) ; 
		template0 = new Template (0 , "Template") ; 
		template1 = new Template (1 , "Slot") ; 
		template1.addValueList(predicates) ; 
		template1.addValueList(entities) ; 
		template1.addValueList(semCodes) ; 			
	}
	
	private static void inferTemplates () throws Exception {	
		String parmsPath = new File (mainPath , "Parms.txt" ).getPath() ;   
		mlhdpData.addComponent(1, 0 , template1.getNumOfValues(0)  , parms.betaP) ; 
		mlhdpData.addComponent(1, 1 , template1.getNumOfValues(1)  , parms.betaE) ; 
		mlhdp = new MLHDPModel (mlhdpData , parmsPath) ; 		
		mlhdp.infer () ; 
		String objectPath = new File (mainPath , "obj").getPath () ; 
		mlhdp.save(objectPath) ; 
		mlhdp.saveRun(mainPath.getPath()) ; 
		
		getModelDocs () ; 
		String xlPath =   new File (mainPath , "Slots.xlsx").getPath () ; 
		Template2Excel.createExcel (1 , template1 , mlhdp , xlPath) ; 
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
	private static void getModelDocs() {
		modelDocList = new ArrayList <ModelDoc> () ; 
		int [] numOfTemplates = mlhdp.getNumOfTemplates () ; 
		InstanceTemplate [] instanceTemplates = mlhdp.getInstanceTempaltes().get (1) ; 
		for (DocFeatures   docFeatures : evalDocList ) { 
			ModelDoc modelDoc = new ModelDoc (docFeatures.getDocName() , numOfTemplates[1]+1 ) ; 
			
			for (InstanceFeatures instance : docFeatures.getInstances() ) { 
				String name = instance.getNameText() ; 
				InstanceTemplate instanceTemplate =  instanceTemplates  [instance.getInstanceId()] ; 
				ModelVal val = new ModelVal (name , instanceTemplate.getProbability()) ; 
				modelDoc.addVal(instanceTemplate.getTemplate()  , val)  ;  				 
			}
			modelDocList.add (modelDoc) ; 
			System.out.println(modelDoc.getID()) ; 
		}
		String file = new File (mainPath , "ModelDocList.txt").getPath () ; 
		SaveObject.write(file ,  modelDocList ) ; 
	}
}
