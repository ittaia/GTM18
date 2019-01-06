package artzi.gtm.lda.ldamwt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import artzi.gtm.lda.train.LDATrain;
import artzi.gtm.mwterms.mwterms.MWFixText;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.gen.DocData;
import artzi.gtm.utils.io.Dirs;
import artzi.gtm.utils.textUtils.Split;

public class LDATrainMainWithMWT {
	
	static String path = "C:\\TestDir\\LDA" ;  
		
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	static LDATrain ldaTrain ; 
	static MWFixText mwFixText ; 
	
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		String mwPath = config.getValue("MWTerms") ; 
		mwFixText = MWFixText.getInstance (mwPath) ; 
		String parmsPath = config.getPath("LDAParms") ; 		
		ldaTrain = new LDATrain (parmsPath) ; 
		loadData () ; 
		ldaTrain.trainModel();
		String modelPath = config.getPath("Model") ; 
		ldaTrain.save (modelPath) ; 
		 
	}
	private static void loadData () throws JsonSyntaxException, IOException, Exception { 
		String dataPath =  config.getPath ("Data") ;
		System.out.println ("Load data " + dataPath);
		String [] filter = {"json"} ; 
		ArrayList <File> datafiles = Dirs.FilesInDir(dataPath , filter) ; 
		for (File file : datafiles) { 			
			int cnt = loadDocs (file) ;
			totFiles += cnt ; 
		}
	}
	private static int loadDocs (File file) throws JsonSyntaxException, IOException, Exception { 
		int cnt = 0 ; 
		System.out.println ("Load docs - json " + file.getAbsolutePath());
		BufferedReader in  = new BufferedReader(new FileReader(file));
		String line ; 
		while ((line = in.readLine()) != null) { 
			DocData docData = gson.fromJson(line, DocData.class) ; 
			docData.toLow();
			String text = docData.getTitle() + " " + docData.getText ()  ; 			 
			ArrayList <String> tokens = Split.Str2WordList(text) ; 
			ArrayList <String> fixTokens = mwFixText.fix(tokens) ; 
			ldaTrain.addDoc(docData.getFile_id() ,  docData.getTitle(),fixTokens ) ;
			cnt ++ ; 
		}
		in.close(); 
		System.out.println (file.getAbsolutePath() + "cnt " + cnt  ) ; 	
		return cnt ; 
	}	
}