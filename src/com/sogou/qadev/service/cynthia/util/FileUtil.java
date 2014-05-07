package com.sogou.qadev.service.cynthia.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
	public static void writeToFile(String filePath, String content,
			boolean isAppend) throws IOException {
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
}
