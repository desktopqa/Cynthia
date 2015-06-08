package com.sogou.qadev.service.cynthia.controller;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sun.tools.tree.NewArrayExpression;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.DataAccessAction;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.MailSender;
import com.sogou.qadev.service.cynthia.service.StreamCloserManager;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.Date;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description:excel import processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:21:01
 * @version:v1.0
 */
@Controller
public class ExcelImportControllerNew extends BaseController {

	/**
	 * @description:excel import error into
	 * @author:liming
	 * @mail:liming@sogou-inc.com
	 * @date:2014-5-5 下午8:20:47
	 * @version:v1.0
	 */
	class ErrorInfo{
		/**
		 * error description
		 */
		String errorDescription; 
		
		/**
		 * error row number
		 */
		int errorRowNum;  
		
		/**
		 * error column name
		 */
		String errorColumnName;
		
		public String getErrorColumnName() {
			return errorColumnName;
		}
		public void setErrorColumnName(String errorColumnName) {
			this.errorColumnName = errorColumnName;
		}
		public String getErrorDescription() {
			return errorDescription;
		}
		public void setErrorDescription(String errorDescription) {
			this.errorDescription = errorDescription;
		}
		public int getErrorRowNum() {
			return errorRowNum;
		}
		public void setErrorRowNum(int errorRowNum) {
			this.errorRowNum = errorRowNum;
		}
	}

	class MailVO{
		/**
		 * success count
		 */
		int sucCount;
		int failCount;
		String fileName;
		String userName;
		int vUserCount;
		boolean success;
		/**
		 * all error info list
		 */
		List<ErrorInfo> errorList;


		public List<ErrorInfo> getErrorList() {
			return errorList;
		}
		public void setErrorList(List<ErrorInfo> errorList) {
			this.errorList = errorList;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public int getSucCount() {
			return sucCount;
		}
		public void setSucCount(int sucCount) {
			this.sucCount = sucCount;
		}
		public int getFailCount() {
			return failCount;
		}
		public void setFailCount(int failCount) {
			this.failCount = failCount;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public int getvUserCount() {
			return vUserCount;
		}
		public void setvUserCount(int vUserCount) {
			this.vUserCount = vUserCount;
		}
	}

	/**
	 * @description:return html header
	 * @date:2014-5-5 下午8:22:26
	 * @version:v1.0
	 * @return
	 */
	private String getHtmlHead()
	{
		StringBuffer result = new StringBuffer();
		result.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\"><title>Cynthia</title></head><body>");
		result.append("<style type=\"text/css\">");
		result.append("table{border:1px #E1E1E1 solid;}");
		result.append("td{border:1px #E1E1E1 solid;}");
		result.append("tr {font-size: 12px; COLOR:#000000; background-color:#FFFFFF; font-family: Tahoma; text-align:center;}");
		result.append("</style>");
		return result.toString();
	}

	private String getHtmlFoot()
	{
		return "</body></html>";
	}
	
	/**
	 * 发送邮件
	 * @param mailVo：邮件对象
	 */
	private void mail(MailVO mailVo,String recieveUser)
	{
		StringBuffer result = new StringBuffer();
		String receivers = recieveUser;
		result.append(this.getHtmlHead());
		result.append("<table>");
		if(mailVo.isSuccess())
		{
			if(mailVo.getFailCount()==0)
				result.append("<tr><td>导入结果</td><td>成功</td></tr>");
			else
				result.append("<tr><td>导入结果</td><td>部分成功</td></tr>");

			result.append("<tr><td>导入成功条数</td><td>").append(mailVo.getSucCount()).append("</td></tr>");
			result.append("<tr><td>导入失败条数</td><td>").append(mailVo.getFailCount()).append("</td></tr>");
			result.append("<tr><td>V用户</td><td>").append(mailVo.getvUserCount()).append("</td></tr>");
		}else
		{
			result.append("<tr><td>导入结果</td><td>失败</td></tr>");
			result.append("<tr><td>导入成功条数</td><td>").append(mailVo.getSucCount()).append("</td></tr>");
			result.append("<tr><td>V用户</td><td>").append(mailVo.getvUserCount()).append("</td></tr>");
		}
		result.append("<tr><td>执行人</td><td>").append(mailVo.getUserName()).append("</td></tr>");
		result.append("<tr><td>文件名</td><td>").append(XMLUtil.toSafeXMLString(mailVo.getFileName())).append("</td></tr>");
		result.append("</table>");

		result.append(GetErrorInfoHtml(mailVo.getErrorList()));
		result.append(this.getHtmlFoot());

		MailSender mailSender = new MailSender();
		mailSender.sendMail("[Cynthia][数据表单导入]导入Excel数据信息", result.toString(), receivers);
	}

	/**
	 * @description:get error info html
	 * @date:2014-5-5 下午8:23:03
	 * @version:v1.0
	 * @param allErrorLists
	 * @return
	 */
	public String GetErrorInfoHtml(List<ErrorInfo> allErrorLists){
		if (allErrorLists == null || allErrorLists.size()==0) {
			return "";
		}
		StringBuffer html = new StringBuffer();
		html.append("<br>");
		html.append("<h3>详细错误信息</h3>");
		html.append("<table>");
		html.append("<tr><th>").append("错误描述").append("</th><th>").append("错误行数").append("</th><th>").append("错误列标头").append("</th></tr>");
		for (ErrorInfo errorInfo : allErrorLists) {
			html.append("<tr><td>").append(errorInfo.getErrorDescription()).append("</td><td>").append(errorInfo.getErrorRowNum()).append("</td><td>").append(errorInfo.getErrorColumnName()).append("</td></tr>");
		}
		html.append("</table>");
		return html.toString();
	}


	/**
	 * @description:judge if the field is required 
	 * @date:2014-5-5 下午8:23:17
	 * @version:v1.0
	 * @param field
	 * @param template
	 * @param flow
	 * @param action
	 * @param user
	 * @return
	 */
	private Boolean IsFieldNeed(Field field ,Template template, Flow flow, Action action , String user){
		if (field == null) {
			return false;
		}
		//查询用户拥有角色
		Role[] userRoles = flow.queryUserNodeRoles(user, template.getId());

		if(field.getControlFieldId()!=null&&field.getControlFieldId().toString().length()!=0){
			//是否为空由上一级决定
			return IsFieldNeed(template.getField(field.getControlFieldId()), template,flow,action,user);
		}else {
			Set<String> allControlActions = field.getControlActionIds();
			//自身决定,通过角色 动作 判定是否必填
			if (allControlActions!=null&&allControlActions.size()>0) {
				//拥有动作角色
				for (Role role : userRoles) {
					String temp = action.getId().getValue() + "_" + role.getId().toString() + "_1";
					if (allControlActions.contains(temp)) {   //后缀带1
						return true;
					}
				}
				return false;  //都不含1则非必填
			}else {
				return false;
			}
		}
	}

	
	public String checkDataControlValid(Data data, Template template){
		String errorString = "";
		try {
			for (Field field : template.getFields()) {
				if (field.getType().equals(Type.t_selection) && field.getControlFieldId() != null) {
					UUID controlFieldId = field.getControlFieldId();
					UUID controlOptionId = data.getSingleSelection(controlFieldId);
					UUID curOptionId = data.getSingleSelection(field.getId());
					Option curOption = field.getOption(curOptionId);
					if (curOption == null || !curOption.getControlOptionId().getValue().equals(controlOptionId.getValue())) {
						Field controlField = template.getField(controlFieldId);
						return controlField.getName() + "选项下没有" + field.getName() + "值！"; 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return errorString;
	}

	/**
	 * @description:get the required fields by template and action
	 * @date:2014-5-5 下午8:24:20
	 * @version:v1.0
	 * @param template
	 * @param actionId
	 * @param data
	 * @return
	 */
	private Set<Field> getMultField(Template template,UUID  actionId , Data data){
		if (data == null || actionId == null) {
			return null;
		}
		
		UUID roleId = DataAccessFactory.getInstance().createUUID("238784"); //筛选录入人员
		Set<Field> allMultFieldSet = new HashSet<Field>();
		allMultFieldSet.addAll(getMustFieldByAction(template, roleId, actionId));
		Iterator<Field> iterator = allMultFieldSet.iterator();
		while(iterator.hasNext()){
			if (data.getObject(iterator.next().getId()) !=  null) {
				iterator.remove();
			}
		}
		return allMultFieldSet;
	}
	
	/**
	 * @description:get the required fields of template and action role
	 * @date:2014-5-5 下午8:24:51
	 * @version:v1.0
	 * @param template
	 * @param roleId
	 * @param actionId
	 * @return
	 */
	private Set<Field> getMustFieldByAction(Template template , UUID roleId ,  UUID actionId){
		Set<Field> allMultFieldSet = new HashSet<Field>();
		Set<Field> allFields = template.getFields();
		//找到该动作下的必填字段
		for (Field field : allFields) {
			Set<String> allControlActionArray = field.getControlActionIds();
			for (String controlActStr : allControlActionArray) {
				if (controlActStr != null) {
					String[] tmp = controlActStr.split("_");
					if (tmp.length >= 3 && tmp[0].equals(actionId.getValue()) && tmp[1].equals(roleId.getValue()) && tmp[2].equals("1")) {
						allFields.add(field);
						break;
					}
				}
			}
		}
		return allMultFieldSet;
	}
	
	/**
	 * @description:return all required fiels 
	 * @date:2014-5-5 下午8:25:12
	 * @version:v1.0
	 * @param allFields
	 * @param template
	 * @param flow
	 * @param action
	 * @param user
	 * @return
	 */
	public Set<Field> GetAllNeedField(Set<Field> allFields , Template template , Flow flow, Action action, String user){
		Set<Field> allNeedFields = new HashSet<Field>();

		for (Field field : allFields) {
			if (field == null) {
				continue;
			}
			if (IsFieldNeed(field, template,flow,action, user)) {
				allNeedFields.add(field);
			}
		}
		return allNeedFields;
	}

	/**
	 * 返回所有字段 不包括废弃字段
	 * @param template
	 * @return
	 */
	public Set<Field> GetAllFields(Template template) {
		Set<Field> allFields = template.getFields();
		Iterator<Field> iter = allFields.iterator();
		while(iter.hasNext()){
			Field field = iter.next();
			if (field.getName().indexOf("废弃")!=-1) {
				iter.remove();
			}
		}
		return allFields;
	}

	/**
	 * 獲取缺少字段
	 * @return
	 */
	private String GetNeedFieldString(Map<String, Integer> excelValueNum , Template template){
		String needFieldName = "";
		if (!excelValueNum.keySet().contains("指派人")) {
			needFieldName += "指派人,";
		}
		Set<String> allFieldSet = new HashSet<String>();
		for (Field field : template.getFields()) {
			allFieldSet.add(field.getName());
		}
		for (String fieldName : excelValueNum.keySet()) {
			if (fieldName.equals("标题") || fieldName.equals("正文") || fieldName.equals("指派人") || fieldName.equals("状态")) {
				continue;
			}
			if (!allFieldSet.contains(fieldName)) {
				needFieldName += fieldName+",";
			}
		}
		
		if (needFieldName.length()>0) {
			needFieldName = needFieldName.substring(0, needFieldName.length()-1);
		}
		return needFieldName;
	}

	@ResponseBody
	@RequestMapping("/excelImportNew.do")
	public String excelImportNew(@RequestParam(value = "excelfile", required = false) MultipartFile multipartFile, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		response.setContentType("text/html;charset=UTF-8");
		String sheetName = request.getParameter("sheetName");
		String templateIdStr = request.getParameter("templateIdStr");
		
		sheetName = (sheetName == null || sheetName.equals("")) ? "sheet1" : sheetName;
		Map<String, String> userMap = new HashMap<String, String>();
		
		///////////////////////////////////////////
		int sucCount = 0;    //添加成功总数
		int failCount = 0;   //添加失败总数
		List<ErrorInfo> errorInfoList = new ArrayList<ErrorInfo>();  //所有错误信息
		boolean flag = false; //添加过程是否出错
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Integer> excelValueNum = new HashMap<String, Integer>();  //excel表单【字段名，列数】对应

		UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		Template template = das.queryTemplate(templateId);  //得到表单
		Flow flow = das.queryFlow(template.getFlowId());

		Set<Field> allFields = GetAllFields(template);//表单所有字段,除出废弃字段

		String addUser = (String)session.getAttribute("userName");
		DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(addUser, DataAccessFactory.magic);
		Set<Field> allNeedFields = new HashSet<Field>();
		//////////////////////////////////////////

		try
		{
			File tmpFile = File.createTempFile("acctachment", ".attachment");
			multipartFile.transferTo(tmpFile);
			String realFileName = multipartFile.getOriginalFilename();
			FileInputStream fis = new FileInputStream(tmpFile);
			Workbook workbook = WorkbookFactory.create(fis); 
			
			if (fis != null) {
				StreamCloserManager.closeInputStream(fis);
			}
			if (workbook == null) {
				return "";
			}

			try{
				Sheet sheet = workbook.getSheet(sheetName);
				int rows = sheet.getPhysicalNumberOfRows();
				Row row = null;
				Cell cell = null;
				if(rows==0)
					return "";
				row = sheet.getRow(0);//获得第一行数据分析每个字段对应的列号

				String value = null;
				int cells = row.getPhysicalNumberOfCells();
				for(int j=0;j<cells;j++){
					cell = row.getCell(j);
					value = cell.getStringCellValue().trim();
					if (value!=null && value.length()>0) {
						excelValueNum.put(value, j);
					}
				}

				if (rows==1) {
					ErrorInfo errorInfo = new ErrorInfo();
					errorInfo.setErrorDescription("数据为空");
					errorInfoList.add(errorInfo);
					flag = false;
				}
				
				Boolean isAllNeedFieldsIn = true;  //必须字段是否全部满足
				//判断必填字段是否都己存在
				String needFieldName = GetNeedFieldString(excelValueNum, template);

				if (needFieldName!=null && needFieldName.length()>0) {
					ErrorInfo errorInfo = new ErrorInfo();
					errorInfo.setErrorDescription("某些字段在表单中无法找到如：【"+needFieldName+"】,全部录入失败");
					errorInfoList.add(errorInfo);
					flag = false;
					isAllNeedFieldsIn = false;
				}
				
				if (isAllNeedFieldsIn) {
					for(int j=1;j<rows;j++)
					{
						row = sheet.getRow(j);
						if(row!=null)
						{
							boolean isSingleFail = false;  //单条信息录入错误

							Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();
							Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();
							Data data = das.addData(templateId);

							String title = "";       //标题
							ErrorInfo errorInfo = null;

							if(data == null){
								return "";
							}

							for (Field field : allFields) {
								if (excelValueNum.keySet().contains(field.getName())) {
									String fieldName = field.getName();
									//excel中存在该字段,从excel中获取内容
									String cellContentString = "";
									java.util.Date cellContentDate = null;
									if(row.getCell(excelValueNum.get(fieldName))!=null)
									{
										if(row.getCell(excelValueNum.get(fieldName)).getCellType()==XSSFCell.CELL_TYPE_NUMERIC)  //日期类型
										{
											cellContentDate = row.getCell(excelValueNum.get(fieldName)).getDateCellValue();
											cellContentString = CynthiaUtil.getValue(row, excelValueNum.get(fieldName));
										}else{
											//其它类型
											cellContentString = CynthiaUtil.getValue(row, excelValueNum.get(fieldName));
										}

									}else {
										//判断该字段是否为必填
										if (allNeedFields.contains(field.getName())) {
											//必填为空,错误
											isSingleFail = true;
											errorInfo = new ErrorInfo() ;
											errorInfo.setErrorDescription("必填字段为空");
											errorInfo.setErrorRowNum(j);
											errorInfo.setErrorColumnName(field.getName());
											break;
										}
									}

									if (field.getDataType()==DataType.dt_timestamp) { //处理日期类型
										Date timeDate = null;
										try {
											timeDate = Date.valueOf(sdf.format(cellContentDate));
											
										} catch (Exception e) {
										}
										if (timeDate == null) {
											try {
												timeDate = Date.valueOf(cellContentString);
											} catch (Exception e) {
											}
										}
										if (timeDate == null && allNeedFields.contains(fieldName)) {
											// 日期类型错误 返回错误
											isSingleFail = true;
											errorInfo = new ErrorInfo() ;
											errorInfo.setErrorDescription("日期类型错误");
											errorInfo.setErrorRowNum(j);
											errorInfo.setErrorColumnName(field.getName());
											break;
										}else {
											if (timeDate != null) {
												data.setDate(field.getId(), timeDate);
												extValueMap.put(field.getId(), new Pair<Object, Object>(null, timeDate));
											}
										}
									}else if (field.getType()==Type.t_selection) {  //处理单选类型
										Option option = field.getOption(cellContentString);
										if (option == null) {
											if (allNeedFields.contains(field)) {  //为空必填
												//错误，单选选项错误
												isSingleFail = true;
												errorInfo = new ErrorInfo() ;
												errorInfo.setErrorDescription("单选选项错误");
												errorInfo.setErrorRowNum(j);
												errorInfo.setErrorColumnName(field.getName());
												break;
											}
										}else {
											data.setSingleSelection(field.getId(), option.getId());
											extValueMap.put(field.getId(), new Pair<Object, Object>(null,option.getId()));
										}
									}else {  //普通字段类型
										data.setString(field.getId(), cellContentString);
										extValueMap.put(field.getId(), new Pair<Object, Object>(null, cellContentString));
									}
								}

							}//end foreach

							if (isSingleFail) {
								//记录错误 开始下一条数据录入
								failCount ++;
								errorInfoList.add(errorInfo);
								continue;
							}
							
							//问题概述
							if (excelValueNum.get("标题")!=null) {
								title = CynthiaUtil.getValue(row, excelValueNum.get("标题"));
							}
							
							//标题
							data.setTitle(title);
							baseValueMap.put("title", new Pair<Object, Object>(null,title));

							data.setObject("logCreateUser", addUser);  //添加人
							//正文
							if (excelValueNum.get("正文")!=null) {
								String content = CynthiaUtil.getValue(row, excelValueNum.get("正文"));
								data.setDescription(content);
								baseValueMap.put("description", new Pair<Object, Object>(null, content));
							}
							
							//状态
							UUID statId = null;
							if (excelValueNum.get("状态")!=null) {
								String statIdStr = CynthiaUtil.getValue(row, excelValueNum.get("状态"));
								if (statIdStr != null && statIdStr.length() >0) {
									Stat stat = flow.getStat(statIdStr);
									if (stat == null) {
										failCount ++;
										errorInfo = new ErrorInfo() ;
										errorInfo.setErrorDescription("状态不存在");
										errorInfo.setErrorRowNum(j);
										errorInfo.setErrorColumnName("状态");
										errorInfoList.add(errorInfo);
										continue;
									}else {
										statId = stat.getId();
									}
								}
							}
							
							boolean isEndStat = isEndStat(flow, statId);
							//指派人
							if (excelValueNum.get("指派人")!=null && !isEndStat) {
								String assignUser = CynthiaUtil.getValue(row, excelValueNum.get("指派人"));

								if (userMap.get(assignUser) == null) {
									
									UserInfo relatedUsers = das.queryUserInfoByUserName(assignUser);

									if (relatedUsers == null) {
										failCount ++;
										errorInfo = new ErrorInfo() ;
										errorInfo.setErrorDescription("指派人不存在");
										errorInfo.setErrorRowNum(j);
										errorInfo.setErrorColumnName("指派人");
										errorInfoList.add(errorInfo);
										continue;
									}else {
										userMap.put(assignUser, relatedUsers.getNickName());
									}
								}

								if (statId != null) {
									if(!isEndStat){
										data.setAssignUsername(assignUser);
										baseValueMap.put("assignUser", new Pair<Object, Object>(null, assignUser));
									}
								}
							}
							
							if (statId != null) {
								data.setObject("logActionId", new ArrayList<Action>(flow.queryActionsByEndStatId(statId)).get(0).getId());
							}else{
								data.setObject("logActionId", null);
							}
							
							data.setStatusId(statId);
							baseValueMap.put("statusId", new Pair<Object, Object>(null, statId));

							data.setObject("logBaseValueMap", baseValueMap);
							data.setObject("logExtValueMap", extValueMap);
							
							//验证控制字段是否正确
							String controlErrorString = checkDataControlValid(data, template);
							if(controlErrorString != null && !controlErrorString.equals("")){
								failCount ++;
								errorInfo = new ErrorInfo() ;
								errorInfo.setErrorDescription(controlErrorString);
								errorInfo.setErrorRowNum(j);
								errorInfoList.add(errorInfo);
								continue;
							}
							
							if (!isSingleFail) {
								Pair<ErrorCode, String> pair = das.modifyData(data);

								if (pair.getFirst().equals(ErrorCode.success)) {
									das.commitTranscation();
									das.updateCache(DataAccessAction.delete, data.getId().getValue(), data);
								}else {
									isSingleFail = true;
									errorInfo = new ErrorInfo();
									errorInfo.setErrorDescription("数据库操作失败!");
									errorInfo.setErrorRowNum(j);
									das.rollbackTranscation();
								}
							}
							
							if (isSingleFail) {
								//记录错误 开始下一条数据录入
								failCount ++;
								errorInfoList.add(errorInfo);
								continue;
							}else {
								sucCount ++;
							}
						}
					
					}
				}
			}  //end try
				catch (Exception e){
					flag = false;
					e.printStackTrace();
				}
			

			MailVO mailVo = new MailVO();
			mailVo.setFailCount(failCount);
			mailVo.setFileName(realFileName);
			mailVo.setSucCount(sucCount);
			mailVo.setUserName(addUser);
			mailVo.setErrorList(errorInfoList);

			if(sucCount>0)
			{
				mailVo.setSuccess(true);
			}else
			{
				mailVo.setSuccess(false);
			}
			this.mail(mailVo,addUser);
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		resultMap.put("successNum", sucCount);
		resultMap.put("failNum", failCount);
		return JSONArray.toJSONString(resultMap);
	}
	
	/**
	 * @Title: saveSingleData
	 * @Description: 保存单条数据 并返回是否错误信息
	 * @param template
	 * @param flow
	 * @param allNeedFields
	 * @param mapData
	 * @param addUser
	 * @return
	 * @return: Pair<String,String>
	 */
	public Pair<String, String> saveSingleData(Template template, Flow flow, Set<Field> allNeedFields , Map<String, String> mapData){
		Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();
		Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();
		DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(ConfigUtil.sysEmail, ConfigUtil.magic);
		Data data = das.addData(template.getId());
		
		if (data == null) {
			return new Pair<String, String>(mapData.get("title"),"数据库操作失误");
		}
		
		Set<Field> allFields = GetAllFields(template);//表单所有字段,除出废弃字段
		
		for (Field field : allFields) {
			if (mapData.containsKey(field.getName())) {
				String fieldName = field.getName();
				String fieldValue = mapData.get(fieldName);
				if (mapData.get(fieldName) == null) {
					//判断该字段是否为必填
					if (allNeedFields.contains(fieldName)) {
						return new Pair<String, String>(mapData.get("title"),"必填字段为空");
					}
				}

				if (field.getDataType() == DataType.dt_timestamp) { //处理日期类型
					Date timeDate =  Date.valueOf(fieldValue);
					if (timeDate == null && allNeedFields.contains(fieldName)) {
						return new Pair<String, String>(mapData.get("title"),"日期类型错误");
					}else {
						if (timeDate != null) {
							data.setDate(field.getId(), timeDate);
							extValueMap.put(field.getId(), new Pair<Object, Object>(null, timeDate));
						}
					}
				}else if (field.getType()==Type.t_selection) {  //处理单选类型
					Option option = field.getOption(fieldValue);
					data.setSingleSelection(field.getId(), option.getId());
					extValueMap.put(field.getId(), new Pair<Object, Object>(null,option.getId()));
				}else {  
					data.setString(field.getId(), fieldValue);
					extValueMap.put(field.getId(), new Pair<Object, Object>(null, fieldValue));
				}
			}
		}

		//设置标题
		String title = mapData.get("title");
		data.setTitle(title);
		baseValueMap.put("title", new Pair<Object, Object>(null,title));

		//添加人
		String createUser = mapData.get("createUser");
		if (CynthiaUtil.isNull(createUser)) {
			createUser = DataAccessFactory.sysUser;
		}
		
		baseValueMap.put("createUser", new Pair<Object, Object>(null,createUser));
		data.setCreateUser(createUser);
		data.setObject("logCreateUser", createUser);  
		
		//正文
		String description = mapData.get("description");
		if (description !=null ) {
			data.setDescription(description);
			baseValueMap.put("description", new Pair<Object, Object>(null, description));
		}
		
		//状态
		UUID actionId = null;
		UUID statId = DataAccessFactory.getInstance().createUUID(mapData.get("statusId"));  //状态
		if (statId == null) {
			Set<Action> allStartActions = flow.getStartActions();
			try {
				Action startAction = allStartActions.toArray(new Action[0])[0];
				actionId = startAction.getId();
				statId = startAction.getEndStatId();
			} catch (Exception e) {
				return new Pair<String, String>(mapData.get("title"),"flow is wrong!");
			}
		}
	    
		//指派人
	    String assignUser = mapData.get("assignUser");
		data.setAssignUsername(assignUser);
		baseValueMap.put("assignUser", new Pair<Object, Object>(null, assignUser));
		
		data.setObject("logActionId", actionId);
		data.setStatusId(statId);
		baseValueMap.put("statusId", new Pair<Object, Object>(null, statId));

		data.setObject("logBaseValueMap", baseValueMap);
		data.setObject("logExtValueMap", extValueMap);
		
		Pair<ErrorCode, String> pair = das.modifyData(data);
		if (pair.getFirst().equals(ErrorCode.success)) {
			das.commitTranscation();
			das.updateCache(DataAccessAction.delete, data.getId().getValue(), data);
			return null;
		}else {
			das.rollbackTranscation();
			return new Pair<String, String>(mapData.get("title"),"data base error!");
		}
	}
	

	private boolean isEndStat(Flow flow, UUID statId) {
		if (statId == null) {
			return false;
		}
		Stat[] endStatArray = flow.getEndStats();
		for (Stat stat : endStatArray) {
			if (stat != null && stat.getId().getValue().equals(statId.getValue())) {
				return true;
			}
		}
		return false;
	}
}
