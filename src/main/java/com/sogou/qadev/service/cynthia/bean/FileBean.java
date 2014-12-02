package com.sogou.qadev.service.cynthia.bean;

/**
 * @description:file bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:47:37
 * @version:v1.0
 */
public class FileBean {
	
	/**
	 * file id
	 */
    private String id;
    
    /**
     * file name
     */
    private String filename;
    
    /**
     * file fileId
     */
    private String fileId;
     
    public FileBean(){}
     
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
