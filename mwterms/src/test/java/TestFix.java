
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import artzi.gtm.mwterms.mwterms.MWFixText;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.gen.DocData;
import artzi.gtm.utils.io.Dirs;
import artzi.gtm.utils.textUtils.Split;

public class TestFix {
	
	static String path = "C:\\TestDir\\MWTerms" ;  
		
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	static MWFixText fix ; 
			
	public static void main(String[] args) throws Exception {
		System.out.println ("start texts fix") ; 
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		String jsonPath = config.getPath("Json") ; 
		fix = MWFixText.getInstance (jsonPath) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		EL.W(" ****** Start - DIR "+ config.getMainPath());
		  
		loadData () ; 
		 
	}
	
	private static void loadData () throws JsonSyntaxException, IOException, Exception { 
		String dataPath =  config.getValue ("Data") ;
		System.out.println ("Load data " + dataPath);
		String [] filter = {"json"} ; 
		ArrayList <File> datafiles = Dirs.FilesInDir(dataPath , filter) ; 
		for (File file : datafiles) { 			
			int cnt = loadDocs (file) ;
			totFiles += cnt ; 
			if (totFiles > 5000 ) break ; 
		}
	}
	
	private static int loadDocs (File file) throws JsonSyntaxException, IOException, Exception { 
		int cnt = 0 ; 
		System.out.println ("Load docs - json " + file.getAbsolutePath());
		BufferedReader in  = new BufferedReader(new FileReader(file));
		String line ; 
		while ((line = in.readLine()) != null) { 
			cnt += 1 ; 
			DocData docData = gson.fromJson(line, DocData.class) ; 
			EL.W (cnt + " - "+ docData.getFile_id()) ; 
			docData.toLow();
			String text = "" ; 
			if (docData.getTitle() != null) text += docData.getTitle()   ; 
			if (docData.getText() != null) text += docData.getText () ; 
				ArrayList <String> tokens = Split.Str2WordList(text) ; 
				ArrayList <String> fixTokens = fix.fix(tokens) ; 
				/*
				for (int i = 0 ; i < tokens.size() ; i ++ ) { 
					String s = i + "-"+ tokens.get(i) ; 
					if (i < fixTokens.size ())  s  += " ----- " + fixTokens.get(i) ; 
					EL.W (s) ; 
				}
				*/
		}
		in.close(); 
		System.out.println (file.getAbsolutePath() + "cnt " + cnt  ) ; 	
		return cnt ; 
	}
}