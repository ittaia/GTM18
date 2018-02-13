package Interface;
import java.util.Comparator;

public class CompareValueProb implements Comparator {

	public int compare  (Object a1 , Object a2) {
		ValueProb ip1 = (ValueProb) a1 ;
		ValueProb ip2 = (ValueProb) a2 ;
		
		if ( ip1.getProbability() >  ip2.getProbability()  ) {
			return -1 ; 
		}
		else if ( ip1.getProbability() <  ip2.getProbability()  ) {
			return 1 ; 				
		}
		else return 0 ; 		   
	}
}

