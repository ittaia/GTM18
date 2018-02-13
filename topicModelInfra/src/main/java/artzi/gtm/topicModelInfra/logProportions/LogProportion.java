package artzi.gtm.topicModelInfra.logProportions;

public class LogProportion {
	
	double logProp ; 
	boolean zero ; 
	
	public LogProportion (double logProp , boolean zero) { 
		this.logProp = logProp ; 
		this.zero = zero ; 		
	}

	public double getLogProp() {
		return logProp;
	}

	public boolean isZero() {
		return zero;
	}
	
	

}
