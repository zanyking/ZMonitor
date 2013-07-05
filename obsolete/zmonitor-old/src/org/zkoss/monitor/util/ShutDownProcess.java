/**JavaShutDownProcess.java
 * 2011/4/3
 * 
 */
package org.zkoss.monitor.util;

import java.util.Vector;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ShutDownProcess {
	
	private ShutDownProcess(){}
	
	private static final Vector<Runnable> shutdownProcs = new Vector<Runnable>();
	private static final Thread thread = new Thread(){
    	public void run(){
    		for(Runnable proc : shutdownProcs){
    			try{
    				proc.run();	
    			}catch(Exception e){
    				//Do nothing for now, JVM is ready to terminate.... 
    				e.printStackTrace();
    			}
    		}
    	}
    };
    static{
    	Runtime.getRuntime().addShutdownHook(thread);
    	System.out.println(">>>> JAVA Runtime Shutdown Hook registered!!!");
    }
    
    /**
     * 
     * @param proc
     */
    public static void register(Runnable proc){
    	shutdownProcs.add(proc);
    }
    
    


}
