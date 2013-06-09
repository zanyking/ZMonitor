/**
 * 
 */
package org.mywebapp.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.zmonitor.webtest.WebAppAssertContext;
import org.zmonitor.webtest.WebAppAssertion;
import org.zmonitor.webtest.WebAppValidityRepository;

import junit.framework.TestCase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
@RunWith(value= Parameterized.class)
public class WebAppParamz_TEST implements WebAppAssertion{

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
	public WebAppParamz_TEST(String url){
		this.url = url;
	}
	
	@Test
	public void testCondition(){
		WebAppValidityRepository webAppValRepo = WebAppValidityRepository.getInstance();
//		Assert.assertEquals(true, false);
	}

	@Override
	public boolean matches(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void test(WebAppAssertContext asrtCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
