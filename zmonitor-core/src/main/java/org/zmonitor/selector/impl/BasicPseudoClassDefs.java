/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The default set of pseudo classes in Selector.
 * @author simonpai
 */
public class BasicPseudoClassDefs {
	
	private static Map<String, PseudoClassDef> _defs = 
		new HashMap<String, PseudoClassDef>();
	
	static {
		
		// :root
		_defs.put("root", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) return false;
				return ctx.getEntry().getParent() == null;
			}
		});
		
		// :first-child
		_defs.put("first-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) return false;
				return ctx.getChildIndex() == 0;
			}
		});
		
		// :last-child
		_defs.put("last-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) return false;
				return ctx.getChildIndex() + 1 == 
					ctx.getSiblingSize();
			}
		});
		
		// :only-child
		_defs.put("only-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) return false;
				return ctx.getSiblingSize() == 1;
			}
		});
		
		// :empty
		_defs.put("empty", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				if(parameters.length > 0) return false;
				return ctx.getEntry().isEmpty();
			}
		});
		
		// :nth-child(n)
		_defs.put("nth-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				return parameters.length == 1 && 
					acceptNthPattern(ctx.getChildIndex()+1, parameters[0]);
			}
		});
		
		// :nth-last-child(n)
		_defs.put("nth-last-child", new PseudoClassDef(){
			public boolean accept(MatchCtx ctx, String ... parameters) {
				return parameters.length == 1 && 
					acceptNthPattern(ctx.getSiblingSize() - 
							ctx.getChildIndex(), parameters[0]);
			}
		});
		
	}
	
	
	
	/**
	 * Returns the pseudo class definition associated with the given name
	 * @param name the pseudo class name
	 * @return a pseudo class definition
	 */
	public static PseudoClassDef getDefinition(String name){
		return _defs.get(name);
	}
	
	// helper //
	private static boolean acceptNthPattern(int index, String pattern){
		return "odd".equals(pattern) && index % 2 == 1 || 
				"even".equals(pattern) && index % 2 == 0 || 
				new NthChildPattern(pattern).accept(index);
	}
	
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
