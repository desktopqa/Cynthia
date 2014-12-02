package com.sogou.qadev.service.cynthia.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;


public class FileUtil {

	/**
	 * @description:write content to file
	 * @date:2014-5-6 下午6:40:02
	 * @version:v1.0
	 * @param filePath
	 * @param content
	 * @param isAppend
	 * @throws IOException
	 */
	public static void writeToFile(String filePath, String content,boolean isAppend) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, isAppend)));
			bw.write(content);
			bw.flush();
		} catch (Exception e) {
		} finally {
			if (bw != null) {
				bw.close();
				bw = null;
			}

		}
	}
	
	/**
	 * @Title: readStringFromFile
	 * @Description: 读取文件内容
	 * @param fileName
	 * @return: void
	 */
	public static String readStringFromFile(String fileName){
		File file = new File(fileName);
		if (!file.exists()) {
			return "";
		}
        BufferedReader reader = null;
        StringBuffer fileContent = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
            	fileContent.append(tempString).append("\r\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            
            return fileContent.toString();
        }
	}
	
	
	 /**  
     * 将InputStream转换成String  
     * @param in InputStream  
     * @return String  
     * @throws Exception  
     */  
    public static String inputStreamTOString(InputStream in) throws Exception{  
          
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[1024];  
        int count = -1;  
        while((count = in.read(data,0,data.length)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return new String(outStream.toByteArray(),"UTF-8");  
    }  
    
	/**
	 * @Title: deleteFile
	 * @Description: 删除文件
	 * @param fileName
	 * @return
	 * @return: boolean
	 */
	public static boolean deleteFile(String fileName){
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			return file.delete();
		}
		return true;
	}
}
