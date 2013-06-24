/**
 * 
 */
package zmonitor.test;

import java.util.List;

import org.junit.Test;
import org.zmonitor.selector.impl.Token;
import org.zmonitor.selector.impl.Tokenizer;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Tokenizer_TEST {
	@Test
	public void test_attr_complexValue(){
		String sel = "A[a='[]']";
		List<Token> tokens = tokenize(sel);
		
		System.out.println("-----------------------------------------");
		System.out.println("Selector String:"+sel);
		System.out.println("-----------------------------------------");
		for(Token token : tokens){
			System.out.println(token.toString(sel));
		}
	}
	
	
	private static List<Token> tokenize(String selector){
		Tokenizer tkzr = new Tokenizer();
		tkzr.setDebugMode(true);
		return tkzr.tokenize(selector);
	}
}
