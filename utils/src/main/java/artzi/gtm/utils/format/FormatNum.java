package artzi.gtm.utils.format;

import java.text.DecimalFormat;

public class FormatNum {
	static DecimalFormat df1 = new DecimalFormat("#.##"); 
	static DecimalFormat df0 = new DecimalFormat("#.####"); 
	public static String format0( double d)  {
		return df0.format(d) ; 
	}
}