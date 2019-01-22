package artzi.gtm.lda.classify;

import artzi.gtm.utils.io.SaveObject;
import artzi.gtm.utils.termList.TermList;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import artzi.gtm.lda.malletutils.Malphabet;

public class TopicClassifier {

	TopicInferencer inferencer = null ; 
	Alphabet alphabet ; 
	
	public TopicClassifier ( String path , TermList termList ) throws Exception { 
		inferencer = (TopicInferencer)SaveObject.read (path + "\\topicInferencer") ; 
		Malphabet mAlphabet = new Malphabet() ; 
		mAlphabet.init (termList) ; 
		alphabet =mAlphabet.getAlphabet() ; 
	}
	public double[] classify(int[] wordArray  ) {
		FeatureSequence wordFeatureSequence = new FeatureSequence(alphabet , wordArray) ; 			  
		Instance instance = new Instance (wordFeatureSequence  , null , null , null); 
		return inferencer.getSampledDistribution(instance, 50 , 5 , 10) ; 
	}
}