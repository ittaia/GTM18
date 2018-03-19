package artzi.gtm.topicModelInfra.dataObjects;

public class DocData {
	String file_id = "" ; 
	String title = "" ; 
	String text = "" ;
	
	public DocData(String file_id, String title, String text) {
		super();
		this.file_id = file_id;
		this.title = title;
		this.text = text;
	}
	public void toLow () { 
		if (title != null) title = title.toLowerCase() ; 
		if (text != null) text = text.toLowerCase() ; 
	}
	public String getFile_id() {
		return file_id;
	}
	public String getTitle() {
		return title;
	}
	public String getText() {
		return text;
	} 
}