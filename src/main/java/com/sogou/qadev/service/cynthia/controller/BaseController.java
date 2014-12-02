package com.sogou.qadev.service.cynthia.controller;

import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;


public abstract class BaseController {
	
	/**
	 * base data process interface 
	 */
	protected DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
	
	protected String baseXml = "<?xml version='1.0' encoding='UTF-8'?>";
	
	protected static String correctJson = "{\"success\" : true}";
	protected static String errorJson   = "{\"success\" : false}";
	
}
