/**Strings.java
 * 2011/3/5
 * 
 */
package org.zmonitor.util;

import java.util.regex.Pattern;

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
	
	public static String capitalize(String str){
		char first = str.charAt(0);
		if(Character.isUpperCase(first))return str;
		return Character.toUpperCase(first) + str.substring(1);
	}
	
	public static String toShortClassName(String fqcn){
		int idx = fqcn.lastIndexOf(".");
		if(idx >=0)
			return fqcn.substring(idx+1);
		else
			return  fqcn;
	}
	
	public static boolean isEmpty(String str){
		return str==null || str.length()==0;
	}
	
	public static String escapeId(String inStr){
//		Pattern s;
		return inStr.replaceAll("[\\W]", "_");
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
	
	
	public static boolean isEmptyString(Object obj){
		if(obj==null)return true;
		String str = obj.toString();
		if("".equals(str)){
			return true;
		}
		return false;
	}
}
