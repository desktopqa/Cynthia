package com.sogou.qadev.service.cynthia.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Date implements Comparable<Date>, Serializable {
	String dateStr = null;

	private Date(String dateStr) {
		this.dateStr = dateStr;
	}

	public static Date valueOf(String dateStr) {
		if (dateStr == null)
			throw new IllegalArgumentException();

		if (dateStr.indexOf("年") > 0)
			return new Date(dateStr);

		String timestampStr = null;
		try {
			timestampStr = Timestamp.valueOf(dateStr).toString();
		} catch (Exception e) {
		}

		if (timestampStr == null) {
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date1 = df.parse(dateStr);
				timestampStr = date1.toLocaleString();
			} catch (Exception e) {
				System.err
						.println("java.text.ParseException: Unparseable date: "
								+ dateStr);
			}
		}

		String timeStr = timestampStr.split("\\ ")[0].split("\\-")[0] + "年";
		timeStr += timestampStr.split("\\ ")[0].split("\\-")[1] + "月";
		timeStr += timestampStr.split("\\ ")[0].split("\\-")[2] + "日";
		timeStr += timestampStr.split("\\ ")[1].split("\\:")[0] + "时";
		timeStr += timestampStr.split("\\ ")[1].split("\\:")[1] + "分";

		if (timestampStr.split("\\ ")[1].split("\\:").length > 2) {
			String[] secondArr = timestampStr.split("\\ ")[1].split("\\:")[2]
					.split("\\.");
			if (secondArr != null && secondArr.length > 0) {
				timeStr += timestampStr.split("\\ ")[1].split("\\:")[2]
						.split("\\.")[0] + "秒";
			}
		}

		return new Date(timeStr);

	}

	public static long getTime(String timeStr) {
		if (timeStr.indexOf("年") > 0) {
			return Date.valueOf(timeStr).toTimestamp().getTime();
		} else {
			return Timestamp.valueOf(timeStr).getTime();
		}
	}

	public int compareTo(Date date) {
		if (this.toString().startsWith(date.toString())
				|| date.toString().startsWith(this.toString()))
			return 0;

		return this.toString().compareTo(date.toString());
	}

	public boolean equals(Object obj) {
		return (this.toString().startsWith(obj.toString()) || obj.toString()
				.startsWith(this.toString()));
	}

	public String toString() {
		return dateStr;
	}

	public Timestamp toTimestamp() {
		// 兼容技术支持组的错误数据 2013年12月2日15:27:40
		if (dateStr.contains("年") && dateStr.contains("月") && dateStr.contains("日")) {
			if (!dateStr.contains("时") && !dateStr.contains("分")
					&& !dateStr.endsWith("日")) {
				String timeStr = dateStr.split("\\年")[0] + "-";
				String month = dateStr.split("\\年")[1].split("\\月")[0];
				if (month.length() == 1) {
					timeStr += "0";
				}
				timeStr += month;
				timeStr += "-";

				String day = dateStr.split("\\月")[1].split("\\日")[0];
				if (day.length() == 1) {
					timeStr += "0";
				}
				timeStr += day;
				timeStr += " ";
				String[] alltime = dateStr.split("\\日")[1].split("：");
				if (alltime == null || alltime.length == 0
						|| alltime.length == 1) {
					alltime = dateStr.split("\\日")[1].split(":");
				}
				if (alltime == null || alltime.length == 0) {
					return null;
				} else if (alltime.length == 1) {
					timeStr += alltime[0] + ":00:00";
				} else if (alltime.length == 2) {
					timeStr += alltime[0] + ":" + alltime[1] + ":00";
				} else {
					timeStr += alltime[0] + ":" + alltime[1] + ":" + alltime[2];
				}
				return Timestamp.valueOf(timeStr);
			}
		}

		String timeStr = dateStr.split("\\年")[0];
		if (dateStr.endsWith("年"))
			timeStr += "-01-01 00:00:00";
		else {
			String month = dateStr.split("\\年")[1].split("\\月")[0];
			if (month.length() == 1) {
				month = "0" + month;
			}
			timeStr += "-" + month;
			if (dateStr.endsWith("月"))
				timeStr += "-01 00:00:00";
			else {
				String day = dateStr.split("\\年")[1].split("\\月")[1]
						.split("\\日")[0];
				if (day.length() == 1) {
					day = "0" + day;
				}
				timeStr += "-" + day;

				if (dateStr.endsWith("日"))
					timeStr += " 00:00:00";
				else {
					String time = dateStr.split("\\年")[1].split("\\月")[1]
							.split("\\日")[1].split("\\时")[0];
					if (time != null && time.length() == 1) {
						time = "0" + time;
					}
					timeStr += " " + time;

					if (dateStr.endsWith("时"))
						timeStr += ":00:00";
					else{
						String[] minuteArray = dateStr.split("\\年")[1].split("\\月")[1]
								.split("\\日")[1].split("\\时")[1].split("分");
						String minute = minuteArray[0];
						if (minute != null && time.length() == 1) {
							minute = "0" + minute;
						}
						timeStr += ":" + minute;

						if (minuteArray.length == 1) {
							timeStr += ":00";
						}else {
							String second = minuteArray[1];
							if (second.endsWith("秒")) {
								second = second.substring(0,second.length() -1);
							}
							
							if (second != null && second.length() == 1) {
								second = "0" + second;
							}
							timeStr += ":" + second;
						}
						
					}
				}
			}
		}
		return Timestamp.valueOf(timeStr);
	}
}
