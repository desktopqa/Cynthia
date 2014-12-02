package com.sogou.qadev.service.cynthia.util;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bsh.Console;

import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserRole;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserStat;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.CookieManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;

public class LoginFilter implements Filter {
	static public final int CAN_SKIP_INPUT = 1;

	static public final int CAN_NOT_SKIP_INPUT = 2;

	private int retryAccount = 5;

	private long productId = -1;

	private int dataAndEventId = 1;

	private String sendRedirectUrl = null;

	private String magic = null;

	private HashSet<String> magicUrlSet = new HashSet<String>();

	private HashSet<String> noIEUrlSet = new HashSet<String>();

	private DataAccessSession das = DataAccessFactory.getInstance()
			.createDataAccessSession(ConfigUtil.sysEmail,
					DataAccessFactory.magic);

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
		magic = trimSafe(config.getInitParameter("magic"));
		sendRedirectUrl = trimSafe(config.getInitParameter("sendRedirectUrl"));

		String retry = trimSafe(config.getInitParameter("retryAccount"));

		String idString = trimSafe(config.getInitParameter("dataAndEventIds"));
		String magicUrls = trimSafe(config.getInitParameter("magicUrls"));

		String noIEUrls = trimSafe(config.getInitParameter("noIEUrls"));

		if (valid(retry))
			retryAccount = Integer.parseInt(retry);

		if (valid(idString)) {
			String[] pairString = idString.split(";");

			for (int i = 0; i < pairString.length; i++) {
				if (idString != null)
					dataAndEventId = Integer.parseInt(idString);
			}
		}

		if (valid(magicUrls)) {
			String[] urls = magicUrls.split(";");
			for (String url : urls)
				if (valid(url))
					magicUrlSet.add(url);
		}

		if (valid(noIEUrls)) {
			String[] urls = noIEUrls.split(";");
			for (String url : urls)
				if (valid(url))
					noIEUrlSet.add(url);
		}

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain nextFilter) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		HttpServletResponse httpResponse = (HttpServletResponse) response;
	
		if (ConfigManager.deployPath == null || CookieManager.getCookieByName(httpRequest, "webRootDir") == null) {
			ConfigManager.deployPath = httpRequest.getContextPath();
			ConfigManager.deployUrl = httpRequest.getHeader("Host");
			CookieManager.addCookie(httpResponse, "webRootDir", ConfigUtil.getCynthiaWebRoot(), 60*60*24*14);
		}
		
		String requestURI = httpRequest.getRequestURI();
		
		for (String magicUrl : magicUrlSet) {
			if (requestURI.contains(magicUrl)) {
				nextFilter.doFilter(request, response);
				return;
			}
		}
		Key key = (Key) session.getAttribute("key");
		// //线上环境结束
		String userName = (String) session.getAttribute("userName");
		if (key != null) {
			userName = key.getUsername();
		} else if (key == null && userName != null) {
			Key tempKey = new Key();
			tempKey.setUsername(userName);
			session.setAttribute("key", tempKey);
		} else if (key == null && userName == null) {
			Cookie userNameCookie = CookieManager.getCookieByName(httpRequest,"login_username");
			if (userNameCookie != null) {
					userName = trimSafe(userNameCookie.getValue());
					Key tempKey = new Key();
					tempKey.setUsername(userName);
					session.setAttribute("userName", userName);
					session.setAttribute("key", tempKey);
			}
			
			UserInfo userInfo = das.queryUserInfoByUserName(userName);
			if (userName == null || userInfo == null) {
				//跳转到登陆界面

				String requestUrl = httpRequest.getRequestURL().toString();
				String queryString = httpRequest.getQueryString();
				if (queryString != null) {
					requestUrl = requestUrl + "?" + queryString;
				}
				
				requestUrl = java.net.URLEncoder.encode(requestUrl, "UTF-8");
				String redirectUrl = ConfigUtil.getCynthiaWebRoot() + "userInfo/login.jsp?targetUrl=" + requestUrl;
				
				httpResponse.sendRedirect(redirectUrl);
				return;
			}
		}

		Long kid = (Long) session.getAttribute("kid");

		if (kid == null) {
			kid = ConfigUtil.magic;
			session.setAttribute("kid", kid);
		}

		if (!authUserRole(dataAndEventId, userName)) {
			httpResponse.sendRedirect(ConfigUtil.getCynthiaWebRoot() + "error.jsp");
			return;
		}

		if (nextFilter != null)
			nextFilter.doFilter(request, response);
	}

	protected boolean authUserRole(int eventId, String userName) {
		if (userName == null)
			return false;
		if (eventId == 1)
			return true;
		//判断是否具有后台权限
		UserInfo userInfo = das.queryUserInfoByUserName(userName);
		
		if (userInfo == null) {
			return false;
		}else {
			//状态正常 并且为管理员或超级管理员具有后台权限 
			return userInfo.getUserStat().equals(UserStat.normal) && 
					(userInfo.getUserRole().equals(UserRole.admin) ||
							userInfo.getUserRole().equals(UserRole.super_admin));
		}
	}

	protected String trimSafe(String str) {
		if (str == null)
			return null;

		return str.trim();
	}

	protected boolean valid(String str) {
		return str != null && str.length() > 0;
	}

}
