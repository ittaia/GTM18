package artzi.gtm.utils.config;

import java.io.File;
import java.io.IOException;
import com.google.gson.JsonObject;

import artzi.gtm.utils.io.JsonIO;


/***********************************   Config ***************************/ 
/* Manage a key value configuration file								*/   
/* The configuration is loaded  from  mainDirPass/config.json. 			*/
/* mainDirPass setting:													*/
/*  Parameter to Config.getInstance										*/
/*  Current directory.  												*/
/************************************************************************/

public class Config {
	
	private static Config config = null;	
	private String mainPath = null;
	private JsonObject configJson = null ; 
	
	private Config () throws IOException {
		this.mainPath = new File(".").getAbsolutePath() ; 
		System.out.println ("Config main path: "+ mainPath) ; 
		String configPath = new File (mainPath, "config.json").getAbsolutePath() ; 
		configJson = JsonIO.read(configPath).getAsJsonObject() ; 		
	}
	private Config (String mainPath) throws IOException {
		this.mainPath = mainPath ; 	
		String configPath = new File (mainPath, "config.json").getAbsolutePath() ; 
		configJson = JsonIO.read(configPath).getAsJsonObject() ; 		
	}	

	public static Config getInstance() throws IOException {	 
		if (config == null) {
			config = new Config () ; 			 
		}
		return config;
	}
	public static Config getInstance(String mainPath) throws IOException {			
		if (config == null) {
			config = new Config (mainPath) ; 			 
		}
		return config;
	}
	
	public String getValue(String _key)  {
		if (configJson.has(_key)) {
			return configJson.get(_key).getAsString();
		}
		return null;
	}
	public String getMainPath() {
		return this.mainPath ; 
	}
	public String getPath(String _key)  {
		String path = "" ; 
		if (configJson.has(_key)) {
			String fileName = configJson.get(_key).getAsString();
			path = new File (this.mainPath , fileName).getAbsolutePath() ; 
		}
		return path ;
	}
}