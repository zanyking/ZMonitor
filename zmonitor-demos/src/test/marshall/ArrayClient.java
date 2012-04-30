/**
 * 
 */
package test.marshall;

import java.io.*;
import java.net.*;
/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ArrayClient {
	public static void main(String argv[]) {
	      ObjectOutputStream oos = null;
	      ObjectInputStream ois = null;
	      // two arrays
	      int dataset1[] = {3, 3, 3, 3, 3, 3, 3};
	      int dataset2[] = {5, 5, 5, 5, 5, 5, 5};
	      try {
	        // open a socket connection
	        Socket socket = new Socket("localhost", 4000);
	        // open I/O streams for objects
	        oos = new ObjectOutputStream(socket.getOutputStream());
	        ois = new ObjectInputStream(socket.getInputStream());
	        // create two serialized objects
	        SerializedObject so1 = new SerializedObject();
	        SerializedObject so2 = new SerializedObject();
	        SerializedObject result = null;
	        int outArray[] = new int[7];
	        so1.setArray(dataset1);
	        so2.setArray(dataset2);
	        // write the objects to the server
	        oos.writeObject(so1);
	        oos.writeObject(so2);
	        oos.flush();
	        // read an object from the server
	        result = (SerializedObject) ois.readObject();
	        outArray = result.getArray();
	        System.out.print("The new array is: ");
	        // after unpacking the array, iterate through it
	        for(int i=0;i<outArray.length;i++) {
	          System.out.print(outArray[i] + "   ");
	        }
	        oos.close();
	        ois.close();
	      } catch(Exception e) {
	        System.out.println(e.getMessage());
	      }
	   }
}
