/**
 * 
 */
package webtest.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zmonitor.test.slf4j.clz.BusinessObject;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DemoServlet2 extends HttpServlet{
	private static final long serialVersionUID = -6280014468787934468L;

	private static final Logger logger = LoggerFactory.getLogger(DemoServlet2.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug(">> do Demo action");
		try{
			new BusinessObject().doBiz();
			resp.sendRedirect("result.html");
		}finally{
			logger.debug("<< finish Demo Biz Logic...");	
		}
		logger.debug(">> do Demo action");
		//DO something
		logger.debug("<< finish Demo Biz Logic...");
	}
	
	

	
	
	
}
