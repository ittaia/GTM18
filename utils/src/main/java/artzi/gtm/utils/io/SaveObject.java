package artzi.gtm.utils.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveObject {
	
	public static void  write(String fileName , Object object) {
		try {
			FileOutputStream objectFile = new FileOutputStream (fileName)  ;
			ObjectOutputStream objectOut = new ObjectOutputStream (objectFile) ; 
			objectOut.writeObject (object) ;
			objectOut.close () ; 
			}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Object  read  (String fileName ) {
		Object rObject  ; 
		rObject = null ; 
		try {
			FileInputStream ObjectFile = new FileInputStream (fileName) ; 
			ObjectInputStream ObjectIN = new ObjectInputStream (ObjectFile) ; 
			rObject = ObjectIN.readObject () ; 
			ObjectIN.close () ; 
			} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rObject ; 				 
	}
}