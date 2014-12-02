package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * @description:timer interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:46:17
 * @version:v1.0
 */
public interface Timer extends Serializable
{
	/**
	 * @description:get timer action id
	 * @date:2014-5-6 下午4:46:30
	 * @version:v1.0
	 * @return
	 */
	public UUID getActionId();

	/**
	 * @description:set timer action id
	 * @date:2014-5-6 下午4:46:45
	 * @version:v1.0
	 * @param actionId
	 */
	public void setActionId(UUID actionId);

	/**
	 * @description:get timer action param
	 * @date:2014-5-6 下午4:46:53
	 * @version:v1.0
	 * @return
	 */
	public String getActionParam();

	/**
	 * @description:set timer action param
	 * @date:2014-5-6 下午4:47:05
	 * @version:v1.0
	 * @param actionParam
	 */
	public void setActionParam(String actionParam);

	/**
	 * @description:get timer create user
	 * @date:2014-5-6 下午4:47:17
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();

	/**
	 * @description:get timer execute day
	 * @date:2014-5-6 下午4:47:28
	 * @version:v1.0
	 * @return
	 */
	public String getDay();

	/**
	 * @description:set timer execute day
	 * @date:2014-5-6 下午4:47:41
	 * @version:v1.0
	 * @param day
	 */
	public void setDay(String day);

	/**
	 * @description:get timer execute hour
	 * @date:2014-5-6 下午4:47:49
	 * @version:v1.0
	 * @return
	 */
	public String getHour();

	/**
	 * @description:set timer execute hour
	 * @date:2014-5-6 下午4:47:59
	 * @version:v1.0
	 * @param hour
	 */
	public void setHour(String hour);

	/**
	 * @description:get timer id
	 * @date:2014-5-6 下午4:48:07
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();

	/**
	 * @description:get timer execute minute
	 * @date:2014-5-6 下午4:48:16
	 * @version:v1.0
	 * @return
	 */
	public String getMinute();

	/**
	 * @description:set timer execute minute
	 * @date:2014-5-6 下午4:48:25
	 * @version:v1.0
	 * @param minute
	 */
	public void setMinute(String minute);

	/**
	 * @description:get timer execute month
	 * @date:2014-5-6 下午4:48:34
	 * @version:v1.0
	 * @return
	 */
	public String getMonth();

	/**
	 * @description:set timer execute month
	 * @date:2014-5-6 下午4:48:44
	 * @version:v1.0
	 * @param month
	 */
	public void setMonth(String month);

	/**
	 * @description:get timer action
	 * @date:2014-5-6 下午4:48:55
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:set timer name
	 * @date:2014-5-6 下午4:49:05
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @description:get timer execute second
	 * @date:2014-5-6 下午4:49:14
	 * @version:v1.0
	 * @return
	 */
	public String getSecond();

	/**
	 * @description:set timer execute second
	 * @date:2014-5-6 下午4:49:25
	 * @version:v1.0
	 * @param second
	 */
	public void setSecond(String second);

	/**
	 * @description:get timer execute  week
	 * @date:2014-5-6 下午4:49:40
	 * @version:v1.0
	 * @return
	 */
	public String getWeek();

	/**
	 * @description:set timer execute week
	 * @date:2014-5-6 下午4:49:48
	 * @version:v1.0
	 * @param week
	 */
	public void setWeek(String week);

	/**
	 * @description:get timer execute  year
	 * @date:2014-5-6 下午4:49:57
	 * @version:v1.0
	 * @return
	 */
	public String getYear();

	/**
	 * @description:set timer execute year
	 * @date:2014-5-6 下午4:50:09
	 * @version:v1.0
	 * @param year
	 */
	public void setYear(String year);

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午4:50:26
	 * @version:v1.0
	 * @return
	 */
	public Timestamp takeNextAlarmTime();

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午4:50:40
	 * @version:v1.0
	 * @return
	 */
	public TimeRegulate takeTimeRegulate();
	
	/**
	 * @description:get if timer is start
	 * @date:2014-5-6 下午4:50:43
	 * @version:v1.0
	 * @return
	 */
	public boolean isStart();

	/**
	 * @description:set timer is start
	 * @date:2014-5-6 下午4:50:55
	 * @version:v1.0
	 * @param isStart
	 */
	public void setStart(boolean isStart);

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午4:51:05
	 * @version:v1.0
	 * @return
	 */
	public Integer reachTimerQueueId();

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午4:51:09
	 * @version:v1.0
	 * @param timerQueueId
	 */
	public void setTimerQueueId(Integer timerQueueId);

	/**
	 * @description:get timer filter id
	 * @date:2014-5-6 下午4:51:13
	 * @version:v1.0
	 * @return
	 */
	public UUID getFilterId();

	/**
	 * @description:set timer filter id
	 * @date:2014-5-6 下午4:51:27
	 * @version:v1.0
	 * @param filterId
	 */
	public void setFilterId(UUID filterId);

	/**
	 * @description:get timer statisticre id
	 * @date:2014-5-6 下午4:51:39
	 * @version:v1.0
	 * @return
	 */
	public UUID getStatisticerId();

	/**
	 * @description:set timer statisticer id
	 * @date:2014-5-6 下午4:51:55
	 * @version:v1.0
	 * @param statisticerId
	 */
	public void setStatisticerId(UUID statisticerId);

	/**
	 * @description:get timer retry count after fail
	 * @date:2014-5-6 下午4:52:07
	 * @version:v1.0
	 * @return
	 */
	public long getRetryAccount();

	/**
	 * @description:set timer retry count after fail
	 * @date:2014-5-6 下午4:52:22
	 * @version:v1.0
	 * @param retryAccount
	 */
	public void setRetryAccount(long retryAccount);

	/**
	 * @description:get timer retry time
	 * @date:2014-5-6 下午4:52:35
	 * @version:v1.0
	 * @return
	 */
	public long getRetryDelay();

	/**
	 * @description:set retry time after timer fail
	 * @date:2014-5-6 下午4:52:50
	 * @version:v1.0
	 * @param retryDelay
	 */
	public void setRetryDelay(long retryDelay);
	
	/**
	 * @description:get timer create time
	 * @date:2014-5-6 下午4:53:08
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getCreateTime();
	
	/**
	 * @description:get if timer retry after fail
	 * @date:2014-5-6 下午4:53:18
	 * @version:v1.0
	 * @return
	 */
	public boolean isRetry();
	
	/**
	 * @description:return if send null of timer
	 * @date:2014-5-6 下午4:53:32
	 * @version:v1.0
	 * @return
	 */
	public boolean isSendNull();

	/**
	 * @description:set timer send null
	 * @date:2014-5-6 下午4:53:47
	 * @version:v1.0
	 * @param sendNull
	 */
	public void setSendNull(boolean sendNull);

	/**
	 * @description:set timer retry
	 * @date:2014-5-6 下午4:53:57
	 * @version:v1.0
	 * @param isRetry
	 */
	public void setRetry(boolean isRetry);
	
	/**
	 * @description:get timer if retry
	 * @date:2014-5-6 下午4:54:10
	 * @version:v1.0
	 * @return
	 */
	public boolean getRetry();
	
	/**
	 * @description:get timer start
	 * @date:2014-5-6 下午4:54:22
	 * @version:v1.0
	 * @return
	 */
	public boolean getStart();

	/**
	 * @description:timer clone
	 * @date:2014-5-6 下午4:54:35
	 * @version:v1.0
	 * @return
	 */
	public Object clone();
}
