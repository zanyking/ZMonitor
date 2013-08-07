/**
 * 
 */
package org.zmonitor.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebUtils {
	private WebUtils() {}
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public static String toURLString(HttpServletRequest req){
		StringBuffer reqUrl = req.getRequestURL();
		
		String query = req.getQueryString();
		
		if(query!=null && !query.isEmpty()){
			reqUrl.append("?").append(query);
		}
		return reqUrl.toString();
	}
	
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public static URL toURL(HttpServletRequest req){
		String urlStr = toURLString(req);
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			//Wont' happen, if happened...there might be some bad things happened in container. 
			throw new Error(e);
		}
		return url;
	}
	/**
	 * 
	 * @param queryStr
	 * @return
	 */
	public static Map<String, Object> toMap(String queryStr) {
		return parseQueryString(queryStr);
	}
	
	 /**
     * Parses a query string passed from the client to the server and builds a
     * <code>HashTable</code> object with key-value pairs. The query string
     * should be in the form of a string packaged by the GET or POST method,
     * that is, it should have key-value pairs in the form <i>key=value</i>,
     * with each pair separated from the next by a &amp; character.
     *
     * <p>A key can appear more than once in the query string with different
     * values. However, the key appears only once in the hashtable, with its
     * value being an array of strings containing the multiple values sent
     * by the query string.
     *
     * <p>The keys and values in the hashtable are stored in their decoded
     * form, so any + characters are converted to spaces, and characters
     * sent in hexadecimal notation (like <i>%xx</i>) are converted to ASCII
     * characters.
     *
     * @param s a string containing the query to be parsed
     *
     * @return a <code>HashTable</code> object built from the parsed key-value
     * pairs
     *
     * @exception IllegalArgumentException if the query string is invalid
     */
	 private static Hashtable<String, Object> parseQueryString(String s) {

        String valArray[] = null;

        if (s == null) {
            throw new IllegalArgumentException();
        }
        Hashtable<String, Object> ht = new Hashtable<String, Object>();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = (String) st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                // XXX
                // should give more detail about the illegal argument
                throw new IllegalArgumentException();
            }
            String key = parseLexeme(pair.substring(0, pos), sb);
            String val = parseLexeme(pair.substring(pos + 1, pair.length()), sb);
            if (ht.containsKey(key)) {
                String oldVals[] = (String[]) ht.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++)
                    valArray[i] = oldVals[i];
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            ht.put(key, valArray);
        }
        return ht;
    }
	
	 /**
	  * from javax.servlet.http.HttpUtils
     * Parse a name in the query string.
     */
    static private String parseLexeme(String s, StringBuffer sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case '+':
                sb.append(' ');
                break;
            case '%':
                try {
                    sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3),
                            16));
                    i += 2;
                } catch (NumberFormatException e) {
                    // XXX
                    // need to be more specific about illegal arg
                    throw new IllegalArgumentException();
                } catch (StringIndexOutOfBoundsException e) {
                    String rest = s.substring(i);
                    sb.append(rest);
                    if (rest.length() == 2)
                        i++;
                }

                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }
	
	

}
