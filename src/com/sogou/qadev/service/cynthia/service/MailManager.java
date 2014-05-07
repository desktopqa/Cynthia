package com.sogou.qadev.service.cynthia.service;

import java.util.Properties;

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

import sun.misc.BASE64Decoder;

/**
 * @description:mail sender processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:11:24
 * @version:v1.0
 */
public class MailManager {
	
	/**
	 * @description:send mail
	 * @date:2014-5-6 下午12:11:34
	 * @version:v1.0
	 * @param subject:mail subject
	 * @param recievers:mail recievers
	 * @param content:mail content
	 * @return:if mail send success
	 */
	public static boolean sendMail(String subject,String[] recievers,String content){
		
        try{
            Properties props = ConfigManager.getEmailProperties();
            
            //配置中定义不发送邮件
            if (props.getProperty("mail.enable") == null || !props.getProperty("mail.enable").equals("true")) {
				System.out.println("there is a mail not send by config!");
            	return true;
			}
            //创建一个程序与邮件服务器的通信
            Session mailConnection=Session.getInstance(props,null);
            Message msg=new MimeMessage(mailConnection);
                                
            //设置发送人和接受人
            Address sender=new InternetAddress(props.getProperty("mail.user"));
            //单个接收人
            //Address receiver=new InternetAddress("xxx@163.com");
            //多个接收人
            StringBuffer buffer=new StringBuffer();
            for (String reciever : recievers) {
				buffer.append(buffer.length() > 0 ? ",":"").append(reciever);
			}
            String all=buffer.toString();
            System.out.println("send Mail,mailList:" + all);
            Address[] allre=InternetAddress.parse(all);
            msg.setFrom(sender);
            msg.setRecipients(Message.RecipientType.TO, allre);
            
            //设置邮件主题
            msg.setSubject(subject);
            
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
            Transport trans=mailConnection.getTransport(props.getProperty("mail.protocal"));
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
    
    //接收邮件时，获取某个邮件的中文附件名，出现乱码
    //对于用base64编码过的中文，则采用base64解码，
    //否则对附件名进行ISO8859_1到gbk的编码转换
    public static String getFileChinese(Part part) throws Exception {
        String temp = part.getFileName();// part为Part实例
        if ((temp.startsWith("=?GBK?") && temp.endsWith("?="))
                || (temp.startsWith("=?gbk?b?") && temp.endsWith("?="))) {
            temp = getFromBASE64(temp.substring(8, temp.indexOf("?=") - 1));
        } else {
            temp = toChinese(temp);
        }
        return temp;
    }
    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(sendMail("测试邮件", new String[]{"liming@sogou-inc.com"}, "这是一封测试邮件"));

	}

}
