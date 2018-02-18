package artzi.gtm.muc.eval;

import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class TestDoc {
	String ID ; 
	ArrayList <ArrayList <ORVals> >  clustersVals ;  
	ArrayList <ArrayList <ORVals> >  slotsVals ;  
	
	public TestDoc(String ID) {
		this.ID = ID ; 
		clustersVals = new ArrayList <ArrayList <ORVals> >  () ; 
		for (int  template = 0 ; template < TemplateNames.numOfTemplates ; template ++   ) {
			for (int slot = 0 ; slot < SlotNames.numOfSlots ; slot ++ )  { 
			clustersVals.add (new ArrayList <ORVals>() )  ; 
			}
		}
		slotsVals = new ArrayList <ArrayList <ORVals> >  () ; 
		for (int slot = 0 ; slot < SlotNames.numOfSlots ; slot ++ )  { 
			slotsVals.add (new ArrayList <ORVals>() )  ; 
		}		 
	}

	public void addVal(int template, int slot, String data, boolean optional) {
		ArrayList <ORVals> slotVals = slotsVals.get (slot) ; 
		ORVals orVals = new ORVals (data , optional) ;  
		if (!(orVals.isEmpty())) slotVals.add(orVals) ;	
		int cluster = (SlotNames.numOfSlots * template) + slot ; 
		ArrayList <ORVals> clusterVals = clustersVals.get (cluster) ; 
		if (!(orVals.isEmpty())) clusterVals.add(orVals) ;		
	}	

	public String getID() {
		return ID;
	}

	public ArrayList<ArrayList<ORVals>> getClusterVals() {
		return clustersVals;
	}
	public ArrayList<ArrayList<ORVals>> getSlotVals() {
		return slotsVals;
	}


	public void print() {
		EL.WE ( 9999 , "--- > Test Doc " + ID);
		for (int template = 0 ; template < TemplateNames.numOfTemplates; template ++ ) { 
			for (int slot = 0 ; slot < SlotNames.numOfSlots ; slot ++ ) { 
				EL.W(" Template " + TemplateNames.names [template] + " Slot -- > " + SlotNames.names[slot] );
				int cluster = (template*SlotNames.numOfSlots) + slot ; 
				for (ORVals sv : clustersVals.get(cluster)) { 
					sv.print () ; 
				}
			}		
		}
	}
}
