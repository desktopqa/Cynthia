package com.sogou.qadev.service.cynthia.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.service.cynthia.bean.Timer;
import com.sogou.qadev.service.cynthia.bean.TimerAction;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.StatisticerManager;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.login.bean.Key;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description:statistic processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:39:30
 * @version:v1.0
 */
@Controller
@RequestMapping("/statistic")
public class StatisticController extends BaseController{

	/**
	 * @description:update a statistic
	 * @date:2014-5-5 下午8:39:43
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/update.do")
	@ResponseBody
	public String update(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		
		Key key = (Key)session.getAttribute("key");
		String statId = request.getParameter("statId");
		String statName = request.getParameter("statName");
		String params = request.getParameter("params");
		params = CynthiaUtil.getXMLStr(params);
		String mailTime = request.getParameter("mailTime");
		String recievers = request.getParameter("recievers");
		
		params = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + params;
		TimerAction timerAction = null;
		Timer timer = null;
		if (statId == null || statId.equals("")) {
			//新创建
			timerAction = das.createTimerAction();
			timerAction.setClassName("com.sogou.qadev.service.cynthia.service.StatisticerManager");
			timerAction.setMethod("execute");
			timerAction.setName(statName);
			timerAction.setCreateUser(key.getUsername());
			timerAction.setParam(params);
			if (mailTime != null && !mailTime.equals("") && recievers != null && !recievers.equals("")) {
				timer = das.createTimer(key.getUsername());
				timer.setActionId(timerAction.getId());
				timer.setActionParam(timerAction.getParam());
				timer.setName(timerAction.getName());
				timer.setRetry(true);
				timer.setRetryAccount(5);
				timer.setRetryDelay(50000);
				timer.setSendNull(true);
				timer.setStart(true);
				timer.setMonth("1,2,3,4,5,6,7,8,9,10,11,12");
				timer.setWeek("1,2,3,4,5,6,7");
				timer.setDay("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31");
				
				String[] mailTimeElemArray = mailTime.split(":");
				
				timer.setHour(mailTimeElemArray[0]);
				timer.setMinute(mailTimeElemArray.length > 1 ? mailTimeElemArray[1] : "00");
				das.addTimer(timer);
			}
			
			return String.valueOf(das.addTimerAction(timerAction).equals(ErrorCode.success));
		}else {
			timerAction = das.queryTimerAction(DataAccessFactory.getInstance().createUUID(statId));
			//更新统计
			timerAction.setName(statName);
			timerAction.setParam(params);
			
			Timer[] timerArray = das.queryTimersByActionId(timerAction.getId());
			
			if (timerArray.length > 0) {
				//己存在定时器更新定时器
				for(int i = 0; i < timerArray.length; i++){
					if(timerArray[i].getActionId().toString().equals(timerAction.getId().toString())){
						timer = timerArray[i];
						timer.setActionParam(timerAction.getParam());
						timer.setName(timerAction.getName());
						
						if (mailTime == null || mailTime.equals("")) {
							//时间为空则直接删除timer
							das.removeTimer(timer.getId());
						}else {
							//否则更新timer时间
							String[] mailTimeElemArray = mailTime.split(":");
							timer.setHour(mailTimeElemArray[0]);
							timer.setMinute(mailTimeElemArray.length > 1 ? (mailTimeElemArray[1]) : "00");
							das.modifyTimer(timer);
						}
						break;
					}
				}
			}else {
				//不存在，创建定时器
				if (mailTime != null && !mailTime.equals("") && recievers != null && !recievers.equals("")) {
					timer = das.createTimer(key.getUsername());
					timer.setActionId(timerAction.getId());
					timer.setActionParam(timerAction.getParam());
					timer.setName(timerAction.getName());
					timer.setRetry(true);
					timer.setRetryAccount(5);
					timer.setRetryDelay(50000);
					timer.setSendNull(true);
					timer.setStart(true);
					timer.setMonth("1,2,3,4,5,6,7,8,9,10,11,12");
					timer.setWeek("1,2,3,4,5,6,7");
					timer.setDay("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31");
					
					String[] mailTimeElemArray = mailTime.split(":");
					
					timer.setHour(mailTimeElemArray[0]);
					timer.setMinute(mailTimeElemArray.length > 1 ? mailTimeElemArray[1] : "00");
					das.addTimer(timer);
				}
			}
			
			return String.valueOf(das.modifyTimerAction(timerAction).equals(ErrorCode.success));
		}
	}
	
	/**
	 * @description:query all statistics of user
	 * @date:2014-5-5 下午8:39:58
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryAllStatistics.do")
	@ResponseBody
	public String queryAllStatistics(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String userName = ((Key)session.getAttribute("key")).getUsername();
		List<TimerAction> allTimerActions = new ArrayList<TimerAction>();
		allTimerActions.addAll(Arrays.asList(das.queryStatisticByUser(userName)));
		return JSONArray.toJSONString(allTimerActions);
	}
	
	/**
	 * @description:delete statistic of user
	 * @date:2014-5-5 下午8:40:12
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/deleteStatistic.do")
	@ResponseBody
	public String deleteStatistic(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		String statId = request.getParameter("statId");
		return String.valueOf(das.removeTimerAction(DataAccessFactory.getInstance().createUUID(statId)).equals(ErrorCode.success));
	}
	
	/**
	 * @description:get statistic info
	 * @date:2014-5-5 下午8:40:28
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStatisticInfo.do")
	@ResponseBody
	public String getStatisticInfo(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		Key key = (Key)session.getAttribute("key");
		String statisticId = request.getParameter("statisticId");
		return JSONArray.toJSONString(StatisticerManager.
				getStatisticResultById(DataAccessFactory.getInstance().createUUID(statisticId), key.getUsername()));
	}

}
