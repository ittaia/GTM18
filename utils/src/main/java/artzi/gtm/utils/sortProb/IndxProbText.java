package artzi.gtm.utils.sortProb;

public class IndxProbText {
	private String text;
	private int indx ; 
	private double prob ;
	
	public IndxProbText(int indx, double prob, String text) {
		super();
		this.indx = indx;
		this.prob = prob;
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public int getIndx() {
		return indx;
	}
	public double getProb() {
		return prob;
	} 
	
	@Override
	public String toString() {
		String str = "";
		if (this.text == null){
			str = "indx="+this.indx+ ", prob="+this.prob;
		}
		else{
			str = "indx="+this.indx+ ", text="+this.text + ", prob="+this.prob;
		}
		return str;
	}

}
