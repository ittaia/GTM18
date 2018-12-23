package artzi.gtm.lda.train;

 
import artzi.gtm.lda.toxlsx.ToXlsx;
import com.google.gson.Gson;
import artzi.gtm.utils.config.Config; 
import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;

public class LDAShutToXlsx {
	
	static String path = "C:\\TestDir\\LDAShut" ;  
		
	static Config config ; 
	static Gson gson ; 
	static int totFiles = 0 ; 
	static LDATrain ldaTrain ; 
	
	public static void main(String[] args) throws Exception {
		gson = new Gson () ; 
		config = Config.getInstance(path) ; 
		System.out.println ("Work on Dir :"+ config.getMainPath()) ; 	
		String modelPath = config.getPath("Model") + "\\trainedModel" ; 
		TrainedMLModel model = TrainedMLModel.getInstance(modelPath) ; 
		ToXlsx xls = new ToXlsx() ; 
		xls.editModelHeb(model);
		String xlsPath = config.getPath("Xls") ; 
		xls.writeXlsx(xlsPath);
		System.out.println(xlsPath + " saved") ; 
	}
}