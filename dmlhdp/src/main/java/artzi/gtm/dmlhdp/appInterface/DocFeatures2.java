package artzi.gtm.dmlhdp.appInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class DocFeatures2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int docId ; 
	String docName ; 
	boolean evalList ; 
	ArrayList <InstanceFeatures> instances ; 	
	public DocFeatures2 (int docId , String docName ) { 	
		this.docId = docId  ; 
		this.docName = docName ; 
		this.evalList = false ; 
		instances = new ArrayList <InstanceFeatures> () ; 
	}
	public void addInstance(InstanceFeatures instanceFeatures) {
		instances.add(instanceFeatures) ; 		
	}
	public int getDocId () { 
		return this.docId ; 
	}
	public String getDocName () { 
		return this.docName  ; 
	}
	public 	ArrayList <InstanceFeatures> getInstances () { 
		return this.instances ; 
	}
	public boolean isEvalList() {
		return evalList;
	}
	public void setEvalList(boolean evalList) {
		this.evalList = evalList;
	}	

}
