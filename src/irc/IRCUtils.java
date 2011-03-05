/**
 * Copyright (C) 2009-2010 Kenneth Prugh
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
	// :Ken69267!n=Ken69267@gentoo/developer/ken69267 PRIVMSG #gentoo-pr0n :ZOMG
	// :biohazrd!n=biohazrd@c-71-195-210-252.hsd1.ut.comcast.net PRIVMSG
	// #gentoo-pr0n :so

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
	 * Return channel that sent this message
	 * 
	 * @param message
	 *            - The message to parse
	 * @return - the channel (without #)
	 */
	public static String getChannel(String message) {
		Pattern pattern = Pattern.compile("#([\\w-]+)");
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

	//public static void main(String[] args) {
	//	String moo =
	//		":ed-|211!n=eplumlee@ip197-140.chouteautel.com PRIVMSG #gentoo-pr0n :!newsnack Ken69267: I just took a massive dum";
	//	String moo1 = ":NetHawk!~nethawk@2002:5d67:1af8:1234:21f:e2ff:febc:9dd5 PRIVMSG #gentoo-pr0n :it kind of sucks to be ignored like that";
	//	System.out.println(getChannel(moo));
	//	System.out.println(getSender(moo));
	//	System.out.println(getMessage(moo));
	//}
}
