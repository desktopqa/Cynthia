package com.sogou.qadev.service.cynthia.bean.impl;

import java.io.Serializable;

import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:uuid implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:07:55
 * @version:v1.0
 */
public final class UUIDImpl implements UUID,Serializable{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午5:08:30
	 */
	private static final long serialVersionUID = 1128474858473603817L;
	
	private int value = 0;
	
	public UUIDImpl(int value){
		this.value = value;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return new UUIDImpl(value);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:equals</p>
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		if(obj == null || !(obj instanceof UUIDImpl)){
			return false;
		}
		
		return Integer.valueOf(value).equals(((UUIDImpl)obj).value);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:hashCode</p>
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return Integer.valueOf(value).hashCode();
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:toString</p>
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return Integer.toString(value);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:compareTo</p>
	 * @param id
	 * @return
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(UUID id){
		if(id == null || !(id instanceof UUIDImpl)){
			throw new RuntimeException();
		}
		
		return Integer.valueOf(value).compareTo(((UUIDImpl)id).value);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getValue</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.UUID#getValue()
	 */
	public String getValue(){
		return Integer.toString(value);
	}
}
