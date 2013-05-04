/**NDCStackOperation_TEST.java
 * 2011/10/24
 * 
 */
package zmonitor.test.log4j;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.junit.Test;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class NDCStackOperation_TEST extends Log4JTestBase {

	protected static Logger getLogger(){ 
		return Logger.getLogger(NDCStackOperation_TEST.class);
	}

	@Test
	public void normalLog4jUsage() throws IOException{
		Logger logger = getLogger();
		
		NDC.push("TIMELINE");
		logger.info("ndc["+NDC.getDepth()+"]");
		
		logger.debug("ndc["+NDC.getDepth()+"] >>> DEBUG,DEBUG,DEBUG,DEBUG,DEBUG,DEBUG,DEBUG,");
		logger.info("ndc["+NDC.getDepth()+"] before doA()");
		doA();
		logger.info("ndc["+NDC.getDepth()+"] After doA()");
		
		try {
			Thread.sleep(1234);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("ndc["+NDC.getDepth()+"] After Sleep...");
		NDC.remove();
		logger.info("ndc["+NDC.getDepth()+"] after NDC remove...");
		
	}
	
	private void doA(){
		Logger logger = getLogger();
		NDC.push("DO_A");
		logger.info("ndc["+NDC.getDepth()+"]before B");
		doB();
		logger.info("ndc["+NDC.getDepth()+"]after B");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]doing something about A, then start to do B.");
	}
	
	private void doB(){
		Logger logger = getLogger();
		NDC.push("DO_B");
		logger.info("ndc["+NDC.getDepth()+"] inside B.");
		NDC.pop();
	}
	
	
	/**
	 * the logger printed depth sequence here will be:
	 * 0, 1, 1, 4, 3, 2, 1, 0.
	 */
	@Test
	public void testLog_NDCDepthSequence_01143210(){
		Logger logger = getLogger();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("1");
		logger.info("ndc["+NDC.getDepth()+"]");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("2");
		NDC.push("3");
		NDC.push("4");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"] End of this timeline");
	}
	
	/**
	 * the logger printed depth sequence here will be:
	 * 0, 1, 1, 4, 3, 5, 2, 1, 0.
	 */
	@Test
	public void testLog_NDCDepthSequence_011435210(){
		Logger logger = getLogger();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("1");
		logger.info("ndc["+NDC.getDepth()+"]");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("2");
		NDC.push("3");
		NDC.push("4");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("4");
		NDC.push("5");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		NDC.pop();
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.pop();
		logger.info("ndc["+NDC.getDepth()+"] End of this timeline");
	}
	
	/**
	 * the logger printed depth sequence here will be:
	 * 0, 1, 2, 3, 4, 5, 0
	 */
	@Test
	public void testLog_NDCDepthSequence_0123450(){
		Logger logger = getLogger();
		//NDC Depth == 0
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("1");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("2");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("3");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("4");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.push("5");
		logger.info("ndc["+NDC.getDepth()+"]");
		NDC.remove();
		logger.info("ndc["+NDC.getDepth()+"] End of this timeline");
	}
}
