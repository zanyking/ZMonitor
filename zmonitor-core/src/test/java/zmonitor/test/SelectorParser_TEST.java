/**
 * 
 */
package zmonitor.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.zmonitor.selector.impl.Parser;
import org.zmonitor.selector.impl.Token;
import org.zmonitor.selector.impl.Tokenizer;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorParser_TEST {

	@Test
	public void test_Tockenizer(){
		String selector = ".test[message.abc*='hello world!']:greater-than(END,50)";
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setDebugMode(true);
		ArrayList<Token> list = tokenizer.tokenize(selector);
		for(Token token : list){
			System.out.println(token.getType()+"["+
					token.getBeginIndex()+","+token.getEndIndex()
					+"]"+token.source(selector));
		}
	}
	@Test
	public void test_SelectorParser(){
		Parser parser = new Parser();
//		parser.setDebugMode(true);
		String query = ".A .B .C .test[message.abc*='hello world!']";
		System.out.println("parse: "+query);
		for(SimpleSelectorSequence sequence : parser.parse(query).get(0)){
			System.out.println("sequence: "+sequence);
		}
	}
	@Test
	public void test_SelectorSeperator(){
		Parser parser = new Parser();
//		parser.setDebugMode(true);
		String query = ".A, .B, .C, .test[message.abc*='hello world!']";
		System.out.println("parse: "+query);
		List<Selector> selectors = parser.parse(query);
		for(Selector selector : selectors){
			System.out.println("selector:"+selector);
			for(SimpleSelectorSequence sequence : selector){
				System.out.println("\tsequence: "+sequence);
			}
		}
	}
	
	
}
