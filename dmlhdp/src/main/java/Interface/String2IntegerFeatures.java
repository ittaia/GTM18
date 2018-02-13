package Interface;

import java.util.ArrayList;

import artzi.gtm.topicModelInfra.dataObjects.ComponentFeatures;
import artzi.gtm.utils.elog.EL;

public class String2IntegerFeatures {
	public static  ArrayList<ComponentFeatures> 
		get( ArrayList<MixFeatures1> Stringfeatures , Template template) {
		ArrayList <ComponentFeatures> intfeatures = new ArrayList <ComponentFeatures> () ; 
		for (MixFeatures1 mf : Stringfeatures ) { 
			int Id = template.getValueLists().getId ( mf.getComponentName() ) ; 		 
			if (Id < 0) EL.WE(9873 , " Unknown Componenet " + mf.getComponentName()) ; 
			ComponentFeatures f = new ComponentFeatures (Id) ; 
			intfeatures.add(f) ; 
			
			for (String val : mf.getFeatures()) { 
				f.addFeature (template.valueLists.getValueList(Id).addTerm(val)) ; 
			}
		}
		return intfeatures ; 
	}
}