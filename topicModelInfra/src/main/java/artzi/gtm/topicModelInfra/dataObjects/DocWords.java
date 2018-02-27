package artzi.gtm.topicModelInfra.dataObjects;

public class DocWords {
	int docId ; 
	int [] wordArray ; 
	
	public DocWords (int docId , int [] wordArray ) { 
		this.docId = docId ; 
		this.wordArray = wordArray ; 
	}

	public int getDocId() {
		return docId;
	}

	public int [] getWordArray() {
		return wordArray;
	}

	public int getNumOfWords() {
		return wordArray.length ; 
	}
}
