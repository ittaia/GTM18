
package artzi.gtm.lda.malletutils;

import java.io.Serializable;
import java.util.Vector;

import artzi.gtm.topicModelInfra.dataObjects.DocHeader;
import cc.mallet.types.Alphabet;
 
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class MInstanceList implements Serializable{
	
	private static final long serialVersionUID = 1L;
	InstanceList instanceList ; 
	Malphabet termAlphabet ; 
	Alphabet alphabet   ;
	Vector <DocHeader> docHeaders ; 

	public MInstanceList  (Malphabet termAlphabet) {  
		this.termAlphabet = termAlphabet ; 
		this.alphabet = termAlphabet.getAlphabet () ;  
		instanceList = new InstanceList(alphabet , null)  ;
		docHeaders = new Vector <DocHeader> () ; 
	}
	public void add (DocHeader docHeader, int [] wordArray ) {
		Instance instance =  getInstance (wordArray) ; 
		instanceList.add(instance) ; 
		docHeaders.add(docHeader) ; 
	}
	
	public InstanceList getInstanceList () { 
		return instanceList ; 
	}
	
	public Vector <DocHeader> getDocHeaders () { 
		return this.docHeaders ; 
	}
	private Instance getInstance (int []  wordArray ) {		   
				
		FeatureSequence wordFeatureSequence = new FeatureSequence(alphabet , wordArray) ; 			  
		Instance rInstance = new Instance (wordFeatureSequence  , null , null , null); 
		return rInstance ; 
	}
}
