package Interface;

import java.io.File;
import java.util.ArrayList;

import DataObjects.ComponentFeatures;
import DataObjects.InstanceTemplate;
import Elog.EL ; 
import Gen.SaveObject;
import Interface.String2IntegerFeatures;
import Interface.Template;
import Interface.Template2Excel;
import MLHDP.MLHDPData;
import MLHDP.MLHDPModel;
import MLHDP.MLHDPParms;

public class TemplateSlotMUC3 {
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
	static Template template2 ; 
	static File mainPath ; 
	static ArrayList <DocFeatures3> evalDocList ; 
	static ArrayList <ModelDoc> modelDocList ;  
	static MLHDPParms parms = null ;  
				
	public static void main(String[] args) throws Exception {  
		mainPath =  new File (".")   ; //*/("D:\\TestDir\\TestEC2NPMUC") ; 
		System.out.println ("Work on Dir :"+ mainPath.getPath()) ; 		
		String logPath = new File (mainPath , "MLHDPLog.txt").getPath () ; 
		String parmPath = new File (mainPath , "parms.json").getPath () ; 
		parms = MLHDPParms.getInstance (parmPath) ; 
		EL.ELFile (logPath) ;
		EL.W("Start read") ; 
		parms.print () ; 
		initUIENP () ; 
		processDocs  () ; 
		inferTemplates () ; 		
		EL.ELClose() ; 
	}
	
	private static void  processDocs  () throws Exception { 
		
		String docsDir =  new File (mainPath , "DocsFeatures3").getPath () ; 
		System.out.println ("Load docs - Docs Dir" + docsDir); 
		ArrayList <String> textFiles = getTextFiles (docsDir) ; 
		evalDocList = new ArrayList <DocFeatures3> () ; 
		int id ; 
		for (id = 0 ; id <    textFiles.size () & id < NPParms.maxDocs   ; id ++ ) {
			if (id > NPParms.startDoc)  { 
				String fullName = textFiles.get(id) ; 
				System.out.println (id + " - " + fullName) ; 
				DocFeatures3 docFeatures = (DocFeatures3) SaveObject.read(fullName) ; 
				if (docFeatures.isEvalList()) { 
					evalDocList.add(docFeatures) ;  
				}
				addDoc(docFeatures) ; 
			}
		}			
		System.out.println ("Docs - " + id ) ; 	
		mlhdpData.print () ; 
	}		
	

	private static void addDoc(DocFeatures3 docFeatures) {
		int docIndx = mlhdpData.addInstance (0 , docFeatures.getDocId() , -1 ,new ArrayList<ComponentFeatures>()) ;  
		for (EventFeatures event : docFeatures.getEvents()) { 
			ArrayList<ComponentFeatures> eventFeatureLists = String2IntegerFeatures.get (event.getFeatures() , template1) ; 
			int eventIndx = mlhdpData.addInstance (1 , event.getEventId() , docIndx , eventFeatureLists  ) ;
			for (InstanceFeatures instance : event.getInstances()) { 
				ArrayList<ComponentFeatures> featureLists = String2IntegerFeatures.get (instance.getFeatures() , template2) ; 
				ArrayList<ComponentFeatures> inverseFeatureLists = null ;
				if (instance.getInverseFeatures() != null) { 
					inverseFeatureLists = String2IntegerFeatures.get (instance.getInverseFeatures() , template2) ; 
				}

				int indx = mlhdpData.addInstance (2 , instance.getInstanceId() , eventIndx , featureLists , inverseFeatureLists ) ;
				instance.setInstanceId(indx) ; 
			}
		}
	} 

	private static void initUIENP() {
		
		mlhdpData = new MLHDPData (3) ; 
		template0 = new Template (0 , "Template") ; 
		template1 = new Template (1 , "Event") ; 
		template1.addValueList(predicates) ;
		template2 = new Template (2 , "Slot") ; 
		template2.addValueList(predicates) ; 
		template2.addValueList(entities) ; 
		template2.addValueList(semCodes) ; 			
	}
	
	private static void inferTemplates () throws Exception {
		String parmsPath = new File (mainPath ,"Parms.txt").getPath() ; 
		mlhdpData.addComponent(1, 0 , template1.getNumOfValues(0)  , parms.betaP) ; 
		mlhdpData.addComponent(2, 0 , template2.getNumOfValues(0)  , parms.betaP) ; 
		mlhdpData.addComponent(2, 1 , template2.getNumOfValues(1)  , parms.betaE) ; 
		mlhdpData.addComponent(2, 2 , template2.getNumOfValues(2)  , parms.betaS) ; 
		mlhdp = new MLHDPModel (mlhdpData , parmsPath) ; 		
		mlhdp.infer () ; 
		String objectPath = new File (mainPath , "obj").getPath () ; 
		mlhdp.save(objectPath) ; 
		mlhdp.saveRun(mainPath.getPath()) ; 
		
		getModelDocs () ; 
		String xlPath =   new File (mainPath , "Slots.xlsx").getPath () ; 
		Template2Excel.createExcel (2 , template2 , mlhdp , xlPath) ; 
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
		InstanceTemplate [] instanceTemplates = mlhdp.getInstanceTempaltes().get (2) ; 
		for (DocFeatures3   docFeatures : evalDocList ) { 
			ModelDoc modelDoc = new ModelDoc (docFeatures.getDocName() , numOfTemplates[2]+1 ) ; 
			for (EventFeatures event: docFeatures.getEvents()) { 
				for (InstanceFeatures instance : event.getInstances() ) { 
					String name = instance.getNameText() ; 
					InstanceTemplate instanceTemplate =  instanceTemplates  [instance.getInstanceId()] ; 
					ModelVal val = new ModelVal (name , instanceTemplate.getProbability()) ; 
					modelDoc.addVal(instanceTemplate.getTemplate()  , val)  ;  				 
				}
			}
			modelDocList.add (modelDoc) ; 
			System.out.println(modelDoc.getID()) ; 
		}
		String file = new File (mainPath , "ModelDocList.txt").getPath () ; 
		SaveObject.write(file ,  modelDocList ) ; 
	}
}
