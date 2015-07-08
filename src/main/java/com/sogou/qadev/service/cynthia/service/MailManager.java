package com.sogou.qadev.service.cynthia.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import bsh.This;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.ChangeLog;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.impl.RoleImpl;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.URLUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description:mail sender processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:11:24
 * @version:v1.0
 */
public class MailManager {
	
	private static String downLoadUrl = ConfigUtil.getCynthiaWebRoot()+ "attachment/download.jsp?method=download&id=";
	private static String referUrl = ConfigUtil.getCynthiaWebRoot()+"taskManagement.html?operation=read&taskid=";
	
	/**
	 * @description:send mail
	 * @date:2014-5-6 下午12:11:34
	 * @version:v1.0
	 * @param subject:mail subject
	 * @param recievers:mail recievers
	 * @param content:mail content
	 * @return:if mail send success
	 */
	public static boolean sendMail(String fromUser, String subject,String[] recievers,String content){
        try{
            Properties props = ConfigManager.getEmailProperties();
            
            //配置中定义不发送邮件
            if (props.getProperty("mail.enable") == null || !props.getProperty("mail.enable").equals("true")) {
				System.out.println("there is a mail not send by config!");
            	return true;
			}
            
            //创建一个程序与邮件服务器的通信
            Session mailConnection = Session.getInstance(props,null);
            Message msg = new MimeMessage(mailConnection);
                                
            //设置发送人和接受人
            Address sender = new InternetAddress(props.getProperty("mail.user"));
            //单个接收人
            //Address receiver = new InternetAddress("xxx@163.com");
            //多个接收人
            StringBuffer buffer = new StringBuffer();
            for (String reciever : recievers) {
				buffer.append(buffer.length() > 0 ? ",":"").append(reciever);
			}
            String all = buffer.toString();
            System.out.println("send Mail,mailList:" + all);
            
            msg.setFrom(sender);
            Set<InternetAddress> toUserSet = new HashSet<InternetAddress>();
            //邮箱有效性较验
            for (int i = 0; i < recievers.length; i++) {
                if(recievers[i].trim().matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)+$")){
                	toUserSet.add(new InternetAddress(recievers[i].trim()));
                }
            }
            
            msg.setRecipients(Message.RecipientType.TO, toUserSet.toArray(new InternetAddress[0]));
            
            //设置邮件主题
            msg.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));   //中文乱码问题
            
            //设置邮件内容
            BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setContent( content, "text/html; charset=utf-8" ); // 中文
            Multipart multipart = new MimeMultipart(); 
            multipart.addBodyPart( messageBodyPart ); 
            msg.setContent(multipart);
                                
            /**********************发送附件************************/
//	            //新建一个MimeMultipart对象用来存放多个BodyPart对象
//	            Multipart mtp=new MimeMultipart();
//	            //------设置信件文本内容------
//	            //新建一个存放信件内容的BodyPart对象
//	            BodyPart mdp=new MimeBodyPart();
//	            //给BodyPart对象设置内容和格式/编码方式
//	            mdp.setContent("hello","text/html;charset=gb2312");
//	            //将含有信件内容的BodyPart加入到MimeMultipart对象中
//	            mtp.addBodyPart(mdp);
//	                                
//	            //设置信件的附件(用本地机上的文件作为附件)
//	            mdp=new MimeBodyPart();
//	            FileDataSource fds=new FileDataSource("f:/webservice.doc");
//	            DataHandler dh=new DataHandler(fds);
//	            mdp.setFileName("webservice.doc");//可以和原文件名不一致
//	            mdp.setDataHandler(dh);
//	            mtp.addBodyPart(mdp);
//	            //把mtp作为消息对象的内容
//	            msg.setContent(mtp);
           /**********************发送附件结束************************/  

            //先进行存储邮件
            msg.saveChanges();
            Transport trans = mailConnection.getTransport(props.getProperty("mail.protocal"));
            //邮件服务器名,用户名，密码
            trans.connect(props.getProperty("mail.smtp.host"), props.getProperty("mail.user"),  props.getProperty("mail.pass"));
            trans.sendMessage(msg, msg.getAllRecipients());
            
            //关闭通道
            if (trans.isConnected()) {
            	trans.close();
			}
            return true;
        }catch(Exception e)
        {
            System.err.println(e);
            return false;
        }
        finally{
        }
	}
	
	
	//发信人，收信人，回执人邮件中有中文处理乱码,res为获取的地址
    //http默认的编码方式为ISO8859_1
    //对含有中文的发送地址，使用MimeUtility.decodeTex方法
    //对其他则把地址从ISO8859_1编码转换成gbk编码
    public static String getChineseFrom(String res) {
        String from = res;
        try {
            if (from.startsWith("=?GB") || from.startsWith("=?gb")
                    || from.startsWith("=?UTF")) {
                from = MimeUtility.decodeText(from);
            } else {
                from = new String(from.getBytes("ISO8859_1"), "GBK");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return from;
    }
       
    //转换为GBK编码
    public static String toChinese(String strvalue) {
        try {
            if (strvalue == null)
                return null;
            else {
                strvalue = new String(strvalue.getBytes("ISO8859_1"), "GBK");
                return strvalue;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * @Title: sendActionMail
     * @Description: send action mail
     * @param data
     * @return: void
     */
    public static void sendActionMail(Data data){
    	DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
    	Template template = das.queryTemplate(data.getTemplateId());
		if (template == null) {
			return;
		}
		if (template.getTemplateMailOption().isSendMail() == false) {
			return;
		}
		
		Flow flow = das.queryFlow(template.getFlowId());
		if (flow == null) {
			return;
		}
		
		Action action = flow.getAction(data.getActionId());
		if (action == null) {
			return;
		}
		
		Stat stat = flow.getStat(data.getStatusId());
		if(stat == null)
			return;

		//判断指派人是否变化	
		boolean isAssignUserChange = false;
		ChangeLog[] allChangeLogs = data.getChangeLogs();
		if (allChangeLogs == null || allChangeLogs.length ==0 ) {
			return;
		}

		boolean isNewTask = true; //是否是新建任务
		if (flow.getAction(data.getActionId()) == null || flow.getAction(data.getActionId()).getBeginStatId() != null) {  //编辑时动作为null
			isNewTask = false;
		}

		ChangeLog lastChangeLog = allChangeLogs[allChangeLogs.length-1];
		java.util.Map map = lastChangeLog.getBaseValueMap();
		if (map.get("assignUser") != null) {
			isAssignUserChange = true;
		}
		                              
		if (!isAssignUserChange) {
			return;
		}

		MailSender sender = new MailSender();
		sender.setHtml( true );
		sender.setSmtp( "transport.mail.sogou-inc.com" );
		sender.setEncode( "GBK" );
		sender.setFromUser(data.getString("logCreateUser"));

		Set<String> allSendUsers = template.getTemplateMailOption().getActionUser(action.getId(),data);
		if (data.getAssignUsername() != null) {
			allSendUsers.addAll(Arrays.asList(data.getAssignUsername().split(",")));
		}
		
		sender.setToUsers(allSendUsers.toArray(new String[0]));
		sender.setSubject(template.getTemplateMailOption().getMailSubject());

		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"/>");
		html.append("<style type=\"text/css\">");
		html.append("table{border:1px #E1E1E1 solid;}");
		html.append("td{border:1px #E1E1E1 solid;padding:10px;}");
		html.append(".tdcolor{background-color:#fedcbd}");
		html.append(".tdcolor2{background-color:#84bf96}");
		html.append("h3{color:red;margin-top:40px}");
		html.append("h4{color:blue;}");
		html.append("h5{margin-left:80px}");
		html.append("th{border:1px #E1E1E1 solid;padding:10px;}");
		html.append("tr {font-size: 15px; COLOR:#000000; background-color:#FFFFFF; font-family: Tahoma; text-align:left;}");
		html.append("</style>");
		html.append("</head>");
		html.append("<body>");
		html.append("<h4>基本信息</h4>");

		String logActionUserName = data.getActionUser();
		UserInfo userInfo = das.queryUserInfoByUserName(logActionUserName);
		if (userInfo != null ) {
			logActionUserName = userInfo.getNickName();
		}
		html.append("<table>");
		html.append("<tr><td class=\"tdcolor\">").append("标题").append(data.getId().toString()).append("</td><td colspan=\"5\">").append("<a href=\"" + ConfigUtil.getCynthiaWebRoot() + "taskManagement.html?operation=read&taskid=" + data.getId().toString() + "\">" + XMLUtil.toSafeXMLString(data.getTitle()) +"</a>").append("</td></tr>");
		html.append("<tr><td class=\"tdcolor\">").append("执行动作").append("</td><td>").append((action == null ? "编辑" : XMLUtil.toSafeXMLString(action.getName()))).append("</td>");
		html.append("<td class=\"tdcolor\">").append("执行人").append("</td><td>").append(XMLUtil.toSafeXMLString(logActionUserName)).append("</td>");
		html.append("<td class=\"tdcolor\">").append("状态").append("</td><td>").append(XMLUtil.toSafeXMLString(stat.getName())).append("</td></tr>");
		html.append("</table>");

		html.append("<h4>字段变更信息</h4>");

		html.append("<table>");

		if (isNewTask) {
			html.append("<tr>").append("<th class=\"tdcolor2\">").append("字段").append("</th>")
		.append("<th class=\"tdcolor2\">").append("新建内容").append("</th></tr>");
		}else {
			html.append("<tr>").append("<th class=\"tdcolor2\">").append("字段").append("</th>")
		.append("<th class=\"tdcolor2\">").append("修改之后").append("</th>")
		.append("<th class=\"tdcolor2\">").append("修改之前").append("</th></tr>");
		}

		Map baseValueMap = lastChangeLog.getBaseValueMap();
		Iterator baseIterator = baseValueMap.entrySet().iterator();
		while(baseIterator.hasNext()){
			Map.Entry entry = (Map.Entry)baseIterator.next();
				
			String fieldName = "";
			StringBuffer before = new StringBuffer();
			StringBuffer after = new StringBuffer();
	
			String key = entry.getKey().toString();
			Pair pair = (Pair)entry.getValue();
			if (pair == null) 
				continue;
	
			if (key.equals("title")) {
				fieldName = "标题";
				before.append(pair.getFirst()==null?"":pair.getFirst());
				after.append(pair.getSecond()==null?"":pair.getSecond());
			}else if (key.equals("description")) {
				
				fieldName = "描述";
				
				if (pair.getFirst() == null) {
					before.append("");
				}else {
					String content = pair.getFirst().toString();
					before.append(content);
				}
				
				if (pair.getSecond() == null) {
					after.append("");
				}else {
					String content = pair.getSecond().toString();
					after.append(content);
				}
	
			}else if (key.equals("assignUser")) {
				fieldName = "指派人";
				if (pair.getFirst()=="" || pair.getFirst() == null) {
					before.append("");
				}else {				
					userInfo = das.queryUserInfoByUserName(pair.getFirst().toString());
					before.append(userInfo == null ? pair.getFirst().toString() : userInfo.getNickName());	
				}
				
				if (pair.getSecond()=="" || pair.getSecond() == null) {
					after.append("");
				}else {
					userInfo = das.queryUserInfoByUserName(pair.getSecond().toString());
					after.append(userInfo == null ? pair.getSecond().toString() : userInfo.getNickName());	
				}
	
			}else if (key.equals("statusId")) {
				fieldName = "状态";
				if (pair.getFirst() == "" || pair.getFirst() == null) {
					before.append("");	
				}else {
					before.append(flow.getStat(DataAccessFactory.getInstance().createUUID(pair.getFirst().toString())).getName());	
				}
				if (pair.getSecond() == "" || pair.getSecond() == null) {
					after.append("");	
				}else {
					after.append(flow.getStat(DataAccessFactory.getInstance().createUUID(pair.getSecond().toString())).getName());	
				}
			}
			
			String beforeStr = before.toString().replace("attachment/download.jsp", ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp").replace("attachment/download_json.jsp", ConfigUtil.getCynthiaWebRoot() + "attachment/download_json.jsp");
			String afterStr = after.toString().replace("attachment/download.jsp", ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp").replace("attachment/download_json.jsp", ConfigUtil.getCynthiaWebRoot() + "attachment/download_json.jsp");
			if (isNewTask) 
				html.append("<tr><td class=\"tdcolor\">").append(fieldName).append("</td><td>").append(afterStr).append("</td></tr>");
			else 
				html.append("<tr><td class=\"tdcolor\">").append(fieldName).append("</td><td>").append(afterStr).append("</td><td>").append(beforeStr).append("</td></tr>");
		}
	
	
		Map extValueMap = lastChangeLog.getExtValueMap();
		Iterator extIterator = extValueMap.entrySet().iterator();
		while(extIterator.hasNext()){
			Map.Entry entry = (Map.Entry)extIterator.next();
			UUID keyUUID = (UUID)entry.getKey();
			Pair pair = (Pair)entry.getValue();
			if (pair == null || keyUUID == null) 
				continue;
			
			Field tmpField = das.queryField(keyUUID);
			String fieldName = "";
			StringBuffer before = new StringBuffer();
			StringBuffer after = new StringBuffer();
	
			if (tmpField == null) {
				continue;
			}
			fieldName = tmpField.getName();
	
			if (pair.getFirst() != null) {
	
					if (tmpField.getType() == Type.t_selection) {
						String[] optionIdStrArray = pair.getFirst().toString().split("\\,");
						for(String optionIdStr : optionIdStrArray){
							UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);
							Option option = tmpField.getOption(optionId);
							if(option == null){
								continue;
							}
							if(before.length() > 0){
								before.append(",");
							}
							before.append(option.getName());
						}
					}else if (tmpField.getType() == Type.t_reference) {
						String[] referIdArray = pair.getFirst().toString().split("\\,");
						for(String referId : referIdArray){
							Data refer = das.queryData(DataAccessFactory.getInstance().createUUID(referId));
							if (refer != null) {
								before.append(before.length() > 0 ? "," :"").append("<a href=\"").append(referUrl+refer.getId().toString()).append("\">").append(refer.getTitle()).append("</a>");
							}
						}
						
					}else if (tmpField.getType() == Type.t_attachment) {
	
						UUID[] attachIdArray = (UUID[])pair.getFirst();
						Attachment[] attachArray = das.queryAttachments(attachIdArray, false);
						for(Attachment attach : attachArray){
							if(before.length() > 0){
								before.append("<br />");
							}
							before.append("<a href=\"").append(downLoadUrl+attach.getId().toString()).append("\">").append(attach.getName()).append("</a>");
						}
					}else {
						String content = pair.getFirst().toString();
						before.append(content);
					}
			} // end for if (pair.getFirst() != null) {
	
			if (pair.getSecond() != null) {
					if (tmpField.getType() == Type.t_selection) {
						String[] optionIdStrArray = pair.getSecond().toString().split("\\,");
						for(String optionIdStr : optionIdStrArray){
							UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);
							Option option = tmpField.getOption(optionId);
							if(option == null){
								continue;
							}
							if(after.length() > 0){
								after.append(",");
							}
							after.append(option.getName());
						}
					}else if (tmpField.getType() == Type.t_reference) {
						
						String[] referIdArray = pair.getSecond().toString().split("\\,");
						for(String referId : referIdArray){
							Data refer = das.queryData(DataAccessFactory.getInstance().createUUID(referId));
							if (refer != null) {
								after.append(after.length() > 0 ? "," :"").append("<a href=\"").append(referUrl+refer.getId().toString()).append("\">").append(refer.getTitle()).append("</a>");
							}
						}
						
					}else if (tmpField.getType() == Type.t_attachment) {
						UUID[] attachIdArray = (UUID[])pair.getSecond();
						
						Attachment[] attachArray = das.queryAttachments(attachIdArray, false);
						
						for(Attachment attach : attachArray){
							if(after.length() > 0){
								after.append("<br />");
							}
							after.append("<a href=\"").append(downLoadUrl+attach.getId().toString()).append("\">").append(attach.getName()).append("</a>");
						}
					}else {
						String content = pair.getSecond().toString();
						after.append(content);
					}
			} // end for if (pair.getSecond() != null) {
	
			if (isNewTask) 
				html.append("<tr><td class=\"tdcolor\">").append(fieldName).append("</td><td>").append(after.toString()).append("</td></tr>");
			else 
				html.append("<tr><td class=\"tdcolor\">").append(fieldName).append("</td><td>").append(after.toString()).append("</td><td>").append(before.toString()).append("</td></tr>");
		}

		html.append("</table>");
		html.append("</body>");
		html.append("</html>");

		String sendHtml =  html.toString();
		sendHtml = sendHtml.replace("</tr>", "</tr>\n");
		sender.setContent(sendHtml);
		sender.sendHtmlEx("GBK");
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println(sendMail("liming@sogou-inc.com","测试邮件", new String[]{"liming@sogou-inc.com"}, "这是一封测试邮件"));

	}
}
