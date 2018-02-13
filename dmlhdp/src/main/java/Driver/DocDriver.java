package Driver;
 
import java.util.ArrayList;
import java.util.Random;

import MLHDP.MLHDPData;
import MLHDP.MLHDPModel;
import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.topicModelInfra.dataObjects.InstanceTemplate;
import artzi.gtm.utils.aMath.DirichletSample;
import artzi.gtm.utils.aMath.Distributions;
import artzi.gtm.utils.elog.EL;


public class DocDriver {

	static ArrayList <ArrayList <DInstance>> IDs ; 
	static ArrayList <ArrayList<ComponentFeatures> > instancesFeatures  ; 
	static Random generator ; 
	static MLHDPData mlhdpData ; 
	static MLHDPModel mlhdpModel ; 
	static ArrayList <double [][]> templateMult ; 
	static ArrayList <double [][]> featureMult ; 	
	static String path ="D:\\TestDIr\\MLHDP" ; 
	static String parmsPath = path+"\\parms.txt" ; 		
	public static void main(String[] args) throws Exception {
		
		EL.ELFile (path+ "\\log.txt") ; 		
		System.out.println("start") ; 
		generator = new Random () ; 
		initMult () ; 
		initUIENP () ; 
		IDs = new ArrayList <ArrayList <DInstance>> () ; 
		for (int level = 0 ; level < DParms.levels ; level ++ ) { 
			IDs.add (new ArrayList <DInstance> ()) ;  
		}
		instancesFeatures = new ArrayList <ArrayList<ComponentFeatures> >  () ; 
		for (int Id = 0 ; Id < DParms.docs ; Id ++) { 
			addDI (0 , Id , null) ; 
		}
		infer () ; 
		//inferLevel1 () ; 			
	}
	
	private static void initMult() {
		templateMult = new ArrayList <double [][]> () ; 
		featureMult = new ArrayList <double [][]> () ; 
		for  (int level = 0 ; level < DParms.levels-1 ; level ++ ) {
			double [] sdParm = new double [DParms.templates[level+1]] ;  
			for (int i = 0 ; i < DParms.templates[level+1]; i ++ ) { 
				sdParm [i] = DParms.alpha [level] ;  
			}
			DirichletSample dr  = new DirichletSample (sdParm) ; 
			double [][] mult = new double [DParms.templates[level]][DParms.templates[level+1]] ; 
			for (int template = 0 ; template <  DParms.templates[level] ; template ++ ) { 
				mult [template] = dr.nextSample() ;  
			}
			templateMult.add (mult) ; 
		}
		for  (int compId = 0 ; compId< DParms.comps ; compId ++ ) {
			double [] sdParm = new double [DParms.numOfValues[compId]] ;  
			for (int i = 0 ; i < DParms.numOfValues[compId] ; i ++ ) { 
				sdParm [i] = DParms.beta  ;  
			}
			DirichletSample dr  = new DirichletSample (sdParm) ; 
			double [][] mult = new double [DParms.templates[DParms.levels-1]][DParms.numOfValues[compId]]  ; 
			for (int template = 0 ; template <  DParms.templates[DParms.levels-1] ; template ++ ) { 
				mult [template] = dr.nextSample() ;  
			}
			featureMult.add(mult) ; 
		}		
	}

	private static void initUIENP() {		
		mlhdpData = new MLHDPData (DParms.levels) ; 
		for (int compId = 0 ; compId< DParms.comps ; compId ++) { 
			mlhdpData.addComponent(DParms.levels-1 , compId, DParms.numOfValues[compId], DParms.beta) ; 
		}		
	}
	
	private static void addDI(int level ,int Id , DInstance owner ) {
		int template ; 
		if (level == 0 ) { 
			template = generator.nextInt (DParms.templates [0] ) ;  
		}
		else {			
			template  =  Distributions.MultSample(templateMult.get (level-1)[owner.getTemplate()]) ; 
		}
		DInstance instance = new  DInstance (Id , template)   ; 
		IDs.get(level).add(instance) ; 
		int ownerId = -1 ; 
		if (owner != null ) ownerId = owner.getId() ; 
		if (level < DParms.levels -1) { 
			int docIndx = mlhdpData.addInstance (level , Id , ownerId ,new ArrayList<ComponentFeatures>()) ;  
			int members = Distributions.PoissoSample(DParms.instances[level])+1 ; 
			for (int i = 0 ; i < members ; i ++ ) { 
				addDI (level+1 , i , instance)  ; 
			}
		}
		else { 
			ArrayList<ComponentFeatures> featureLists = new ArrayList<ComponentFeatures> () ; 
			
			for (int compId = 0 ; compId < DParms.comps ; compId ++) { 
				featureLists.add( generateCompFeatures (template , compId)) ; 
			}
			instancesFeatures.add(featureLists   ) ; 
			int docIndx = mlhdpData.addInstance (level , Id , ownerId ,featureLists) ;  
		}
	}
	 
	private static ComponentFeatures generateCompFeatures(int template , int compId) {
		
		ComponentFeatures compFeatures = new ComponentFeatures (compId ) ; 
		int numOfFeatures  = Distributions.PoissoSample(DParms.numOfFeatures[compId])+3  ; 
		for (int i = 0 ; i < numOfFeatures ; i ++) { 
			int feature = Distributions.MultSample (featureMult.get(compId)[template] ) ; 
			compFeatures.addFeature(feature)  ; 
		}
		return compFeatures ; 
	}
	
	private static void infer() throws Exception {
		mlhdpModel = new MLHDPModel (mlhdpData , parmsPath) ; 		
		mlhdpModel.infer () ; 
		eval () ; 		 
	}
	private static void inferLevel1() throws Exception {
		MLHDPData mlhdpData1 = new MLHDPData (1) ; 
		for (int compId = 0 ; compId< DParms.comps ; compId ++) { 
			mlhdpData1.addComponent(0 , compId, DParms.numOfValues[compId], DParms.beta) ; 
		}
		for (int Id = 0 ; Id < IDs.get(1).size () ; Id ++ ) {  
			int docIndx = mlhdpData1.addInstance (0 , Id , -1 ,instancesFeatures.get(Id)) ;   
		}
		EL.W(" --------------------------------------------- Infer with 1 Level . docs " + IDs.get(1).size ()) ; 
		mlhdpModel = new MLHDPModel (mlhdpData1 , parmsPath) ;
		mlhdpModel.infer () ; 	
		eval1 () ; 
	}

	private static void eval ()  {
		for (int level = 0 ; level < DParms.levels ; level ++ ) { 		
		 
			String [] gold = new String [DParms.templates[level]] ; 
			for (int i = 0 ; i < DParms.templates[level] ; i ++ ) gold [i] = "Gold:"+i+ " " ; 
			int mc = mlhdpModel.getNumOfTemplates()[level] ; 
			String [] md = new String [mc] ; 
			for (int i = 0 ; i < mc ; i ++ ) md [i] = "Model:"+i+ " " ; 
			ClusteringMeasure cm = new ClusteringMeasure (gold , md) ; 
			InstanceTemplate []  modelC = mlhdpModel.getInstanceTempaltes().get(level) ; 
			ArrayList <DInstance> goldInstances = IDs.get(level) ; 
			for (int instanceIndx= 0 ; instanceIndx  < goldInstances.size() ; instanceIndx ++ ) { 
				cm.add(goldInstances.get(instanceIndx).getTemplate(),  modelC [instanceIndx].getTemplate() ) ; 
			}
			cm.BestF1measure() ; 
			//cm.ManyTo1measure() ; 
			cm.VMeasure() ;
		}
	}
	private static void eval1 ()  {
		String [] gold = new String [DParms.templates[1]] ; 
		for (int i = 0 ; i < DParms.templates[1] ; i ++ ) gold [i] = "Gold:"+i+ " " ; 
		int mc = mlhdpModel.getNumOfTemplates()[0] ; 
		String [] md = new String [mc] ; 
		for (int i = 0 ; i < mc ; i ++ ) md [i] = "Model:"+i+ " " ; 
		ClusteringMeasure cm = new ClusteringMeasure (gold , md) ; 
		InstanceTemplate []  modelC = mlhdpModel.getInstanceTempaltes().get(0) ; 
		ArrayList <DInstance> goldInstances = IDs.get(1) ; 
		for (int instanceIndx= 0 ; instanceIndx  < goldInstances.size() ; instanceIndx ++ ) { 
			cm.add(goldInstances.get(instanceIndx).getTemplate(),  modelC [instanceIndx].getTemplate()  ) ; 
		}
		cm.BestF1measure() ; 
		//cm.ManyTo1measure() ; 
		cm.VMeasure() ;
	}

}
