/**
 * Copyright (C) 2009-2011 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package irc;

import config.Config;

public class IRCCtcp {
    private String OS, Identifier, Contact;
    public static final String MARKER = "\001";

    public IRCCtcp() {
        Config config = Config.getInstance();
        this.Identifier = config.getIdent();
        this.OS = config.getOS();
        this.Contact = config.getContact();
    }

    /**
     * Parses the CTCP and gives the appropriate reponse
     *
     * @return the ctcp response
     */
    public String parseCTCP(String msg) {
        String[] ctcp = msg.replaceAll(MARKER, "").split(" ");

        if (ctcp[0].equals("PING")) {
            return msg;
        } else if (ctcp[0].equals("VERSION")) {
            return version();
        } else if (ctcp[0].equals("CLIENTINFO")) {
            return clientInfo(ctcp);
        } else {
            return null;
        }
    }

    private String version() {
        StringBuilder s = new StringBuilder();
        s.append(MARKER);
        s.append("VERSION");
        s.append(" "); s.append(Identifier);
        s.append(" "); s.append(OS);
        s.append(" "); s.append(Contact);
        s.append(MARKER);
        return s.toString();
    }

    private String clientInfo(String[] args) {
        StringBuilder s = new StringBuilder();
        s.append(MARKER);
        s.append(" PING VERSION CLIENTINFO");
        s.append(MARKER);
        return s.toString();
    }
}
