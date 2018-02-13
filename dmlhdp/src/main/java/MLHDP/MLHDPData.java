package MLHDP;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.topicModelInfra.dataObjects.GInstance;
import artzi.gtm.utils.elog.EL;

public class MLHDPData  implements Serializable{ 
	 
	private static final long serialVersionUID = 1L;
	int levels ;
	public ArrayList <MLHDPLevelData> levelsData ; 	
	
	public MLHDPData (int levels) { 
		this.levels = levels ; 
		levelsData = new ArrayList <MLHDPLevelData> () ; 
		for (int level = 0 ; level < levels ; level ++) { 
			levelsData.add (new MLHDPLevelData (level)) ; 
		}
	}
	public void addComponent (int level , int Id , int numOfFeatures , double beta) { 
		levelsData.get(level).addComponent (Id , numOfFeatures , beta)  ; 		
	}
	public int addInstance (int level , int Id , int ownerIndx ,  ArrayList<ComponentFeatures> featureLists) { 
		if (level > levels -1 ) EL.WE (9870 , "Wrong level " + level ) ;
		ArrayList <GInstance> instanceList = levelsData.get(level).getInstanceList() ; 
		GInstance owner = null ;  
		int indx = instanceList.size () ; 
		if (level > 0) { 
			if (ownerIndx < 0 )   EL.WE (9871 , "Wrong owner " + ownerIndx  + "Level " + level) ; 			 
			ArrayList <GInstance> ownerInstanceList = levelsData.get(level-1).getInstanceList()  ;
			if (ownerIndx >=   ownerInstanceList.size())   EL.WE (9871 , "Wrong owner " + ownerIndx  + "Level " + level) ; 
			owner = ownerInstanceList.get (ownerIndx) ; 
		}		
		GInstance instance = new GInstance (level , Id , indx ,  ownerIndx , featureLists) ; 
		instanceList.add (instance) ;
		if (owner != null) owner.setMember (indx) ; 
		return indx ; 		
	}
	public int addInstance (int level , int Id , int ownerIndx ,  ArrayList<ComponentFeatures> featureLists , 
			ArrayList <ComponentFeatures> inversFeatureLists) { 
		if (level > levels -1 ) EL.WE (9870 , "Wrong level " + level ) ;
		ArrayList <GInstance> instanceList = levelsData.get(level).getInstanceList() ; 
		GInstance owner = null ;  
		int indx = instanceList.size () ; 
		if (level > 0) { 
			if (ownerIndx < 0 )   EL.WE (9871 , "Wrong owner " + ownerIndx  + "Level " + level) ; 			 
			ArrayList <GInstance> ownerInstanceList = levelsData.get(level-1).getInstanceList()  ;
			if (ownerIndx >=   ownerInstanceList.size())   EL.WE (9871 , "Wrong owner " + ownerIndx  + "Level " + level) ; 
			owner = ownerInstanceList.get (ownerIndx) ; 
		}		
		GInstance instance = new GInstance (level , Id , indx ,  ownerIndx , featureLists , inversFeatureLists) ; 
		instanceList.add (instance) ;
		if (owner != null) owner.setMember (indx) ; 
		return indx ; 		
	}
	public MLHDPLevelData getLevelData (int level) { 
		return levelsData.get(level) ; 
	}
	
	public void initObjectComponentCount () { 
		for (int level = 0 ; level < levels-1 ; level ++) { 
			MLHDPLevelData levelData = levelsData.get(level) ; 
			MLHDPLevelData nextLevelData = levelsData.get(level+1) ; 
			levelData.objectComponentCount = new int [levelData.instanceList.size()  ] ; 
			int nextLevelObjectIndx = 0 ; 
			objectLoop: 
			for (int objectIndx = 0 ; objectIndx < levelData.instanceList.size() ; objectIndx ++  ) { 
				levelData.objectComponentCount[objectIndx] = 0 ; 
				while (nextLevelData.instanceList.get(nextLevelObjectIndx).getOwnerIndx() == objectIndx) { 
					levelData.objectComponentCount[objectIndx] ++ ; 
					nextLevelObjectIndx ++ ; 
					if (nextLevelObjectIndx >= nextLevelData.instanceList.size ()) { 
						break objectLoop ; 
					}
				}				
			}			 
		} 
	}
	
	public void print() {
		for (int l = 0 ; l < levels ; l ++) { 
			System.out.println(" ==== > " + l ) ; 
			levelsData.get (l).print () ; 
		}		
	}
}