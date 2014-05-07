package com.sogou.qadev.service.cynthia.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.FileBean;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;

/**
 * @description:file processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:11:27
 * @version:v1.0
 */
@Controller
@RequestMapping("/file")
public class FileUploadController extends BaseController{

	/**
	 * @description:upload file
	 * @date:2014-5-6 下午5:11:37
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/upload.do")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		
		request.setCharacterEncoding("UTF-8");
		
		final long MAX_SIZE = 500*1024*1024;
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		upload.setHeaderEncoding("UTF-8");
		
		upload.setFileSizeMax(MAX_SIZE);
		
		List<FileBean> allFileBean = new ArrayList<FileBean>();
		
		List items = null;
		
		Map result = new HashMap();
		
		boolean isError = false;
		
		String errorInfo = "";
		try
		{
			items = upload.parseRequest(request);
		}catch(Exception e)
		{
			e.printStackTrace();
			isError = true;
			errorInfo = "receive file error!";
		}
		
		if(items == null||items.size() == 0)
		{
			isError = true;
			errorInfo = "no files!";
		}else
		{
			Iterator iter = items.iterator();
			while(iter.hasNext())
			{
				FileItem item = (FileItem)iter.next();
				long fileSize = 0;
				if(item == null||item.isFormField())
					continue;
				String path = item.getName();
				fileSize = item.getSize();
				
				if(path == null)
					continue;
				String realFileName = path.substring(path.lastIndexOf("\\") + 1);
				try
				{
					realFileName = java.net.URLDecoder.decode(realFileName,"UTF-8");
				}catch(Exception e)
				{
					System.err.print("java decode file name exception");
				}

				
				Attachment attachment = das.createAttachment(realFileName, item.get());
				
				FileBean fBean = new FileBean();
				fBean.setId(attachment.getId().getValue());
				fBean.setFilename(attachment.getName());
				fBean.setFileId(attachment.getFileId());
				allFileBean.add(fBean);
				
			}
		}
		if(isError)
		{
			result.put("success", false);
			result.put("msg", errorInfo);
			result.put("allData", allFileBean);
		}else
		{
			result.put("success", true);
			result.put("allData", allFileBean);
		}

		String callBack = request.getParameter("callback");
		String resultJson = JSON.toJSONString(result);

		response.setContentType("application/json;charset=UTF-8");
		if(callBack!=null&&!"".equals(callBack))
			response.getWriter().write(callBack+"("+resultJson+")");
		else
			response.getWriter().write(resultJson);
	    }
}
