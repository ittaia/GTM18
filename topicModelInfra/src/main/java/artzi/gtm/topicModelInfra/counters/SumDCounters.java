package artzi.gtm.topicModelInfra.counters; 

import java.io.Serializable;

import artzi.gtm.utils.elog.EL;

public class SumDCounters implements Serializable{	
	private static final int chunkLen = 20 ; 
	private static final long serialVersionUID = 1L;
	private  int maxX , maxY ; 
	private  int lenX , lenY ; 
	private int [][] mat ;
	private int dimention = 0 ; 
	private int numOfSums = 0 ; 
	public SumDCounters (int maxX, int maxY) {
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
	public SumDCounters (int maxY) {
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
	public void sum (DCounters inCounters ) throws Exception { 		
		if (dimention != inCounters.getDim() ) { 
			EL.WF(6661 ,  " SUm Counter dimention error. Input Dim: "+ inCounters.getDim() ) ; 
		}
		extendMat (inCounters.getMaxX() , inCounters.getMaxY()) ; 
		int [][] inMat = inCounters.getMat() ; 
		for (int x = 0 ; x <= inCounters.getMaxX() ; x ++ ) { 
			for (int y = 0 ; y <= inCounters.getMaxY() ; y ++) { 
				mat[x][y] += inMat[x][y] ; 
			}
		}
		numOfSums ++ ; 		 
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
	public double getAverage (int x, int y) throws Exception {
		if (x > 0 & dimention != 2 ) { 
			EL.WF(666 ,  " Counter dimention error " ) ; 
		}
		if (x > maxX | y > maxY ) return 0 ;
		double c = mat[x][y] ;
		return c/numOfSums ; 
	}
	public double  getAverage (int y) throws Exception {
		if (dimention != 1 ) { 
			EL.WF(6660 ,  " Counter dimention error " ) ; 
		}
		if (y > maxY  ) return 0 ; 
		double c =  mat[0][y]   ; 
		return c/numOfSums ; 
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
	public int[][] getMat() {
		return this.mat ; 
	}
	public int getNumOfSums () { 
		return this.numOfSums ; 
	}
}