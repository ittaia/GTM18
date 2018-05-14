package artzi.gtm.utils.clustering;

import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class PAMResult {
	int [] clusterAssignment ; 
	int [] meds ;
	ArrayList <ArrayList <Integer >> clusterMat ; 
	public PAMResult(int[] clusterAssignment, int[] meds) {
		super();
		this.clusterAssignment = clusterAssignment;
		this.meds = meds;
		clusterMat = new ArrayList  <> () ; 
		for (int cluster = 0 ; cluster < meds.length ; cluster ++) { 
			clusterMat.add(new ArrayList <Integer>()); 
		}
		for (int i = 0 ; i < clusterAssignment.length ; i ++) { 
			clusterMat.get(clusterAssignment[i]).add(i) ; 
		}		
	}
	public int getNumOfClusters () { 
		return meds.length ; 
	}
	public int[] getClusters() {
		return clusterAssignment;
	}
	public int[] getMeds() {
		return meds;
	}
	public int getMed (int cluster) { 
		return meds[cluster] ; 
	}
	public ArrayList <Integer>  getCluster (int cluster) { 
		return clusterMat.get(cluster) ; 
	}
	public void print() {
		EL.W( " Clustering results ");
		for (int i = 0 ; i < clusterAssignment.length ; i ++ ) { 
			EL.W ("Object: " + i + " Cluster " +  clusterAssignment [i] ) ; 
		}
		for (int c = 0 ; c < meds.length ; c++) { 
			EL.W (" Cluster: "+ c + " Med:" + meds[c]) ; 
		}
		
	}
}