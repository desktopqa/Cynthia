package com.sogou.qadev.service.cynthia.util;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.statistics.extended.ExtendedStatistics.Statistic;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.sogou.qadev.cache.impl.UserInfoCache;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;

public class CynthiaUtil {

	// 声明图片后缀名数组
	private static Set<String> imageSet = new HashSet<String>();
	static {
		imageSet.add("bmp");
		imageSet.add("djb");
		imageSet.add("gif");
		imageSet.add("jfif");
		imageSet.add("jpe");
		imageSet.add("djb");
		imageSet.add("jpeg");
		imageSet.add("jpg");
		imageSet.add("png");
		imageSet.add("tif");
		imageSet.add("tiff");
		imageSet.add("ico");
	};

	/**
	 * @description:get excel column value
	 * @date:2014-5-6 下午6:36:07
	 * @version:v1.0
	 * @param row
	 * @param num
	 * @return
	 */
	public static String getValue(Row row, int num) {
		if (num < 0)
			return "";
		Cell cell = row.getCell(num);
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == Cell.CELL_TYPE_STRING)
			return cell.getStringCellValue();
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return Integer.toString((int) cell.getNumericCellValue());
		if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
			return "";
		return "";
	}

	/**
	 * @description:return xml string
	 * @date:2014-5-6 下午6:36:30
	 * @version:v1.0
	 * @param str
	 * @return
	 */
	public static String getXMLStr(String str) {
		if (str == null) {
			return "";
		} else {
			str = str.replaceAll("&amp;", "&");
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&gt;", ">");
			str = str.replaceAll("&quot;", "\"");
			return str;
		}
	}

	/**
	 * @description:return today string
	 * @date:2014-5-6 下午6:36:39
	 * @version:v1.0
	 * @return
	 */
	public static String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new java.util.Date());
	}

	/**
	 * @description:cancel order and group info of sql
	 * @date:2014-5-6 下午6:36:50
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public static String cancelGroupOrder(String sql) {
		if (sql.indexOf("group") != -1) {
			sql = sql.substring(0, sql.indexOf("group"));
		}
		if (sql.indexOf("order") != -1) {
			sql = sql.substring(0, sql.indexOf("order"));
		}
		return sql;
	}

	/**
	 * @description:return user nick name
	 * @date:2014-5-6 下午6:37:05
	 * @version:v1.0
	 * @param user
	 * @param das
	 * @return
	 */
	public static String getUserAlias(String user) {
		if (user == null || user.equals("null")) {
			return "";
		}
		String userName = "";
		UserInfo userInfo = UserInfoCache.getInstance().get(user);
		
		if (userInfo != null && userInfo.getNickName() != null && !userInfo.getNickName().equals("")) {
			userName = userInfo.getNickName(); 
		}else {
			if (ConfigManager.getEnableSso()) {
				userName = ProjectInvolveManager.getInstance().getUserName(user);
			}
			
			if (isNull(userName)) {
				if (user.indexOf("@") != -1) {
					userName = user.substring(0,user.indexOf("@"));
				}else {
					userName = user;
				}
			}
		}
		return userName;
	}
	
	/**
	 * @description:check if file is picture
	 * @date:2014-5-6 下午6:37:15
	 * @version:v1.0
	 * @param pInput
	 * @return
	 */
	public static boolean isPicture(String pInput) {
		// 文件名称为空的场合
		if (pInput == null) {
			return false;
		}
		String tmpName = "";
		try { // 获得文件后缀名
			tmpName = pInput.substring(pInput.lastIndexOf(".") + 1,
					pInput.length());
		} catch (Exception e) {
		}
		if (tmpName == "" || tmpName == null) {
			return false;
		}
		return imageSet.contains(tmpName.toLowerCase());
	}

	public static String checkXML(String xml, String encode) throws Exception {
		byte[] byteArray = xml.getBytes(encode);
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] == 0x1a || byteArray[i] == 0x19
					|| byteArray[i] == 0x1 || byteArray[i] == 0x4
					|| byteArray[i] == 0x5) {
				byteArray[i] = 78;
			}
		}

		return new String(byteArray, encode);
	}

	/**
	 * @description:get current ip
	 * @date:2014-5-6 下午6:37:37
	 * @version:v1.0
	 * @return
	 */
	public static String getLocalIp() {
		String ip = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString();// 获得本机IP
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * @description:get week day of timestamp
	 * @date:2014-5-6 下午6:37:46
	 * @version:v1.0
	 * @param timestamp
	 * @return
	 */
	public static int getDayOfWeek(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date(timestamp.getTime());
		cal.setTime(date);
		int dayForWeek = 0;
		dayForWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayForWeek == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = dayForWeek - 1;
		}
		return dayForWeek;
	}

	/**
	 * @description:string to safe json
	 * @date:2014-5-6 下午6:38:00
	 * @version:v1.0
	 * @param s
	 * @return
	 */
	public static String stringToJson(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * @description:
	 * @date:2014-5-6 下午6:38:18
	 * @version:v1.0
	 * @param timestamp
	 * @return
	 */
	public static boolean IsTechStatisticWorkTime(Timestamp timestamp) {
		try {
			int weekDay = getDayOfWeek(timestamp);

			if (weekDay >= 1 && weekDay <= 5) {
				if ((timestamp.getHours() >= 8 && timestamp.getHours() < 22)) {
					return true;
				}
			} else {
				if (((timestamp.getHours() >= 10)) && timestamp.getHours() < 19) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @description:return char is chinese
	 * @date:2014-5-6 下午6:38:27
	 * @version:v1.0
	 * @param c
	 * @return
	 */
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * @description:get if string contain chinese
	 * @date:2014-5-6 下午6:38:40
	 * @version:v1.0
	 * @param strName
	 * @return
	 */
	public static final boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *  @description:get if the id in ids
	 * @date:2014-5-5 下午8:32:56
	 * @version:v1.0
	 * @param ids
	 * @param id
	 * @return
	 */
	public static boolean idInArray(UUID[] ids, String id) {
		if(ids==null||id==null)
			return false;
		for(UUID uid : ids)
		{
			if(uid.toString().equals(id))
				return true;
		}
		return false;
	}

	/**
	 * @description:return uuid array by split idstr by ,
	 * @date:2014-5-5 下午8:34:32
	 * @version:v1.0
	 * @param actions
	 * @return
	 */
	public static UUID[] stringToIdArray(String idStr) {
		if(idStr == null)
			return new UUID[0];
		String[] idStrs = idStr.split(",");
		List<UUID> idsList = new ArrayList<UUID>();
		for(String str : idStrs)
		{
			if(str!=null&&!"".equals(str)&&!"null".equals(str))
				idsList.add(DataAccessFactory.getInstance().createUUID(str));
		}
		return idsList.toArray(new UUID[idsList.size()]);
	}
	
	/**
	 * @description:get file extension
	 * @date:2014-5-6 下午6:39:05
	 * @version:v1.0
	 * @param srcImageFile
	 * @return
	 */
    public static String getExtension(String srcImageFile) {
       String ext = null;
       if(srcImageFile!=null && srcImageFile.lastIndexOf(".")>-1){
           ext = srcImageFile.substring(srcImageFile.lastIndexOf(".")+1);
       }
       return ext;
   }
    
    public static boolean isNull(String str){
    	if(str == null || str.equals("")){
    		return true;
    	}else {
			return false;
		}
    }
    
    /**
     * @description:根据用户邮箱返回用户昵称
     * @date:2014-11-14 上午10:48:17
     * @version:v1.0
     * @param users
     * @return
     */
    public static String getAssignUserAlias(String users){
    	if (users == null || users.equals("")) {
			return "";
		}else{
			List<String> alias = new ArrayList<String>();
			String[] allUsers = users.split(",");
			for(String user : allUsers){
				alias.add(CynthiaUtil.getUserAlias(user));
			}
			return ArrayUtil.strArray2String(alias.toArray(new String[0]));
		}
    }
    
    public static String toLocalDateString(Date date) {
    	if(date == null) {
    		date = new java.util.Date();
    	}
    	SimpleDateFormat datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return datetime.format(date);
    }
}
