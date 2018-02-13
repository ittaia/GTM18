package artzi.gtm.utils.elog;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import artzi.gtm.utils.config.Config;

public class EL {
	
	private static Logger logger = null ; 
	private static Config config = null ; 
	
	public static void ELFile (String file)  {
		if (logger == null) initLogger () ; 
	} 
	
	private static void initLogger () { 
		try {
			config = Config.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String logPath = new File (config.getMainPath (), "log4j2.xml").getAbsolutePath() ; 
		System.setProperty("log4j.configurationFile", logPath);
		logger = LogManager.getLogger("root");
		logger.info("--------------------------------------------- ") ; 
		logger.info("----------------- start*log ----------------- ") ; 
	}
	
		
	private static String getCaller() {
	 
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		String thisFileName = "EL.java";
		
		int index = -1;
		boolean found = false;
		for (int i = 0; i < stElements.length; i++)
		{
			if (stElements[i].getFileName() == null){
				continue;
			}
			if (stElements[i].getFileName().equals(thisFileName))
			{
				found = true;
				continue;
			}
			if (!found)
			{
				continue;
			}
			index = i;
			break;
		}
		if (index < 0)
		{
			return "";
		}
		StackTraceElement ste = stElements[index];
		String fileName = ste.getFileName();
		int where = fileName.indexOf(".java");
		fileName = fileName.substring(0, where);
		String methodName = ste.getMethodName();
		return fileName + "/" + methodName;
	}	
	
	public static void W (String s) {	 
		if (logger==null) initLogger() ; 		
		String cls = getCaller();
		logger.info("("+cls+") "+s);
	}
	
	public static void WE (int errorCode, String s ) {  
		if (logger==null) initLogger() ; 	
		String cls = getCaller();
		logger.error("*** "+errorCode+" *** "+"("+cls+") "+s);
	}	
	
	public static void WF (int errorCode, String s) throws Exception {
		if (logger==null) initLogger() ; 
		String cls = getCaller();
				
		logger.fatal("*** FATAL *** "+errorCode+" *** " +"("+cls+") "+s);
		Exception ee = new Exception(errorCode + " > " + "("+cls+") "+s);
		ee.fillInStackTrace();
		throw ee;
	}
}