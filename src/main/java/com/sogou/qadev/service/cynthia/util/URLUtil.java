package com.sogou.qadev.service.cynthia.util;

public class URLUtil {
	public static String toSafeURLString(String urlStr) {
		if (urlStr == null)
			return "";

		return urlStr.trim().replaceAll("#", "%23");
	}
}
