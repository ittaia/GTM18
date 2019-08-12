package artzi.gtm.utils.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDate {
	public static String formatDate (Date dated)  { 
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy,HH:mm:ss");
		String dates = formatter.format(dated);
		return dates ; 
	}
}
