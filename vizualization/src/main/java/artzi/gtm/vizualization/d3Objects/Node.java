package artzi.gtm.vizualization.d3Objects;

public class Node {
	
	int id ; 
	String header ; 
	Double x ; 
	Double  y ; 
	String [] terms ; 
	Double fx = null ; 
	Double fy = null ; 
	int group ; 
	public Node (int id , double x , double y  , String header , String [] terms , int group) { 
		this.id = id ; 
		this.x = x ; 
		this.y = y ; 
		this.header = header ;	
		this.terms = terms ; 
		this.group = group ; 
	}
	public void setxy () { 
		final int shift = 10 ; 
		final int scale = 200 ;  
		x =  (x+shift)*scale ; 
		y =  (y+shift)*scale ; 
	}
	public void setFxy () { 
		final int shift = 10 ; 
		final int scale = 100 ;  
		fx =  (x+shift)*scale ; 
		fy =  (y+shift)*scale ; 
	}
}