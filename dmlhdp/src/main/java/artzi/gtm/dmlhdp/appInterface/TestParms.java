package artzi.gtm.dmlhdp.appInterface;

import java.io.File;
import java.io.IOException;

import MLHDP.MLHDPParms;
import artzi.gtm.utils.elog.EL;

public class TestParms {

	static MLHDPParms parms = null ;
	public static void main(String[] args) throws IOException {
		File mainPath =  new File ("D:\\TestDir\\TestEC2NPMUC") ; 
		String logPath = new File (mainPath , "testParms.txt").getPath () ; 
		String parmPath = new File (mainPath , "parms.json").getPath () ; 
		parms = MLHDPParms.getInstance (parmPath) ; 
		EL.ELFile (logPath) ;
		EL.W("Start read") ; 
		parms.print () ; 
	}
}