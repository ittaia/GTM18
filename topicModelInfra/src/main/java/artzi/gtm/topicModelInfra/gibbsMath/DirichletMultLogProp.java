package artzi.gtm.topicModelInfra.gibbsMath;

import artzi.gtm.topicModelInfra.counters.Counters;
import artzi.gtm.topicModelInfra.counters.DCounters;
import artzi.gtm.topicModelInfra.counters.FeatureCount;
import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;

public class DirichletMultLogProp {

	/*********************************************************************/ 
	/** set  topic proportions  according to Wang Blei 3.1 (7)          **/      
	/**                                      Mark Johnson CG168 Notes   **/ 
	/*********************************************************************/

	public  static double  contentObjectComponentMixLogProportion ( ComponentFeatures  contentObjectComponentFeatures , int mixfeaturesSum ,
			Counters mixfeaturesCounter , double beta , double betaNumOfFeatureValues  ) { 

		/*********************  Current topic  == current mix's topic          *************/ 
		

		//double  f1 = Functions.logGamma(mixfeaturesSum + betaNumOfFeatureValues)  ; 
		//double  f2 = Functions.logGamma(mixfeaturesSum + contentObjectComponentFeatures.getNumOfFeatures() + betaNumOfFeatureValues)  ;
		//double 	logProp=  f1-f2 ; 
		double logProp = -LogGammaFraction.get(mixfeaturesSum + betaNumOfFeatureValues ,contentObjectComponentFeatures.getNumOfFeaturesN()) ;  

		for (FeatureCount featureCount : contentObjectComponentFeatures.getFeaturesList() ) { 
			if (featureCount.getCount() > 0)  { 
				int feature = featureCount.getFeature() ; 
				//double f3 = Functions.logGamma(mixfeaturesCounter.get (feature) + featureCount.getCount() +  beta) ; 
				//double f4 = Functions.logGamma(mixfeaturesCounter.get (feature) + beta) ; 
				// logProp += (f3-f4)
				logProp += LogGammaFraction.get(mixfeaturesCounter.get (feature) + beta , featureCount.getCount()) ; 
			}
		}
		return logProp ; 
	}
	public  static double  contentObjectComponentNewMixLogProportion ( ComponentFeatures  contentObjectComponentFeatures , double beta , 
			double betaNumOfFeatureValues  ) { 		

		//double f1 = Functions.logGamma(betaNumOfFeatureValues) ; 
		//double f2 = Functions.logGamma(betaNumOfFeatureValues +contentObjectComponentFeatures.getNumOfFeatures())  ; 
		//double logProp = f1-f2 ; 
		double logProp =  -LogGammaFraction.get (betaNumOfFeatureValues ,  contentObjectComponentFeatures.getNumOfFeaturesN()) ; 
		for (FeatureCount featureCount : contentObjectComponentFeatures.getFeaturesList()  ) { 
			if (featureCount.getCount() > 0)  { 		

				//double f3 = Functions.logGamma(featureCount.getCount() + beta) ; 
				//double f4 = Functions.logGamma( beta) ; 
				//logProp += f3-f4 ; 
				logProp += LogGammaFraction.get(beta , featureCount.getCount()) ; 
			}
		}
		return logProp ; 
	}
	/******  article  
	 * @throws Exception ******/
	public  static double  contentObjectComponentMixLogProportion ( ComponentFeatures  contentObjectComponentFeatures , 
			int mixfeaturesSum ,
			DCounters mixfeaturesCounter , double [] stickBreakingWeights , double alpha0  ) throws Exception { 

		/*********************  Current topic  == current mix's topic          *************/ 

		//double  f1 = Functions.logGamma(mixfeaturesSum + alpha0)  ; 
		//double  f2 = Functions.logGamma(mixfeaturesSum + contentObjectComponentFeatures.getNumOfFeatures() + alpha0)  ;
		//double 	logProp=  f1-f2 ; 
		double logProp = - LogGammaFraction.get(mixfeaturesSum + alpha0 , contentObjectComponentFeatures.getNumOfFeaturesN() ) ;
		for (FeatureCount featureCount : contentObjectComponentFeatures.getFeaturesList() ) { 
			if (featureCount.getCount() > 0)  { 
				int feature = featureCount.getFeature() ; 
		        //double f3 = Functions.logGamma(mixfeaturesCounter.get (feature) + featureCount.getCount() + 
				// (alpha0*stickBreakingWeights[feature])) ; 
				//double f4 = Functions.logGamma(mixfeaturesCounter.get (feature) + (alpha0*stickBreakingWeights[feature]))) ; 
				//logProp += (f3-f4) ; 
				logProp += LogGammaFraction.get (mixfeaturesCounter.get (feature) +
						(alpha0*stickBreakingWeights[feature]) ,featureCount.getCount() ) ; 
			}
		}
		return logProp ; 
	}
	/******  article  ******/
	public  static double  contentObjectComponentNewMixLogProportion ( ComponentFeatures  contentObjectComponentFeatures ,
			double  [] stickBreakingWeights , double alpha0  ) { 		

		//double f1 = Functions.logGamma (alpha0) ; 
		//double f2 = Functions.logGamma (alpha0 +contentObjectComponentFeatures.getNumOfFeatures())  ; 
		//double logProp = f1-f2 ; 
		double logProp =  -LogGammaFraction.get (alpha0 ,  contentObjectComponentFeatures.getNumOfFeaturesN()) ; 
		for (FeatureCount featureCount : contentObjectComponentFeatures.getFeaturesList()  ) { 
			if (featureCount.getCount() > 0)  { 		
				int newMix = stickBreakingWeights.length-1 ; 
				//double f3 = Functions.logGamma(featureCount.getCount() + (alpha0*stickBreakingWeights[newMix])) ; 
				//double f4 = Functions.logGamma( (alpha0*stickBreakingWeights[newMix])) ; 
				//logProp += f3-f4 ; 
				logProp += LogGammaFraction.get((alpha0*stickBreakingWeights[newMix]) , featureCount.getCount()) ; 
			}
		}
		return logProp ; 
	}
	
}
