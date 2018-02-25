package artzi.gtm.dmlhdp.mlhdp;

import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;

import artzi.gtm.utils.io.JsonIO;

public class MLHDPRan {	 
    
	int levels ; 
	int [] numOfMixes ; 
	double likelihood ; 
	ArrayList <Double> likelihoods ;  
	public MLHDPRan(int levels) {
		super();		 
		this.levels = levels;
		numOfMixes = new int [levels] ; 
		likelihoods = new ArrayList <Double> () ; 
	}
	public int getLevels() {
		return levels;
	}
	
	public int[] getNumOfMixes() {
		return numOfMixes;
	}
	public void setNumOfMixes(int []   numOfMixes ) {
		this.numOfMixes   = numOfMixes ;
	}
	public double getLikelihood() {
		return likelihood;
	}
	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}
	public ArrayList<Double> getLikelihoods() {
		return likelihoods;
	}
	public void addLikelihood (double likelihood) { 
		likelihoods.add (likelihood) ; 
	}
	
	public void save (String path) throws IOException { 
		Gson gson = new Gson () ; 
		String s = gson.toJson(this) ; 		
		JsonIO.write(path, s) ; 
	}	
}
