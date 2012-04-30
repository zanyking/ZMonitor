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
public class ArrayMultiplier  extends Thread {

	   private ServerSocket arrayServer;
	 

	   public static void main(String argv[]) throws Exception {
	     new ArrayMultiplier();
	   }

	   public ArrayMultiplier() throws Exception {
	     arrayServer = new ServerSocket(4000);
	     System.out.println("Server listening on port 4000.");
	     this.start();
	   } 

	   public void run() {
	     while(true) {
	       try {
	        System.out.println("Waiting for connections.");
	        Socket client = arrayServer.accept();
	        System.out.println("Accepted a connection from: "+
	client.getInetAddress());
	        Connect2 c = new Connect2(client);
	       } catch(Exception e) {}
	     }
	   }
	}

	class Connect2 extends Thread {
	   private Socket client = null;
	   private ObjectInputStream ois = null;
	   private ObjectOutputStream oos = null;
	    
	   public Connect2() {}

	   public Connect2(Socket clientSocket) {
	     client = clientSocket;
	     try {
	      ois = new ObjectInputStream(client.getInputStream());
	      oos = new ObjectOutputStream(client.getOutputStream());
	     } catch(Exception e1) {
	         try {
	            client.close();
	         }catch(Exception e) {
	           System.out.println(e.getMessage());
	         }
	         return;
	     }
	     this.start();
	   }

	   public void run() {
	      SerializedObject x = null;
	      SerializedObject y = null;
	      int dataset1[] = new int[7];
	      int dataset2[] = new int[7];
	      int result[] = new int[7];
	      try {
	         x = (SerializedObject) ois.readObject();
	         y = (SerializedObject) ois.readObject();
	         dataset1 = x.getArray();
	         dataset2 = y.getArray();
	         // create an array by multiplying two arrays
	         for(int i=0;i<dataset1.length;i++) {
	           result[i] = dataset1[i] * dataset2[i];
	         }
	         // ship the object to the client
	         SerializedObject output = new SerializedObject();
	         output.setArray(result);
	         oos.writeObject(output);
	         oos.flush();
	         // close connections
	         ois.close();
	         oos.close();
	         client.close(); 
	      } catch(Exception e) {}       
	   }
	}
