package com.sogou.qadev.service.cynthia.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpRequest;

import net.sf.ehcache.statistics.extended.ExtendedStatistics.Statistic;

import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;

public class ConfigUtil {

	// 缓存默认配置字段
	final static public String templateFieldCacheprefix = "templateFieldCache_";
	// 内部使用
	final static public long ProductId = 45;

	final static public String sysEmail = "cynthia@sogou-inc.com";

	final static public long magic = DataAccessFactory.magic;

	final static public long maxUploadFileSize = 30 * 1024 * 1024;

	final static public long maxUploadFileAccount = 5;

	final static public long filterTaskCountRefreshInterval = 10 * 60 * 1000;

	static public int maxNewTaskCount = 500;

	public static Map<String, String> baseFieldNameMap = new HashMap<String, String>();

	public static Map<String, String> baseFieldIdMap = new HashMap<String, String>();

	public static Set<String> abandonTemplateIdSet = new HashSet<String>(); // 废弃的表单

	static {
		baseFieldNameMap.put("id", "编号");
		baseFieldNameMap.put("title", "标题");
		baseFieldNameMap.put("description", "描述");
		baseFieldNameMap.put("status_id", "状态");
		baseFieldNameMap.put("create_user", "创建人");
		baseFieldNameMap.put("create_time", "创建时间");
		baseFieldNameMap.put("assign_user", "指派人");
		baseFieldNameMap.put("last_modify_time", "修改时间");
		baseFieldNameMap.put("node_id", "项目");
		baseFieldNameMap.put("action_id", "执行动作");
		baseFieldNameMap.put("action_user", "执行人");
		baseFieldNameMap.put("action_comment", "执行描述");
		baseFieldNameMap.put("action_index", "执行序号");
	}

	static {
		for (Map.Entry<String, String> entry : baseFieldNameMap.entrySet())
			baseFieldIdMap.put(entry.getValue(), entry.getKey());
	}

	// 配置所有系统过滤器
	public static List<UUID> allSysFilterList = new ArrayList<UUID>();

	public static Map<String, String> allSysFilterMap = new LinkedHashMap<String, String>();
	
	static {
		allSysFilterList.add(DataAccessFactory.getInstance().createUUID("119695"));
		allSysFilterList.add(DataAccessFactory.getInstance().createUUID("119891"));
		allSysFilterList.add(DataAccessFactory.getInstance().createUUID("119892"));
		allSysFilterList.add(DataAccessFactory.getInstance().createUUID("119893"));
		
		allSysFilterMap.put("119695", "待处理");
		allSysFilterMap.put("119891", "待跟踪");
		allSysFilterMap.put("119892", "已处理[未关闭]");
		allSysFilterMap.put("119893", "已处理[己关闭]");
	}
	
	public static Map<String, String> templateTypeIconMap = new HashMap<String, String>();
	static {
		templateTypeIconMap.put("缺陷", "Bug.gif");
		templateTypeIconMap.put("任务", "Task.gif");
		templateTypeIconMap.put("日常管理", "Daily.gif");
	}

	// 需要在部署时确定实际的存储路径
	final static public String attachmentSavePath = "/tmp/";

	final static public LinkedHashMap<String, String> taskTableTitle = new LinkedHashMap<String, String>();

	final static public LinkedHashMap<String, String> taskProcessorJspMap = new LinkedHashMap<String, String>();

	// doTimer最大重试次数
	static public int maxRetryDoTimerAccount = 5;

	// doTimer重试延迟时间，毫秒
	static public int doTimerDelayTime = 50000;

	static {
		taskProcessorJspMap.put("Bug", "newBug.jsp");
		taskProcessorJspMap.put("Task", "newTask.jsp");
	}

	private static String getHostUrl(){
		return ConfigManager.deployScheme + "://" + ConfigManager.deployUrl;
	}
	
	public static String getCynthiaWebRoot() {
		StringBuffer webRootBuffer = new StringBuffer();
		webRootBuffer.append(getHostUrl());
		if (ConfigManager.deployPath != null && !ConfigManager.deployPath.equals("")) {
			webRootBuffer.append(ConfigManager.deployPath + "/");
		}else {
			webRootBuffer.append("/");
		}
		return webRootBuffer.toString();
	}
	
	public static String getTargetUrl(HttpServletRequest request){
		String requestURI = request.getRequestURI();
		if(!CynthiaUtil.isNull(requestURI)){
			requestURI = requestURI.substring(1);
		}
		
		String targetUrl = getHostUrl() + "/" + requestURI + (request.getQueryString() != null ? "?" + request.getQueryString() : "" );
		try {
			return URLEncoder.encode(targetUrl,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return targetUrl;
		}
	}

	public static String[] getTaskTableHeaderKey() {
		return taskTableTitle.keySet().toArray(
				new String[taskTableTitle.size()]);
	}

	public static String[] getTaskTableHeaderValue() {
		String[] values = new String[taskTableTitle.size()];
		String[] keys = getTaskTableHeaderKey();

		for (int i = 0; i < keys.length; i++)
			values[i] = taskTableTitle.get(keys[i]);

		return values;
	}

	public static String getEnvXML(String username, UUID templateTypeId,
			UUID templateId, DataAccessSession das) {
		StringBuffer envXMLBuffer = new StringBuffer(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");

		envXMLBuffer.append("<env>");
		envXMLBuffer.append("<current_user>").append(username)
				.append("</current_user>");
		envXMLBuffer.append("<current_template_type>")
				.append(templateTypeId != null ? templateTypeId : "")
				.append("</current_template_type>");
		envXMLBuffer.append("<current_template>")
				.append(templateId != null ? templateId : "")
				.append("</current_template>");

		envXMLBuffer.append("<user_list>").append(username).append("</user_list>");

		envXMLBuffer.append("</env>");

		return envXMLBuffer.toString();
	}

	// 内部使用
	final static public int beanAccount = 14;

	final static public HashMap<String, HashMap<String, String>> fieldMap = new HashMap<String, HashMap<String, String>>();

	final static public LinkedHashMap<String, Class> beanClassMap = new LinkedHashMap<String, Class>();

	final static public LinkedHashMap<String, String> beanNameMap = new LinkedHashMap<String, String>();

	static {
		fieldMap.put("Action", new HashMap<String, String>());
		fieldMap.get("Action").put("EndStatId", "Stat");
		fieldMap.get("Action").put("StartStatId", "Stat");

		fieldMap.put("Node", new HashMap<String, String>());
		fieldMap.get("Node").put("FatherId", "Node");

		fieldMap.put("Stat", new HashMap<String, String>());
		fieldMap.get("Stat").put("FlowId", "Flow");

		fieldMap.put("Role", new HashMap<String, String>());
		fieldMap.get("Role").put("FlowId", "Flow");
	}

	static {
		beanClassMap.put("Action", Action.class);
		beanClassMap.put("Flow", Flow.class);
		beanClassMap.put("Role", Role.class);
		beanClassMap.put("Stat", Stat.class);
	}

	static {
		beanNameMap.put("Flow", "流程管理");
		beanNameMap.put("Stat", "状态管理");
	}

	/**
	 * 返回bean的类实例
	 * 
	 * @param i
	 * 
	 * @return
	 */
	static final public Class getBeanClass(String className) {
		return beanClassMap.get(className);
	}

	static final public String getBeanName(String beanType) {
		return beanNameMap.get(beanType);
	}

	static final public Set<String> getBeanNames() {
		return beanNameMap.keySet();
	}

	static final public String getQueryMethodName(String name) {
		return "query" + name;
	}

	static final public String getInsertMethodName(String name) {
		return "insert" + name;
	}

	static final public String getRemoveMethodName(String name) {
		return "remove" + name;
	}

	static final public String getModifyMethodName(String name) {
		return "modify" + name;
	}

	public static List<UUID> getAllSysFilters() {
		return allSysFilterList;
	}

	public static String getLoginUrl(){
		if (ConfigManager.getEnableSso()) {
			//SSO单点登录
			return ConfigManager.getSsoProperties().getProperty("sso.login.url");
		}else {
			//系统本机登录
			return ConfigUtil.getCynthiaWebRoot() + "userInfo/login.jsp";
		}
	}
	
	public static String getLogOutUrl(){
		if (ConfigManager.getEnableSso()) {
			//SSO单点登录
			return ConfigManager.getSsoProperties().getProperty("sso.logout.url");
		}else {
			//系统本机登录
			return ConfigUtil.getCynthiaWebRoot() + "userInfo/login.jsp";
		}
	}
	/**
	 * 传进一个时间区域参数，传出时间区域的边界值，传出参数的0下标位置为起始时间，下标１为终止时间
	 * 
	 * @param fieldType
	 * @return
	 */
	static public Timestamp[] getTimeRange(String fieldType) {
		if (fieldType == null)
			return null;

		Timestamp[] range = new Timestamp[2];
		range[0] = null;
		range[1] = null;

		Calendar calendar = Calendar.getInstance();
		Calendar cc = (Calendar) calendar.clone();
		cc.set(Calendar.HOUR_OF_DAY, 0);
		cc.set(Calendar.MINUTE, 0);
		cc.set(Calendar.SECOND, 0);
		cc.set(Calendar.MILLISECOND, 0);

		if (fieldType.equals("本周")) {
			cc.add(Calendar.DATE, 2 - cc.get(Calendar.DAY_OF_WEEK));
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("本月")) {
			cc.add(Calendar.DAY_OF_MONTH, 1 - cc.get(Calendar.DAY_OF_MONTH));
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("本季")) {
			cc.add(Calendar.DAY_OF_MONTH, 1 - cc.get(Calendar.DAY_OF_MONTH));
			cc.add(Calendar.MONTH, 0 - cc.get(Calendar.MONTH) % 3);
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("本年")) {
			cc.set(Calendar.DAY_OF_MONTH, 1);
			cc.set(Calendar.MONTH, 0);
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("上周")) {
			cc.add(Calendar.DATE, 2 - cc.get(Calendar.DAY_OF_WEEK));
			range[1] = new Timestamp(cc.getTimeInMillis());
			cc.add(Calendar.DATE, -7);
			range[0] = new Timestamp(cc.getTimeInMillis());
		} else if (fieldType.equals("上月")) {
			cc.add(Calendar.DAY_OF_MONTH, 1 - cc.get(Calendar.DAY_OF_MONTH));
			range[1] = new Timestamp(cc.getTimeInMillis());
			cc.add(Calendar.MONTH, -1);
			range[0] = new Timestamp(cc.getTimeInMillis());
		} else if (fieldType.equals("上季")) {
			cc.add(Calendar.DAY_OF_MONTH, 1 - cc.get(Calendar.DAY_OF_MONTH));
			cc.add(Calendar.MONTH, 0 - cc.get(Calendar.MONTH) % 3);
			range[1] = new Timestamp(cc.getTimeInMillis());
			cc.add(Calendar.MONTH, -3);
			range[0] = new Timestamp(cc.getTimeInMillis());
		} else if (fieldType.equals("上年")) {
			cc.set(Calendar.DAY_OF_MONTH, 1);
			cc.set(Calendar.MONTH, 0);
			range[1] = new Timestamp(cc.getTimeInMillis());
			cc.add(Calendar.YEAR, -1);
			range[0] = new Timestamp(cc.getTimeInMillis());
		} else if (fieldType.equals("过去1周")) {
			cc.add(Calendar.DATE, -7);
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("过去1个月")) {
			cc.add(Calendar.MONTH, -1);
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("过去3个月")) {
			cc.add(Calendar.MONTH, -3);
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		} else if (fieldType.equals("过去1年")) {
			cc.add(Calendar.YEAR, -1);
			range[0] = new Timestamp(cc.getTimeInMillis());
			range[1] = null;
		}

		return range;
	}
}