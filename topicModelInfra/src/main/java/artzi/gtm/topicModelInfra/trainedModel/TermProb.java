package artzi.gtm.topicModelInfra.trainedModel;

public class TermProb {
	int termId ; 
	String term ; 
	double prob ;
	public TermProb(int termId, String term, double prob) {
		super();
		this.termId = termId;
		this.term = term;
		this.prob = prob;
	}
	public int getTermId() {
		return termId;
	}
	public String getTerm() {
		return term;
	}
	public double getProb() {
		return prob;
	}
}