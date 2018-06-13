/*
 * 
 */
package artzi.gtm.utils.textUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import artzi.gtm.utils.io.JsonIO;


/**
 * determine whether a word is a stop-word / a real word
 * stop-word list can be read from a json file (json array) or from a static list
 * 
 * json file name is "Config.getFullPath()" + "StopWords.json" (main project path + file name)
 * json file format :
 * {"StopWords":["word", "word", ... , "lastword"]}
 */
public class StopWords {
	
	private static String StopWordString = "a,able,about,across,after,all,almost,also,am," +
			"among,an,and,any,are,as,at,be,because,been,but,by,can,cannot," +
			"could,dear,did,do,does,either,else,ever,every,for,from,get,got," +
			"had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it," +
			"its,just,least,let,like,likely,may,me,might,most,must,my,neither," +
			"no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say," +
			"says,she,should,since,so,some,than,that,the,their,them,then,there," +
			"these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where," +
			"which,while,who,whom,why,will,with,would,yet,you,your,"+
			"one,system,first,second,third,fourth,present,invention,method,methods,includes,relates,each,disclosure"; //,cell,cells,nanoparticles,particles" ; 
	
	private static Hashtable <Object, Integer> stopHash = null ; 
	
	private static Integer I  = 1 ; 
	
	private static String stopFilePath;
	
	/**
	 * check in pre-prepared list if word is a stop-word
	 * 
	 * @param word = word to check
	 * @return = true if stop-word, false otherwise
	 * @throws Exception 
	 */
	public  static boolean isStopWord (String word)  { 
		if (stopHash == null){
			initStopHash () ;  
		}
		
		Integer J = stopHash.get(word.toLowerCase()) ; 
		
		if (J!=null) {
			return true ; 
		}
		
		return false ; 
	}
	
	/**
	 * add words to stop-word list and update file
	 * 
	 * @param _words = array of words to add
	 * @throws Exception 
	 */
	public static void addStopWords(ArrayList<String> _words) throws Exception{
		
		boolean update = false;
		
		if (stopHash == null){
			initStopHash ();
		}
		for (String word : _words){
			word = word.trim().toLowerCase();
			if (word.length() < 1){
				continue;
			}
			if (stopHash.containsKey(word)){
				continue;
			}
			stopHash.put(word,I);
			update = true;
		}
		if (update){
			updateStopFile();
		}
	}
	
	/**
	 * remove words from stop-word list and update file
	 * 
	 * @param _words = array of words to remove
	 * @throws Exception 
	 */
	public static void removeStopWords(ArrayList<String> _words) throws Exception{
		
		boolean update = false;
		
		if (stopHash == null){
			initStopHash ();
		}
		for (String word : _words){
			word = word.trim().toLowerCase();
			if (stopHash.containsKey(word)){
				stopHash.remove(word);
				update = true;
			}
		}
		
		if (update){
			updateStopFile();
		}
	}
	
	/**
	 * save stop words file after update (add/remove)
	 * @throws IOException
	 */
	private static void updateStopFile() throws IOException{
		if (stopFilePath == null || ! ( stopFilePath.endsWith(".json"))){
			return;
		}
		JsonArray jarr = new JsonArray();
		JsonObject jo = new JsonObject();
		Enumeration<Object> words = stopHash.keys();
		while (words.hasMoreElements()){
			jarr.add(words.nextElement().toString());
		}
		jo.add("StopWords", jarr);
		JsonIO.write(stopFilePath, jo);
	}
	
	/**
	 * initialize stop-words table
	 * try to read stop-word list from a file
	 * if failed - use static list
	 * @throws Exception 
	 */
	private static  void initStopHash ()  {
		stopHash = new Hashtable <Object, Integer> (120) ; 
		String [] vsw = StopWordString.split(",") ; 
		for (String sw : vsw   ) {
			stopHash.put(sw,I) ; 
		}		
	}
	
	/**
	 * read stop words from a json file and build hash table
	 * 
	 * @param _file = json file
	 * @return = true if succeeded, false if failed
	 * @throws IOException 
	 */
	private static boolean readStopFile(File _file) 
	{
		
		JsonElement element = null;
		try {
			element = JsonIO.read(_file.getAbsolutePath());
		} catch (IOException e) {
			element = null;
		}
		if (element == null){
			return false;
		}
		JsonArray jarr = (JsonArray) element.getAsJsonObject().get("StopWords");
		if (! jarr.isJsonArray()){
			return false;
		}
		
		stopFilePath = _file.getAbsolutePath();
		
		Iterator<JsonElement> itr = jarr.iterator();
		while (itr.hasNext()){
			String word = itr.next().getAsString().trim().toLowerCase();
			stopHash.put(word,I) ;
		}
		return true;
	}
	
	/**
	 * determine whether a token is a real word using regex
	 * 
	 * @param word = token to check
	 * @return = true = token is probably a word
	 *           false = token failed regex test and is not a word
	 */
	public static boolean isWord (String word) { 
		String token  = "[A-Za-z][A-Za-z[-]]*" ; 		 
		Pattern ps = Pattern.compile(token) ; 
		Matcher m = ps.matcher(word) ; 
		return m.matches () ; 
	}

}
