package artzi.gtm.topicModelInfra.dataObjects;

import java.io.Serializable;

public class DocHeader  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int docId ; 
	String docName ; 
	String header ;
	public DocHeader(int docId, String docName, String header) {
		super();
		this.docId = docId;
		this.docName = docName;
		this.header = header;
	}
	public int getDocId() {
		return docId;
	}
	public String getDocName() {
		return docName;
	}
	public String getHeader() {
		return header;
	} 
}
