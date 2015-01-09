package com.sogou.qadev.service.cynthia.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;


/**
 * @description:stream closer processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:12:26
 * @version:v1.0
 */
public class StreamCloserManager {

	/**
	 * @description:close inputstream
	 * @date:2014-5-6 下午12:12:39
	 * @version:v1.0
	 * @param is
	 */
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

	/**
	 * @description:close outputstream
	 * @date:2014-5-6 下午12:12:53
	 * @version:v1.0
	 * @param os
	 */
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

	/**
	 * @description:close reader
	 * @date:2014-5-6 下午12:13:05
	 * @version:v1.0
	 * @param reader
	 */
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

	/**
	 * @description:close urlconnection
	 * @date:2014-5-6 下午12:13:15
	 * @version:v1.0
	 * @param uconn
	 */
	public static void closeUrlConnection(HttpURLConnection uconn){
		if (uconn != null) {
			uconn.disconnect();
			uconn = null;
		}
	}
}
