/**
 * Feb 21, 2011
 */
package org.zmonitor.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorFilter implements Filter{
	public static final String TRACKER_NAME = "web";
	
	ZMonitorFilterHelper helper;
	public void init(FilterConfig fConfig) throws ServletException {
		helper = ZMonitorFilterHelper.getInstance(fConfig.getServletContext());
	}
	public void destroy() {
	}
	
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain filterChain) 
	throws IOException, ServletException {
		helper.doFilter((HttpServletRequest)request, 
				(HttpServletResponse)res, 
				filterChain);
	}
}
