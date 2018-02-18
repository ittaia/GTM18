package artzi.gtm.dmlhdp.appInterface;

import java.io.IOException;

import artzi.gtm.dmlhdp.mlhdp.MLHDPParms;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;

public class TestParms {

	static MLHDPParms parms = null ;
	public static void main(String[] args) throws IOException {
		String mainPath =  "C:\\TestDir\\MACUIE" ; 
		Config config = Config.getInstance(mainPath) ; 
		String parmPath = config.getPath("MLHDPParms") ; 
		System.out.println (parmPath) ; 
		parms = MLHDPParms.getInstance (parmPath) ; 
		EL.W("Start read") ; 
		parms.print () ; 
	}
}