/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package irc;

import modules.Auth;

public class Message {
    public final String msg;
    public final String user;
    public final String ident;
    public final String host;
    public final String channel;
    public final String command;
    private final boolean bAdmin;
    private final boolean bcommand;

    public Message(String input) {
        user = IRCUtils.getSender(input);
        channel = IRCUtils.getChannel(input);
        msg = IRCUtils.getMessage(input);
        ident = IRCUtils.getIdent(input);
        host = IRCUtils.getHostname(input);

        if (msg.startsWith("!")) {
            // possibly a command
            bcommand = true;
            command = msg.split(" ")[0].substring(1);
        } else {
            bcommand = false;
            command = null;
        }
        bAdmin = Auth.isAuthenticated(ident, host);
    }

    public boolean isCommand() {
        return bcommand;
    }

    public String toString() {
        return "MSGOBJ: USER:" + user + " CHAN:" + channel + " MSG:" + msg;
    }

    public boolean isAdmin() {
        return bAdmin;
    }
}
