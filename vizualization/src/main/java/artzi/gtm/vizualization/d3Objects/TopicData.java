package artzi.gtm.vizualization.d3Objects;

import artzi.gtm.topicModelInfra.trainedModel.TermProb;

public class TopicData {
	int id ; 
	int topicId ; 
	String header ; 
	TermProb [] terms ; 
	int group ;
	int size ; 
	public TopicData (int id , int topicId , int group , String header , TermProb [] terms , int size ) { 
		this.id = id ; 
		this.topicId = topicId ; 
		this.header = header ; 
		this.terms = terms ; 
		this.group = group ; 
		this.size = size ; 
	}
}