package artzi.gtm.utils.format;

public class FormatJson {
	public static String setNewLines (String jsonString) { 
		String s = jsonString.replaceAll (",\"" , ",\n\"") ; 
		return s ; 
	}
}
