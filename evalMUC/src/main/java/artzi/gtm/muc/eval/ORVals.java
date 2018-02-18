package artzi.gtm.muc.eval;

import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class ORVals {	
	ArrayList <String> vals ;   
	boolean optional ; 
	public ORVals(String data, boolean optional) {
		vals = new ArrayList <String> () ; 
		if (data.substring(0,1).equals("?")) { 
			this.optional = true ; 			
		}
		else { 
			this.optional = optional ; 
		}		
		data = data.replace("\\\"" , "**") ; 
		int pos = 0 ; 
		while (pos < data.length())  { 
			int start = data.substring(pos).indexOf("\"") ; 
			if (start < 0 ) break ; 
			int end = data.substring(pos + start +1 ).indexOf("\"") ; 
			if (end < 0) EL.WE(8877 , "Bad line " + data + "pos " + pos + "  Start " + start )  ; 
			String val = data.substring(pos + start+ 1 , pos + start + end +1 ) ; 
			vals.add(val) ; 
			pos = pos + start + end +2 ; 
		} 
	}
	public ArrayList<String> getVals() {
		return vals;
	}
	public boolean isOptional() {
		return optional;
	}
	public void print() {
		String s = "Test names:  "  ; 
		if (optional ) s = "? " ; 
		for (String v : vals) s += "\"" + v + "\" " ; 
		EL.W(s);
	}
	public String getString() {
		String s = "  "  ; 
		if (optional ) s = "? " ; 
		for (String v : vals) s += "\"" + v + "\" " ; 
		return s ; 
	}
	public boolean isEmpty () { 
		return (vals.size() == 0) ; 
	}
}
