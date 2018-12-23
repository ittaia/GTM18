package artzi.gtm.utils.hebrew;

import java.util.HashMap;

public class Hebrew {
	static String heb_char = null ; 
	static String chars = "&abcdefghijklmnopqrstuvwxyz" ; 
	static HashMap <Character,Character>  translate_tab = null ;    
	public static String tr2heb (String text) { 
		if (translate_tab == null) {
			translate_tab = new HashMap <> () ; 
			int alef = 0x05d0 ;
			for (int char_indx = 0 ; char_indx < 27 ; char_indx ++ ) { 
				int c1 = alef + char_indx ; 			 
				char heb_char =(char)c1;
				char c_char = chars.charAt(char_indx) ; 
				System.out.println (Character.toString(c_char) + "-" + Character.toString(heb_char)) ; 
				translate_tab.put(c_char, heb_char) ; 						
			}
		}
		char [] heb_chars = new char [text.length()] ; 
		for (int i = 0 ; i < text.length(); i ++) { 
			char src_char = text.charAt(i) ; 
			Character heb_char = translate_tab.get(src_char) ; 
			if (heb_char != null) heb_chars[i] = heb_char ; 
			else                  heb_chars[i] = src_char ;
		}
		String heb_string = new String(heb_chars) ; 
		return heb_string ; 			 
	}
}
	
