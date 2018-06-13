package artzi.gtm.utils.textUtils; 
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;


public class Sentence {

	public static String [] TextIntoLines(String text) {		
		String[] lines;
		ArrayList<String> lineList = new ArrayList<>();			
		BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
		boundary.setText(text);

		int start = boundary.first();
		for (int end = boundary.next();  end != BreakIterator.DONE;   end = boundary.next()) {
			lineList.add(text.substring(start,end));
			start = end ; 
		}
		
		lines = new String[lineList.size()];
		for(int i=0; i<lines.length; ++i) {		
			lines[i]= lineList.get(i);
		}
		return lines;		 
	}
}