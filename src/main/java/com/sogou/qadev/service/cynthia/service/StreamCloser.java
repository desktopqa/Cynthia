package com.sogou.qadev.service.cynthia.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;

public class StreamCloser {

	public static void closeInputStream(InputStream is){
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				is = null;
			}
		}
	}

	public static void closeOutputStream(OutputStream os){
		if(os != null){
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				os = null;
			}
		}
	}

	public static void closeReader(Reader reader){
		if(reader != null){
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				reader = null;
			}
		}
	}

	public static void closeUrlConnection(HttpURLConnection uconn){
		if (uconn != null) {
			uconn.disconnect();
			uconn = null;
		}
	}
}
