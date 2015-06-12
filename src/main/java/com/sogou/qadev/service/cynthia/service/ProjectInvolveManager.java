package com.sogou.qadev.service.cynthia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.net.URLEncoder;
import java.net.UnknownServiceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import sun.tools.tree.NewArrayExpression;

import bsh.This;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.fabric.xmlrpc.base.Value;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.ActionRole;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserRole;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserStat;
import com.sogou.qadev.service.cynthia.bean.impl.RoleImpl;
import com.sogou.qadev.service.cynthia.bean.impl.UserInfoImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.util.ArrayUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.URLUtil;

/**
 * @description:与项目管理相关方法处理类
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:01:29
 * @version:v1.0
 */
public class ProjectInvolveManager {
	private static Logger logger = Logger.getLogger(ProjectInvolveManager.class.getName());
	
	private static Map<String, String> userSignMap = new HashMap<String, String>();
	
	public static Map<String, String> proNameMap = new HashMap<String, String>();  //产品 项目 id -- Name

	public static Map<String, String> userNameMap = new HashMap<String, String>();  //用户邮箱 --姓名
	
	private static Properties properties = ConfigManager.getProInvolvedProperties();
	
	private ProjectInvolveManager() {}

	private static class SingletonHolder{
		private static ProjectInvolveManager databasePool = new ProjectInvolveManager();
	}

	public static ProjectInvolveManager getInstance() {
		return SingletonHolder.databasePool;
	}

	/**
	 * @Title: isProjectInvolved
	 * @Description: 是否与项目管理关联
	 * @return
	 * @return: boolean
	 */
	public boolean isProjectInvolved(){
		return ConfigManager.getProjectInvolved();
	}
	
	/**
	 * @Title: getRoleMap
	 * @Description: 获取所有角色信息
	 * @return
	 * @return: Map<String,String>
	 */
	public List<Role> getAllRole(String userMail){
		List<Role> allRoles = new ArrayList<Role>();
		String cookie = getUserSign(userMail,"");
		if (isProjectInvolved()) {
			try {
				String getUrl = properties.getProperty("base_url") + properties.getProperty("role_get_url");
				String result = URLUtil.sendGet(getUrl, "",cookie);
				
				JSONArray jsonArray = JSONArray.parseArray(result);
				if (jsonArray != null) {
					for (Object object : jsonArray) {
						JSONObject jsonObject = JSONObject.parseObject(object.toString());
						allRoles.add(new RoleImpl(jsonObject.getString("id"), "", jsonObject.getString("name")));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return allRoles;
	}
	
	/**
	 * @Title: getProjectNameById
	 * @Description: 根据项目Id查询项目名字
	 * @param productId
	 * @return
	 * @return: String
	 */
	public String getProjectNameById(String projectId){
		String projectName = proNameMap.get(projectId);
		String result = "";
		if (proNameMap.get(projectName) == null) {
			if (isProjectInvolved()) {
				String cookie = getUserSign("", "1");  // 1为管理员用户
				try {
					String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("project_name_get_url"), projectId);
					result = URLUtil.sendGet(getUrl, "",cookie);
					JSONObject jsonObject = JSONArray.parseObject(result);
					if (jsonObject != null) {
						projectName = jsonObject.getString("name");
						if (projectName != null) {
							proNameMap.put(projectId, projectName);
						}
					}
				} catch (Exception e) {
					System.out.println("getProjectNameById error! projectId:" + projectId + " and result is : " + result);
				}
			}
		}
		return projectName;
	}
	
	/**
	 * @Title: getProductNameById
	 * @Description: 根据产品Id查询产品名字
	 * @param productId
	 * @return
	 * @return: String
	 */
	public String getProductNameById(String productId){
		String productName = proNameMap.get(productId);
		if (proNameMap.get(productId) == null) {
			if (isProjectInvolved()) {
				String cookie = getUserSign("", "1");
				try {
					String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("product_name_get_url"), productId);
					String result = URLUtil.sendGet(getUrl, "",cookie);
					
					JSONObject jsonObject = JSONArray.parseObject(result);
					if (jsonObject != null) {
						productName = jsonObject.getString("name");
						if (productName != null) {
							proNameMap.put(productId, productName);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return productName;
	}
	
	/**
	 * @Title: isUserInRole
	 * @Description: 判断用户是否有某角色
	 * @param userMail
	 * @param roleId
	 * @return
	 * @return: boolean
	 */
	public boolean isUserInRole(String userMail, UUID roleId){
		if (userMail == null || roleId == null) {
			return false;
		}
		List<Role> allRoles = getAllRole(userMail);
		for (Role role : allRoles) {
			if (role.getId().getValue().equals(roleId.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @Title: getProductMap
	 * @Description: 获取所有产品信息
	 * @return
	 * @return: Map<String,String>
	 */
	public Map<String, String> getProductMap(String userMail){
		Map<String, String> productMap = new HashMap<String, String>();
		String cookie = getUserSign(userMail,"");
		if (isProjectInvolved()) {
			try {
				String getUrl = properties.getProperty("base_url") + properties.getProperty("product_get_url");
				String result = URLUtil.sendGet(getUrl, "", cookie);
				
				JSONArray jsonArray = JSONArray.parseArray(result);
				for (Object object : jsonArray) {
					JSONObject jsonObject = JSONObject.parseObject(object.toString());
					productMap.put(jsonObject.getString("id"), jsonObject.getString("name"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		proNameMap.putAll(productMap);
		return productMap;
	}
	
	/**
	 * @Title: getProjectMap
	 * @Description: 获取所有项目信息
	 * @param productId
	 * @return
	 * @return: Map<String,String>
	 */
	public Map<String, String> getProjectMap(String userMail,String productId){
		String cookie = getUserSign(userMail,"");
		Map<String, String> projectMap = new HashMap<String, String>();
		if (isProjectInvolved()) {
			try {
				String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("project_get_by_product_url"), productId);
				String result = URLUtil.sendGet(getUrl, "", cookie);
				
				JSONArray jsonArray = JSONArray.parseArray(result);
				for (Object object : jsonArray) {
					JSONObject jsonObject = JSONObject.parseObject(object.toString());
					projectMap.put(jsonObject.getString("id"), jsonObject.getString("name"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		proNameMap.putAll(projectMap);
		return projectMap;
	}
	
	/**
	 * @Title: getUserSign
	 * @Description: 获取用户签名
	 * @param userMail
	 * @return
	 * @return: String
	 */
	public String getUserSign(String userMail,String userId){
		String sign = userSignMap.get(userMail);
		if (sign == null) {
			Map<String, String> jsonMap = new HashMap<String, String>();
			jsonMap.put("username", userMail);
			jsonMap.put("id", userId);
			String getUrl = "";
			try {
				getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("user_sign_get_url"), URLEncoder.encode(JSONArray.toJSONString(jsonMap),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			List<String> cookies = URLUtil.getResponseCookie(getUrl, "","");
			for (String cookieKey : cookies) {
			    String[] values = cookieKey.split("=");
			    if (values[0] != null && values[0].equals("id")) {
			    	sign = values[1].split(";")[0];
					if (sign != null && !sign.equals("")) {
						userSignMap.put(userMail, sign);
					}
				}
 			}
		}
		
		System.out.println("getUserSign of " + userMail + " Result:" + sign);
		return "id=" + sign;
	}
	
	/**
	 * @Title: getUserInfoByProjectAndRole
	 * @Description: 通过项目Id和角色Id获取用户信息
	 * @param projectId
	 * @param roleId
	 * @return
	 * @return: List<UserInfo>
	 */
	public List<UserInfo> getUserInfoByProjectAndRole (String userMail, String projectId,String roleId){
		List<UserInfo> allUsers = new ArrayList<UserInfo>();
		if (CynthiaUtil.isNull(userMail) || CynthiaUtil.isNull(projectId) || CynthiaUtil.isNull(roleId)) {
			return allUsers;
		}
		String cookie = getUserSign(userMail,"");
		String[] allRoleIds = roleId.split(",");
		if (isProjectInvolved()) {
			try {
				String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("user_get_by_project_and_role"), JSONArray.toJSONString(allRoleIds),projectId);
				String result = URLUtil.sendGet(getUrl, "",cookie);
				JSONArray jsonArray = JSONArray.parseArray(result);
				for (Object object : jsonArray) {
					JSONObject jsonObject = JSONObject.parseObject(object.toString());
					UserInfo userInfo = new UserInfoImpl();
					userInfo.setUserName(jsonObject.getString("email"));
					userInfo.setNickName(jsonObject.getString("name"));
					userInfo.setId(jsonObject.getInteger("id"));
					userInfo.setCreateTime(Timestamp.valueOf(jsonObject.getString("updateTime").replace("T", " ").replace("Z", "")));
					userNameMap.put(userInfo.getUserName(), userInfo.getNickName());
					allUsers.add(userInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return allUsers;
	}
	
	/**
	 * @Title: getUserInfoByMail
	 * @Description: 获取用户信息
	 * @return
	 * @return: List<UserInfo>
	 */
	public UserInfo getUserInfoById(String userId){
		UserInfo userInfo = null;
		if (userId == null) {
			return null;
		}
		if (userId.indexOf(".") != -1) {
			userId = userId.split("\\.")[0];
		}
		String cookie = getUserSign("",userId);
		if (CynthiaUtil.isNull(userId)) {
			return userInfo;
		}
		if (isProjectInvolved()) {
			try {
				String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("user_get_by_id_url"), userId);
				String result = URLUtil.sendGet(getUrl, "",cookie);
				JSONObject jsonObject = JSONArray.parseObject(result);
				userInfo = new UserInfoImpl();
				userInfo.setId(Integer.parseInt(jsonObject.getString("id")));
				userInfo.setUserName(jsonObject.getString("email"));
				userInfo.setNickName(jsonObject.getString("name"));
				userInfo.setCreateTime(Timestamp.valueOf(jsonObject.getString("updateTime").replace("T", " ").replace("Z", "")));
				userInfo.setUserRole(priviledgeQuery(userId, "cynthia_entryConfig") ? UserRole.admin : UserRole.normal);
				userInfo.setPicUrl("http://www.effevo.com/anonymous/resource/user/logo/" + userInfo.getId()+ "/large");
				userNameMap.put(userInfo.getUserName(), userInfo.getNickName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userInfo;
	}
	
	/**
	 * @Title: priviledgeQuery
	 * @Description: 用户权限查询
	 * @param userId
	 * @param queryStr
	 * @return
	 * @return: boolean
	 */
	public boolean priviledgeQuery(String userId, String queryStr){
		String cookie = getUserSign("",userId);
		if (CynthiaUtil.isNull(userId) || CynthiaUtil.isNull(queryStr)) {
			return false;
		}
		if (isProjectInvolved()) {
			String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("user_right_query_url"),userId,queryStr);
			String result = URLUtil.sendGet(getUrl, "",cookie);
			return Boolean.parseBoolean(result);
		}
		return false;
	}
	
	/**
	 * @Title: getUserInfoByMail
	 * @Description: 获取用户信息
	 * @return
	 * @return: List<UserInfo>
	 */
	public UserInfo getUserInfoByMail(String userMail){
		UserInfo userInfo = null;
		if (CynthiaUtil.isNull(userMail)) {
			return userInfo;
		}
		String cookie = getUserSign(userMail,"");
		if (isProjectInvolved()) {
			try {
				String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("user_get_by_mail_url"), userMail);
				System.out.println("getUserInfoByMail,url:" + getUrl);
				String result = URLUtil.sendGet(getUrl, "",cookie);
				JSONObject jsonObject = JSONArray.parseObject(result);
				userInfo = new UserInfoImpl();
				userInfo.setId(Integer.parseInt(jsonObject.getString("id")));
				userInfo.setUserName(jsonObject.getString("email"));
				userInfo.setNickName(jsonObject.getString("name"));
				userInfo.setCreateTime(Timestamp.valueOf(jsonObject.getString("updateTime").replace("T", " ").replace("Z", "")));
				userInfo.setUserStat(UserStat.normal);
				userInfo.setUserRole(priviledgeQuery(String.valueOf(userInfo.getId()), "cynthia_entryConfig") ? UserRole.admin : UserRole.normal);
				userInfo.setPicUrl("http://www.effevo.com/resource/user/logo/" + userInfo.getId()+ "/large");
				userNameMap.put(userInfo.getUserName(), userInfo.getNickName());
			} catch (Exception e) {
				System.out.println("error in getUserInfoByMail , userMail :" + userMail);
			}
		}
		return userInfo;
	}
	
	
	/**
	 * @Title: queryActionRoles
	 * @Description: TODO
	 * @param userName
	 * @param flow
	 * @param actionId
	 * @return
	 * @return: Role[]
	 */
	public Role[] queryActionRoles(String userName, Flow flow, UUID actionId) {
		Set<Role> allRoleSet = new HashSet<Role>();
		List<Role> allRoles = getAllRole(userName);
		Map<String, Role> roleMap = new HashMap<String, Role>();
		for (Role role : allRoles) {
			roleMap.put(role.getId().getValue(), role);
		}
		
		Set<ActionRole> allActionRoles = flow.getActionRoleSet();
		for (ActionRole actionRole : allActionRoles) {
			if (actionRole.getActionId().equals(actionId)) {
				Role role = roleMap.get(actionRole.getRoleId().getValue());
				if (role != null) {
					allRoleSet.add(role);
				}
			}
		}
		return allRoleSet.toArray(new Role[0]);
	}
	
	/**
	 * @Title: getCompanyUsersByMail
	 * @Description: 获取同公司的所有用户邮箱 
	 * @param userMail
	 * @return
	 * @return: List<UserInfo>
	 */
	public List<UserInfo> getCompanyUsersByMail(String userMail){
		List<UserInfo>userInfos = new ArrayList<UserInfo>();
		if (CynthiaUtil.isNull(userMail)) {
			return userInfos;
		}
		String cookie = getUserSign(userMail,"");
		if (isProjectInvolved()) {
			try {
				Set<String> allBackUsers = ProjectInvolveManager.getInstance().getAllBackUserMails(userMail);
				
				String getUrl = properties.getProperty("base_url") + properties.getProperty("get_company_user_url");
				String result = URLUtil.sendGet(getUrl, "",cookie);
				
				JSONArray jsonArray = JSONArray.parseArray(result);
				for (Object object : jsonArray) {
					JSONObject jsonObject = JSONObject.parseObject(object.toString());
					UserInfo userInfo = new UserInfoImpl();
					userInfo.setUserName(jsonObject.getString("email"));
					userInfo.setNickName(jsonObject.getString("name"));
					userInfo.setCreateTime(Timestamp.valueOf(jsonObject.getString("updateTime").replace("T", " ").replace("Z", "")));
					userInfo.setId(jsonObject.getInteger("id"));
					if (allBackUsers.contains(userInfo.getUserName())) {
						userInfo.setUserRole(UserRole.admin);
					}else {
						userInfo.setUserRole(UserRole.normal);
					}
					userNameMap.put(userInfo.getUserName(), userInfo.getNickName());
					userInfos.add(userInfo);
				}
			} catch (Exception e) {
				System.out.println("error in getCompanyUsersByMail , userMail :" + userMail);
			}
		}
		return userInfos;
	}
	
	/**
	 * @Title: getCompanyUserMails
	 * @Description: TODO
	 * @param userMail
	 * @return
	 * @return: Set<String>
	 */
	public Set<String> getCompanyUserMails(String userMail){
		Set<String>userMails = new HashSet<String>();
		List<UserInfo> userInfos = getCompanyUsersByMail(userMail);
		if (userInfos != null && userInfos.size() > 0) {
			for (UserInfo userInfo : userInfos) {
				userMails.add(userInfo.getUserName());
			}
		}
		return userMails;
	}
	
	/**
	 * @Title: getUserName
	 * @Description: 返回用户昵称
	 * @param userMail
	 * @return
	 * @return: String
	 */
	public String getUserName(String userMail){
		String userName = userNameMap.get(userMail);
		if (userName == null) {
			UserInfo userInfo = getUserInfoByMail(userMail);
			if (userInfo != null) {
				userName = userInfo.getNickName();
				ProjectInvolveManager.userNameMap.put(userMail, userName);
			}
		}
		return userName;
	}
	
	/**
	 * @Title: getAllBackUsers
	 * @Description: 查询所有有后台权限的用户
	 * @param userMail
	 * @return
	 * @return: Set<UserInfo>
	 */
	public Set<UserInfo> getAllBackUsers(String userMail){
		Set<UserInfo> allUsers = new HashSet<UserInfo>();
		String cookie = getUserSign(userMail,"");
		if (isProjectInvolved()) {
			try {
				String getUrl = String.format(properties.getProperty("base_url") + properties.getProperty("right_users_url"), "cynthia_entryConfig");
				String result = URLUtil.sendGet(getUrl, "",cookie);
				JSONArray jsonArray = JSONArray.parseArray(result);
				for (Object object : jsonArray) {
					JSONObject jsonObject = JSONObject.parseObject(object.toString());
					UserInfo userInfo = new UserInfoImpl();
					userInfo.setUserName(jsonObject.getString("email"));
					userInfo.setNickName(jsonObject.getString("name"));
					userInfo.setId(jsonObject.getInteger("id"));
					userInfo.setCreateTime(Timestamp.valueOf(jsonObject.getString("updateTime").replace("T", " ").replace("Z", "")));
					userNameMap.put(userInfo.getUserName(), userInfo.getNickName());
					allUsers.add(userInfo);
				}
			} catch (Exception e) {
				System.out.println("error in getUserInfoByMail , userMail :" + userMail);
			}
		}
		return allUsers;
	}
	/**
	 * @Title: getAllBackUsers
	 * @Description: 查询所有有后台权限的用户
	 * @param userMail
	 * @return
	 * @return: Set<String>
	 */
	public Set<String> getAllBackUserMails(String userMail){
		Set<UserInfo> allUsers = getAllBackUsers(userMail);
		Set<String> userMails = new HashSet<String>();
		if (allUsers != null && allUsers.size() > 0) {
			for (UserInfo userInfo : allUsers) {
				userMails.add(userInfo.getUserName());
			}
		}
		return userMails;
	}
	
	public boolean sendMail(String fromUser,String title,String[] recievers,String content){
		UserInfo userInfo = getUserInfoByMail(fromUser);
//		int userId = userInfo != null ? userInfo.getId() : 1;
		String cookie = getUserSign("","1");
		if (ConfigManager.getProjectInvolved()) {
			try {
				String mailPostUrl = properties.getProperty("base_url") + properties.getProperty("mail_post_url");
				Map<String, Object> mailParams = new HashMap<String, Object>();
//				mailParams.put("userId", userId);
				mailParams.put("appName", "Bug管理");
				mailParams.put("recommendConsumer", new String[]{"mail"});
				
				List<Map<String, String>> toMailList = new ArrayList<Map<String,String>>();
				Map<String, String> toUserMap = new HashMap<String, String>();
				toUserMap.put("type", "mail");
				toUserMap.put("address", ArrayUtil.strArray2String(recievers));
				toMailList.add(toUserMap);
				
				mailParams.put("to", toMailList);
				mailParams.put("title", title);
				mailParams.put("contentType", "text/html");
				mailParams.put("content", content);
				
				String result = URLUtil.sendPost(mailPostUrl, JSONArray.toJSONString(mailParams) ,cookie);
				return result.indexOf("OK") != -1;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	public static void main(String[] args){
		System.out.println(new ProjectInvolveManager().getAllBackUsers("liming@sogou-inc.com"));
	}
}