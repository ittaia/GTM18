package artzi.gtm.vizualization.topicMat;

import artzi.gtm.topicModelInfra.trainedModel.TermProb;

public class TopicCluster {
	
	int clusterId ; 
	int [] topicIds ; 
	TermProb [] topTerms ;
	int med ; 
	public TopicCluster(int clusterId, int med ,  int[] topicIds,TermProb[] topTerms) {
		super();
		this.clusterId = clusterId;
		this.med =  med ; 
		this.topicIds = topicIds;
		this.topTerms = topTerms;
	}
	public int getClusterId() {
		return clusterId;
	}
	public int getMed() {
		return med;
	}
	public int[] getTopicIds() {
		return topicIds;
	}
	public TermProb[] getTopTerms() {
		return topTerms;
	}
}