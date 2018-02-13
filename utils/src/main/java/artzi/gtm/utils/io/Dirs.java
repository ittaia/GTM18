/*
 * 
 */
package artzi.gtm.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 
 * get all non-directory files from directory + sub directories
 * get string content of file
 *
 */
public class Dirs {
	static ArrayList<File> files;
	
	/**
	 * get basic files from directory with or without filter
	 * 
	 * @param _path = full path of uppermost directory
	 * @param _filter = null or empty for all files, 
	 *                  file suffix to get only these files (".txt", ".json" etc)
	 *                  
	 * @return = array list of files, or null if none found
	 */
	public static ArrayList<File> FilesInDir(String _path, String[] _filter)
	{
		files = new ArrayList<File>();
		
		if (_path == null || _path.isEmpty())
		{
			return null;
		}
		
		File dir = new File(_path);
		if (! dir.exists())
		{
			return null;
		}
		
		//-- path points at a basic file, not a directory
		if (! dir.isDirectory())
		{
			//-- no filter - return this file
			if (_filter == null || _filter.length < 1)
			{
				files.add(dir);
				return files;
			}
			//-- check all filters 
			for (String str : _filter)
			{
				//-- file name ends with this filter
				if (dir.getName().endsWith(str))
				{
					files.add(dir);
					return files;
				}
			}
			//-- file name does not end with any filter
			return null;
		}
		
		//-- path points at a directory
		findFiles(dir, _filter);
		return files;
	}
	
	/**
	 * recursive function to retrieve files from all sub directories
	 * 
	 * @param _dir = current directory
	 * @param _filter = optional filter for file suffix
	 */
	private static void findFiles(File _dir, String[] _filter)
	{
		File[] fs = _dir.listFiles();
		for (File file : fs)
		{
			if (file.isDirectory())
			{
				findFiles(file, _filter);
			}
			if (_filter == null || _filter.length < 1)
			{
				if (! file.isDirectory())
				{
					files.add(file);
				}
				continue;
			}
			for (String suffix : _filter)
			{
				if (file.getName().endsWith(suffix) && ! file.isDirectory())
				{
					files.add(file);
				}
			}
			continue;
		}
	}
	
	/**
	 * get string content of file
	 * 
	 * @param _file = File object to read
	 * @return = string content 
	 * @throws IOException
	 */
	public static String file2String(File _file) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(_file.getAbsoluteFile())));
		String str = in.readLine();
		while (str != null){
			sb.append(str);
			str = in.readLine();
		}
		in.close();
		return sb.toString();
	}
}
