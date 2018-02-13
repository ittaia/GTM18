package Interface;

import java.io.Serializable;

public class ModelVal implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name ; 
	double prob ;
	public ModelVal(String name, double prob) {
		super();
		this.name = name;
		this.prob = prob;
	}
	public String getName() {
		return name;
	}
	public double getProb() {
		return prob;
	}
	public void setMaxProb(double prob2) {
		if (this.prob < prob2) this.prob = prob2 ; 		
	} 
}