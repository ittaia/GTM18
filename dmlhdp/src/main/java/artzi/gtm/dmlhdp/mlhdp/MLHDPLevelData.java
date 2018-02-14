package artzi.gtm.dmlhdp.mlhdp;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.topicModelInfra.dataObjects.GInstance;
import artzi.gtm.topicModelInfra.dataObjects.ObservedComponent;
import artzi.gtm.utils.elog.EL;

public class MLHDPLevelData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int level ; 
	ArrayList <ObservedComponent> observedComponents ; 
	ArrayList <GInstance>  instanceList ;
	int [] objectComponentCount = null ; 
	public MLHDPLevelData (int level ) { 
		this.level = level ; 
		observedComponents = new ArrayList <ObservedComponent> () ; 
		instanceList = new ArrayList <GInstance> () ; 
	}
	public int getLevel () { 
		return this.level ; 
	}
	public ArrayList <ObservedComponent> getObservedComponents  () { 
		return this.observedComponents ; 		
	}
	public void addComponent (int Id , int numOfFeatures , double beta) { 
		if (Id != observedComponents.size ()) { 
			EL.WE(9875 , " Bad component " + Id ) ; 
		}
		observedComponents.add(new ObservedComponent (Id , numOfFeatures , beta)) ; 		
	}
	public ArrayList <GInstance> getInstanceList () { 
		return this.instanceList ; 
	}
	public int [] getObjectComponentCount () { 
		return this.objectComponentCount ; 
	}
	public void print() {
		System.out.println(" Instances " + instanceList.size ()) ;  		
	}
}
