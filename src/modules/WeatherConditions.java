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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WeatherConditions {
	private static DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance();

	public String getWeather(String location) {
		try {
			return parseData(location);
		} catch (IOException e) {
			return "I threw a wobbly";
		}
	}

	private String parseData(String location) throws IOException {
		WeatherData WD = null;

		// Using factory get an instance of document builder
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			return "Accidentally massively";
		}

		// parse using builder to get DOM representation of the XML file
		Document dom;

		URL wurl = new URL(
				"http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query="
						+ location);

		URLConnection conn;

		conn = wurl.openConnection();
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(15000);
		InputStream in = conn.getInputStream();

		try {
			dom = db.parse(in);

		} catch (SAXException e) {
			return "Parse error";
		} catch (SocketTimeoutException e) {
			return "Request timed out";
		} finally {
			in.close();
		}

		Element docEle = dom.getDocumentElement();

		String area, time, temp, condition, humidity, wind, pressure;
		NodeList tmpnl;

		// get the area (2 levels down)
		tmpnl = docEle.getElementsByTagName("display_location");
		Element el = (Element) tmpnl.item(0);
		area = Weather.getValueForTag(el, "full");

		// get rest
		time = Weather.getValueForTag(docEle, "observation_time");
		temp = Weather.getValueForTag(docEle, "temperature_string");
		condition = Weather.getValueForTag(docEle, "weather");
		humidity = Weather.getValueForTag(docEle, "relative_humidity");
		wind = Weather.getValueForTag(docEle, "wind_string");
		pressure = Weather.getValueForTag(docEle, "pressure_string");

		WD = new WeatherData(area, time, temp, condition, humidity, wind,
				pressure);

		return WD.prettyWeatherFormat();
	}
}

class WeatherData {
	private String area, time, temp, condition, humidity, wind, pressure;

	public WeatherData(String area, String time, String temp, String condition,
			String humidity, String wind, String pressure) {
		this.area = area;
		this.time = time;
		this.temp = temp;
		this.condition = condition;
		this.humidity = humidity;
		this.wind = wind;
		this.pressure = pressure;
	}

	public String prettyWeatherFormat() {
		StringBuilder sb = new StringBuilder();

		sb.append(area);
		sb.append(". ");
		sb.append("The Temperature is ");
		sb.append(temp);
		sb.append(" with ");
		sb.append(humidity);
		sb.append(" humidity. ");
		sb.append("The weather is ");
		sb.append(condition);
		sb.append(" with wind ");
		sb.append(wind);
		sb.append("The barometric pressure is ");
		sb.append(pressure);
		sb.append(". ");
		sb.append(time);
		sb.append(".");

		// return area + time + temp + condition + humidity + wind + pressure;
		return sb.toString();
	}
}
