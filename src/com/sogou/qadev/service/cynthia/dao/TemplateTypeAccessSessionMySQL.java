/**
* @Title: TemplateTypeAccessSessionMySQL.java
* @Package : com.sogou.qadev.service.cynthia.mysql
* @Description : 
* @author : liuyanlei
* @date : 2013-8-21
* @version : v1.0
*/
package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sogou.qadev.service.cynthia.bean.TemplateType;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.TemplateTypeImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;


/**
 * @ClassName : TemplateTypeAccessSessionMySQL
 * @Description : template mysql
 * @author : liuyanlei
 * @date 2013-8-21
 */
public class TemplateTypeAccessSessionMySQL {

	/**
	 * @description : add a template type
	 * @author : liuyanlei
	 * @parm
	 * @date : 2013-08-21
	 */
	public boolean addTemplateType(TemplateType templateType)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert into template_type (name,description,displayIndex) values (?,?,?)";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, templateType.getName());
			pstm.setString(2, templateType.getDescription());
			pstm.setInt(3, templateType.getDisplayIndex());
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
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}

	/**
	 * @description : query template from database
	 * @author : liuyanlei
	 * @parm : templateId
	 * @date : 2013-08-21
	 */
	public TemplateType queryTemplateTypeById(UUID templateTypeId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from template_type where id = ?";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, Integer.parseInt(templateTypeId.getValue()));
			rs = pstm.executeQuery();
			TemplateType templateType = null;
			if(rs.next())
			{
				templateType = new TemplateTypeImpl();
				templateType.setId(DataAccessFactory.getInstance().createUUID(Integer.toString(rs.getInt("id"))));
				templateType.setName(rs.getString("name"));
				templateType.setDisplayIndex(rs.getInt("displayIndex"));
			}
			return templateType;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
	}

	/**
	 * @description : query all templateType from database
	 * @author : liuyanlei
	 * @parm : templateId
	 * @date : 2013-08-21
	 */
	public List<TemplateType> queryAllTemplateType()
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<TemplateType> templateTypeList = new ArrayList<TemplateType>();
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select * from template_type";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				TemplateType templateType = new TemplateTypeImpl();
				templateType.setId(DataAccessFactory.getInstance().createUUID(Integer.toString(rs.getInt("id"))));
				templateType.setName(rs.getString("name"));
				templateType.setDisplayIndex(rs.getInt("displayIndex"));
				templateTypeList.add(templateType);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return templateTypeList;
	}
}
