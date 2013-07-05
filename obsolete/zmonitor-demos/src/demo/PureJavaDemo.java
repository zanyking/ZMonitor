/**PureJavaDemo.java
 * 2011/4/1
 * 
 */
package demo;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

/**
 * @author Ian YT Tsai
 *
 */
public class PureJavaDemo {
	
	private static final Logger logger = Logger.getLogger(PureJavaDemo.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		new Thread(){
			public void run(){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				NDC.push("TIMELINE");
				logger.info(">>> run");
				logger.debug(">>> DEBUG,DEBUG,DEBUG,DEBUG,DEBUG,DEBUG,DEBUG,");
				logger.info(" before doA()");
				doA();
				logger.info(" After doA()");
				
				try {
					Thread.sleep(1234);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info(" After Sleep...");
				NDC.remove();
				logger.info(" after NDC remove...");
					
		
			}
		}.start();
		
//		System.out.println("press ENTER key to stop server....");
//		System.in.read();
	}

	private static void doA(){
		NDC.push("DO_A");
		logger.info("before B");
		doB();
		logger.info("after B");
		NDC.pop();
		logger.info("doing something about A, then start to do B.");
	}
	
	private static void doB(){
		NDC.push("DO_B");
		logger.info(" inside B.");
		NDC.pop();
	}
}
