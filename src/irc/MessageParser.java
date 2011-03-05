/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package irc;

public class MessageParser {
    private Message msgobj;
    private String type;
    private String user;
    private String msg;

    protected void parseInput(snackb0t j, String input) {
        System.out.println(input);
        /*
         * Server messages
         */
        if (input.matches("PING.*")) {
            snackb0t.irc.doPong(input.split(":")[1]);
            return;
        }

        /*
         * User Messages
         */
        type = input.split(" ")[1];

        msgobj = new Message(input);
        user = msgobj.user;
        msg = msgobj.msg;

        /*
         * Handle the msgobj
         */
        if (type.equals("PRIVMSG")) {
            /*
             * Handle CTCP {ugly}
             */
			if (msg.startsWith(IRCCtcp.MARKER) && !msg.contains("ACTION")) {
				snackb0t.irc.sendNotice(user, j.ctcpEngine.parseCTCP(msg));
			} else if (msgobj.isCommand()) {
                /*
                 * Its a user command
                 */
                j.parseCommand(msgobj);
            }

            /*
             * Since its a user message, send it through the passive reader
             */
            j.passiveInput(msgobj);
        }
        else {
            System.out.println("UNHANDLED MSG: " + msgobj);
        }
    }
}
