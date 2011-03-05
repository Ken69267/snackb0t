/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package modules;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Forecast {
	private static DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance();
	private List<String> results = new ArrayList<String>();

	public List<String> getForecast(String location) {
		results.clear();
		try {
			return parseData(location);
		} catch (IOException e) {
			results.add("I threw a wobbly");
			return results;
		}
	}

	private List<String> parseData(String location) throws IOException {
		// Using factory get an instance of document builder
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			results.add("Accidentally massively");
			return results;
		}

		// parse using builder to get DOM representation of the XML file
		Document dom;

		URL wurl;

		try {
			wurl = new URL(
					"http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query="
							+ location);
		} catch (MalformedURLException e) {
			results.add("Accidentally the URL");
			return results;
		}

		URLConnection conn;

		conn = wurl.openConnection();

		conn.setConnectTimeout(15000);
		conn.setReadTimeout(15000);
		InputStream in = conn.getInputStream();

		try {
			dom = db.parse(in);
		} catch (SAXException e) {
			results.add("Parse Error");
			return results;
		} catch (SocketTimeoutException e) {
			results.add("Request timed out");
			return results;
		} finally {
			in.close();
		}

		Element rootDoc = dom.getDocumentElement();

		NodeList fNodes;

		// Forecast nodes
		fNodes = rootDoc.getElementsByTagName("forecastday");

		// 2 instead of fNodes length due to poor formatting
		for (int i = 0; i < 2; i++) {

			// a forecast element
			Element fEL = (Element) fNodes.item(i);

			String ftext = Weather.getValueForTag(fEL, "fcttext");
			String period = Weather.getValueForTag(fEL, "title");
			results.add(period + ": " + ftext);
		}

		return results;
	}
}
