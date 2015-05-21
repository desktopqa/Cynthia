/**  
 * Copyright © 2015 sogou. All rights reserved.
 *
 * @Title: CorsFilter.java
 * @Prject: Cynthia_File_Upload
 * @Package: com.sogou.qadev.service.cynthia.util
 * @Description: TODO
 * @author: liming  
 * @date: 2015-4-21 上午10:56:59
 * @version: V1.0  
 */
package com.sogou.qadev.service.cynthia.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

    @Override
    public void destroy() {

    }

	/* 
	 * (non Javadoc)
	 * @Title: doFilter
	 * @Description: TODO
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws IOException
	 * @throws ServletException
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletResponse response = (HttpServletResponse) servletResponse;
//		response.addHeader("Access-Control-Allow-Origin", "*"); // open your api to any client 
//      response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.addHeader("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS");
//	    response.addHeader("Access-Control-Allow-Headers", "accept,Origin, Accept,No-Cache, x-xsrf-token, X-Requested-With,X-XSRF-TOKEN,x-xsrf-token,If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
//		response.addHeader("Access-Control-Max-Age", "36000000" ); //
        filterChain.doFilter(servletRequest, servletResponse);
	}

	/* 
	 * (non Javadoc)
	 * @Title: init
	 * @Description: TODO
	 * @param arg0
	 * @throws ServletException
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}
}