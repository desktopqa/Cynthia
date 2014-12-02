/**
 * filename : UserDefaultTemplateMySQL.java
 * date     : 2013-2-20
 * author   : afrous
 */
package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @description:user default template db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:01:50
 * @version:v1.0
 */
public class UserDefaultTemplateMySQL {

	public UserDefaultTemplateMySQL() {

	}

	/**
	 * @description:get user default template
	 * @date:2014-5-6 下午6:02:04
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public String getDefaultTemplateId(String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		String templateId = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from user_default_template where user_name=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();

			while(rs.next())
			{
				templateId = rs.getString("template_id");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}

		return templateId;
	}

	/**
	 * @description:insert user default template
	 * @date:2014-5-6 下午6:02:13
	 * @version:v1.0
	 * @param userName
	 * @param templateId
	 * @return
	 */
	public boolean insertUserDefaultTemplate(String userName,String templateId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert into user_default_template (user_name,template_id) values (?,?)";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			pstm.setString(2, templateId);
			return pstm.execute();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

	/**
	 * @description:update user default template
	 * @date:2014-5-6 下午6:02:28
	 * @version:v1.0
	 * @param userName
	 * @param templateId
	 * @return
	 */
	public boolean updateUserDefaultTemplate(String userName,String templateId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update user_default_template set template_id=? where user_name=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, templateId);
			pstm.setString(2, userName);
			return pstm.execute();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

	/**
	 * @description:remove user default template
	 * @date:2014-5-6 下午6:02:40
	 * @version:v1.0
	 * @param userName
	 * @param templateId
	 * @return
	 */
	public boolean removeUserDefaultTemplate(String userName,String templateId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "delete from user_default_template where user_name=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			return pstm.execute();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

	/**
	 * @description:add or update user default template
	 * @date:2014-5-6 下午6:02:54
	 * @version:v1.0
	 * @param userName
	 * @param templateId
	 * @return
	 */
	public boolean addOrUpdateUserDefaultTemplate(String userName,String templateId)
	{
		String localTemplateId = this.getDefaultTemplateId(userName);
		if(localTemplateId==null)
		{
			return this.insertUserDefaultTemplate(userName, templateId);
		}else
		{
			return this.updateUserDefaultTemplate(userName, templateId);
		}
	}
}
