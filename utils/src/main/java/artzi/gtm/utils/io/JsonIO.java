package artzi.gtm.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class JsonIO {
	

	/**
	 * write a json object to a file
	 * 
	 * @param _path = full path of output file (ends with ".json")
	 * @param _obj = json object to save
	 * @throws IOException
	 */
	public static void write(String _path, Object _obj) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_path)));
		out.newLine();
		String str = String.valueOf(_obj);
		out.write(str);
		out.close();
	}
	
	/**
	 * write a json object to a file
	 * 
	 * @param _file = File object of the target directory
	 * @param _name = name of file to write
	 * @param _obj = json object to save
	 * @throws IOException
	 */
	public static void write(File _file, String _name, Object _obj) throws IOException
	{
		String path = _file.getAbsolutePath()+File.separatorChar+_name;
		write(path, _obj);
	}
		

	/**
	 * read a json object from a file
	 * @param _path = full path of input file (ends with ".json")
	 * @return = string representing json object as read from input file
	 * @throws IOException
	 */
	public static JsonElement read(String _path) throws IOException
	{
		
		FileReader in = new FileReader(_path);
		BufferedReader reader = new BufferedReader(in);
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		
		while (line != null)
		{
			sb.append(line);
			line = reader.readLine();
		}
		reader.close();
		JsonParser parser = new JsonParser();
		return parser.parse(sb.toString());
	}
	
	/**
	 * read a large DocWords json file 
	 * 
	 * @param _path = full path of file to read
	 * 
	 * @return = json object for the DocWords collection constructor
	 * 
	 * @throws IOException
	 */
	public static JsonElement ReadDocWords(String _path) throws IOException{

		JsonObject jo = new JsonObject();
		JsonArray JsonDocWords = new JsonArray();
		JsonReader reader = new JsonReader(new FileReader(_path));
		reader.setLenient(true);
		JsonArray docWords;
		JsonObject oneDocWord;
		JsonToken token;
		
		while(reader.hasNext()){
			token = reader.peek();
			if (token.equals(JsonToken.END_DOCUMENT)){
				break;
			}
			if (token.equals(JsonToken.BEGIN_OBJECT)){
				reader.beginObject();
				oneDocWord = new JsonObject();
				String name = reader.nextName();
				int docId = reader.nextInt();
				oneDocWord.addProperty(name, docId);
				name = reader.nextName();
				docWords = new JsonArray();
				reader.beginArray();
				while (reader.hasNext()){
					docWords.add(reader.nextInt());
				}
				reader.endArray();
				oneDocWord.add(name, docWords);
				JsonDocWords.add(oneDocWord);
				reader.endObject();
			}
			else{
				reader.skipValue();
			}
		}
		
		reader.close();
		jo.add("words", JsonDocWords);
		return jo;
	}
}
