package com.sogou.qadev.service.cynthia.service;

import java.lang.reflect.Method;

import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.TimerAction;

/**
 * @description:timeraction processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:15:35
 * @version:v1.0
 */
public class TimerActionManager
{	
	private static TimerActionManager instance = null;
	
	public static TimerActionManager getInstance()
	{
		if (instance == null)
			instance = new TimerActionManager();
	
		return instance;
	}

	public void doTimerAction(TimerAction timerAction, Data[] dataArray, String username, String xml)
	{
		if (timerAction == null)
			return;

		try
		{
			Class queryClass = Class.forName(timerAction.getClassName());

			Object queryObject = queryClass.newInstance();
			Method queryMethod = queryClass.getMethod(timerAction.getMethod(), new Class[]{String.class, Data[].class, String.class, String.class});

			queryMethod.invoke(queryObject, new Object[] {timerAction.getId().getValue(), dataArray, username, xml });
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void doTimerAction(TimerAction timerAction, String content, String username, String xml )
	{
		if (timerAction == null)
			return;

		try
		{
			Class queryClass = Class.forName(timerAction.getClassName());

			Object queryObject = queryClass.newInstance();
			Method queryMethod = queryClass.getMethod(timerAction.getMethod(), new Class[]{String.class, String.class, String.class, String.class});

			queryMethod.invoke(queryObject, new Object[] {timerAction.getId().getValue(), content, username, xml });
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
