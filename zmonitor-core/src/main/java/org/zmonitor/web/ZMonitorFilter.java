/**
 * Feb 21, 2011
 */
package org.zmonitor.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 
 * @author Ian YT Tsai (Zanyking)
 */
public class ZMonitorFilter implements Filter{
	public static final String TRACKER_NAME = "web";
	
	ZMonitorFilterHelper helper;
	ServletContext context;
	public void init(FilterConfig fConfig) throws ServletException {
		helper = ZMonitorFilterHelper.getInstance(context = fConfig.getServletContext());
	}
	public void destroy() {
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) 
	throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse) res;
		
//		System.out.println("init()  ========================"+WebUtils.toURLString(request));
		
		helper.doFilterStart( request, response);
		try {
			filterChain.doFilter(req, res);
		} finally {
			helper.doFilterEnd(request);
		}

		
//		System.out.println("dispose() ========================"+context);
	}
	
}
