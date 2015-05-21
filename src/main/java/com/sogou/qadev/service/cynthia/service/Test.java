/**  
 * Copyright © 2015 sogou. All rights reserved.
 *
 * @Title: Test.java
 * @Prject: Cynthia_Open
 * @Package: com.sogou.qadev.service.cynthia.service
 * @Description: TODO
 * @author: liming  
 * @date: 2015-4-27 下午4:01:30
 * @version: V1.0  
 */
package com.sogou.qadev.service.cynthia.service;

/**
 * @ClassName: Test
 * @Description: TODO
 * @author: liming
 * @date: 2015-4-27 下午4:01:30
 */
public class Test {

	/**
	 * @Title: main
	 * @Description: TODO
	 * @param args
	 * @return: void
	 */
	public static void main(String[] args) {
		System.out.println(DataManager.getInstance().queryUserTemplates("731538084@qq.com").length);
		
	}

}
