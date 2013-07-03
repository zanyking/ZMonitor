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

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.log4j.clz.node.A;
import zmonitor.test.log4j.clz.node.D;
import zmonitor.test.log4j.clz.node.E;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DemoServlet extends HttpServlet{
	private static final long serialVersionUID = -6280014468787934468L;

	private static final Logger logger = LoggerFactory.getLogger(DemoServlet.class);
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
			thiredPartyLibs();
			resp.sendRedirect("result.html");
		}finally{
			logger.debug("<< finish Demo Biz Logic...");	
		}
		logger.debug(">> do Demo action");
		//DO something
		logger.debug("<< finish Demo Biz Logic...");
	}
	
	
	
	private void thiredPartyLibs(){
		org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(DemoServlet.class);
		log4j.debug(">> Hello Log 4J!!");
		A root = new A();
		root.toFirstChild(D.D_FAC.newFac(
				">> do list classical music artist", 
				"<< end of list"))
				.toFirstChild(E.E_FAC.newFac(" Frederic Chopin", null))
				.toNextSibling(D.D_FAC.newFac(" Pyotr Ilyich Tchaikovsky", null))
				.toNextSibling(E.E_FAC.newFac(" Wolfgang Amadeus Mozart", null))
				.toNextSibling(D.D_FAC.newFac(" Robert Alexander Schumann", null));
		root.doNode();
		log4j.debug("<< Hello Log 4J!!");
	}

	
	
	
}
