/**
 * 
 */
package zmonitor.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zmonitor.selector.impl.Parser;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SelSequence;

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
	
	@Test
	public void test_complex(){
		printAllTockens(".A .B ~ .C", 3);
	}
	
	@Test
	public void test_complex2(){
		printAllTockens("A + C + B > C ~ A + B", 6);
	}
	
	@Test
	public void test_complex3(){
		printAllTockens("A  C ~ B > C  A + B", 6);
	}
	@Test
	public void test_complex4(){
		printAllTockens("A  C + B  C + A + B", 6);
	}
	
	@Test
	public void test_complex5(){
		printAllTockens("A  C ~ B  C + A ~ B + D", 7);
	}
	
	private void printAllTockens(String selectorStr, int... selectorArr){
		Parser parser = new Parser();
		parser.setDebugMode(true);
		System.out.println("parse: "+selectorStr);
		List<Selector> selectors = parser.parse(selectorStr);
		Assert.assertEquals(selectors.size(), selectorArr.length);
		for(Selector selector : selectors){
			System.out.println("selector:"+selector);
			for(SelSequence sequence : selector){
				System.out.println("\tseq[" +sequence.getIndex()+"]: "+sequence+
						", cb:"+sequence.getCombinator()+
						", transitableIdx:"+sequence.getTransitableIdx()+
						", inheritableIdx:"+sequence.getInheritableIdx()
						);
			}
			Assert.assertEquals(selector.size(), selectorArr[selector.getSelectorIndex()]);
		}
		
	}
	
}
