/**
 * 
 */
package org.zmonitor.test.junit;

import java.net.URL;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.zmonitor.util.Loader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LogbackTestBase extends TestBase{

	@Override
	protected void beforeZmonitorManagerInit() {
		//TODO: init logback configuration...
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		URL url = Loader.getResource(packagePath+"/logback-test.xml");
		ILoggerFactory logFac = LoggerFactory.getILoggerFactory();
		
		if(logFac instanceof LoggerContext){
			initLogbackByConfiguration((LoggerContext)logFac, url);
		}else{
			throw new IllegalStateException("contains multiple slf4j adapter, " +
					"please remove the others except Logback-classic.");
		}
	}
	
	
	private static void initLogbackByConfiguration(LoggerContext context, URL url){
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			// Call context.reset() to clear any previous configuration, e.g.
			// default
			// configuration. For multi-step configuration, omit calling context.reset().
			context.reset();
			configurator.doConfigure(url);
		} catch (JoranException je) {
			je.printStackTrace();
			throw new RuntimeException(je);
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}

	
	
}
