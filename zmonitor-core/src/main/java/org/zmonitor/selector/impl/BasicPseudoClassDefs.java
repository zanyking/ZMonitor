/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.zmonitor.selector.SelectorEvalException;

/**
 * The default set of pseudo classes in Selector.
 * @author simonpai
 */
@SuppressWarnings("rawtypes")
public class BasicPseudoClassDefs {
	
	
	private static final Map<String, PseudoClassDef> DEFs;
	
	static {
		Map<String, PseudoClassDef> tempDefs = 
				new HashMap<String, PseudoClassDef>();
		// :root
		tempDefs.put("root", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) 
					throw new SelectorEvalException("Does not accept argument, args:"+parameters);
				return ctx.getEntry().getParent() == null;
			}
		});
		
		// :first-child
		tempDefs.put("first-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) 
					throw new SelectorEvalException("Does not accept argument, args:"+parameters);
				return ctx.getChildIndex() == 0;
			}
		});
		
		// :last-child
		tempDefs.put("last-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0)
					throw new SelectorEvalException("Does not accept argument, args:"+parameters);
				return ctx.getChildIndex() + 1 == 
					ctx.getSiblingSize();
			}
		});
		
		// :only-child
		tempDefs.put("only-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) 
					throw new SelectorEvalException("Does not accept argument, args:"+parameters);
				return ctx.getSiblingSize() == 1;
			}
		});
		
		// :empty
		tempDefs.put("empty", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) 
					throw new SelectorEvalException("Does not accept argument, args:"+parameters);
				return ctx.getEntry().isEmpty();
			}
		});
		
		// :nth-child(n)
		tempDefs.put("nth-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if( parameters.length != 1 )
					throw new SelectorEvalException("must assign only one argument, args:"+parameters);
				return 
					acceptNthPattern(ctx.getChildIndex()+1, parameters[0]);
			}
		});
		
		// :nth-last-child(n)
		tempDefs.put("nth-last-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if( parameters.length != 1 )
					throw new SelectorEvalException("must assign only one argument, args:"+parameters);
				return 
					acceptNthPattern(ctx.getSiblingSize() - 
							ctx.getChildIndex(), parameters[0]);
			}
		});
		DEFs = Collections.unmodifiableMap(tempDefs);
	}
	
	
	
	/**
	 * Returns the pseudo class definition associated with the given name
	 * @param name the pseudo class name
	 * @return a pseudo class definition
	 */
	public static PseudoClassDef getDefinition(String name){
		return DEFs.get(name);
	}
	
	// helper //
	private static boolean acceptNthPattern(int index, String pattern){
		return "odd".equals(pattern) && index % 2 == 1 || 
				"even".equals(pattern) && index % 2 == 0 || 
				new NthChildPattern(pattern).accept(index);
	}
	/**
	 * 
	 * @author simonpai
	 *
	 */
	private static class NthChildPattern {
		
		private final int _preNum;
		private final int _postNum;
		private final boolean _valid;
		
		private NthChildPattern(String pattern) {
			
			int npos = pattern.indexOf('n');
			String preStr = npos < 0 ? "" : pattern.substring(0, npos);
			String postStr = npos < 0 ? pattern : pattern.substring(npos + 1);
			
			_valid = Pattern.matches("(?:\\+|-)?\\d*", preStr) && 
					Pattern.matches("(?:(?:\\+|-)?\\d+)?", postStr); 
			
			_preNum = _valid ? value(preStr, npos < 0? 0 : 1) : -1;
			_postNum = _valid ? value(postStr, 0) : -1;
		}
		
		private boolean accept(int index) {
			if(!_valid) return false;
			if(_preNum == 0) return index == _postNum;
			int diff = index - _postNum;
			return diff % _preNum == 0 && diff/_preNum >= 0;
		}
		
		private int value(String str, int defValue){
			if(str.isEmpty()) return defValue;
			char p = str.charAt(0);
			String s = (p=='+' || p=='-') ? str.substring(1) : str;
			return (p=='-'?-1:1) * (s.isEmpty()? defValue : Integer.valueOf(s));
		}
	}
	
}
