package artzi.gtm.lda.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

import artzi.gtm.lda.train.LDADocs;
import artzi.gtm.topicModelInfra.dataObjects.DocHeader;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.io.SaveObject;

public class DocHeadersJson {
	
	static String path = "C:\\TestDir\\LDA" ;  
		
	
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 
		String modelPath = config.getPath("Model") ; 
		String ldaDocsPath = new File (modelPath , "ldaDocs").getAbsolutePath() ; 
		LDADocs ldaDocs = (LDADocs) SaveObject.read (ldaDocsPath) ; 
		System.out.println ("ldaDocs " + ldaDocs.getM$numOfDocs()) ; 
		ArrayList <DocHeader> dl = ldaDocs.getDocHeaders()  ; 
		String dhPath = new File (modelPath , "docheaders.json").getAbsolutePath() ; 
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dhPath)));	
		for (DocHeader dh : dl) { 			
			String jl = gson.toJson(dh) ; 
			out .write (jl) ; 
			out.newLine();
		}		 
		out.close(); 		
	}
}	