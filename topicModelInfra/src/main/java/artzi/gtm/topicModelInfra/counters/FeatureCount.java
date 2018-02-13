package artzi.gtm.topicModelInfra.counters;
import java.io.Serializable;
import artzi.gtm.utils.elog.EL;

public class FeatureCount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int feature ; 
	int count ;
	public FeatureCount(int feature) {
		super();
		this.feature = feature;
		this.count = 1 ; 
	} 
	public void add1 () { 
		count ++ ; 
	}
	public int getFeature () { 
		return this.feature ; 
	}
	public int getCount () { 
		return this.count ; 
	}
	public void dec1() {
		if (count <= 0 ) { 
			EL.WE(12345 ,  " Negative Feature Count " + feature) ; 
		}
		count -- ; 		
	}
}
