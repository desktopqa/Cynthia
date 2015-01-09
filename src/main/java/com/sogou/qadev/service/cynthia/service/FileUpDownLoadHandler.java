package com.sogou.qadev.service.cynthia.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class FileUpDownLoadHandler {

	public static final String uploadURL = ConfigManager.getFileSystemProperties().getProperty("fdfs.upload.url");
	public static final String downloadURL = ConfigManager.getFileSystemProperties().getProperty("fdfs.download.url");

	/**
	 * 上传文件到分布式系统  返回文件fileId 可以根据fileId组装成下载链接
	 * @param fileName ：文件名
	 * @param fileData ：文件数据
	 * @return ：文件在分布式上ID
	 */
	public static String postFile(String fileName,byte[] fileData){

		String res = "";
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		OutputStream out = null;

		String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
		try {
			URL url = new URL(uploadURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(300000000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17 SE 2.X MetaSr 1.0");
			conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);

			out = new DataOutputStream(conn.getOutputStream());

			// file
			String	contentType = "application/octet-stream";
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
			strBuf.append("Content-Disposition: form-data; name=\""+ encode(fileName) + "\"; filename=\"" + encode(fileName)+ "\"\r\n");
			strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
			out.write(strBuf.toString().getBytes());
			out.write(fileData);

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// 读取返回数据
			StringBuffer sb = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			res = sb.toString();

		} catch (Exception e) {
			String line = "";
			try {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
				while ((line = reader.readLine()) != null) {
					line += (line.length() > 0 ? "\n" : "") + line;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			System.out.println("发送POST请求出错。" + line);
		} finally {
			StreamCloser.closeUrlConnection(conn);
			StreamCloser.closeReader(reader);
			StreamCloser.closeOutputStream(out);
		}
		return getFileIdFromJson(res);
	}

	/**
	 * 通过服务器返回JSON取得文件fileId
	 * @param json
	 * @return :返回分布式上fileId
	 */
	public static String getFileIdFromJson(String json){
		try {
			JSONObject jsonObject = JSONObject.fromObject(json);
			Object fileId = jsonObject.get("fileId");
			return fileId.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	 // 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
    private static String encode(String value) throws Exception{
        return java.net.URLEncoder.encode(value, "UTF-8");
    }

	/**
	 * 根据fileId 下载文件
	 * @param fileId ：文件在分布式上存储的fileId
	 * @return
	 */
	public static byte[] downloadData(String fileId){
		if(fileId == null || fileId.length()==0 ){
			return new byte[0];
		}
		String destUrl =downloadURL+fileId;  //构建下载路径

	    BufferedInputStream bis = null;
	    FileOutputStream fos = null;
	    FileInputStream fis = null;
	    File tempFile = null;

	    HttpURLConnection httpUrl = null;
	    URL url = null;
	    byte[] buf = new byte[1024];
	    int size = 0;
	    byte[] returnBytes = null;
	    //建立链接
	    try {
			url = new URL(destUrl);

			httpUrl = (HttpURLConnection) url.openConnection();
			
			//连接指定的资源
			httpUrl.connect();
			//获取网络输入流
			bis = new BufferedInputStream(httpUrl.getInputStream());
			//创建临时文件，由于FileInputStream.avaiable受网络阻塞原因，得到的大小可能不靠谱
			tempFile = File.createTempFile("temp", ".tmp");
			fos = new FileOutputStream(tempFile);

			while ((size = bis.read(buf, 0, buf.length)) != -1){
				fos.write(buf, 0, size);
			}

			returnBytes = new byte[(int)tempFile.length()];
		    fis = new FileInputStream(tempFile);
			fis.read(returnBytes);

		}catch (IOException ie) {
			ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			tempFile.delete();
			StreamCloser.closeUrlConnection(httpUrl);
			StreamCloser.closeInputStream(bis);
			StreamCloser.closeInputStream(fis);
			StreamCloser.closeOutputStream(fos);
		}
		if(returnBytes == null){
			returnBytes = new byte[0];
		}
		return returnBytes;
	}

	/**
	 * @description:保存jfreechart 数据为图片并上传至分布式文件系统,返回文件地址
	 * @date:2014-5-30 下午12:03:31
	 * @version:v1.0
	 * @param chart
	 * @param showName：chart name
	 * @param width
	 * @param height
	 * @return
	 */
	public static String saveChartAsFile(JFreeChart chart, String showName,int width,int height){
		FileInputStream fin = null;
		String fileId = "";
		try {
			//创建临时文件
			File tempFile = File.createTempFile(System.currentTimeMillis() + showName , "png");
			ChartUtilities.saveChartAsPNG(tempFile, chart, width, height);
			//图片存储到图片服务器
			fin = new FileInputStream(tempFile);
			byte[] data = new byte[fin.available()];
			fin.read(data);
			fin.close();
			//图片上传到分布式文件系统
			fileId = FileUpDownLoadHandler.postFile("cynthia" + showName + System.currentTimeMillis(), data);
			tempFile.delete();  //删除临时文件
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			StreamCloser.closeInputStream(fin);
		}
		
		return downloadURL + fileId;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			String filepath="D:\\工作\\代码\\linux\\tomcat_service.txt";

	        Map<String, String> fileMap = new HashMap<String, String>();

	        fileMap.put("userfile", filepath);

	        File file = new File(filepath);
	        byte[] bytes = new byte[(int)file.length()];
			try {
				FileInputStream fis = new FileInputStream(file);
				fis.read(bytes);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String ret = postFile(file.getName(),bytes);
			System.out.println(ret);
	}

}
