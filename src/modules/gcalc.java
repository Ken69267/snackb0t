/**
 * Copyright (C) 2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class gcalc {

	private static String gcalculuate(String input) {

		input = input.replaceAll("\\+", " plus ").replaceAll(" ", "%20");
		URL google;

		try { 
			 google = new URL("http://www.google.com/ig/calculator?hl=en&q=" + input);
		} catch (MalformedURLException e) {
			return "I threw a wobbly :(";
		}

		String result;
		String answer;

		try {
			URLConnection conn = google.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);

			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			result = r.readLine();

			Pattern pattern = Pattern.compile("\".*?\"");

			Matcher matcher = pattern.matcher(result);

			matcher.find();
			matcher.group();
			matcher.find();
			answer = matcher.group().replaceAll("\"", "");
			if (answer.matches(".*e.*")) {
				answer = answer.replaceAll("([^0-9e\\.-])(\\\\x26#215; 10\\\\x3csup\\\\x3)", "");
				answer = answer.replaceAll("([^0-9e\\.-]+)(.*)", "");
			} else {
				answer = answer.replaceAll("[^0-9\\.-]", "");
			}

			if (answer.equals("")) {
				return "I threw a wobbly :(";
			}

		} catch (SocketTimeoutException e) {
			return "Request timed out";
		} catch (IOException e) {
			return "Connection Error";
		}

		return answer;
	}

	public static String convert(String input) {
		input = input.substring(8).trim();	
		return gcalculuate(input);
	}

	public static String calc(String input) {
		input = input.substring(5).trim();
		return gcalculuate(input);
	}
}

