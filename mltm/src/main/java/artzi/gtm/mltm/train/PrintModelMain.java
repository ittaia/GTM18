package artzi.gtm.mltm.train;

import java.io.File;

import com.google.gson.Gson;
import artzi.gtm.mltm.mlshdp.MLSHDPParms;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.io.Dirs;

public class PrintModelMain {
	
	static String path = "C:\\TestDir\\MLTM" ;  
		
	static MLTMTrain mltmTrain ; 
	static MLSHDPParms parms ;
	static Config config ; 
	static Gson gson ; 
	
	public static void main(String[] args) throws Exception {
		System.out.println ("Start") ; 
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		File f = new File (config.getPath("Model"), "tmodel.json") ; 
		String s = Dirs.file2String(f) ; 
		MLTMTrainedModel tmodel = gson.fromJson(s, MLTMTrainedModel.class) ; 
		tmodel.printTopics();
		//tmodel.print2Levels();  
	}	
}