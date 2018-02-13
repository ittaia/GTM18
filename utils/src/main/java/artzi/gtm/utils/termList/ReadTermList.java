package artzi.gtm.utils.termList;

import java.io.IOException;


import com.google.gson.JsonElement;

import artzi.gtm.utils.io.JsonIO;

public class ReadTermList {

	public static TermList Read(String _path) throws IOException{
		JsonElement je = JsonIO.read(_path);
		TermList tl = TermList.GetInstance(je);
		return tl;
	}
}
