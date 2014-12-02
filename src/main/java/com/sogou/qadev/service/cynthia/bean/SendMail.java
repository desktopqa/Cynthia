package com.sogou.qadev.service.cynthia.bean;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sogou.qadev.service.cynthia.service.MailSender;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.URLUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:Send script Mail
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:24:05
 * @version:v1.0
 */
public class SendMail
{
	/**
	 * @description:send mail from action id
	 * @date:2014-5-6 下午4:24:18
	 * @version:v1.0
	 * @param id:action id
	 * @param content:
	 * @param username:current user
	 * @param xml:action xml
	 * @return
	 */
	public boolean sendMail(String id, String content, String username, String xml)
	{
		try
		{
			Document document = XMLUtil.string2Document(xml, "UTF-8");
			
			NodeList paramList = document.getElementsByTagName( "param" );
			
			MailSender  sender = new MailSender();
			sender.setFromUser( username );
			
			sender.setHtml( true );
			sender.setSmtp( "transport.mail.sogou-inc.com" );

			for( int in = 0; in < paramList.getLength(); in++ )
			{
				Element node = (Element)paramList.item( in );
				if( node.getAttribute( "name" ).equals( "title") )
					sender.setSubject( node.getAttribute( "value" ) );
				else if( node.getAttribute( "name" ).equals( "mailList") )
				{
					String value = node.getAttribute( "value" );
					value = value.replaceAll( ";", "," );
					value = value.replaceAll( "\r\n", "," );
					value = value.replaceAll( "\n", "," );
					
					sender.setToUsers( value.split( "," ) );
				}
				else if( node.getAttribute( "name" ).equals( "ccMailList") )
				{
					String value = node.getAttribute( "value" );
					value = value.replaceAll( ";", "," );
					value = value.replaceAll( "\r\n", "," );
					value = value.replaceAll( "\n", "," );
					
					sender.setCcUsers( value.split( "," ) );
				}
				else if( node.getAttribute( "name" ).equals( "bccMailList") )
				{
					String value = node.getAttribute( "value" );
					value = value.replaceAll( ";", "," );
					value = value.replaceAll( "\r\n", "," );
					value = value.replaceAll( "\n", "," );
					
					sender.setBccUsers( value.split( "," ) );
				}
			}	
			sender.setContent( content.replaceAll("utf-8", "GBK").replaceAll("UTF-8", "GBK") );
			sender.setEncode( "GBK" );
				
			return sender.sendHtmlEx("GBK");
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * @description:send mail from data array
	 * @date:2014-5-6 下午4:24:51
	 * @version:v1.0
	 * @param id:action id
	 * @param dataArray:data
	 * @param username:current user
	 * @param xml:action xml
	 * @return
	 */
	public boolean sendMail(String id,Data[] dataArray, String username, String xml)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse( new ByteArrayInputStream( xml.getBytes() ) );
			
			NodeList paramList = document.getElementsByTagName( "param" );
			
			MailSender  sender = new MailSender();
			sender.setFromUser( username );
			
			sender.setHtml( true );
			sender.setSmtp( "transport.mail.sogou-inc.com" );
			
			for( int in = 0; in < paramList.getLength(); in++ )
			{
				Element node = (Element)paramList.item( in );
				if( node.getAttribute( "name" ).equals( "title") )
					sender.setSubject( node.getAttribute( "value" ) );
				else if( node.getAttribute( "name" ).equals( "mailList") )
				{
					String value = node.getAttribute( "value" );
					value = value.replaceAll( ";", "," );
					value = value.replaceAll( "\r\n", "," );
					value = value.replaceAll( "\n", "," );
					
					sender.setToUsers( value.split( "," ) );
				}
				else if( node.getAttribute( "name" ).equals( "ccMailList") )
				{
					String value = node.getAttribute( "value" );
					value = value.replaceAll( ";", "," );
					value = value.replaceAll( "\r\n", "," );
					value = value.replaceAll( "\n", "," );
					
					sender.setCcUsers( value.split( "," ) );
				}
				else if( node.getAttribute( "name" ).equals( "bccMailList") )
				{
					String value = node.getAttribute( "value" );
					value = value.replaceAll( ";", "," );
					value = value.replaceAll( "\r\n", "," );
					value = value.replaceAll( "\n", "," );
					
					sender.setBccUsers( value.split( "," ) );
				}
			}
			
			StringBuffer content = new StringBuffer();
			
			if( dataArray != null && dataArray.length > 0 )
			{
				content.append( "<table>" );
				content.append( "<tr bgcolor='#EEFFFF'>" );
				content.append( "<td nowrap>序号</td>" );
				content.append( "<td nowrap>任务ID</td>" );
				content.append( "<td nowrap>任务标题</td>" );
				content.append( "<td nowrap>状态</td>" );
				content.append( "<td nowrap>描述</td>" );
				content.append( "<td nowrap>指派人</td>" );
				content.append( "<td nowrap>指派类型</td>" );
				content.append( "<td nowrap>创建人</td>" );
				content.append( "<td nowrap>创建时间</td>" );
				content.append( "<td nowrap>最后修改时间</td>" );
				content.append( "</tr>" );
				
				for( int i = 0; i < dataArray.length; i ++ )
				{
					content.append( "<tr bgcolor='#EEEEEE'>" );
					content.append( "<td nowrap>").append((i + 1)).append("</td>" );
					content.append( "<td nowrap>").append(dataArray[i].getId()).append("</td>" );
					content.append( "<td nowrap><a href=\"").append(ConfigUtil.getCynthiaWebRoot()).append("taskManagement.html?operation=read&taskid=").append(URLUtil.toSafeURLString(dataArray[i].getId().toString())).append("\">").append(( dataArray[i].getTitle()==null?"-":dataArray[i].getTitle() )).append("</a></td>" );
					content.append( "<td nowrap>").append(dataArray[i].getStatusId()).append("</td>" );
					content.append( "<td nowrap>").append(( dataArray[i].getDescription() == null?"-":dataArray[i].getDescription() )).append("</td>" );
					content.append( "<td nowrap>").append(( dataArray[i].getAssignUsername() == null?"-":dataArray[i].getAssignUsername() )).append("</td>" );
					content.append( "<td nowrap>").append(dataArray[i].getCreateUsername() ).append("</td>" );
					content.append( "<td nowrap>").append(dataArray[i].getCreateTime().toString() ).append("</td>" );
					content.append( "<td nowrap>").append(dataArray[i].getLastModifyTime().toString() ).append("</td>" );
					content.append( "</tr>" );
				}
				content.append( "</table>" );
			}
			
			sender.setContent( content.toString() );
			
			return sender.send();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
}


