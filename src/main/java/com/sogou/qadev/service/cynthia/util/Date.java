package com.sogou.qadev.service.cynthia.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Date implements Comparable<Date>, Serializable {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = 1L;
	String dateStr = null;

	private Date(String dateStr) {
		this.dateStr = dateStr;
	}

	public static Date valueOf(String dateStr, String formatStr){
		if (dateStr == null)
			throw new IllegalArgumentException();
		
		if (dateStr.equals("")) {
			return null;
		}
		
		String timestampStr = null;
		try {
			timestampStr = Timestamp.valueOf(dateStr).toString();
		} catch (Exception e) {
		}

		dateStr = checkValue(dateStr);
		
		if (timestampStr == null) {
			try {
				DateFormat df = new SimpleDateFormat(formatStr);
				java.util.Date date1 = df.parse(dateStr);
				timestampStr = CynthiaUtil.toLocalDateString(date1);
			} catch (Exception e) {
				System.err.println("error in date.java valueOf -- java.text.ParseException: Unparseable date: "+ dateStr);
			}
		}
		
		return new Date(timestampStr);
	}
	
	public static String checkValue(String timeStr){
		try {
			StringBuffer timeBuffer = new StringBuffer();
			String[] allDate = timeStr.split(" ");
			String[] dateArr = allDate[0].split("-");
			timeBuffer.append(dateArr[0]);
			timeBuffer.append("-");
			if (dateArr.length == 1) {
				timeBuffer.append("00-00 00:00:00");
			}else{
				timeBuffer.append(dateArr[1].length() == 1 ? "0" : "").append(dateArr[1]);
				timeBuffer.append("-");
				if (dateArr.length == 2) {
					timeBuffer.append("00 00:00:00");
				}else {
					timeBuffer.append(dateArr[2].length() == 1 ? "0" : "").append(dateArr[2]);
					
					
					if (allDate.length == 1) {
						timeBuffer.append(" 00:00:00");
					}else {
						String[] timeArr = allDate[1].split(":");
						timeBuffer.append(" ");
						timeBuffer.append(timeArr[0].length() == 1 ? "0" : "").append(timeArr[0]);
						if (timeArr.length == 1) {
							timeBuffer.append(":00:00");
						}else {
							timeBuffer.append(":");
							timeBuffer.append(timeArr[1].length() == 1 ? "0" : "").append(timeArr[1]);
							if (timeArr.length == 2) {
								timeBuffer.append(":00");
							}else {
								timeBuffer.append(":");
								timeBuffer.append(timeArr[2].length() == 1 ? "0" : "").append(timeArr[2]);
							}
						}
					}
				}
			}
			
			return timeBuffer.toString();
		} catch (Exception e) {
			System.err.println("Date checkValue parse error! timeStr:" + timeStr);
			return timeStr;
		}
	}
	
	public static Date valueOf(String dateStr) {
		return valueOf(dateStr, "yyyy-MM-dd HH:mm:ss");
	}

	public static String formatDate(String dateStr, String formatStr){
		java.text.DateFormat format2 = new java.text.SimpleDateFormat(formatStr);  
		try {
			java.util.Date date1 = (java.util.Date) format2.parseObject(dateStr);
			dateStr = format2.format(date1);  
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateStr;
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
		if (dateStr.contains("年")) {
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
					String day = dateStr.split("\\年")[1].split("\\月")[1].split("\\日")[0];
					if (day.length() == 1) {
						day = "0" + day;
					}
					timeStr += "-" + day;

					if (dateStr.endsWith("日"))
						timeStr += " 00:00:00";
					else {
						String time = dateStr.split("\\年")[1].split("\\月")[1].split("\\日")[1].split("\\时")[0];
						if (time != null && time.length() == 1) {
							time = "0" + time;
						}
						timeStr += " " + time;

						if (dateStr.endsWith("时"))
							timeStr += ":00:00";
						else{
							String[] minuteArray = dateStr.split("\\年")[1].split("\\月")[1].split("\\日")[1].split("\\时")[1].split("分");
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
		}else {
			dateStr = checkValue(dateStr);
			System.out.println("date format str in Date.java :" + dateStr);
			return Timestamp.valueOf(dateStr);
		}
	}
}
