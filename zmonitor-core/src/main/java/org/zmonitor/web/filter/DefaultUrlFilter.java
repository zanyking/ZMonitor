/**
 * 
 */
package org.zmonitor.web.filter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultUrlFilter implements UrlFilter{
	private final List<Condition> conditions = 
		Collections.synchronizedList(new ArrayList<Condition>(10));
	
	public void add(Condition cond){
		conditions.add(cond);
	}
	
	private static final Acceptor AND = new Acceptor(){
		public boolean accept( URL url, List<Condition> conds) {
			for (Condition cond : new ArrayList<Condition>(conds)) {
				if(cond.match(url)){
					continue;
				}else{
					return false;
				}
			}
			return true;
		}

		public String getName() {
			return "and";
		}};
	private static final Acceptor OR = new Acceptor(){
		public boolean accept( URL url, List<Condition> conds) {
			for (Condition cond : new ArrayList<Condition>(conds)) {
				if(cond.match(url)){
					return true;
				}else{
					continue;
				}
			}
			return false;
		}
		public String getName() {
			return "and";
		}};
		
	private Acceptor fAcceptor = AND;
	/**
	 * 
	 * @param urlStr
	 * @return
	 */
	public boolean shouldAccept(URL url){
		return not ^ fAcceptor.accept(url, conditions);
	}
	
	
	
	public String getMode() {
		return fAcceptor.getName();
	}
	public void setMode(String mode) {
		if(mode==null)
			throw new IllegalArgumentException("mode cannot be set to NULL!");
		mode = mode.toLowerCase();
		if("and".equals(mode)){
			fAcceptor = AND;
		}else if("or".equals(mode)){
			fAcceptor = OR;
		}else{
			throw new IllegalArgumentException("mode must be [end|or]: "+mode);
		}
	}
	
	
	private boolean not;
	private String rule;
	
	public void setRule(String rule){
		this.rule = rule;
		this.not = "not".equals(rule);
	}
	public String getRule() {
		return rule;
	}

	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	interface Acceptor{
		/**
		 * 
		 * @param url
		 * @return
		 */
		public boolean accept(URL url, List<Condition> conds);
		/**
		 * 
		 * @return
		 */
		public String getName();
	}//end of class...

}
