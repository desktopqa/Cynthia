package com.sogou.qadev.service.cynthia.util;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Timer;
import com.sogou.qadev.service.cynthia.bean.TimerAction;
import com.sogou.qadev.service.cynthia.dao.FilterAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.TimerAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.TimerActionAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ExportDataManager;
import com.sogou.qadev.service.cynthia.service.TimerActionManager;

/**
 * @description:timer execute threader
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:16:07
 * @version:v1.0
 */
public class TimerExecutor extends Thread {
	
	private class TimerRunner extends Thread {
		
		public void run() {
			try {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());

				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);

				int week = c.get(Calendar.DAY_OF_WEEK) - 1;
				if (week == 0) {
					week = 7;
				}

				c.get(Calendar.MONTH);

				Timer[] timerArray = new TimerAccessSessionMySQL().queryTimers();
				
				for (Timer timer : timerArray) {
					if (!timer.isStart())
						continue;

					if (timer.getHour() == null || timer.getMinute() == null) {
						continue;
					}

					if (timer.getWeek() != null) {
						Set<Integer> weekSet = new HashSet<Integer>();

						String[] weekStrArray = timer.getWeek().split(",");
						for (String weekStr : weekStrArray) {
							weekSet.add(Integer.parseInt(weekStr.trim()));
						}

						if (!weekSet.contains(week)) {
							continue;
						}
					} else if (timer.getMonth() != null
							&& timer.getDay() != null) {
						Set<Integer> monthSet = new HashSet<Integer>();

						String[] monthStrArray = timer.getMonth().split(",");
						for (String monthStr : monthStrArray) {
							monthSet.add(Integer.parseInt(monthStr.trim()));
						}

						if (!monthSet.contains(month)) {
							continue;
						}

						Set<Integer> daySet = new HashSet<Integer>();

						String[] dayStrArray = timer.getDay().split(",");
						for (String dayStr : dayStrArray) {
							daySet.add(Integer.parseInt(dayStr.trim()));
						}

						if (!daySet.contains(day)) {
							continue;
						}
					} else {
						continue;
					}

					Set<Integer> hourSet = new HashSet<Integer>();

					String[] hourStrArray = timer.getHour().split(",");
					for (String hourStr : hourStrArray) {
						hourSet.add(Integer.parseInt(hourStr.trim()));
					}

					if (!hourSet.contains(hour)) {
						continue;
					}

					Set<Integer> minuteSet = new HashSet<Integer>();

					String[] minuteStrArray = timer.getMinute().split(",");
					for (String minuteStr : minuteStrArray) {
						minuteSet.add(Integer.parseInt(minuteStr.trim()));
					}

					if (!minuteSet.contains(minute)) {
						continue;
					}

					this.doTimer(timer);

					System.out.println("run timer " + timer.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void doTimer(Timer timer) throws Exception {
			long timerStartTime = System.currentTimeMillis();
			
			System.out.println("start execute timer" + timer.getId());
			
			TimerAction timerAction = new TimerActionAccessSessionMySQL().queryTimerAction(timer.getActionId());
			
			if (timerAction == null) {
				return;
			}

			Filter filter = null;
			
			if (timer.getFilterId() != null) {
				
				filter = new FilterAccessSessionMySQL().queryFilter(timer.getFilterId());
				
				if (filter == null) {
					return;
				}
			}

			if (timer.getStatisticerId() != null) {
				return;
			}

			Data[] dataArray = null;
			String content = null;

			if (filter != null) {
				DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(timer.getCreateUser(),ConfigUtil.magic);
				content = ExportDataManager.exportMailHtmlFilter(das, ConfigUtil.magic, filter, timer.getCreateUser());
			}

			if (content == null) {
				TimerActionManager.getInstance().doTimerAction(timerAction, dataArray, timer.getCreateUser(), timer.getActionParam());
			} else {
				TimerActionManager.getInstance().doTimerAction(timerAction, content, timer.getCreateUser(), timer.getActionParam());
			}

			long endTimerTime = System.currentTimeMillis();
			long spendTime = endTimerTime - timerStartTime;
			System.out.println("timer action executed spend :" + spendTime);
		}
	}

	public void run() {
		while (true) {
			try {
				new TimerRunner().start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}