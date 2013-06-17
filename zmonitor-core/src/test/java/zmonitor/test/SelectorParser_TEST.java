/**
 * 
 */
package zmonitor.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zmonitor.selector.impl.Parser;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorParser_TEST {

	@Test
	public void test_ComplexAttributeChain(){
		printAllTockens(".a.test[message.abc*='hello world!']", 1);
	}
	@Test
	public void test_joinedClasses(){
		printAllTockens(".A.B .C", 2);
	}
	@Test
	public void test_selectorSeperator(){
		printAllTockens(".A, .B, .C, .test:greater-than(END,50)",1,1,1,1);
	}
	@Test
	public void test_nextSiblings(){
		printAllTockens(".A ~ .B + .C", 3);
	}
	
	
	private void printAllTockens(String selectorStr, int... selectorArr){
		Parser parser = new Parser();
		parser.setDebugMode(true);
		System.out.println("parse: "+selectorStr);
		List<Selector> selectors = parser.parse(selectorStr);
		Assert.assertEquals(selectors.size(), selectorArr.length);
		for(Selector selector : selectors){
			System.out.println("selector:"+selector);
			for(SimpleSelectorSequence sequence : selector){
				System.out.println("\tsequence: "+sequence+", cb:"+sequence.getCombinator());
			}
			Assert.assertEquals(selector.size(), selectorArr[selector.getSelectorIndex()]);
		}
		
	}
	
}
