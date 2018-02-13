package Interface;

public class Template {
	String name ; 
	int level ; 
	ValueLists valueLists ; 
	
	public Template (int  level , String name) { 
		this.level = level ; 
		this.name = name ; 
		valueLists = new ValueLists () ; 
	}
	public void addValueList (String name) { 
		valueLists.addList(name) ; 
	}
	public ValueLists getValueLists() {
		return this.valueLists ; 
	}
	public String getName () { 
		return this.name ; 
	}
	public int getNumOfLists () { 
		return valueLists.getNumOfLists () ;  
	}
	public int getNumOfValues (int Id) { 
		return valueLists.getNumOfValues(Id) ; 
	}
	

}
