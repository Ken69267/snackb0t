/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package modules;

import irc.IRCore;

public class Seen {

	private String requestUser;
	private String seenUser;
	private String channel;

	public void checkSeen(String msg, IRCore irc) {
		if (msg.contains("Last seen")) {
			String response = msg.split("seen")[1].substring(2);
			String rString = seenUser + " last identified on" + response;
			irc.sendMsgTo("#" + channel, requestUser, rString);
		}
	}

	public void askSeen(String message, String user, String channel, IRCore test) {
		test.sendPrivMsgTo("NickServ", "info " + message.substring(5));
		this.requestUser = user;
		this.seenUser = message.substring(5);
		this.channel = channel;
	}
}
