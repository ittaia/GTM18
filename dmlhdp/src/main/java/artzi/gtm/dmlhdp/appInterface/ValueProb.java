package artzi.gtm.dmlhdp.appInterface;

import java.io.Serializable;

public class ValueProb implements Serializable{

	private static final long serialVersionUID = 1L;
	int valueIndx ; 
	String value ; 
	double probability ;
	public ValueProb(int valueIndx, String value, double probability) {
		super();
		this.valueIndx = valueIndx;
		this.value = value;
		this.probability = probability;
	}
	public int getValueIndx() {
		return valueIndx;
	}
	public String getValue() {
		return value;
	}
	public double getProbability() {
		return probability;
	} 
	public String toString () { 
		String r = value.replace("-LRB-" , "(") ; 
		r = r.replace("-RRB-" , ")") ; 
		// r = r + " (" +Double.toString(probability).substring(0, 5) + ")"  ; 
		return r ; 
	}
}
