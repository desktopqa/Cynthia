package com.sogou.qadev.service.cynthia.util;

import java.util.ArrayList;
import java.util.List;

import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

public class ArrayUtil {
	
	public static Object[] format(Object[] array, Object[] replaceArray) {
		if (array == null)
			return replaceArray;

		return array;
	}
	/**
	 * @description:id array to string 
	 * @date:2014-5-6 下午5:48:16
	 * @version:v1.0
	 * @param idArray
	 * @return
	 */
	public static  String idArray2String(UUID[] idArray)
	{
		if(idArray == null || idArray.length == 0)
			return null;
		
		StringBuffer strb = new StringBuffer();
		for(UUID id : idArray)
		{
			if(strb.length() > 0)
				strb.append(",");
			strb.append(id);
		}
		
		return strb.toString();
	}
	
    public static String[] splitArray(String[] srcArr , int start, int end){
    	List<Object> result = new ArrayList<Object>();
    	if (start < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
    	end = end > srcArr.length ? srcArr.length : end;
    	for (int i = start; i < end ; i ++ ) {
			result.add(srcArr[i]);
		}
    	
    	return result.toArray(new String[0]);
    }
	
	public static String strArray2String(String[] idArray){
		if(idArray == null || idArray.length == 0)
			return "";
		
		StringBuffer strb = new StringBuffer();
		for(String id : idArray)
		{
			if(strb.length() > 0)
				strb.append(",");
			strb.append(id);
		}
		
		return strb.toString();
	}
	
	public static String strArray2String(String[] idArray,String splitFex){
		if(idArray == null || idArray.length == 0)
			return "";
		
		StringBuffer strb = new StringBuffer();
		for(String id : idArray)
		{
			if(strb.length() > 0)
				strb.append(splitFex);
			strb.append(id);
		}
		
		return strb.toString();
	}

	/**
	 * @description:string to uuid array 
	 * @date:2014-5-6 下午5:49:51
	 * @version:v1.0
	 * @param str
	 * @return
	 */
	public static UUID[] string2IdArray(String str)
	{
		if(str == null || str.equals("")||str.equals("*"))
			return null;
		
		String[] idStrArray = str.split("\\,");
		UUID[] idArray = new UUID[idStrArray.length];
		for(int i = 0; i < idArray.length; i++)
			idArray[i] = DataAccessFactory.getInstance().createUUID(idStrArray[i]);
		
		return idArray;
	}
	
}
