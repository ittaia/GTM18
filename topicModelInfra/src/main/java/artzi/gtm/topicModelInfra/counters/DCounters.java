package artzi.gtm.topicModelInfra.counters; 
import java.io.Serializable;

import artzi.gtm.utils.elog.EL;



public class DCounters implements Serializable{	
	private static final int chunkLen = 20 ; 
	private static final long serialVersionUID = 1L;
	private  int maxX , maxY ; 
	private  int lenX , lenY ; 
	private int [][] mat ;
	private int dimention = 0 ; 
	public DCounters (int maxX, int maxY) {
		this.dimention = 2 ;  
		this.maxX = maxX ; 
		this.maxY = maxY ; 
		lenX = getChunk (maxX) ; 
		lenY = getChunk (maxY) ; 
		mat = new int [lenX][lenY]; 
		for (int x = 0 ; x < lenX ; x ++) { 
			for (int y = 0 ; y <  lenY ; y ++ ) { 
				mat[x][y] = 0 ; 
			}
		}
	}
	private int getChunk(int max) {
		int chunks = ((max+1) /chunkLen)+1 ; 
		int len = chunks * chunkLen ; 
		return len;
	}
	public DCounters (int maxY) {
		this.dimention = 1; 
		this.maxX = 0 ; 
		this.maxY = maxY; 
		lenX = 1 ; 
		lenY = getChunk (maxY) ; 
		mat = new int [lenX][lenY]; 
		for (int x = 0 ; x < lenX ; x ++) { 
			for (int y = 0 ; y <  lenY ; y ++ ) { 
				mat[x][y] = 0 ; 
			}
		}
	}	
	
	private void extendMat(int newMaxX, int  newMaxY) {		
		if (maxX < newMaxX )  { 
			maxX = Math.max(maxX, newMaxX) ; 
			lenX = getChunk(maxX) ; 
		}
		if (maxY < newMaxY )  { 
			maxY = Math.max(maxY, newMaxY) ; 
			lenY = getChunk(maxY) ; 
		}
		if (lenX > mat.length | lenY > mat[0].length ) { 
			int [][] newMat = new int [lenX][lenY] ; 
			matCopy (newMat , mat) ; 
			this.mat = newMat ; 
		}
	}					
	 
	private void matCopy(int[][] newMat, int[][] oldMat) {
		for (int x = 0 ; x < oldMat.length ; x ++ ) { 
			System.arraycopy(oldMat[x] , 0 , newMat[x] , 0 , oldMat[x].length) ; 
			for (int y = oldMat[x].length ; y < newMat[x].length ; y ++) { 
				newMat [x][y] = 0 ; 
			}
		}
		for (int x = oldMat.length ;   x < newMat.length ; x ++ ) { 
			for (int y = 0 ; y < newMat[x].length ; y ++) { 
				newMat [x][y] = 0 ; 
			}
		}		
	}
	public void add1 (int x , int y ) throws Exception { 		
		if (dimention != 2 ) { 
			EL.WF(6661 ,  " Counter dimention error " ) ; 
		}
		extendMat (x , y) ; 
		mat[x][y]++ ; 
	}	
	public  void add1(int y) throws Exception {
		if (dimention != 1 ) { 
			EL.WF(6662 ,  " Counter dimention error " ) ; 
		}
		extendMat (0 , y) ; 
		mat[0][y] ++ ; 
	}
	public void dec1(int x, int y) throws Exception {
		if (dimention != 2 ) { 
			EL.WF(6663 ,  " Counter dimention error " ) ; 
		}
		extendMat (x , y) ; 
		mat[x][y] -- ; 
		if (mat[x][y] < 0 ) { 
			EL.WF(6771 , " Negative Counter " +  x + " - " + y);
		}
	}
	public void dec1(int y) throws Exception {
		if (dimention != 1 ) { 
			EL.WE(6664 ,  " Counter dimention error " ) ; 
		}
		extendMat (0 , y) ; 		 
		mat[0][y] -- ; 
		if (mat[0][y] < 0 ) { 
			EL.WF(6772 , " Negative Counter " +  y);
		}
	}
	public void addZ (int x , int y ,  int z) throws Exception { 
		if (dimention != 2 ) { 
			EL.WF(6665 ,  " Counter dimention error " ) ; 
		}
		extendMat (x , y) ; 
		mat[x][y] += z ; 
		if (mat[x][y] < 0 ) { 
			EL.WF(6773 , " Negative Counter " +  x + " - " + y);
		}
	}	 
	public  void addZ(int y , int z) throws Exception {
		if (dimention != 1 ) { 
			EL.WF(6665 ,  " Counter dimention error " ) ; 
		}
		extendMat (0 , y) ; 
		mat[0][y] += z ; 
		if (mat[0][y] < 0 ) { 
			EL.WF(6784 , " Negative Counter " +  y);
		}	
	}
	public void decZ(int x, int y , int z) throws Exception {
		if (dimention != 2 ) { 
			EL.WF(6666 ,  " Counter dimention error " ) ; 
		}
		extendMat (x , y) ; 
		mat[x][y] -= z ; 
		if (mat[x][y] < 0 ) { 
			EL.WF(6783 , " Negative Counter " +  x + " - " + y);
		}
	}
	public void decZ(int y , int z) throws Exception {
		if (dimention != 1 ) { 
			EL.WF(6667 ,  " Counter dimention error " ) ; 
		}
		extendMat (0 , y) ; 
		mat[0][y] -= z ; 
		if (mat[0][y] < 0 ) { 
			EL.WF(6774 , " Negative Counter " +  y);
		}
	}
	public int get(int x, int y) throws Exception {
		if (x > 0 & dimention != 2 ) { 
			EL.WF(666 ,  " Counter dimention error " ) ; 
		}
		if (x > maxX | y > maxY ) return 0 ; 
		return mat[x][y] ; 
	}
	public int get(int y) throws Exception {
		if (dimention != 1 ) { 
			EL.WF(6660 ,  " Counter dimention error " ) ; 
		}
		if (y > maxY  ) return 0 ; 
		return mat[0][y]   ; 
	}
	public void print () { 
		EL.W( "max x " + maxX + " max y" + maxY  ) ; 
		for (int x = 0 ;  x <= maxX ; x ++ ) { 
			if (mat.length < 50 ) { 
				String s = " Counters :" + x + " --> "   ; 
				for  (int y = 0 ;   y <=maxY ; y ++  ) { 
					s+=  + y + ": " +  mat[x][y] + "; "  ; 
				}			 
				EL.W(s) ; 
			}
			else { 
				for (int y = 0 ;   y < maxY ; y ++  ) { 				  
					EL.W("Counter " + x+","+y+" - " + mat[x][y] ) ; 
				}
			}
		}		
	}
	public int getMaxX() {
		//EL.WE( 9876 , " Get Size  Max X " + maxX + " size " + mat.size () ) ; 
		return maxX ; 
	}
	public int getMaxY() {
		return maxY ;  
	}
	
	public int getXSize() {
		return mat.length; 
	}
	public int getYSize() {
		return mat[0].length ; 
	}
	public int getYSize(int x) {
		return mat[x].length ; 
	}
	public void copy(DCounters inCounters) throws Exception {
		int maxInX = inCounters.getMaxX() ; 
		int maxInY = inCounters.getMaxY() ; 
		extendMat (maxInX , maxInY) ; 
		for (int x = 0 ; x <= maxInX ; x ++) { 
			for (int y = 0 ; y <= maxInY ; y ++ ) { 
				mat[x][y] = inCounters.get(x,y) ; 
			}				
		}		
	}
	public void copyCell(int targetY, DCounters inCounters , int sourceY) throws Exception {
		extendMat (0 , targetY) ; 
		if (dimention != 1 ) { 
			EL.WE(6661 ,  " Counter dimention error " ) ; 
		}
		mat[0][targetY] = inCounters.get(sourceY) ; 	
	}
	public void copyCell(int targetX , int targetY , DCounters inCounters , int sourceX , int sourceY) throws Exception { 
		extendMat (targetX , targetY) ;
		if (dimention != 2 ) { 
			EL.WE(6669 ,  " Counter dimention error " ) ; 
		}
		mat[targetX][targetY] =  inCounters.get(sourceX , sourceY) ; 	
	}	
	
	public void addDelta(DCounters newCounters ,DCounters baseCounters  ) throws Exception {
		int maxInX = newCounters.getMaxX () ;  
		int maxInY = newCounters.getMaxY() ; 
		extendMat (maxInX , maxInY) ; 
		for (int x = 0 ; x <= maxX ; x ++) { 
			for (int y = 0 ; y <= maxY ; y ++ ) {
				int delta =  newCounters.get (x ,y) - baseCounters.get (x ,  y) ; 
				mat[x][y] += delta ; 
			}
		}		
	}
	public int[][] getMat() {
		return this.mat ; 
	}
	public int getDim() {
		return this.dimention ; 		 
	}
}