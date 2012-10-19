/**
 * Copyright (C) 2009-2012 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package irc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCUtils {
	/**
	 * @return user that sent this message
	 * 
	 * @param message
	 *            - The message to parse
	 */
	public static String getSender(String message) {
		Pattern pattern = Pattern.compile(":(.*?)!");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	/**
	 * @return user's ident that sent this message
	 * 
	 * @param message
	 *            - The message to parse
	 */
	public static String getIdent(String message) {
		Pattern pattern = Pattern.compile("!~(.*?)@");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	/**
	 * @return user's hostname that sent this message
	 * 
	 * @param message
	 *            - The message to parse
	 */
	public static String getHostname(String message) {
		Pattern pattern = Pattern.compile("@(.*?) PRIVMSG");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	/**
	 * @return - the channel (#foo)
	 */
	public static String getChannel(String message) {
		Pattern pattern = Pattern.compile("PRIVMSG (.+?) :");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	/**
	 * Return message contents from this message
	 * 
	 * @param message
	 *            - The message to parse
	 * @return - The message
	 */
	public static String getMessage(String message) {
		Pattern pattern = Pattern.compile(":(.+? ):(.*)");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return matcher.group(2);
		} else {
			return "";
		}
	}
}
