package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.TemplateImpl;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:template db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:56:08
 * @version:v1.0
 */
public class TemplateAccessSessionMySQL {
	
	private static Logger logger = Logger.getLogger(TemplateAccessSessionMySQL.class.getName());
	/**
	 * @description query template by id
	 * @author liuyanlei
	 * @param templateId
	 * @date 2013-08-26
	 * */
	public Template queryTemplateById(UUID templateId)
	{
		if (templateId == null) {
			return null;
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Template template = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from template where id = "+templateId.getValue();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				boolean isNew = rs.getBoolean("is_new");
				String xml = null;
				if(isNew)
					xml = rs.getString("layout_xml");
				else
					xml = rs.getString("xml");
				Document doc = XMLUtil.string2Document(xml, "UTF-8");
				if(doc != null)
				{
					template = new TemplateImpl(doc,rs.getString("create_user"));
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return template;
	}
	
	/**
	 * @description query all templates
	 * @author liuyanlei
	 * @date 2013-08-26
	 * */
	public List<Template> queryAllTemplate()
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<Template> templateList = new ArrayList<Template>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			
			pstm = conn.prepareStatement("select * from template where is_valid=? order by name");
			pstm.setString(1, "1");
			
			rs = pstm.executeQuery();
			while(rs.next())
			{
				boolean isNew = rs.getBoolean("is_new");
				String xml = null;
				if(isNew)
					xml = rs.getString("layout_xml");
				else
					xml = rs.getString("xml");
				Document doc = null;
				try {
					doc = XMLUtil.string2Document(xml, "UTF-8");
				} catch (Exception e) {
					System.out.println(rs.getString("name"));
				}
				
				if(doc != null)
				{
					Template template = new TemplateImpl(doc,rs.getString("create_user"));
	//				if (template != null && !ConfigUtil.abandonTemplateIdSet.contains(template.getId().getValue())) {
					if (template != null) {
						templateList.add(template);
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return templateList;
	}
	
	/**
	 * @description query template by id
	 * @author liuyanlei
	 * @param templateId
	 * @date 2013-08-26
	 * */
	public boolean addTemplate(Template template)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert into template (id,name,layout_xml,is_new,create_user) values (?,?,?,?,?)";
			
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, template.getId().getValue());
			pstm.setString(2, template.getName());
			pstm.setString(3, template.toXMLString());
			pstm.setBoolean(4, true);
			pstm.setString(5, template.getCreateUser());
			
			if (pstm.executeUpdate()>0) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
	
	/**
	 * @description update a template
	 * @author liuyanlei
	 * @date 2013-08-26
	 * */
	public boolean updateTemplate(Template template)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update template set name = ? , layout_xml = ?, is_new = ? where id = ?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, XMLUtil.toSafeXMLString(template.getName()));
			pstm.setString(2, template.toXMLString());
			pstm.setBoolean(3, true);
			pstm.setString(4, template.getId().getValue());
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
	
	/**
	 * @description remove a template
	 * @author liuyanlei
	 * @date 2013-08-26
	 * */
	public boolean removeTemplateById(UUID templateId)
	{
		return setTemplateValid(templateId, false);
	}
	
	/**
	 * @description:query templates id name map
	 * @date:2014-5-6 下午5:56:55
	 * @version:v1.0
	 * @return
	 */
	public Map<String, String> queryIdName(){
		Map<String, String> idNameMap = new HashMap<String, String>();
		
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select id,name from template";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				idNameMap.put(rs.getString("id"), rs.getString("name"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return idNameMap;
	}
	
	
	/**
	 * @description:set template is_valid
	 * @date:2014-5-6 下午5:57:09
	 * @version:v1.0
	 * @param templateId
	 * @param isValid
	 * @return
	 */
	public boolean setTemplateValid(UUID templateId, boolean isValid ){
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update template set is_valid = ? where id = ?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, isValid ? "1" : "0");
			pstm.setString(2, templateId.getValue());
			return pstm.executeUpdate() >0 ; 
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}
	
}
