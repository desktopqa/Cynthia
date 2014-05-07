package com.sogou.qadev.service.cynthia.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;


public class CommonUtil {
	
	public static long calcRealTime(Timestamp beginTime, Timestamp endTime) {
		Calendar beginC = getRealCalendar(beginTime);
		Calendar endC = getRealCalendar(endTime);

		long span = (endC.getTimeInMillis() - beginC.getTimeInMillis()) / 1000;

		long a = span / (86400 * 7);
		long b = span % (86400 * 7);
		long c = b / 86400;
		long d = b % 86400;

		if (endC.get(Calendar.DAY_OF_WEEK) < beginC.get(Calendar.DAY_OF_WEEK)
				|| endC.get(Calendar.DAY_OF_WEEK) == beginC
						.get(Calendar.DAY_OF_WEEK) && c == 6)
			c -= 2;

		if (d >= 32400)
			d -= 54000;

		return (32400 * 5) * a + 32400 * c + d;
	}

	protected static Calendar getRealCalendar(Timestamp time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time.getTime());

		if (c.get(Calendar.DAY_OF_WEEK) == 6
				&& c.get(Calendar.HOUR_OF_DAY) >= 19) {
			c.setTimeInMillis(c.getTimeInMillis() + 86400000 * 3);
			clearTime(c);
			return c;
		}

		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			c.setTimeInMillis(c.getTimeInMillis() + 86400000 * 2);
			clearTime(c);
			return c;
		}

		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			c.setTimeInMillis(c.getTimeInMillis() + 86400000);
			clearTime(c);
			return c;
		}

		if (c.get(Calendar.HOUR_OF_DAY) >= 10
				&& c.get(Calendar.HOUR_OF_DAY) < 19)
			return c;

		if (c.get(Calendar.HOUR_OF_DAY) >= 19)
			c.setTimeInMillis(c.getTimeInMillis() + 86400000);

		clearTime(c);
		return c;
	}

	protected static void clearTime(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 10);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}

	public static boolean isPosNum(String str) {
		if (str == null) {
			return false;
		}
		return str.matches("[1-9]+[0-9]*");
	}

	public static String functionSetToStr(Set<String> set) {
		if (set == null)
			return null;

		if (set.size() == 0)
			return "";

		return set.toString().substring(1, set.toString().length() - 1)
				.replace(", ", ";");
	}

	public static String idSetToStr(Set<UUID> set) {
		if (set == null)
			return null;

		if (set.size() == 0)
			return "";

		return set.toString().substring(1, set.toString().length() - 1)
				.replace(", ", ",");
	}

	public static Set<String> functionStrToSet(String str) {
		if (str == null)
			return null;

		if (str.length() == 0)
			return new LinkedHashSet<String>();

		Set<String> set = new LinkedHashSet<String>();
		String[] strElemArray = str.split(";");
		for (String strElem : strElemArray)
			set.add(strElem);

		return set;
	}

	public static Set<UUID> idStrToSet(String str) {
		if (str == null)
			return null;

		if (str.length() == 0)
			return new LinkedHashSet<UUID>();

		Set<UUID> set = new LinkedHashSet<UUID>();
		String[] strElemArray = str.split(",");
		for (String strElem : strElemArray)
			set.add(DataAccessFactory.getInstance().createUUID(strElem));

		return set;
	}

	public static String arrayToStr(String[] strArray) {
		StringBuffer strBuffer = new StringBuffer();
		for (String str : strArray) {
			strBuffer.append(strBuffer.length() > 0 ? "," : "").append(str);
		}
		return strBuffer.toString();
	}

	public static byte[] str2gzip(String str, String code) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);

		gzos.write(str.getBytes(code));
		gzos.close();

		return baos.toByteArray();
	}

	public static String gzip2str(byte[] byteArray, String code)
			throws Exception {
		GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(
				byteArray));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];

		while (true) {
			int len = gzis.read(buf);
			if (len == -1) {
				break;
			}

			baos.write(buf, 0, len);
		}

		gzis.close();
		baos.close();

		return baos.toString(code);
	}
}
