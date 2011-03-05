/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package irc;

public class Message {
    public final String msg;
    public final String user;
    public final String channel;
    public final String command;
    private boolean bcommand;

    public Message(String input) {
        user = IRCUtils.getSender(input);
        channel = IRCUtils.getChannel(input);
        msg = IRCUtils.getMessage(input);

        if (msg.startsWith("!")) {
            // possibly a command
            bcommand = true;
            command = msg.split(" ")[0].substring(1);
        } else {
            bcommand = false;
            command = null;
        }
    }

    public boolean isCommand() {
        return bcommand;
    }

    public String toString() {
        return "MSGOBJ: USER:" + user + " CHAN:" + channel + " MSG:" + msg;
    }
}
