package artzi.gtm.topicModelInfra.counters;
 

import java.io.Serializable;

import artzi.gtm.utils.elog.EL;

public class Counters implements Serializable{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public   int maxX , maxY ; 
	public int [][] mat ;
	public int dimention = 0 ; 
	public Counters (int maxX, int maxY) {
		this.dimention = 2 ;  
		this.maxX = maxX ; 
		this.maxY = maxY ; 
		mat = new int [maxX+1][maxY+1]; 
		for (int x = 0 ; x <= maxX ; x ++) { 
			for (int y = 0 ; y <= maxY ; y ++ ) { 
				mat[x][y] = 0 ; 
			}
		}
	}
	public Counters (int maxX) {
		this.dimention = 1; 
		this.maxX = maxX ; 
		this.maxY = 0; 
		mat = new int [maxX+1][maxY+1]; 
		for (int x = 0 ; x <= maxX ; x ++) { 
			for (int y = 0 ; y <= maxY ; y ++ ) { 
				mat[x][y] = 0 ; 
			}
		}
	}
	public void add1 (int x , int y ) { 
		if (dimention != 2 ) { 
			EL.WE(7661 ,  " Counter dimention error " ) ; 
		}
		 mat[x][y]++ ; 
	}	 
	
	public  void add1(int x) {
		if (dimention != 1 ) { 
			EL.WE(7662 ,  " Counter dimention error " ) ; 
		}
		mat[x][0] ++ ; 
	}
	public void dec1(int x, int y) {
		if (dimention != 2 ) { 
			EL.WE(7663 ,  " Counter dimention error " ) ; 
		}
		mat[x][y] -- ; 
		if (mat[x][y] < 0 ) { 
			EL.WE(67 , " Negative Counter " +  x + " - " + y);
		}
	}
	public void dec1(int x) {
		if (dimention != 1 ) { 
			EL.WE(7664 ,  " Counter dimention error " ) ; 
		}
		mat[x][0] -- ;  		
		if (mat[x][0] < 0 ) { 
			EL.WE(7665 , " Negative Counter " +  x);
		}
	}
	public void addZ (int x , int y ,  int z) { 
		if (dimention != 2 ) { 
			EL.WE(7666 ,  " Counter dimention error " ) ; 
		}
		mat[x][y]+= z ; 
	}	 
	
	public  void addZ(int x , int z) {
		if (dimention != 1 ) { 
			EL.WE(7667 ,  " Counter dimention error " ) ; 
		}
		mat[x][0] += z ; 
	}
	public void decZ(int x, int y , int z) {
		if (dimention != 2 ) { 
			EL.WE(7668 ,  " Counter dimention error " ) ; 
		}
		mat[x][y] -= z ; 
		if (mat[x][y] < 0 ) { 
			EL.WE(6799 , " Negative Counter " +  x + " - " + y);
			//throw new Exception () ; 
		}
	}
	public void decZ(int x , int z) {
		if (dimention != 1 ) { 
			EL.WE(7669 ,  " Counter dimention error " ) ; 
		}
		mat[x][0] -=z  ; 
		if (mat[x][0] < 0 ) { 
			EL.WE(6798 , " Negative Counter " +  x);
		}
	}
	public int get(int x, int y) {
		if (dimention != 2 ) { 
			EL.WE(7660 ,  " Counter dimention error " ) ; 
		}
		return mat [x][y] ; 
	}
	public void print () { 
		EL.W( "max x " + maxX + " max y" + maxY  ) ; 
		for (int x = 0 ;  x <= maxX ; x ++ ) { 
			if (maxY < 50 ) { 
				String s = " Counters :" + x + " --> "   ; 
				for  (int y = 0 ;   y <= maxY ; y ++  ) { 
					s+=  + y + ": " +  mat[x][y] + "; "  ; 
				}			 
				EL.W(s) ; 
			}
			else { 
				for (int y = 0 ;   y <= maxY ; y ++  ) { 				  
					EL.W("Counter " + x+","+y+" - " + mat[x][y] ) ; 
				}
			}
		}		
	}
	public int get(int x) {
		if (dimention != 1 ) { 
			EL.WE(7671 ,  " Counter dimention error " ) ; 
		}
		 return mat[x][0] ; 
	}
	public void copy(Counters inCounters) {
		for (int x = 0 ; x <= maxX ; x ++) { 
			for (int y = 0 ; y <= maxY ; y ++ ) { 
				mat[x][y] = inCounters.getMat () [x][y] ;  ; 
			}
		}		
	}
	private int[][] getMat() {
		return this.mat ; 
	}
	public void addDelta(Counters newCounters ,Counters baseCounters  ) {
		for (int x = 0 ; x <= maxX ; x ++) { 
			for (int y = 0 ; y <= maxY ; y ++ ) { 
				mat[x][y] = mat [x][y] + (newCounters.getMat () [x][y] - baseCounters.getMat() [x][y])  ;  
				if (mat [x][y] < 0) EL.WE(7699 , " Negative Counter x: " + x + " y:" +  y + " val " + mat [x][y]) ; 
			}
		}		
	}
	public void copyColl(int targetY, Counters inCounters , int sourceY) {
		for (int x = 0 ; x <= maxX ; x ++) { 
			mat[x][targetY] = inCounters.getMat () [x][sourceY] ;   
		}
	}
	public void copyLine(int targetX, Counters inCounters, 	int sourceX) {
		for (int y = 0 ; y <= maxY ; y ++) { 
			mat[targetX][y] = inCounters.getMat () [sourceX][y] ;   
		}
		
	}
	public void copyCell(int targetX, Counters inCounters , int sourceX) {
		if (dimention != 1 ) { 
			EL.WE(6661 ,  " Counter dimention error " ) ; 
		}
		mat [targetX][0] = inCounters.get(sourceX) ; 		
	}
	public void copyCell(int targetX , int targetY , Counters inCounters , int sourceX , int sourceY) { 
		mat[targetX][targetY] = inCounters.get(sourceX , sourceY) ;	
	}
}
