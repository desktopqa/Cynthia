package com.sogou.qadev.service.cynthia.bean;

/**
 * @description:TODO
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:23:36
 * @version:v1.0
 */
public class SegmentTagBase {

		public SegmentTagBase()
		{
		}
	
		public void fillBySegmentTagBase(SegmentTagBase segmentTagBase)
		{
			this.indent = segmentTagBase.indent;
			this.indentFieldsName = segmentTagBase.indentFieldsName;
			this.displayFieldsName = segmentTagBase.displayFieldsName;
		}
	
		public int indent = 0;
	
		public String[] displayFieldsName = null;
		public String[] indentFieldsName = null;
}
