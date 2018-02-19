package artzi.gtm.muc.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import artzi.gtm.utils.elog.EL;

public class TestDocs {

	ArrayList <TestDoc> testDocs ; 
	String lastID = "" ; 
	
	static final  String _S = ("\\") ; 	 
	int docCnt = 0 ;	 
    String path ; 
    boolean optional ;
    int templateIndx ; 
    int [] templateCount = new int [TemplateNames.numOfTemplates] ; 
    

	public TestDocs (String path) throws IOException { 
		this.path = path ; 
		testDocs = new ArrayList <TestDoc> () ; 
		for (int i = 0 ; i < TemplateNames.numOfTemplates ; i ++) templateCount [i] = 0 ; 
		String pathKey = new File (path ,"key-tst3.v2" ).getAbsolutePath() ; 
		load  (pathKey)  ; 
		pathKey = new File (path ,"key-tst4.v2" ).getAbsolutePath() ; 
		load  (pathKey)  ; 
		for (int i = 0 ; i < TemplateNames.numOfTemplates ; i ++) { 
			EL.WE( 8876 , " Tempaltes - " + i + " - " + templateCount [i] )    ;  
		}
	}
	
	private  void    load (String textFile) throws IOException	 {   
		String str;
		String lastHead = "**" ; 

		BufferedReader in;
		in = new BufferedReader(new InputStreamReader(new FileInputStream(textFile)));
		while ((str = in.readLine()) != null)	{
			if (!str.trim().equals("") ) { 
				String head = str.substring(0,33) ; 
				if (head.trim().equals("")) { 
					head = lastHead ; 
				}
				lastHead = head ; 
				String data = str.substring(35) ; 
				// EL.WE (8888 , head + "->" + data) ; 
				process (head , data) ; 
			}
		}
		in.close () ; 		 
	}
	
	private  void process (String head , String data) {
		if (head.contains ("MESSAGE: ID")) { 
			if (data.equals(lastID)  ) { 
				System.out.println ( " Dup " + data) ; 
			}
			else { 

				TestDoc testDoc= new TestDoc (data) ; 
				testDocs.add( testDoc ) ; 	
				lastID = data  ; 
			}
		}
		else if (head.contains ("MESSAGE: TEMPLATE")) {
			if (data.contains("OPTIONAL")) { 
				optional = true ; 
				// System.out.println ("Optional" ) ; 
			}
			else {
				optional = false ; 
			}
		}
		else if (head.contains ("INCIDENT: TYPE")) { 
			templateIndx = TemplateNames.getTemplateIndx (data) ; 
			templateCount [templateIndx] ++ ; 
		}				
		
		else { 
			if (templateIndx != TemplateNames.templateOther) { 
			
				TestDoc testDoc = testDocs.get(testDocs.size()-1) ; 
				if  (head.contains ("INSTRUMENT ID")) testDoc.addVal (templateIndx , SlotNames.slotInstrument , data , optional) ; 
				else if (head.contains ("HUM TGT: NAME") | head.contains ("HUM TGT: DESCRIPTION")) 
					testDoc.addVal (templateIndx ,SlotNames.slotHTarget , data , optional) ; 
				else if (head.contains ("PHYS TGT: ID")) testDoc.addVal (templateIndx ,SlotNames.slotPHTarget , data , optional) ; 
				else if (head.contains ("PERP: ORGANIZATION ID") | head.contains ("PERP: INDIVIDUAL ID")) 
					testDoc.addVal (templateIndx ,SlotNames.slotPerp , data , optional) ; 	
			}
		}			
	}

	public void print() {
		EL.W(" *********************** Test Docs ************");
		for (TestDoc td : testDocs) { 
			td.print(); 
		}
		
	}

	public ArrayList<TestDoc> getList() {
		return this.testDocs ; 
	}
}
