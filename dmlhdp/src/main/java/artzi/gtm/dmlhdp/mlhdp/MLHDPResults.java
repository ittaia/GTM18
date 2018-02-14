package artzi.gtm.dmlhdp.mlhdp;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.topicModelInfra.dataObjects.InstanceTemplate;
import artzi.gtm.utils.elog.EL;

public class MLHDPResults implements Serializable{

	private static final long serialVersionUID = 1L;
	int [] numOfTemplates ; 
	double [] multL0 ;
	ArrayList <double [][]> multLevels ; 
	ArrayList <ArrayList<double [][]>> multFeatures ; 	
	ArrayList <InstanceTemplate [] > instanceTemplates ; 
	MLHDPParms parms = null ; 	
	public MLHDPResults () { 
		parms = MLHDPParms.getInstance() ; 
	}
	public void printMult () {
		printMat ("Top " , multL0) ; 
		for (int l = 0 ; l < multLevels.size () ; l++) { 
			printMat (l  , l+1  ,  multLevels.get(l) )  ; 
		}
	}
	private void printMat (int l , int l1 , double [][] mat) { 
		int m1 = mat.length   ; 
		int m2 = mat[0].length ; 
		EL.W("******************* " + l + " - " + l1) ; 
		for (int i = 0 ; i < m1 ; i ++ ) { 
			String s = l + ": " +  i + " - "; 
			for (int j = 0 ; j < m2 ; j ++ ) { 
				if (mat [i][j] > parms.minEditProb)
					s += " " + j + ": " + mat [i][j]  ; 
				if (s.length() > 100) { 
					EL.W(s) ; 
					s = l1 + ": " +  i + " - "; 
				}
			}
			EL.W(s) ; 
		}
	}
	private void printMat (String h1 , double [] mat) { 
		int m1 = mat.length   ; 
		EL.W("******************* " + h1) ; 
		String s = "" ; 
		for (int i = 0 ; i < m1 ; i ++ ) { 
			s = " " + i + ": " + mat [i]  ; 		 
			EL.W(s) ;
		}		 
	}
	public int[] getNumOfTemplates() {
		return numOfTemplates;
	}
	public double[] getMultL0() {
		return multL0;
	}
	public ArrayList<double[][]> getMultLevels() {
		return multLevels;
	}
	public ArrayList<ArrayList<double[][]>> getMultFeatures() {
		return multFeatures;
	}
	public ArrayList<InstanceTemplate[]> getInstanceTemplates() {
		return instanceTemplates;
	}

}