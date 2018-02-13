package DHDP;

import java.util.ArrayList;

import artzi.gtm.topicModelInfra.dataObjects.ContentObject;

public class HDPState {

	public int level ; 
	public int levels ; 
	public int numOfGroups ; 
	public int numOfMixs ;	
	int numOfContentObjects ; 
	public ArrayList <MixtureComponent> mixs ; 
	public ArrayList <ContentObject> contentObjects ;
	 
	public int numOfNextLevelMixs ; 
	int [] numOfNextLevelContentObjects ; 
	public double gammaTop ; 
	public double gamma; 	
	public double alpha0; 
	public HDPState (int level , int levels ,double gammaTop , double gamma , double alpha0) { 
		this.level = level ; 
		this.levels = levels ; 
		this.gamma = gamma ; 
		this.gammaTop = gammaTop ; 
		this.alpha0 = alpha0 ; 
		mixs = new ArrayList <MixtureComponent> () ; 
		contentObjects = new ArrayList <ContentObject> () ; 	
		this.numOfContentObjects = 0 ; 
	}
}