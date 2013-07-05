/**
 * 
 */
package org.zmonitor.test.junit;

import java.net.URL;

import org.zmonitor.config.ConfigSource;
import org.zmonitor.util.Loader;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TestBaseUtils {
	
	
	/**
	 * a.b -> a/b/zm.xml
	 * a -> a/zm.xml
	 * 
	 * 
	 * @param packagePath must be "a/b/c", not "a.b.c"
	 */
	public static URL findSettingFromPackagePath(String packagePath){
		URL url = Loader.getResource(packagePath+"/"+ConfigSource.ZMONITOR_XML);
		if(url!=null)return url;
		
		int lastIdx = packagePath.lastIndexOf(".");
		if(lastIdx<=0)
			return Loader.getResource(ConfigSource.ZMONITOR_XML);
		
		String parent = packagePath.substring(0, lastIdx);
		
		return (url==null)? 
			findSettingFromPackagePath(parent) :
			url;
	}
}
