package artzi.gtm.topicModelInfra.assignments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Assignments {	
	int levels ; 
	int [] numOfInstances ; 
	ArrayList <Assignment> assignmentList ; 
	public Assignments(int levels) {
		this.levels = levels ; 
		this.numOfInstances = new int [levels] ; 
		this.assignmentList = new ArrayList <> () ; 
	}
	public void setNumOfInstances(int level, int numOfInstances) {
		this.numOfInstances[level] =numOfInstances ; 		
	}
	public Assignment addAssignment() {
		Assignment assignment = new Assignment (levels, numOfInstances) ;  
		assignmentList.add(assignment) ; 
		return assignment ; 
	}
	public Assignment getMaxAssignment() {
		Assignment maxAssignment = new Assignment (levels, numOfInstances) ; 
		for (int level = 0 ; level < levels ;  level ++) { 
			ArrayList <int []> levelAssignments= new ArrayList <>() ; 
			for (Assignment assignment: assignmentList) { 
				levelAssignments.add(assignment.get(level)) ; 
			}
			for (int instanceIndx = 0 ; instanceIndx< numOfInstances[level] ; instanceIndx ++) { 
				int maxt = getMax (levelAssignments , instanceIndx) ; 	 
				maxAssignment.set(level, instanceIndx, maxt ) ; 
			}			
		}			 
		return maxAssignment ; 
	}
	private int getMax(ArrayList<int[]> levelAssignments, int instanceId) {
		HashMap <Integer,Integer> map = new HashMap <> () ;  
		for (int [] levelAssignment : levelAssignments) { 
			int template = levelAssignment [instanceId] ; 
			Integer cnt = map.get(template) ; 
			if (cnt == null) cnt = 0 ; 
			cnt += 1 ; 
			map.put(template, cnt) ; 
		}
		int maxTemplate = -1 ; 
		int maxCnt = 0 ; 
		for (Entry<Integer,Integer> e : map.entrySet() ) { 
			if (e.getValue() > maxCnt) { 
				maxTemplate = e.getKey() ; 
				maxCnt = e.getValue() ; 
			}		
		}
		return maxTemplate ; 
	}	
}