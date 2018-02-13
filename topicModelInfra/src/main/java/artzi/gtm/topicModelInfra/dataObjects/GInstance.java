package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class GInstance implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int Id ; 
	int indx ; 
	int contentObjectIndx ; 
	int level ; 
	int thread ; 
	int ownerIndx ; 
	int fromMember ; 
	int toMember ; 
	ArrayList<ComponentFeatures> featureLists ; 
	ArrayList<ComponentFeatures> inverseFeatureLists ; 
			
	public GInstance(int level, int Id , int indx ,  int ownerIndx, ArrayList<ComponentFeatures> featureLists) {
		this.Id = Id ; 
		this.indx = indx ; 
		this.level = level ; 
		thread = -1 ; 
		this.ownerIndx = ownerIndx ; 
		this.contentObjectIndx = -1 ; 
		this.featureLists = featureLists ; 
		this.inverseFeatureLists = null ; 
		this.fromMember = -1 ; 
		this.toMember = -1 ; 				
	}
	public GInstance(int level, int Id , int indx ,  int ownerIndx, ArrayList<ComponentFeatures> featureLists ,
			 ArrayList<ComponentFeatures> inverseFeatureLists) {
		this.Id = Id ; 
		this.indx = indx ; 
		this.level = level ; 
		thread = -1 ; 
		this.ownerIndx = ownerIndx ; 
		this.contentObjectIndx = -1 ; 
		this.featureLists = featureLists ; 
		this.inverseFeatureLists = inverseFeatureLists ; 
		this.fromMember = -1 ; 
		this.toMember = -1 ; 				
	}

	public void setMember(int indx) {
		if (indx > this.toMember) this.toMember = indx ;  
		if (this.fromMember < 0) this.fromMember = indx ; 		
	}

	public int getId() {
		return Id;
	}

	public int getIndx() {
		return indx;
	}

	public int getLevel() {
		return level;
	}
	
	public void setThread (int thread) { 
		this.thread = thread ; 		
	}
	public int getThread () { 
		return thread ; 
	}

	public int getOwnerIndx() {
		return ownerIndx;
	}

	public int getFromMember() {
		return fromMember;
	}

	public int getToMember() {
		return toMember;
	}

	public ArrayList<ComponentFeatures> getFeatureLists() {
		return featureLists;
	}
	public ArrayList<ComponentFeatures> getInverseFeatureLists() {
		return inverseFeatureLists;
	}

	public void setContentObjectIndx(int customerIndx) {
		if (this.contentObjectIndx != -1) EL.WE (33333 , " dup Objects " ) ;  
		this.contentObjectIndx = customerIndx ; 	
	}
	public int getContentObjectIndx () { 
		return this.contentObjectIndx ; 
	}

	public double getNumOfMebers() {
		if (fromMember > -1 ) return toMember-fromMember  +1 ; 
		else return 0 ; 
	}
	public boolean hasInverseFeatures() {
		if (this.inverseFeatureLists != null) return true ;  
		else return false ; 
	}
	
}
