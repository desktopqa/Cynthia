/**
 * 
 */
package com.sogou.qadev.service.cynthia.bean;
import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class DataAndEventId
  implements Serializable
{
  private long dataId;
  private long eventId;
  private Object __equalsCalc = null;

  private boolean __hashCodeCalc = false;

  private static TypeDesc typeDesc = new TypeDesc(DataAndEventId.class, true);

  static {
    typeDesc.setXmlType(new QName("http://bean.login.service.qadev.sogou.com", "DataAndEventId"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("dataId");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "dataId"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("eventId");
    elemField.setXmlName(new QName("http://bean.login.service.qadev.sogou.com", "eventId"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }

  public DataAndEventId()
  {
  }

  public DataAndEventId(long dataId, long eventId)
  {
    this.dataId = dataId;
    this.eventId = eventId;
  }

  public long getDataId()
  {
    return this.dataId;
  }

  public void setDataId(long dataId)
  {
    this.dataId = dataId;
  }

  public long getEventId()
  {
    return this.eventId;
  }

  public void setEventId(long eventId)
  {
    this.eventId = eventId;
  }

  public synchronized boolean equals(Object obj)
  {
    if (!(obj instanceof DataAndEventId)) return false;
    DataAndEventId other = (DataAndEventId)obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (this.__equalsCalc != null) {
      return this.__equalsCalc == obj;
    }
    this.__equalsCalc = obj;

    boolean _equals = 
      (this.dataId == other.getDataId()) && 
      (this.eventId == other.getEventId());
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
    _hashCode += new Long(getDataId()).hashCode();
    _hashCode += new Long(getEventId()).hashCode();
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