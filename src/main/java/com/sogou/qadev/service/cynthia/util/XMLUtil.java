/**
 * 
 */
package com.sogou.qadev.service.cynthia.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @className:XmlUtil
 * @description:TODO
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-11-11 下午4:07:44
 * @version:v1.0
 */
public class XMLUtil {

	/**
	 * @description:transfer document to string 
	 * @date:2014-11-11 下午4:09:14
	 * @version:v1.0
	 * @param document
	 * @param encode
	 * @return
	 * @throws TransformerException
	 */
	public static String document2String(Document document, String encode) throws TransformerException
	{
	    String xml = null;
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    DOMSource source = new DOMSource(document);
	    transformer.setOutputProperty("encoding", encode);
	    transformer.setOutputProperty("indent", "yes");
	    StringWriter sw = new StringWriter();
	    transformer.transform(source, new StreamResult(sw));
	    xml = sw.toString();
	    return xml;
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:11:04
	 * @version:v1.0
	 * @param document
	 * @param encode
	 * @param file
	 * @throws TransformerException
	 */
	public static void document2File(Document document, String encode, File file) throws TransformerException
	{
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    DOMSource source = new DOMSource(document);
	    transformer.setOutputProperty("encoding", encode);
	    transformer.setOutputProperty("indent", "yes");
	
	    transformer.transform(source, new StreamResult(file));
	}

	/**
	 * 
	 * @description:TODO
	 * @date:2014-11-11 下午4:11:25
	 * @version:v1.0
	 * @param document
	 * @param encode
	 * @param os
	 * @throws TransformerException
	 */
	public static void document2OutputStream(Document document, String encode, OutputStream os) throws TransformerException
	{
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    DOMSource source = new DOMSource(document);
	    transformer.setOutputProperty("encoding", encode);
	    transformer.setOutputProperty("indent", "yes");

	    transformer.transform(source, new StreamResult(os));
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:11:38
	 * @version:v1.0
	 * @param document
	 * @param encode
	 * @return
	 * @throws TransformerException
	 */
	public static byte[] document2Bytes(Document document, String encode) throws TransformerException
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    document2OutputStream(document, encode, baos);
	    return baos.toByteArray();
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:12:20
	 * @version:v1.0
	 * @param xml
	 * @param encode
	 * @return
	 * @throws ParserConfigurationException
	 * @throws UnsupportedEncodingException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document string2Document(String xml, String encode) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException
	{
	    Document document = null;

	    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = builderFactory.newDocumentBuilder();
	    try {
	    	document = builder.parse(new ByteArrayInputStream(xml.getBytes(encode)));
		} catch (Exception e) {
		}

	    return document;
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:12:43
	 * @version:v1.0
	 * @param is
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document inputStream2Document(InputStream is) throws ParserConfigurationException, SAXException, IOException
	{
	    Document document = null;

	    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = builderFactory.newDocumentBuilder();
	    document = builder.parse(is);

	    return document;
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:12:51
	 * @version:v1.0
	 * @param filename
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document file2Document(String filename) throws ParserConfigurationException, SAXException, IOException
	{
	    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	    if (is == null)
	    {
	      is = ClassLoader.getSystemResourceAsStream(filename);
	    }

	    if (is == null) {
	      return null;
	    }
	    return inputStream2Document(is);
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:13:32
	 * @version:v1.0
	 * @param node
	 * @param path
	 * @return
	 */
	public static Node getSingleNode(Node node, String path)
	{
	    List nodeList = getNodes(node, path);

	    if (nodeList.size() > 0) {
	      return (Node)nodeList.get(0);
	    }
	    return null;
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:14:00
	 * @version:v1.0
	 * @param node
	 * @param path
	 * @return
	 */
	public static String getSingleNodeTextContent(Node node, String path)
	{
	    List nodeList = getNodes(node, path);

	    if ((nodeList.size() > 0) && (nodeList.get(0) != null) && (((Node)nodeList.get(0)).getTextContent().length() > 0)) {
	      return ((Node)nodeList.get(0)).getTextContent();
	    }
	    return null;
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:14:06
	 * @version:v1.0
	 * @param node
	 * @param path
	 * @return
	 */
	public static List<Node> getNodes(Node node, String path)
	{
	    ArrayList nodeList = new ArrayList();

	    ArrayList pathList = new ArrayList();
	    String[] pathArray = path.split("/");
	    for (int i = 0; i < pathArray.length; i++)
	    {
	      if (pathArray[i].equals(""))
	        continue;
	      pathList.add(pathArray[i]);
	    }

	    for (int i = 0; i < pathList.size(); i++)
	    {
	      StringBuffer restPath = new StringBuffer();
	      for (int k = i + 1; k < pathList.size(); k++)
	      {
	        restPath.append("/").append((String)pathList.get(k));
	      }

	      for (int j = 0; j < node.getChildNodes().getLength(); j++)
	      {
	        if (!node.getChildNodes().item(j).getNodeName().equals(pathList.get(i)))
	          continue;
	        if (restPath.length() == 0)
	        {
	          nodeList.add(node.getChildNodes().item(j));
	        }
	        else
	        {
	          nodeList.addAll(getNodes(node.getChildNodes().item(j), restPath.toString()));
	        }
	      }

	    }

	    return nodeList;
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:14:20
	 * @version:v1.0
	 * @param node
	 * @param attributeName
	 * @return
	 */
	public static String getAttribute(Node node, String attributeName)
	{
	    String attributeValue = null;

	    if (node.getAttributes().getNamedItem(attributeName) != null)
	    {
	      attributeValue = node.getAttributes().getNamedItem(attributeName).getNodeValue();
	    }

	    return attributeValue;
	}

	/**
	 * 
	 * @description:TODO
	 * @date:2014-11-11 下午4:14:30
	 * @version:v1.0
	 * @param node
	 * @param key
	 * @param value
	 */
	public static void setAttribute(Node node, String key, String value)
	{
	    Node attributeNode = node.getOwnerDocument().createAttribute(key);
	    attributeNode.setNodeValue(value);

	    node.getAttributes().setNamedItem(attributeNode);
	}

	public static void removeAll(Node node)
	{
	    while (node.getChildNodes().getLength() > 0)
	    {
	      node.removeChild(node.getFirstChild());
	    }
	}

	/**
	 * @description:TODO
	 * @date:2014-11-11 下午4:14:57
	 * @version:v1.0
	 * @param str
	 * @return
	 */
	public static String toSafeXMLString(String str)
	{
	    StringBuffer safeXMLStr = new StringBuffer();

	    for (int i = 0; (str != null) && (i < str.length()); i++)
	    {
	      char ch = str.charAt(i);

	      if (ch == '<')
	        safeXMLStr.append("&lt;");
	      else if (ch == '>')
	        safeXMLStr.append("&gt;");
	      else if (ch == '&')
	        safeXMLStr.append("&amp;");
	      else if (ch == '"')
	        safeXMLStr.append("&quot;");
	      else {
	        safeXMLStr.append(ch);
	      }
	    }
	    return safeXMLStr.toString();
	}
}
