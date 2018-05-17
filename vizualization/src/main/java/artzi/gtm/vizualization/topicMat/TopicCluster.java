package artzi.gtm.vizualization.topicMat;

import artzi.gtm.topicModelInfra.trainedModel.TermProb;

public class TopicCluster {
	
	int clusterId ; 
	int [] topicIds ; 
	TermProb [] topTerms ;
	public TopicCluster(int clusterId, int[] topicIds,TermProb[] topTerms) {
		super();
		this.clusterId = clusterId;
		this.topicIds = topicIds;
		this.topTerms = topTerms;
	}
	public int getClusterId() {
		return clusterId;
	}
	public int[] getTopicIds() {
		return topicIds;
	}
	public TermProb[] getTopTerms() {
		return topTerms;
	} 	
}