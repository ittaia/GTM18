package Interface;

import java.util.ArrayList;
import artzi.gtm.utils.termList.Term2Id;

public class ValueLists {
	ArrayList <String> listNames  ; 
	ArrayList <Term2Id> valueList ; 
	public ValueLists () { 
		listNames = new ArrayList <String> () ; 
		valueList = new ArrayList <Term2Id> () ; 
	}
	public void addList (String name) { 
		listNames.add(name) ; 
		valueList.add (new Term2Id ()) ; 		
	}
	public String getName (int Id) { 
		return listNames.get (Id) ; 
	}
	public int getId(String name) {
		int Id = -1 ; 
		for (int i = 0 ; i < listNames.size () ; i ++) { 
			if (listNames.get(i).equals(name)) { 
				Id = i ;  
				break ; 
			}
		}
		return Id ; 
	}
	public Term2Id getValueList (int Id) { 
		return (valueList.get(Id)) ; 
	}
	public Term2Id getValueList (String name) { 
		Term2Id r = null ; 
		for (int i = 0 ; i < listNames.size () ; i ++) { 
			if (listNames.get(i).equals(name)) { 
				r = valueList.get(i) ; 
				break ; 
			}
		}
		return r ; 		
	}
	public int getNumOfLists() {
		return listNames.size () ; 
	}
	public int getNumOfValues (int Id) { 
		return valueList.get(Id).getSize() ; 
	}
}
