package zmonitor.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import zmonitor.test.log4j.Log4jMonitor_TEST;

@RunWith(Suite.class)
@SuiteClasses({ SelectorParser_TEST.class, 
		SimpleEntryIterator_TEST.class,
		SimplSelection_TEST.class, 
		ZmonitorNativeAPI_TEST.class,
		Log4jMonitor_TEST.class})
public class AllTests {

	
	
	
	
	
	
}
