/**
 *{{IS_NOTE
 *	Purpose:
 *		
 *	Description:
 *		
 *	History:
 *		Nov 25, 2008, Created by Dennis.Chen
 *		Apr 03, 2011, Modified by Ian YT Tsai
 *}}IS_NOTE
 *
 *Copyright (C) 2007 Potix Corporation. All Rights Reserved.
 *
 *{{IS_RIGHT
 *	This program is distributed under LGPL in the hope that
 *	it will be useful, but WITHOUT ANY WARRANTY.
 *}}IS_RIGHT
 */
package org.zkoss.monitor.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * @author Dennis.Chen, Ian YT Tsai(Zanyking)
 */
public class DOMRetriever {
	
	private XPathFactory xPathFactory = XPathFactory.newInstance();
	private String xml;
	private LinkedHashMap<String, WeakReference<XPathExpression>> xPathCache = 
		new LinkedHashMap<String, WeakReference<XPathExpression>> ();
	
	private InputSource getSource(){
		return new InputSource(new StringReader(xml));
	}
	private XPathExpression createXPath(String query) throws XPathExpressionException{
		WeakReference<XPathExpression> ref = xPathCache.get(query);
		XPathExpression path = null;
		if(ref!=null){
			path = ref.get();
		}
		if(path!=null){
			return path;
		}
		
		path = xPathFactory.newXPath().compile(query);
		
		xPathCache.put(query,new WeakReference<XPathExpression>(path));
		return path;
	}
	/**
	 * 
	 * @param xml
	 */
	public DOMRetriever(String xml){
		this.xml = xml;
	}
	/**
	 * 
	 * @param is
	 * @throws IOException
	 */
	public DOMRetriever(InputStream is) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Streams.flush(is,bos,1024);
		try {
			xml = new String(bos.toByteArray(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * @param reader
	 */
	public DOMRetriever(Reader reader){
		try {
			StringBuffer sb = new StringBuffer();
			char[] buf = new char[1024];
			int r;
			while((r = reader.read(buf)) !=-1){
				sb.append(buf,0,r);
			}
			xml = sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public static String getTextValue(Node n) {
		if (n == null)
			return ""; //$NON-NLS-1$
		return n.getTextContent();
	}
	
	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static String getAttributeValue(Node node, String name) {
		NamedNodeMap nnm = node.getAttributes();
		if (nnm == null)
			return null;
		Node n = nnm.getNamedItem(name);
		if (n != null)
			return n.getNodeValue();
		return ""; //$NON-NLS-1$
	}
	/**
	 * 
	 * @param node
	 * @return
	 */
	public static Properties getAttributes(Node node) {
		Properties props = new Properties();
		NamedNodeMap nnm = node.getAttributes();
		if (nnm == null)return props;
		
		for(int i=0, index = nnm.getLength(); i<index ;i++){
			Node temp = nnm.item(i);
			props.setProperty(temp.getNodeName(), temp.getNodeValue());
		}
		return props;
	}
	/**
	 * 
	 * @param parent
	 * @param queryStr
	 * @return
	 */
	public String getNodeContentValue(Node parent, String queryStr) {
		try {
			Node node = (Node)createXPath(queryStr).evaluate(parent, XPathConstants.NODE);
			if(node!=null)
				return getTextValue(node);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	/**
	 * Retrieving an {@link NodeList} based on the given XPath Query.
	 * @param node the node
	 * @param xPathQuery
	 * @return a Node List based on given query.
	 */
	public NodeList getNodeList(Node node, String xPathQuery) {
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) createXPath(xPathQuery).evaluate(node,XPathConstants.NODESET);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return nodeList;
	}
	/**
	 * 
	 * @param queryStr
	 * @return
	 */
	public String getNodeContentValue(String queryStr) {
		try {
			Node node = (Node)createXPath(queryStr).evaluate(getSource(),XPathConstants.NODE);
			if(node!=null)
				return getTextValue(node);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	/**
	 * 
	 * @param queryStr
	 * @return
	 */
	public NodeList getNodeList(String queryStr) {
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) createXPath(queryStr).evaluate(getSource(),XPathConstants.NODESET);
		}  catch (Exception e) {
			throw new RuntimeException(e);
		}
		return nodeList;
	}



}
