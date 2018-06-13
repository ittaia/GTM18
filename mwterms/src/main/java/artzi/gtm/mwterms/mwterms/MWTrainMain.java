package artzi.gtm.mwterms.mwterms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.gen.DocData;
import artzi.gtm.utils.io.Dirs;
import artzi.gtm.utils.textUtils.Sentence;
import artzi.gtm.utils.textUtils.Split;

public class MWTrainMain {
	
	static String path = "C:\\TestDir\\MWTerms" ;  
		
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	static MWTerms mwTerms ; 
			
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		mwTerms = new MWTerms() ; 
		loadData () ; 
		mwTerms.initCandidateTerms() ; 
		String jsonPath= config.getPath("Json") ; 
		mwTerms.toJson (jsonPath) ;
	}
	
	private static void loadData () throws JsonSyntaxException, IOException, Exception { 
		String dataPath =  config.getValue ("Data") ;
		System.out.println ("Load data " + dataPath);
		String [] filter = {"json"} ; 
		ArrayList <File> datafiles = Dirs.FilesInDir(dataPath , filter) ; 
		for (File file : datafiles) { 			
			int cnt = loadDocs (file) ;
			totFiles += cnt ; 
			if (totFiles > 500000 ) break ; 
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
			if (docData.getText() != null ) {
				String [] text = Sentence.TextIntoLines(docData.getText()) ; 				
				for (String t : text) addTerms (t) ;
			}
			if (docData.getTitle() != null) { 
				String [] text = Sentence.TextIntoLines(docData.getTitle()) ; 
				if (text.length > 1) System.out.println(docData.getTitle())  ; 
				for (String t : text) addTerms (t) ;
			}
			cnt ++ ; 			
		}
		in.close(); 
		System.out.println (file.getAbsolutePath() + "cnt " + cnt  ) ; 	
		return cnt ; 
	}

	private static void addTerms(String text) {
		ArrayList <String> wordList = Split.Str2WordList(text) ; 
		mwTerms.addWordList(wordList);
	}	
}