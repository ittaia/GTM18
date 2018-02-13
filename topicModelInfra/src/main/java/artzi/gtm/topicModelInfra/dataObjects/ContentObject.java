package artzi.gtm.topicModelInfra.dataObjects;


public class ContentObject {
	int indx ; 
	int ownerIndx ; 
	int thread ; 
	int fromMember ; 
	int toMember ; 
	GInstance  instance ; 
	int mixId ; 
	ComponentFeatures mixMemberCounters ; 
	public ContentObject (GInstance instance , int indx ,   int ownerIndx   ) { 
		this.indx = indx ; 
		this.instance = instance ; 
		this.ownerIndx = ownerIndx ; 
		this.mixId = -1 ; 
		this.fromMember = -1 ; 
		this.toMember = -2 ; 
		mixMemberCounters = new ComponentFeatures (0) ;  
	}
	public int getIndx () {
		return this.instance.getIndx() ; 
	}
	public GInstance getInstance () { 
		return this.instance ; 
	}
	public void setMix (int mixId) { 
		this.mixId = mixId ; 
	}
	public int getMix () { 
		return this.mixId ; 
	}
	public void setOwnerInxd(int ownerIndx) {
		this.ownerIndx = ownerIndx ; 		
	}
	public int  getOwnerIndx () { 
		return this.ownerIndx ; 
	}
	public void setMember(int indx) {
		if (indx > this.toMember) this.toMember = indx ;  
		if (this.fromMember < 0) this.fromMember = indx ; 			
	}
	public int getFromMember() {
		return this.fromMember ; 
	}
	public int getToMember() {
		return this.toMember ; 
	}
	public ComponentFeatures getMixMemberCounters() {
		return this.mixMemberCounters  ; 		 
	}
	public int getNumOfMembers() {
		if (this.fromMember < 0) return 0 ; 
		return (toMember - fromMember +1 ) ; 
	}
	public void addMemberMix(int memberMixId) {
		mixMemberCounters.addFeature(memberMixId) ; 		
	}
	public void decMemberMix(int memberMixId) throws Exception {
		mixMemberCounters.decFeature(memberMixId) ; 		
	}
}
