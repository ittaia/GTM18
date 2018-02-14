package artzi.gtm.dmlhdp.appInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class DocFeatures3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int docId ; 
	String docName ; 
	boolean evalList ; 
	ArrayList <EventFeatures> events ; 	
	public DocFeatures3 (int docId , String docName ) { 	
		this.docId = docId  ; 
		this.docName = docName ; 
		this.evalList = false ; 
		events = new ArrayList <EventFeatures> () ; 
	}
	public void addEvent(EventFeatures eventFeatures) {
		events.add(eventFeatures) ; 		
	}
	public int getDocId () { 
		return this.docId ; 
	}
	public String getDocName () { 
		return this.docName  ; 
	}
	public 	ArrayList <EventFeatures> getEvents () { 
		return this.events ; 
	}
	public boolean isEvalList() {
		return evalList;
	}
	public void setEvalList(boolean evalList) {
		this.evalList = evalList;
	}
}
