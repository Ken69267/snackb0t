/**
 * Copyright (C) 2009 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package modules;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FML {
	private final DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance();

	// public static void main(String[] args) {
	// FML f = new FML();
	// String result = f.getFML();
	// System.out.println(result);
	// }

	public String getFML() {
		// Using factory get an instance of document builder
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom;
			URL fml = new URL(
					"http://api.betacie.com/view/random&key=readonly&language=en");

			URLConnection conn = fml.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);

			dom = db.parse(conn.getInputStream());

			Element docEle = dom.getDocumentElement();

			NodeList fNodes;

			// get the FML text
			fNodes = docEle.getElementsByTagName("items");

			Element fEL = (Element) fNodes.item(0);

			return getValueForTag(fEL, "text");

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "URL malformed";
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return "Parser failure";
		} catch (IOException e) {
			e.printStackTrace();
			return "Connection failure";
		} catch (SAXException e) {
			e.printStackTrace();
			return "Parse error";
		}
	}

	/**
	 * TODO This should go in its own class some day
	 * 
	 * @param docEle
	 * @param tag
	 */
	private static String getValueForTag(Element docEle, String tag) {
		String tagValue;
		try {
			NodeList nl = docEle.getElementsByTagName(tag);

			Element el = (Element) nl.item(0);
			tagValue = el.getFirstChild().getNodeValue();
		} catch (Exception e) {
			tagValue = "unknown";
		}
		return tagValue;
	}

}
