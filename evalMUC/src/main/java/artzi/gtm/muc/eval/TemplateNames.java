package artzi.gtm.muc.eval;

import artzi.gtm.utils.elog.EL;

public class TemplateNames {
	public static int numOfTemplates = 5 ; 
	public static int templateAttack = 0 ;    
	public static int templateBomb = 1 ;
	public static int templateKidnap = 2 ; 
	public static int templateArson = 3 ; 
	public static int templateOther = 4 ; 
	static String [] names = {"Attack" , "Bomb" , "Kidnap" , "Arson" , "Other"} ;
	public static int getTemplateIndx(String data) {
		if (data.contains ("ATTACK")) return templateAttack ;  
		if (data.contains ("BOMB")) return templateBomb ;  
		if (data.contains ("KIDNAP")) return templateKidnap ;
		if (data.contains ("ARSON")) return templateArson ;
		EL.WE(88990 ,  data) ;  
		return templateOther ;  		
	} 
}
