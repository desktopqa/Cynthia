package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.AttachmentImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.service.FileUpDownLoadHandler;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;

/**
 * @description:attachment db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:11:55
 * @version:v1.0
 */
public class AttachmentAccessSessionMySQL
{
	public AttachmentAccessSessionMySQL()
	{
		super();
	}

	/**
	 * @description:return attachment id and name map by ids
	 * @date:2014-5-6 下午5:12:15
	 * @version:v1.0
	 * @param ids
	 * @return
	 */
	public Map<String ,String> getAttachIdAndNameByIds(Set<String> ids){
		if(ids == null || ids.size() == 0){
			return new HashMap<String, String>();
		}
		Map<String, String> attachIdNameMap = new HashMap<String, String>();
 		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select id , name from attachment where id in (");
		for (String attachId : ids) {
			sqlBuffer.append(attachId).append(",");
		}
		sqlBuffer.deleteCharAt(sqlBuffer.length() -1 );
		sqlBuffer.append(")");

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				attachIdNameMap.put(rs.getString("id"), rs.getString("name"));
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return attachIdNameMap;
	}
	
	/**
	 * @description:create attachment 
	 * @date:2014-5-6 下午5:12:40
	 * @version:v1.0
	 * @param name
	 * @param username
	 * @param data
	 * @return
	 */
	public Attachment createAttachment(String name, String username, byte[] data)
	{
		if(name == null || data == null){
			return null;
		}

		int dataLength = data.length;
		byte[] returnData = data;
		
		Connection conn = null;
		PreparedStatement pstm = null;
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			UUID id = DataAccessFactory.getInstance().newUUID("ATTA");
			Timestamp createTime = new Timestamp(System.currentTimeMillis());
			
			String fileId = "";
			
			if (ConfigManager.getEnableFileSystem()) {
				//分布式上传
				fileId = FileUpDownLoadHandler.postFile(name,data);
				if (fileId == null || fileId.length() == 0) {
					throw new Exception("上传文件出错");
				}
				data = null;
			}
			
			pstm = conn.prepareStatement("insert into attachment set id = ?, size = ?, create_user = ?, name = ?, create_time = ?, file_id=? ,data=?");
			pstm.setLong(1, Long.parseLong(id.getValue()));
			pstm.setLong(2, dataLength);
			pstm.setString(3, username);
			pstm.setString(4, name);
			pstm.setTimestamp(5, createTime);
			pstm.setString(6, fileId);
			pstm.setBytes(7, data);
			pstm.execute();

			Attachment attachment = new AttachmentImpl(id);
			attachment.setName(name);
			attachment.setCreateUser(username);
			attachment.setCreateTime(createTime);
			attachment.setData(returnData);
			attachment.setSize(dataLength);
			attachment.setFileId(fileId);

			return attachment;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}

		return null;
	}

	/**
	 * @description:query attachment by ids 
	 * @date:2014-5-6 下午5:12:56
	 * @version:v1.0
	 * @param ids
	 * @param needData:need file data
	 * @return
	 */
	public Attachment[] queryAttachments(UUID[] ids, boolean needData)
	{
		if(ids == null || ids.length == 0){
			return new Attachment[0];
		}

		PreparedStatement statement = null;
		List<Attachment> attachmentList = new LinkedList<Attachment>();
		Connection conn = null;
		ResultSet rs = null;
		
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("select id, size, name, create_user, create_time, file_id,data from attachment");

			for(int i = 0; i < ids.length; i++){
				if (ids[i] == null)
					continue;
				sql.append(" " + (i == 0 ? "where" : "or") + " id = ?");
			}

			statement = conn.prepareStatement(sql.toString());

			for(int i = 0; i < ids.length; i++){
				if (ids[i] == null)
						continue;
				statement.setLong(i + 1, Long.parseLong(ids[i].getValue()));
			}

			rs = statement.executeQuery();
			while (rs.next()){
				Attachment attachment = new AttachmentImpl(DataAccessFactory.getInstance().createUUID(Long.toString(rs.getLong(1))));
				attachment.setSize(rs.getLong(2));
				attachment.setName(rs.getString(3));
				attachment.setCreateUser(rs.getString(4));
				attachment.setCreateTime(rs.getTimestamp(5));
				if (needData){
					byte[] data = null;
					if(ConfigManager.getEnableFileSystem()){
						//分布式文件系统
						data = FileUpDownLoadHandler.downloadData(rs.getString("file_id"));
					}
					if(data == null)
						data = rs.getBytes("data");
					
					attachment.setData(data);
				}
				attachmentList.add(attachment);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(statement);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return attachmentList.toArray(new Attachment[attachmentList.size()]);
	}

	/**
	 * @description:get all attachment ids
	 * @date:2014-5-6 下午5:13:28
	 * @version:v1.0
	 * @return
	 */
	public List<Long> getAllAttachmentIds()
	{
		PreparedStatement pstm = null;
		List<Long> ids = new ArrayList<Long>();
		Connection conn = null;
		ResultSet rs = null;
		
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select id from attachment");
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement(sql.toString());
			rs = pstm.executeQuery();
			while (rs.next()){
				long id = rs.getLong(1);
				ids.add(id);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}

		return ids;
	}
	
	/**
	 * @description:update a attachment
	 * @date:2014-5-6 下午5:13:41
	 * @version:v1.0
	 * @param attachment
	 * @return
	 */
	public boolean updateAttachment(Attachment attachment) {
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			StringBuffer sql = new StringBuffer();
			
			sql.append("update attachment set size=?,name=?,create_user=?,create_time=? ,file_id=? , data = ? where id = ?");

			pstm = conn.prepareStatement(sql.toString());
			pstm.setLong(1, attachment.getSize());
			pstm.setString(2, attachment.getName());
			pstm.setString(3, attachment.getCreateUser());
			pstm.setTimestamp(4, attachment.getCreateTime());
			pstm.setString(5, attachment.getFileId() == null ?  "": attachment.getFileId());
			pstm.setBytes(6, attachment.getData());
			pstm.setString(7, attachment.getId().getValue());
			return pstm.executeUpdate() > 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
		finally{
			DbPoolConnection.getInstance().closeAll(rs,pstm,conn);
		}
	}


	/**
	 * @description:query attachments by ids
	 * @date:2014-5-6 下午5:13:57
	 * @version:v1.0
	 * @param attachmentIdArray
	 * @return
	 */
	public Map<String, String> queryAttachmentIdNames(UUID[] attachmentIdArray) {
		Map<String, String> attachMap = new HashMap<String, String>();
		if(attachmentIdArray == null || attachmentIdArray.length == 0){
			return attachMap;
		}
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("select id ,name from attachment");

			for(int i = 0; i < attachmentIdArray.length; i++){
				sql.append(" " + (i == 0 ? "where" : "or") + " id = ?");
			}

			pstm = conn.prepareStatement(sql.toString());

			for(int i = 0; i < attachmentIdArray.length; i++){
				pstm.setLong(i + 1, Long.parseLong(attachmentIdArray[i].getValue()));
			}

			rs = pstm.executeQuery();
			while (rs.next()){
				attachMap.put(rs.getString("id"), rs.getString("name"));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeAll(rs,pstm,conn);
		}

		return attachMap;
	}

}
