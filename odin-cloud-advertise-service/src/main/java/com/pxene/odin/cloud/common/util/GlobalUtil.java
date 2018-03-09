package com.pxene.odin.cloud.common.util;

public class GlobalUtil {
	public static String parseString(String s, String v) {
		if (s == null) {
			return v;
		}
		return s;
	}
	
	public static String parseString(Object s, String v) {
		if (s == null) {
			return v;
		}
		return String.valueOf(s);
	}
	
	public static int parseInt(Object s, int v) throws Exception {
		if (s == null || "".equals(s)) {
			return v;
		}
		return Integer.parseInt(String.valueOf(s));
	}
	
	public static int parseInt(String s, int v) throws Exception {
		if (s == null || "".equals(s)) {
			return v;
		}
		return Integer.parseInt(s);
	}
}
