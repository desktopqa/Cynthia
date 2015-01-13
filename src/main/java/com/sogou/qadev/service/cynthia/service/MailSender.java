package com.sogou.qadev.service.cynthia.service;

import java.util.ArrayList;
import java.util.List;

import com.sogou.qadev.service.cynthia.util.ArrayUtil;

public class MailSender {
	private String uid = "liuyanlei@sogou-inc.com";     //申请权限的user_id
	private String fr_name = "Cynthia"; 			    //发信人姓名
	private String fr_addr = "liuyanlei@sogou-inc.com"; //发信人email
	private String title = null;		 //邮件标题
	private String body = null;		//邮件内容
	private String mode = "html";	//邮件类型 html或者txt
	private String maillist = null;	//收件人邮箱 多个人用;隔开
	private String cclist = null;     //抄送人员（暂时不可用）
	private String attname = null;	//附件文件名
	private String attbody = null;	//附件正文
	//兼容之前的邮件发送
	private String smtp;
	private String fromUser;
	private String[] toUsers;
	private String[] ccUsers;
	private String[] bccUsers;
	private String subject;
	private String content;
	private boolean html;
	private String encode;
	
	public void sendMail(String uid,String fr_name,String fr_addr,String title,String body,String mode,String maillist,String cclist)
	{
		this.sendMail(uid, fr_name, fr_addr, title, body, mode, maillist, null, null,cclist);	
	}
	
	public void sendMail(String uid,String fr_name,String fr_addr,String title,String body,String maillist,String cclist)
	{
		this.sendMail(uid, fr_name, fr_addr, title, body, this.mode, maillist, null, null,cclist);	
	}
	
	public void sendMail(String title,String body,String maillist,String attname,String attbody,String cclist)
	{
		this.sendMail(this.uid, this.fr_name, this.fr_addr, title,  body, this.mode, maillist, attname, attbody,cclist);
	}
	
	public void sendMail(String uid,String fr_name,String fr_addr,String title,String body,String maillist,String attname,String attbody,String cclist)
	{
		this.sendMail(uid, fr_name, fr_addr, title, body, this.mode, maillist, attname, attbody,cclist);
	}
	
	public void sendMail(String title,String body,String maillist,String cclist)
	{
		this.sendMail(title, body, maillist, null, null,cclist);
	}
	
	public void sendMail(String title,String body,String maillist)
	{
		this.sendMail(title, body, maillist,null);
	}
	public void sendMail(String uid,String fr_name,String fr_addr,String title,String body,String mode,String maillist,String attname,String attbody,String cclist)
	{
		if(maillist!=null && !"".equals(maillist) && cclist!=null && !"".equals(cclist)){
			maillist = maillist+";"+cclist;
		}
		
		if (maillist == null || maillist.equals("")) {
			return;
		}
		
		body = body.replace("</td>", "</td>\n").replace("</tr>", "</tr>\n");
		
		String[] toUsers = maillist.split(";");
		if (ConfigManager.getProjectInvolved()) {
			//走项目管理邮件发送流程
			ProjectInvolveManager.getInstance().sendMail(fr_name,title, toUsers, body);
		}else{
			MailManager.sendMail(fr_name,title, toUsers, body);
		}
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFr_name() {
		return fr_name;
	}

	public void setFr_name(String frName) {
		fr_name = frName;
	}

	public String getFr_addr() {
		return fr_addr;
	}

	public void setFr_addr(String frAddr) {
		fr_addr = frAddr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMaillist() {
		return maillist;
	}

	public void setMaillist(String maillist) {
		this.maillist = maillist;
	}

	public String getAttname() {
		return attname;
	}

	public void setAttname(String attname) {
		this.attname = attname;
	}

	public String getAttbody() {
		return attbody;
	}

	public void setAttbody(String attbody) {
		this.attbody = attbody;
	}

	public String getCclist() {
		return cclist;
	}

	public void setCclist(String cclist) {
		this.cclist = cclist;
	}
	
	public static void main(String[] args) 
	{
		String title = "[Cynthia]";
		String body = "<p style='font-color:red'><h1>Hello World</h1></p>";
		String maillist = "liuyanlei@sogou-inc.com";
		new MailSender().sendMail(title, body, maillist);
	}
	
	
	/*-------兼容旧系统-------*/

	public String getFromUser() {
		return fromUser;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String[] getToUsers() {
		return toUsers;
	}

	public void setToUsers(String[] toUsers) {
		this.toUsers = toUsers;
	}

	public String[] getCcUsers() {
		return ccUsers;
	}

	public void setCcUsers(String[] ccUsers) {
		this.ccUsers = ccUsers;
	}

	public String[] getBccUsers() {
		return bccUsers;
	}

	public void setBccUsers(String[] bccUsers) {
		this.bccUsers = bccUsers;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}
	
	public boolean sendHtmlEx(String encode)
	{
		if(fromUser == null )
		{
			fromUser = "cynthia@sogou-inc.com";
		}
		List<String> toUsersList = new ArrayList<String>();
		if(toUsers!=null)
		{
			for(String user:toUsers)
				toUsersList.add(user);
		}
		if(ccUsers!=null)
		{
			for(String user:ccUsers)
			{
				toUsersList.add(user);
			}
		}
		if(bccUsers!=null)
		{
			for(String user:bccUsers)
			{
				toUsersList.add(user);
			}
		}
		
		if(toUsersList==null||toUsersList.size()==0)
		{
			return true;
		}
		String[] allMailUsers = toUsersList.toArray(new String[0]);
		
		String maillist = ArrayUtil.strArray2String(allMailUsers, ";");
		
		this.sendMail(fromUser,fromUser,fromUser,this.subject,this.content,maillist,null);
		
		return true;
	}
	
	public boolean send()
	{
		return this.sendHtmlEx("");
	}

}
