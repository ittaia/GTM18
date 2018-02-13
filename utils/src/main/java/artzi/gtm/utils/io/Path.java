package artzi.gtm.utils.io;

import java.io.File;

/**
 * combine directory path and file name to a full path
 */
public class Path {
	/**
	 * combine two strings to a full path
	 * 
	 * @param _dir = string of directory path (with/without ending separator)
	 * @param _name = string of file name
	 * 
	 * @return = full path of directory and filename
	 */
	public static String GetFullPath(String _dir, String _name){
		String ret = "";
		char sep = File.separatorChar;
		if (_dir.endsWith(String.valueOf(sep))){
			ret = _dir + _name;
		}
		else{
			ret = _dir + sep + _name;
		}
		return ret;
	}
	
	/**
	 * combine directory path and file name to a full path
	 * 
	 * @param _dir = File object of directory
	 * @param _name = string of file name
	 * 
	 * @return = full path of directory and filename
	 */
	public static String GetFullPath(File _dir, String _name){
		String path = _dir.getAbsolutePath();
		String ret = GetFullPath(path, _name);
		return ret;
	}
}
