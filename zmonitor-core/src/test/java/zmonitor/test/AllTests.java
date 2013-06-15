package zmonitor.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SelectorParser_TEST.class, 
		SimpleSelector_TEST.class,
		SimplSelection_TEST.class, 
		ZmonitorNativeAPI_TEST.class })
public class AllTests {

	
	
	
}
