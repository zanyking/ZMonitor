/**
 * 
 */
package org.mywebapp.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
@RunWith(value= Parameterized.class)
public class SimpleParamz_TEST extends TestCase {

	@Parameters
	public static Collection<Object[]> getUrls(){
		String prefix = "http://localhost:8080/mywebapp";
		Object[][] urls = new Object[][]{
				{prefix+"/Login"},
				{prefix+"/fetchUserList"},
				{prefix+"/listProduct"},
				{prefix+"/listUserOrder"},
		};
		return Arrays.asList(urls);
	}
	
	private String url;
	public SimpleParamz_TEST(String url){
		this.url = url;
	}
	
	@Test
	public void testCondition(){
		
	}
}
