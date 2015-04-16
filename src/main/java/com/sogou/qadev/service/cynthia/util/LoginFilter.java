package com.sogou.qadev.service.cynthia.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

import org.apache.commons.codec.binary.Base64;

import bsh.Console;

import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserRole;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserStat;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.CookieManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;

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

	public void doFilter(ServletRequest request, ServletResponse response,FilterChain nextFilter) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		HttpServletResponse httpResponse = (HttpServletResponse) response;
	
		String requestURI = httpRequest.getRequestURI();
		
//		if (ConfigManager.deployUrl == null || (!ConfigManager.getProjectInvolved() && CookieManager.getCookieByName(httpRequest, "webRootDir") == null)) {
			ConfigManager.deployPath = httpRequest.getContextPath();
			ConfigManager.deployUrl = httpRequest.getHeader("Origin");
			ConfigManager.deployScheme = httpRequest.getScheme();
			
			if(CynthiaUtil.isNull(ConfigManager.deployUrl)){
				ConfigManager.deployUrl = httpRequest.getHeader("Host");
			}
			ConfigManager.deployUrl = ConfigManager.deployUrl.replace("http://", "");
			CookieManager.addCookie(httpResponse, "webRootDir", ConfigUtil.getCynthiaWebRoot(),  60 * 60 * 24 * 14 ,null);
//		}
		
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
			if (ConfigManager.getEnableSso()) {
				Cookie idCookie = CookieManager.getCookieByName(httpRequest,"id");
				if (idCookie != null) {
					String userId = trimSafe(idCookie.getValue()).split("\\.")[0];
					UserInfo userInfo = ProjectInvolveManager.getInstance().getUserInfoById(userId);
					if (userInfo != null) {
						userName = trimSafe(userInfo.getUserName());
					}
				}
			}else {
				Cookie userNameCookie = CookieManager.getCookieByName(httpRequest,"login_username");
				if (userNameCookie != null) {
					userName = trimSafe(userNameCookie.getValue());
				}
				
				UserInfo userInfo = das.queryUserInfoByUserName(userName);
				if (userInfo == null) {
					userName = null;
				}
			}
			
			if (userName == null) {
				if(!CynthiaUtil.isNull(requestURI)){
					requestURI = requestURI.substring(1);
				}
				
				String redirectUrl = null;
				String targetUrl = null;
				
				// *用户登录以后需手动添加session  
			    if("XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"))){  
			    	//ajax请求跳转到首页
			    	targetUrl = ConfigUtil.getCynthiaWebRoot();
			    	redirectUrl = ConfigUtil.getLoginUrl() + ( ConfigUtil.getLoginUrl().indexOf("?") != -1 ? "&" : "?" ) +  "targetUrl=" + targetUrl;
			    	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			    	httpResponse.addHeader("Vary", "Origin");
			    	httpResponse.addHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
			    	httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
			    	httpResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
			    	httpResponse.addHeader("Access-Control-Allow-Headers", "host, user-agent, accept, content-type, x-real-ip, x-forwarded-ip, x-forwarded-for, x-xsrf-token, x-requested-with");
			    	httpResponse.getWriter().println(redirectUrl);
			    } else {  
			    	targetUrl = ConfigUtil.getTargetUrl(httpRequest);
					redirectUrl = ConfigUtil.getLoginUrl() + ( ConfigUtil.getLoginUrl().indexOf("?") != -1 ? "&" : "?" ) +  "targetUrl=" + targetUrl;
			    	httpResponse.sendRedirect(redirectUrl);  
			    } 
				return;
			}else {
				Key tempKey = new Key();
				tempKey.setUsername(userName);
				session.setAttribute("userName", userName);
				session.setAttribute("key", tempKey);
			}
		}

		Long kid = (Long) session.getAttribute("kid");

		if (kid == null) {
			kid = ConfigUtil.magic;
			session.setAttribute("kid", kid);
		}
		
		if (!authUserRole(dataAndEventId, userName) && httpRequest.getQueryString().indexOf("previewFlow") == -1) {
			httpResponse.sendRedirect(ConfigUtil.getCynthiaWebRoot() + "error.html");
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
	
	public static String sign(String userId, HttpServletRequest request) throws Exception
    {
        String userdata = "";
        String[] allHeaders = {"user-agent", "x-real-ip", "x-forwarded-for"};
        for (String header : allHeaders) {
        	if (request.getHeader(header) != null) {
        		userdata += request.getHeader(header);
			}
		}
        String sign = userId + "." + encode("lsh", (userId + userdata));
        return sign;
    }
	
	/**
	 * @Title: encode
	 * @Description: sha-256加密
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 * @return: String
	 */
	public static String encode(String key, String data) throws Exception {
		 Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		 SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA256");
		 sha256_HMAC.init(secret_key);
		 return Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("utf-8")));
	}

}
