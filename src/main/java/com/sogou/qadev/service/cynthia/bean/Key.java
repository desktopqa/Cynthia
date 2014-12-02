/**
 * 
 */
package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import javax.xml.namespace.QName;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class Key
  implements Serializable
{
  private int[] authStatus;
  private DataAndEventId[] dataAndEventId;
  private String keyIDParamName;
  private String loginUrlPrefix;
  private int maxRetryTimes;
  private long productID;
  private int retryTimes;
  private String targetUrl;
  private String userDomain;
  private long userID;
  private String username;
  private Object __equalsCalc = null;

  private boolean __hashCodeCalc = false;

  private static TypeDesc typeDesc = new TypeDesc(Key.class, true);

  static {
    typeDesc.setXmlType(new QName("http://bean.login.service.qadev.sogou.com", "Key"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("authStatus");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "authStatus"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(true);
    elemField.setItemQName(new QName("http://core.login.service.qadev.sogou.com", "item"));
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("dataAndEventId");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "dataAndEventId"));
    elemField.setXmlType(new QName("http://bean.login.service.qadev.sogou.com", "DataAndEventId"));
    elemField.setNillable(true);
    elemField.setItemQName(new QName("http://core.login.service.qadev.sogou.com", "item"));
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("keyIDParamName");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "keyIDParamName"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("loginUrlPrefix");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "loginUrlPrefix"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("maxRetryTimes");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "maxRetryTimes"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("productID");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "productID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("retryTimes");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "retryTimes"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("targetUrl");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "targetUrl"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("userDomain");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "userDomain"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("userID");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "userID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("username");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "username"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
  }

  public Key()
  {
  }

  public Key(int[] authStatus, DataAndEventId[] dataAndEventId, String keyIDParamName, String loginUrlPrefix, int maxRetryTimes, long productID, int retryTimes, String targetUrl, String userDomain, long userID, String username)
  {
    this.authStatus = authStatus;
    this.dataAndEventId = dataAndEventId;
    this.keyIDParamName = keyIDParamName;
    this.loginUrlPrefix = loginUrlPrefix;
    this.maxRetryTimes = maxRetryTimes;
    this.productID = productID;
    this.retryTimes = retryTimes;
    this.targetUrl = targetUrl;
    this.userDomain = userDomain;
    this.userID = userID;
    this.username = username;
  }

  public int[] getAuthStatus()
  {
    return this.authStatus;
  }

  public void setAuthStatus(int[] authStatus)
  {
    this.authStatus = authStatus;
  }

  public DataAndEventId[] getDataAndEventId()
  {
    return this.dataAndEventId;
  }

  public void setDataAndEventId(DataAndEventId[] dataAndEventId)
  {
    this.dataAndEventId = dataAndEventId;
  }

  public String getKeyIDParamName()
  {
    return this.keyIDParamName;
  }

  public void setKeyIDParamName(String keyIDParamName)
  {
    this.keyIDParamName = keyIDParamName;
  }

  public String getLoginUrlPrefix()
  {
    return this.loginUrlPrefix;
  }

  public void setLoginUrlPrefix(String loginUrlPrefix)
  {
    this.loginUrlPrefix = loginUrlPrefix;
  }

  public int getMaxRetryTimes()
  {
    return this.maxRetryTimes;
  }

  public void setMaxRetryTimes(int maxRetryTimes)
  {
    this.maxRetryTimes = maxRetryTimes;
  }

  public long getProductID()
  {
    return this.productID;
  }

  public void setProductID(long productID)
  {
    this.productID = productID;
  }

  public int getRetryTimes()
  {
    return this.retryTimes;
  }

  public void setRetryTimes(int retryTimes)
  {
    this.retryTimes = retryTimes;
  }

  public String getTargetUrl()
  {
    return this.targetUrl;
  }

  public void setTargetUrl(String targetUrl)
  {
    this.targetUrl = targetUrl;
  }

  public String getUserDomain()
  {
    return this.userDomain;
  }

  public void setUserDomain(String userDomain)
  {
    this.userDomain = userDomain;
  }

  public long getUserID()
  {
    return this.userID;
  }

  public void setUserID(long userID)
  {
    this.userID = userID;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public synchronized boolean equals(Object obj)
  {
    if (!(obj instanceof Key)) return false;
    Key other = (Key)obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (this.__equalsCalc != null) {
      return this.__equalsCalc == obj;
    }
    this.__equalsCalc = obj;

    boolean _equals = 
      ((this.authStatus == null) && (other.getAuthStatus() == null)) || (
      (this.authStatus != null) && 
      (Arrays.equals(this.authStatus, other.getAuthStatus())) && (
      ((this.dataAndEventId == null) && (other.getDataAndEventId() == null)) || (
      (this.dataAndEventId != null) && 
      (Arrays.equals(this.dataAndEventId, other.getDataAndEventId())) && (
      ((this.keyIDParamName == null) && (other.getKeyIDParamName() == null)) || (
      (this.keyIDParamName != null) && 
      (this.keyIDParamName.equals(other.getKeyIDParamName())) && (
      ((this.loginUrlPrefix == null) && (other.getLoginUrlPrefix() == null)) || (
      (this.loginUrlPrefix != null) && 
      (this.loginUrlPrefix.equals(other.getLoginUrlPrefix())) && 
      (this.maxRetryTimes == other.getMaxRetryTimes()) && 
      (this.productID == other.getProductID()) && 
      (this.retryTimes == other.getRetryTimes()) && (
      ((this.targetUrl == null) && (other.getTargetUrl() == null)) || (
      (this.targetUrl != null) && 
      (this.targetUrl.equals(other.getTargetUrl())) && (
      ((this.userDomain == null) && (other.getUserDomain() == null)) || (
      (this.userDomain != null) && 
      (this.userDomain.equals(other.getUserDomain())) && 
      (this.userID == other.getUserID()) && (
      ((this.username == null) && (other.getUsername() == null)) || (
      (this.username != null) && 
      (this.username.equals(other.getUsername())))))))))))))));
    this.__equalsCalc = null;
    return _equals;
  }

  public synchronized int hashCode()
  {
    if (this.__hashCodeCalc) {
      return 0;
    }
    this.__hashCodeCalc = true;
    int _hashCode = 1;
    if (getAuthStatus() != null) {
      int i = 0;
      while (i < Array.getLength(getAuthStatus()))
      {
        Object obj = Array.get(getAuthStatus(), i);
        if ((obj != null) && 
          (!obj.getClass().isArray()))
          _hashCode += obj.hashCode();
        i++;
      }

    }

    if (getDataAndEventId() != null) {
      int i = 0;
      while (i < Array.getLength(getDataAndEventId()))
      {
        Object obj = Array.get(getDataAndEventId(), i);
        if ((obj != null) && 
          (!obj.getClass().isArray()))
          _hashCode += obj.hashCode();
        i++;
      }

    }

    if (getKeyIDParamName() != null) {
      _hashCode += getKeyIDParamName().hashCode();
    }
    if (getLoginUrlPrefix() != null) {
      _hashCode += getLoginUrlPrefix().hashCode();
    }
    _hashCode += getMaxRetryTimes();
    _hashCode += new Long(getProductID()).hashCode();
    _hashCode += getRetryTimes();
    if (getTargetUrl() != null) {
      _hashCode += getTargetUrl().hashCode();
    }
    if (getUserDomain() != null) {
      _hashCode += getUserDomain().hashCode();
    }
    _hashCode += new Long(getUserID()).hashCode();
    if (getUsername() != null) {
      _hashCode += getUsername().hashCode();
    }
    this.__hashCodeCalc = false;
    return _hashCode;
  }

  public static TypeDesc getTypeDesc()
  {
    return typeDesc;
  }

  public static Serializer getSerializer(String mechType, Class _javaType, QName _xmlType)
  {
    return 
      new BeanSerializer(
      _javaType, _xmlType, typeDesc);
  }

  public static Deserializer getDeserializer(String mechType, Class _javaType, QName _xmlType)
  {
    return 
      new BeanDeserializer(
      _javaType, _xmlType, typeDesc);
  }
}