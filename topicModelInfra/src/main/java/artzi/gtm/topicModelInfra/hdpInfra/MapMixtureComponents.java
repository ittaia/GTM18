package artzi.gtm.topicModelInfra.hdpInfra;

import java.util.ArrayList;

public class MapMixtureComponents {
	
	ArrayList <Integer>    rootToThread ; 
	ArrayList <Integer>    threadToRoot ; 
	public MapMixtureComponents () {
		rootToThread = new ArrayList <Integer> () ; 
		threadToRoot=  new ArrayList <Integer> () ; 
		rootToThread.add(0) ;
		threadToRoot.add(0) ; 
	}
	public void mapMixs (int rootMix , int threadMix) { 
		while (rootMix >= rootToThread.size()) { 
			rootToThread.add(rootToThread.size() ) ; 			
		}
		while (threadMix >= threadToRoot.size()) { 
			threadToRoot.add( threadToRoot.size() ) ; 			
		}		 
		rootToThread.set(rootMix, threadMix ) ;   
		threadToRoot.set(threadMix, rootMix ) ; 
	}
	public int getThreadMix (int rootMix) { 
		return rootToThread.get (rootMix) ; 
	}
	public int getRootMix (int threadMix) { 
		return threadToRoot.get(threadMix) ; 
	}
	public int getNumOfMixs () { 
		return rootToThread.size()  ; 
	}
}
