package artzi.gtm.vizualization.d3Objects;

public class TopicData {
	int id ; 
	int topicId ; 
	String header ; 
	String [] terms ; 
	int group ;
	int size ; 
	public TopicData (int id , int topicId , int group , String header , String [] terms , int size ) { 
		this.id = id ; 
		this.topicId = topicId ; 
		this.header = header ; 
		this.terms = terms ; 
		this.group = group ; 
		this.size = size ; 
	}
}