package com.sogou.qadev.service.cynthia.util;

import java.io.IOException;

import javax.servlet.http.HttpServlet;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.cache.impl.FlowCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.cache.impl.TemplateTypeCache;
import com.sogou.qadev.cache.impl.UserInfoCache;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.UpdateManager;


/**
 * @description:web app init
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:33:50
 * @version:v1.0
 */
public class ApplicationInitLoader extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private TimerExecutor timerExecutor = null;
	
	@Override
	public void init() {
		//检测并更新DataBase
		System.out.println("check and update database ....");
		UpdateManager.getInstance().UpdateDataBase();
		System.out.println("check and update database done ....");
		// 加载缓存数据
		System.out.println("init cache start....");
		FieldNameCache.getInstance().putAllDataToCache();
		TemplateCache.getInstance().putAllDataToCache();
		FlowCache.getInstance().putAllDataToCache();
		TemplateTypeCache.getInstance().putAllDataToCache();
		UserInfoCache.getInstance().putAllDataToCache();
		System.out.println("init cache end....");

		if (ConfigManager.getEnableEmail()) {
			timerExecutor = new TimerExecutor(); // 线上的话只能配置一台定时器！
			timerExecutor.start();
		}
		for (int i = 0; i < 5; i++) {
			// 每台机器开5个线程异步执行脚本
			new Thread(new ScriptExecuteThread()).start();
		}
	}

	@Override
	public void destroy() {
		if (timerExecutor != null) {
			timerExecutor.interrupt();
		}
		super.destroy();
	}
}
