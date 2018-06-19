package artzi.gtm.vizualization.docsTopics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;

import artzi.gtm.lda.train.LDADocs;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.io.SaveObject;

public class DocsTopicsMain {
	
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
		DocsTopics docsTopics = new DocsTopics (ldaDocs) ; 
		docsTopics.printCounts () ; 
		String dtPath = new File (modelPath , "docsTopics.json").getAbsolutePath() ; 
		String jl = gson.toJson(docsTopics) ; 
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dtPath)));	
		out.write(jl);
		out.close(); 
		
	}
}	