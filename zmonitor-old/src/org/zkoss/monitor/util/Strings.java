/**Strings.java
 * 2011/3/5
 * 
 */
package org.zkoss.monitor.util;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Strings {
	
	public static void append(StringBuffer sb,  Object... strs){
		for(Object s : strs){
			sb.append(s);	
		}
	}
	
	public static String append( Object... strs){
		StringBuffer sb = new StringBuffer();
		for(Object s : strs){
			sb.append(s);	
		}
		return sb.toString();
	}
	
	public static void appendln(StringBuffer sb, Object... strs){
		for(Object s : strs){
			sb.append(s);	
		}
		sb.append("\n");	
	}
	
	public static void append(StringBuffer sb, String... strs){
		for(String s : strs){
			sb.append(s);	
		}
	}
	
	public static boolean isEmpty(String str){
		return str==null || str.length()==0;
	}
	
	
	
	public static String toNumericString(long num, String spliter){
		String temp = String.valueOf(num);
		if(isEmptyString(spliter)||temp.length()<=3)return temp;
	
		StringBuffer sb = new StringBuffer();
		int j = temp.length();
		for(int i=j-1;i>=0;i--){			
			char a = temp.charAt(i);
			sb.append(a);
			if((j-i)%3==0 && i>0){
				sb.append(spliter);
			}
		}
		
		return sb.reverse().toString();
	}
	
	
	public static String alignedMillisStr(long ms){
		String prefix = "";
		     if(ms < 10) prefix = "    ";
		else if(ms < 100) prefix = "   ";
		else if(ms < 1000) prefix = "  ";
		else if(ms < 10000) prefix = " ";
//		else if(ms < 100000) prefix = " ";
		return prefix + ms;
	}
	
	public static boolean isEmptyString(Object obj){
		if(obj==null)return true;
		String str = obj.toString();
		if("".equals(str)){
			return true;
		}
		return false;
	}
}
