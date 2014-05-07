package com.sogou.qadev.service.cynthia.util;

public class EscapeUtil {
	public static final String ESCAPE_APOS = "QADEV_ESCAPE_APOS";
	public static final String ESCAPE_QUOT = "QADEV_ESCAPE_QUOT";
	public static final String ESCAPE_LT = "QADEV_ESCAPE_LT";
	public static final String ESCAPE_GT = "QADEV_ESCAPE_GT";
	public static final String ESCAPE_AMP = "QADEV_ESCAPE_AMP";

	public static String encodeAll(String str) {
		str = encodeAPOS(str);
		str = encodeQUOT(str);
		str = encodeLT(str);
		str = encodeGT(str);
		str = encodeAMP(str);
		str = str.replaceAll("\\\\", "");
		return str;
	}

	public static String decodeAll(String str) {
		str = decodeAPOS(str);
		str = decodeQUOT(str);
		str = decodeLT(str);
		str = decodeGT(str);
		str = decodeAMP(str);
		return str;
	}

	public static String encodeAPOS(String str) {
		return str.replaceAll("'", ESCAPE_APOS);
	}

	public static String encodeQUOT(String str) {
		return str.replaceAll("\"", ESCAPE_QUOT);
	}

	public static String encodeLT(String str) {
		return str.replaceAll("<", ESCAPE_LT);
	}

	public static String encodeGT(String str) {
		return str.replaceAll(">", ESCAPE_GT);
	}

	public static String encodeAMP(String str) {
		return str.replaceAll("&", ESCAPE_AMP);
	}

	public static String decodeAPOS(String str) {
		return str.replaceAll(ESCAPE_APOS, "'");
	}

	public static String decodeQUOT(String str) {
		return str.replaceAll(ESCAPE_QUOT, "\"");
	}

	public static String decodeLT(String str) {
		return str.replaceAll(ESCAPE_LT, "<");
	}

	public static String decodeGT(String str) {
		return str.replaceAll(ESCAPE_GT, ">");
	}

	public static String decodeAMP(String str) {
		return str.replaceAll(ESCAPE_AMP, "&");
	}
}
