package artzi.gtm.mltm.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import artzi.gtm.mltm.mlshdp.MLSHDPParms;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.Dirs;

public class TrainMainNips {
	
	static String path = "C:\\TestDir\\MLTMNIPS" ;  
		
	static MLTMTrain mltmTrain ; 
	static MLSHDPParms parms ;
	static Config config ; 
	static int totFiles = 0 ; 
	
	public static void main(String[] args) throws Exception {
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		String parmsPath = config.getPath("MLSHDPParms") ; 
		mltmTrain = new MLTMTrain (parmsPath) ; 
		loadData () ; 
		mltmTrain.trainModel();
		TrainedMLModel tmodel = mltmTrain.save (config.getPath("Model")) ; 
		tmodel.printTopics();
	}
	private static void loadData () throws  IOException, Exception { 
		String dataPath =  config.getPath ("Data") ;
		System.out.println ("Load data " + dataPath);
		String [] filter = {"txt"} ; 
		ArrayList <File> datafiles = Dirs.FilesInDir(dataPath , filter) ; 
		for (File file : datafiles) { 			
			int cnt = loadDocs (file) ;
			totFiles += cnt ; 
		}
	}
	private static int loadDocs (File file) throws IOException, Exception { 
		System.out.println ("Load docs - json " + file.getAbsolutePath());
		String line ; 
		line = Dirs.file2String(file).toLowerCase() ; 		
		mltmTrain.addDoc(file.getName() , "" ,  line ) ;	 
		return 1 ; 
	}
}