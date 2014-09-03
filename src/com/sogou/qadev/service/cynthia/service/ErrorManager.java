package com.sogou.qadev.service.cynthia.service;

public class ErrorManager {

	//错误类型
	public enum ErrorType
	{
		//数据库
		database_update_error,   //数据库更新异常

		//表单
		template_not_found, //表单无法到找
		field_update_error, //字段修改错误
		copy_template_not_fount, //复制的表单无法找到
		fieldcolName_error,       //无法在数据库中添加列名
		field_option_not_fount,  //无法找到字段选项名
		template_xml_error,   //表单解析xml发生错误
		//流程
		flow_not_found,  //流程无法找到
		stat_not_found,  //状态无法找到
		action_not_found, //动作无法找到
		role_not_found, //角色无法找到
		//数据
		data_not_found_inDb, //数据在数据库中无法找到
		time_parse_error,   //时间类型解析异常
		not_read_right,     //没有查看数据权限
		not_delete_right,   //没有删除数据权限
		not_edit_right,     //没有编辑数据权限
		
		//用户
		user_not_fount, //用户无法找到
		user_not_login, //用户没有登陆
		//参数错误
		param_error,
	}
	
	/**
	 * 返回错误xml
	 * @param errorInfo:错误信息
	 * @return
	 */
	public static String getErrorXml(ErrorType errorType)
	{
		String errorInfo = "";
		if (errorType.equals(ErrorType.flow_not_found)) {
			errorInfo = "流程无法找到!";
		}else if (errorType.equals(ErrorType.stat_not_found)) {
			errorInfo = "状态在流程中无法找到!";
		}else if (errorType.equals(ErrorType.action_not_found)) {
			errorInfo = "动作在流程中无法找到!";
		}else if (errorType.equals(ErrorType.database_update_error)) {
			errorInfo = "数据库更新异常!";
		}else if (errorType.equals(ErrorType.template_not_found)) {
			errorInfo = "表单无法找到!";
		}else if (errorType.equals(ErrorType.data_not_found_inDb)) {
			errorInfo = "数据无法找到或数据己被删除!";
		}else if (errorType.equals(ErrorType.time_parse_error)) {
			errorInfo = "时间类型解析异常!";
		}else if (errorType.equals(ErrorType.role_not_found)) {
			errorInfo = "角色无法找到!";
		}else if (errorType.equals(ErrorType.param_error)) {
			errorInfo = "没有传递正确参数!";
		}else if (errorType.equals(ErrorType.field_update_error)) {
			errorInfo = "字段修改发生错误!";
		}else if (errorType.equals(ErrorType.copy_template_not_fount)) {
			errorInfo = "复制的原表单无法找到!";
		}else if (errorType.equals(ErrorType.fieldcolName_error)) {
			errorInfo = "无法在数据库中添加列名!";
		}else if (errorType.equals(ErrorType.field_option_not_fount)) {
			errorInfo = "无法找到字段选项名!";
		}else if (errorType.equals(ErrorType.user_not_fount)) {
			errorInfo = "用户无法找到!";
		}else if (errorType.equals(ErrorType.template_xml_error)) {
			errorInfo = "表单解析xml发生错误!";
		}else if (errorType.equals(ErrorType.user_not_login)) {
			errorInfo = "用户没有登陆!";
		}else if (errorType.equals(ErrorType.not_read_right)) {
			errorInfo = "没有查看数据权限!";
		}else if (errorType.equals(ErrorType.not_delete_right)) {
			errorInfo = "没有删除数据权限!";
		}else if (errorType.equals(ErrorType.not_edit_right)) {
			errorInfo = "没有编辑数据权限!";
		}
		
		//TODO 是否记录日志
		return getErrorXml(errorInfo);
	}
	
	public static String getErrorXml(String errorInfo)
	{
		return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>true</isError><errorInfo>%s</errorInfo></root>", errorInfo);
	}
	
	
	/**
	 * 返回正确xml
	 * @return
	 */
	public static String getCorrectXml()
	{
		String correctXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>false</isError></root>";
		return correctXml;
	}

}
