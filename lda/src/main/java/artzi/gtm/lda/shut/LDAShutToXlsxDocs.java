package artzi.gtm.lda.shut;

 
import artzi.gtm.lda.toxlsx.ToXlsxDocs;
import artzi.gtm.lda.train.LDADocs;
import artzi.gtm.lda.train.LDATrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.gen.DocData;
import artzi.gtm.utils.io.Dirs;
import artzi.gtm.utils.io.SaveObject;
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;

public class LDAShutToXlsxDocs {
	
	static String path = "C:\\TestDir\\LDAShut" ;  
		
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	static LDATrain ldaTrain ;
	static ToXlsxDocs xls ;  
	
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		String modelPath = config.getPath("Model") + "\\trainedModel" ; 
		TrainedMLModel model = TrainedMLModel.getInstance(modelPath) ;
		String ldaDocsPath = config.getPath("Model") + "\\ldaDocs" ; 
		LDADocs ldaDocs = (LDADocs)SaveObject.read(ldaDocsPath) ; 
		xls = new ToXlsxDocs() ; 
		xls.editModelHeb(model, ldaDocs);
		loadData () ; 
		String xlsPath = config.getPath("XlsDocs") ; 
		xls.writeXlsx(xlsPath);
		System.out.println(xlsPath + " saved") ; 
	}
	private static void loadData () throws JsonSyntaxException, IOException, Exception { 
		String dataPath =  config.getValue ("Data") ;
		System.out.println ("Load data " + dataPath);
		String [] filter = {"json"} ; 
		ArrayList <File> datafiles = Dirs.FilesInDir(dataPath , filter) ;
		int cnt = 0 ; 
		for (File file : datafiles) { 
			String name = file.getName() ; 
			int lineNum = xls.getLineId (name) ; 
			if (lineNum > -1) {  						
				BufferedReader in  = new BufferedReader(new FileReader(file));
				String line ; 
				line = in.readLine() ; 
				if (line != null) {
					DocData docData = gson.fromJson(line, DocData.class) ; 
					String text = docData.getText() ;
					if (text.length() > 500) text = text.substring(0,500) ;  
					xls.setDocText(lineNum, text) ; 
				}
				cnt += 1 ; 
				in.close(); 
			}			  
		}
		System.out.println ("load Files " + cnt) ; 
	}
}