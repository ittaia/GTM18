package artzi.gtm.mltm.train;

import java.util.ArrayList;

import artzi.gtm.utils.sortProb.IndxProbText;

//import SortProb.IndxProbText;

public class Topic100 {
	private int topic;
	private String name;
	private ArrayList<IndxProbText> probs;
//	private ArrayList<IndxProb> docs;
	
	public Topic100(int _topic, String _name){
		this.topic = _topic;
		this.name = _name;
		this.probs = new ArrayList<IndxProbText>();
//		this.docs = new ArrayList<IndxProb>();
	}
	
	public void addProb(IndxProbText _prob){
		this.probs.add(_prob);
	}

//	public void addDoc(IndxProb _doc){
//		this.docs.add(_doc);
//	}
	
	public int getTopic() {
		return topic;
	}

	public String getName() {
		return name;
	}

	public ArrayList<IndxProbText> getProbs() {
		return probs;
	}

//	public ArrayList<IndxProb> getDocs() {
//		return docs;
//	}
	@Override
	public String toString() {
		
		return "Topic="+this.topic;
	}
	
}
