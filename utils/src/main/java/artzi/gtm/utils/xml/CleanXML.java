/*
 * 
 */
package artzi.gtm.utils.xml;  
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
	
/**
 * 
 * fix XML special tags and characters
 * read = change XML string to a normal string
 * write = change a normal string to XML string
 *
 */
public class CleanXML {	
	
	/**
	 * change a normal string to XML string 
	 * first to check is "&" because all XML-characters contain "&"
	 * 
	 * @param txt = string to manipulate
	 * @return = XML-legal string
	 */
		public static String write  ( String txt ) { 
			 	
			String outTxt = txt;
			String expamp = "&";  
			String repamp = "&amp;";

			String explt = "<";  
			String replt = "&lt;";
			String expgt = ">";  
			String repgt = "&gt;";
			String expapos = "'";  
			String repapos = "&apos;";
			char chqout = '"';
			String expquot = chqout + "";  
			String repquot = "&quot;";
			
			Pattern ptrAmp = Pattern.compile(expamp);
			Matcher mtrAmp = ptrAmp.matcher(outTxt);
			outTxt = mtrAmp.replaceAll(repamp);

			Pattern ptrLt = Pattern.compile(explt);
			Matcher mtrLt = ptrLt.matcher(outTxt);
			outTxt = mtrLt.replaceAll(replt);
			
			Pattern ptrGt = Pattern.compile(expgt);
			Matcher mtrGt = ptrGt.matcher(outTxt);
			outTxt = mtrGt.replaceAll(repgt);
			
			Pattern ptrApos = Pattern.compile(expapos);
			Matcher mtrApos = ptrApos.matcher(outTxt);
			outTxt = mtrApos.replaceAll(repapos);

			Pattern ptrQuot = Pattern.compile(expquot);
			Matcher mtrQuot = ptrQuot.matcher(outTxt);
			outTxt = mtrQuot.replaceAll(repquot);

			return outTxt;
		}
		
		/**
		 * change XML string to normal string using regex of all XML special characters
		 * 
		 * @param txt = XML string
		 * @return = normal string
		 */
		public static String read(String txt)
		{
			String outTxt = txt;
			String explt = "<";  
			String replt = "&lt;";
			String expgt = ">";  
			String repgt = "&gt;";
			String expamp = "&";  
			String repamp = "&amp;";
			String expapos = "'";  
			String repapos = "&apos;";
			char chqout = '"';
			String expquot = chqout + "";  
			String repquot = "&quot;";
			
			Pattern ptrLt = Pattern.compile(replt);
			Matcher mtrLt = ptrLt.matcher(txt);
			outTxt = mtrLt.replaceAll(explt);
			
			Pattern ptrGt = Pattern.compile(repgt);
			Matcher mtrGt = ptrGt.matcher(outTxt);
			outTxt = mtrGt.replaceAll(expgt);
			
			Pattern ptrAmp = Pattern.compile(repamp);
			Matcher mtrAmp = ptrAmp.matcher(outTxt);
			outTxt = mtrAmp.replaceAll(expamp);
			
			Pattern ptrApos = Pattern.compile(repapos);
			Matcher mtrApos = ptrApos.matcher(outTxt);
			outTxt = mtrApos.replaceAll(expapos);

			Pattern ptrQuot = Pattern.compile(repquot);
			Matcher mtrQuot = ptrQuot.matcher(outTxt);
			outTxt = mtrQuot.replaceAll(expquot);

			return outTxt;
		}

}