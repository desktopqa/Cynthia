package com.sogou.qadev.service.cynthia.util;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public abstract class ItemBaseUtil implements Serializable, Cloneable
{
	public static final Logger log = Logger.getLogger(ItemBaseUtil.class.getName());
	
	protected Map privatePropertyMap = null;
	
	static
	{
	    Appender appender = new ConsoleAppender();
	    Layout layout = new SimpleLayout();
	    ((ConsoleAppender)appender).setLayout(layout);
	    ((ConsoleAppender)appender).setWriter(new OutputStreamWriter(System.err));
	    log.addAppender(appender);
	    log.setLevel(Level.ALL);
	}
	
	public Object getPrivateProperty(String name)
	{
	    return this.privatePropertyMap == null ? null : this.privatePropertyMap.get(name);
	}
	
	public String[] getPrivatePropertyName()
	{
	    return this.privatePropertyMap == null ? null : (String[])this.privatePropertyMap.keySet().toArray();
	}
	
	public Class[] getPrivatePropertyType()
	{
	    if (this.privatePropertyMap == null) {
	      return null;
	    }
	    Class[] type = new Class[this.privatePropertyMap.size()];
	    String[] name = getPrivatePropertyName();
	
	    for (int i = 0; i < type.length; i++) {
	      type[i] = this.privatePropertyMap.get(name[i]).getClass();
	    }
	    return type;
	}
	
	public String toString()
	{
	    Method[] method = getClass().getMethods();
	
	    if (method == null) {
	      return null;
	    }
	    boolean first = true;
	
	    StringBuffer buf = new StringBuffer();
	
	    buf.append("[");
	
	    for (int i = 0; i < method.length; i++)
	    {
	      String name = method[i].getName();
	      Class[] pt = method[i].getParameterTypes();
	
	      if (name.equals("getPrivatePropertyName"))
	        continue;
	      if (name.equals("getPrivatePropertyType"))
	        continue;
	      if (name.equals("getClass"))
	        continue;
	      if (name.equals("getKey")) {
	        continue;
	      }
	      if ((!name.startsWith("get")) || (pt.length != 0))
	        continue;
	      if (!first)
	        buf.append(", ");
	      else {
	        first = false;
	      }
	      buf.append(name.substring(3)).append(": ");
	      try
	      {
	        buf.append(method[i].invoke(this, new Object[0]));
	      }
	      catch (Exception e) {
	        e.printStackTrace();
	      }
	
	    }
	
	    buf.append("]");
	
	    return buf.toString();
	}
	
	public static String toSafeSQLString(String string)
	{
	    if (string == null)
	      string = "null";
	    string = string.replaceAll("\\\\", "\\\\\\\\");
	    string = string.replaceAll("'", "\\\\'");
	    String ret = "'" + string + "'";
	    return ret;
	}
	
	public static String toSafeSQLString(Timestamp time)
	{
	    String ret = "'" + time + "'";
	    return ret;
	}
	
	public abstract Object getKey();
	
	public abstract Object clone();
}