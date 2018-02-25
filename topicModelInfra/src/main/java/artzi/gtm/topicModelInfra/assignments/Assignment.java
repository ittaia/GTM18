package artzi.gtm.topicModelInfra.assignments;

import java.util.ArrayList;

public class Assignment {
	int levels ; 
	int [] numOfInstances ; 
	ArrayList <int []> instancesAssignments ;
	public Assignment(int levels, int[] numOfInstances) {
		this.levels = levels ; 
		this.numOfInstances = numOfInstances ; 
		instancesAssignments = new ArrayList <> () ; 
		for (int level = 0 ; level < levels ; level ++) { 
			instancesAssignments.add(new int [numOfInstances[level]]) ; 
		}
	}
	
	public void set(int level, int instanceIndx, int instanceTemplate) {
		instancesAssignments.get(level) [instanceIndx] = instanceTemplate ; 		
	}

	public int[] get(int level) {
		return instancesAssignments.get(level) ; 		 
	}

	public int get(int level, int instanceIndx) {
		return instancesAssignments.get(level)[instanceIndx] ;  
	}	
}