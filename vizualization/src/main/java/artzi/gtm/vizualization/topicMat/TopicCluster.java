package artzi.gtm.vizualization.topicMat;

public class TopicCluster {
	
	int clusterId ; 
	int [] topicIds ; 
	String [] topTerms ;
	public TopicCluster(int clusterId, int[] topicIds, String[] topTerms) {
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
	public String[] getTopTerms() {
		return topTerms;
	} 	
}