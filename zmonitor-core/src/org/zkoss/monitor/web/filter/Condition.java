/**Condition.java
 * 2011/4/6
 * 
 */
package org.zkoss.monitor.web.filter;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.zkoss.monitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Condition {

	interface Ma{
		boolean doMatch(String pattern, String value);
	}
	public static final String MATCH_ENDWITH = "endWith";
	public static final String MATCH_CONTAINS = "contains";
	public static final String MATCH_MATCH = "match";

	private String pattern;
	private String match;
	private String rule;
	private Ma ma;
	private boolean not = false;
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public void setMatch(String match) {
		this.match = match;
		if(MATCH_ENDWITH.equals(match)){
			ma = new Ma() {
				public boolean doMatch(String pattern, String value) {
					return value.endsWith(pattern);
				}
			};
		}else if(MATCH_CONTAINS.equals(match)){
			ma = new Ma() {
				public boolean doMatch(String pattern, String value) {
					return value.contains(pattern);
				}
			};
		}else if(MATCH_MATCH.matches(match)){
			ma = new Ma() {
				public boolean doMatch(String pattern, String value) {
					return getRegexPattern(pattern).matcher(value).matches();
				}
			};
		}else{
			throw new IllegalArgumentException("unknown match:"+match);
		}
	}
	public void setRule(String rule) {
		this.rule = rule;
		this.not = "not".equals(rule);
	}
	
	private static Pattern getRegexPattern(String pattern){
		return Pattern.compile(pattern);//TODO: cache this.
	}
	
	public boolean match(String urlStr, URL url){
		return not ^ ma.doMatch(pattern, 
				tFecher.get(urlStr, url));
	}
	
	public String getPattern() {
		return pattern;
	}

	public String getMatch() {
		return match;
	}

	public String getRule() {
		return rule;
	}

	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	interface TargetFecher{
		/**
		 * 
		 * @param urlStr
		 * @param url
		 * @return
		 */
		String get(String urlStr, URL url);
	}
	private static final TargetFecher FULL = new TargetFecher() {
		public String toString() {return "full";}
		public String get(String urlStr, URL url) {
			return urlStr;
		}
	};
	private static final TargetFecher PATH = new TargetFecher() {
		public String toString() {return "path";}
		public String get(String urlStr, URL url) {
			return url.getPath();
		}
	};
	private static final TargetFecher HOST = new TargetFecher() {
		public String toString() {return "host";}
		public String get(String urlStr, URL url) {
			return url.getHost();
		}
	}; 
	private static final TargetFecher QUERY = new TargetFecher() {
		public String toString() {return "query";}
		public String get(String urlStr, URL url) {
			return url.getQuery();
		}
	}; 
	private static final Map<String, TargetFecher> fTargetFechers = 
		new HashMap<String, TargetFecher>();
	private static final String fetcherStrs;
	static{
		fTargetFechers.put(FULL.toString(), FULL);
		fTargetFechers.put(PATH.toString(), PATH);
		fTargetFechers.put(HOST.toString(), HOST);
		fTargetFechers.put(QUERY.toString(), QUERY);
		fetcherStrs = Strings.append("[",FULL,"|",PATH,"|",HOST,"|",QUERY,"]");
	}
	
	private TargetFecher tFecher = PATH;

	public String getUrlPart() {
		return tFecher.toString();
	}
	public void setUrlPart(String urlPart) {
		TargetFecher f = fTargetFechers.get(urlPart.toLowerCase());
		if(f==null){
			throw new IllegalArgumentException("no matching url from" +
					fetcherStrs+ ": "+urlPart);
		}
		tFecher = f;
	}
}
