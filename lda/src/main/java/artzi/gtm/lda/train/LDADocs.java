package artzi.gtm.lda.train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import artzi.gtm.lda.malletutils.MInstanceList;
import artzi.gtm.topicModelInfra.dataObjects.DocHeader;
import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;


public class LDADocs implements Serializable{

	private static final long serialVersionUID = 1L;

	ArrayList <DocHeader> docHeaders ; 
	int M$numOfDocs ; 
	int K$numOfTopics ; 
	double [][] docTopicProb ; 
	public LDADocs (int K$numOfTopics , MInstanceList mInstanceList , double [][] docTopicProb ) { 
		this.K$numOfTopics = K$numOfTopics ; 
		docHeaders= new ArrayList <DocHeader> () ; 
		for  (int docId = 0 ; docId <  mInstanceList.getInstanceList().size () ; docId ++) { 
			docHeaders.add( mInstanceList.getDocHeaders().get(docId))  ; 
		}
		M$numOfDocs = docHeaders.size() ; 
		this.docTopicProb = docTopicProb ; 
	}
	public IndxProb [] getTopicDocs (int topicIndx) { 
		IndxProb [] termProbArray  = new IndxProb [M$numOfDocs] ; 
		for (int docIndx = 0 ; docIndx < M$numOfDocs ; docIndx ++ ) {
			termProbArray [docIndx] = new IndxProb (docIndx,docTopicProb [docIndx][topicIndx]) ;   
		}			
		Arrays.sort ( termProbArray  ,  new CompareProb()) ;
		return termProbArray ; 
	}
	public ArrayList<DocHeader> getDocHeaders() {
		return docHeaders;
	}
	public int getM$numOfDocs() {
		return M$numOfDocs;
	}
	public int getK$numOfTopics() {
		return K$numOfTopics;
	}
	public double[][] getDocTopicProb() {
		return docTopicProb;
	}
	
}
		  
