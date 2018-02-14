package artzi.gtm.dmlhdp.appInterface;

import java.io.Serializable;
import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class ModelDoc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ID ; 
	int numOfSlots ; 

	ArrayList <ArrayList <ModelVal> >  clustersVals ;  
	int vals = 0 , dup1 = 0 ,dup2 = 0 ; 
	
	public ModelDoc(String ID , int numOfSlots ) {
		this.ID = ID ; 
		this.numOfSlots = numOfSlots ; 
		clustersVals = new ArrayList <ArrayList <ModelVal> >  () ;  
		for (int slot = 0 ; slot < numOfSlots ; slot ++ )  { 
			clustersVals.add (new ArrayList <ModelVal>() )  ; 			
		}
	}

	public void addVal(int slot, ModelVal val) {
		ArrayList <ModelVal> slotVals = clustersVals.get (slot) ; 
		boolean dup = false ; 
		for (ModelVal v : slotVals) { 
			if (v.getName().equals(val.getName()) ) {  
				dup = true ; 
				v.setMaxProb (val.getProb()) ; 
				break ; 
			}
		}
		if (!dup )  slotVals.add(val) ;	
		else ; //System.out.println (" dup: " + val.getName()) ; 
	}
	
	public String getID() {
		return ID;
	}

	public ArrayList<ArrayList<ModelVal>> getClustersVals() {
		return clustersVals;
	}

	public void print() {
		EL.W("--- > Model Doc " + ID);
		for (int cluster = 0 ; cluster < numOfSlots ; cluster ++ ) { 
			if (clustersVals.get(cluster).size () > 0 ) { 
				String s = " Slot -- > " + cluster ; 		 
				for (ModelVal sv : clustersVals.get(cluster)) { 
		    		s += " \"" + sv.getName() + "\"" + sv.getProb() + " ; " ; 		    	  
				}
				EL.W(s) ; 
			}
		}		
	}

	public int getVals() {
		return vals;
	}

	public int getDup1() {
		return dup1;
	}

	public int getDup2() {
		return dup2;
	}	
}
