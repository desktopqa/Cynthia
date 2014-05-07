package com.sogou.qadev.service.cynthia.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserRole;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserStat;
import com.sogou.qadev.service.cynthia.service.CookieManager;
import com.sogou.qadev.service.cynthia.service.ImageManager;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;

/**
 * @description:user processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:44:13
 * @version:v1.0
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

	/**
	 * @description:user register
	 * @date:2014-5-5 下午8:44:23
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/register.do")
	@ResponseBody
	public String register(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8"); 
		String userMail = request.getParameter("userMail");
		String userPassword = request.getParameter("userPassword");
		String userAlias = request.getParameter("userAlias");
		
		UserInfo userInfo = new UserInfo();
		userInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		userInfo.setNickName(userAlias);
		userInfo.setUserName(userMail);
		userInfo.setUserPassword(userPassword);
		userInfo.setUserRole(UserRole.normal);
		userInfo.setUserStat(UserStat.not_auth);
		boolean isSuccess = das.addUserInfo(userInfo);
		return String.valueOf(isSuccess);
	}
	
	/**
	 * @description:check if user is exist
	 * @date:2014-5-5 下午8:44:36
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/checkExist.do")
	@ResponseBody
	public String addTag(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String userMail = request.getParameter("userMail");
		return String.valueOf(das.queryUserInfoByUserName(userMail)  != null);
	}
	
	/**
	 * @description:change the status of user
	 * @date:2014-5-5 下午8:44:49
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/changeStat.do")
	@ResponseBody
	public String changeStat(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String userMail = request.getParameter("user");
		if (userMail == null || userMail.length() == 0) {
			return "false";
		}
		UserInfo userInfo = das.queryUserInfoByUserName(userMail);
		if (userInfo == null) {
			return "false";
		}
		UserStat userStat = null;
		try {
			userStat = UserStat.valueOf(request.getParameter("status"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (userStat == null) {
			return "false";
		}
		userInfo.setUserStat(userStat);
		return String.valueOf(das.updateUserInfo(userInfo));
	}
	
	/**
	 * @description:return the user info
	 * @date:2014-5-5 下午8:45:03
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getUserInfo.do")
	@ResponseBody
	public String getUserInfo(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String userMail = request.getParameter("user");
		if (userMail == null || userMail.length() == 0) {
			return "false";
		}
		UserInfo userInfo = das.queryUserInfoByUserName(userMail);
		if (userInfo == null) {
			return "false";
		}
		
		return JSONArray.toJSONString(userInfo);
	}
	
	/**
	 * @description:return web root dir
	 * @date:2014-5-5 下午8:45:18
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getWebRootDir.do")
	@ResponseBody
	public String getWebRootDir(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		return ConfigUtil.getCynthiaWebRoot();
	}
	
	/**
	 * @description:update the user info
	 * @date:2014-5-5 下午8:45:32
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateUserInfo.do")
	@ResponseBody
	public String updateUserInfo(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String userMail = request.getParameter("user");
		if (userMail == null || userMail.length() == 0) {
			return "false";
		}
		UserInfo userInfo = das.queryUserInfoByUserName(userMail);

		String picId = request.getParameter("picId");
		if (picId != null && !picId.equals("")) {
			userInfo.setPicId(picId);
		}
		
		String nickName = request.getParameter("nickName");
		if (nickName != null && !nickName.equals("")) {
			userInfo.setNickName(nickName);
		}
		
		String nowPass = request.getParameter("nowPass");
		String changePass = request.getParameter("changePass");
		if (nowPass != null && !nowPass.equals("") && changePass != null && !changePass.equals("")) {
			if (!nowPass.equals(userInfo.getUserPassword())) {
				return "当前密码错误!";
			}else {
				userInfo.setUserPassword(changePass);
			}
		}
		
		return String.valueOf(das.updateUserInfo(userInfo));
	}
	
	/**
	 * @description:user login
	 * @date:2014-5-5 下午8:45:46
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/login.do")
	@ResponseBody
	public String login(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String remember = request.getParameter("remember");
		String targetUrl = request.getParameter("targetUrl");
		session.setAttribute("loginErrorInfo","");
		if(validate(userName, password,session)){
        	int loginMaxAge = 0;
        	if (remember != null && remember.equals("true")) {
				//自动登陆，定义账户密码的生命周期，这里是一个月;
        		loginMaxAge = 30*24*60*60;   
        		CookieManager.addCookie(response , "remember" , remember , loginMaxAge); 
			}else {
				loginMaxAge = 24*60*60;   //不自动登陆则为一天
			}
        	
        	UserInfo userInfo = das.queryUserInfoByUserName(userName);
        	
        	CookieManager.addCookie(response , "login_username" , userName , loginMaxAge); 
		    CookieManager.addCookie(response , "login_password" , password , loginMaxAge);   
		    CookieManager.addCookie(response , "login_nickname" , URLEncoder.encode(userInfo.getNickName(), "UTF-8") , loginMaxAge);   
		    
		    session.setAttribute("userName",userName);
		    
			if(userInfo != null)  //中文名
				session.setAttribute("userAlis", userInfo.getNickName());
			
			//更新最后登陆时间
			userInfo.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			das.updateUserInfo(userInfo);
			
		    if (targetUrl != null && !targetUrl.equals("")) {
		    	return targetUrl;   //跳转到目标页
			}else {
				return "/index.html";  //跳转到首页
			}
        }else {
        	 CookieManager.addCookie(response,"login_username","",0);  //清除Cookie
        	 CookieManager.addCookie(response,"login_password","",0);    //清除Cookie
        	 CookieManager.addCookie(response,"login_nickname","",0);    //清除Cookie
		     return "/userInfo/login.jsp"; //跳转回登陆页
        }
	}
	
	/**
	 * @description:validate the user
	 * @date:2014-5-5 下午8:45:55
	 * @version:v1.0
	 * @param userName
	 * @param password
	 * @param session
	 * @return
	 */
	public boolean validate(String userName,String password,HttpSession session)
	{
		UserInfo userInfo = das.queryUserInfoByUserName(userName);
		if (userInfo == null) {
			session.setAttribute("loginErrorInfo","用户名不存在!");
			return false;
		}
		if (userInfo.getUserPassword() != null && !userInfo.getUserPassword().equals(password)) {
			session.setAttribute("loginErrorInfo","密码错误!");
			return false;
		}
		if (userInfo.getUserStat().equals(UserStat.not_auth)) {
			session.setAttribute("loginErrorInfo","帐号目前未通过管理员审核!");
			return false;
		}else if (userInfo.getUserStat().equals(UserStat.lock)) {
			session.setAttribute("loginErrorInfo","帐号目前己被锁定,请与管理员联系!");
			return false;
		}
		return true;
	}
	
	/**
	 * @description:update the user pic
	 * @date:2014-5-5 下午8:46:09
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/uploadPic.do")
	@ResponseBody
	public ModelAndView uploadPreviewImage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile image = multipartRequest.getFile("userPic");
        
        response.setCharacterEncoding("UTF-8");
        response.setHeader("ContentType", "json");
        PrintWriter out = response.getWriter();
        
        String imgBase64 = ImageManager.getImageStr(image.getInputStream());
        
        //传给页面base64编码，没做错误处理
        out.print("{");  
        out.print("imgBase64:'"+ JSONArray.toJSONString(imgBase64) +"',"); 
        out.print("msg:'success'"); 
        out.print("}");
        out.flush();
        out.close(); 
        return null;
    }
    
    /**
     * @description:cur image
     * @date:2014-5-5 下午8:46:30
     * @version:v1.0
     * @param request
     * @param response
     * @param session
     * @return
     * @throws IOException
     */
	@RequestMapping("/cutImage.do")
	@ResponseBody
    public String cutImage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		int x = Integer.parseInt(request.getParameter("x"));
		int y = Integer.parseInt(request.getParameter("y"));
		int w = Integer.parseInt(request.getParameter("w"));
		int h = Integer.parseInt(request.getParameter("h"));
		String fileId = request.getParameter("fileId");

        if(w <= 0)
            w = 200;
        if(h<=0)
            h = 200;
        if(x<0)
            x = 0;
        if(y<0)
            y = 0;
       return String.valueOf(ImageManager.abscut(fileId, x, y, w, h));
    }
    
}
