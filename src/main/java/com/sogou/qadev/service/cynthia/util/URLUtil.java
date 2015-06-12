package com.sogou.qadev.service.cynthia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lowagie.text.Header;

public class URLUtil {
	
	public static String toSafeURLString(String urlStr) {
		if (urlStr == null)
			return "";

		return urlStr.trim().replaceAll("#", "%23");
	}
	
	/**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param params请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
	 * @throws UnsupportedEncodingException 
     */
    public static String sendGet(String url, String params,String cookies) {
        String result = "";
        String line;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        
        try {
            String urlName = url;
            if (!params.equals("")) {
            	urlName += "?" + params;
			}
            URL realUrl = new URL(urlName);
            // 打开和URL之间的连接
            conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            if (!cookies.equals("")) {
            	conn.setRequestProperty("Cookie", cookies);
			}
            
            // 建立实际的连接
            conn.connect();
 
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
           
            while ((line = in.readLine()) != null) {
            	result += (result.length() > 0 ? "\n" : "") + line;
            }
        } catch (Exception e) {
        	try {
				in = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
				while ((line = in.readLine()) != null) {
					result += (result.length() > 0 ? "\n" : "") + line;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
            System.out.println("error in sendGet url :"+ url);
        }finally {
            try {
            	if (conn != null) {
					conn.disconnect();
				}
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
       
        return result;
    }
    
    /**
     * 获取响应cookie
     */
    public static List<String> getResponseCookie(String url, String params,String cookies) {
        HttpURLConnection conn = null;
        List<String> cookieList = null;
        try {
            String urlName = url;
            if (!params.equals("")) {
            	urlName += "?" + params;
			}
            URL realUrl = new URL(urlName);
            // 打开和URL之间的连接
            conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            if (!cookies.equals("")) {
            	conn.setRequestProperty("Cookie", cookies);
			}
            
            // 建立实际的连接
            conn.connect();
            cookieList = conn.getHeaderFields().get("set-cookie");
        } catch (Exception e) {
        }finally {
            if (conn != null) {
				conn.disconnect();
			}
        }
       
        return cookieList;
    }
    
    /**
     * 向指定URL发送POST方法的请求
     * @param url 发送请求的URL
     * @param params 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     */
    public static String sendPost(String url, String params , String cookies) {
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        String line;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", "application/json");  
            
            if (!cookies.equals("")) {
            	conn.setRequestProperty("Cookie", cookies);
			}
            
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(params);
            // flush输出流的缓冲
            out.flush();
 
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
        	try {
				in = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
				while ((line = in.readLine()) != null) {
					result += (result.length() > 0 ? "\n" : "") + line;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
        	System.out.println("error in sendPost url :"+ url + " result :" + result);
        }finally {
            try {
            	if (conn != null) {
					conn.disconnect();
				}
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
}
