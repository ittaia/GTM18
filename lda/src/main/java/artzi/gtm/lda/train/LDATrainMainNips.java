package artzi.gtm.lda.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.io.Dirs;

public class LDATrainMainNips {
	
	static String path = "C:\\TestDir\\LDANIPS" ;  
		
	static Config config ; 
	static int totFiles = 0 ; 
	static LDATrain ldaTrain ; 
		
	public static void main(String[] args) throws Exception {
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		String parmsPath = config.getPath("LDAParms") ; 
		ldaTrain = new LDATrain (parmsPath) ; 
		loadData () ; 
		ldaTrain.trainModel();
		String modelPath = config.getPath("Model") ; 
		ldaTrain.save (modelPath) ; 
		 
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
		ldaTrain.addDoc(file.getName() , "" ,  line ) ;	 
		return 1 ; 
	}	
}