/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import config.Config;

public class IRCore {
	/* Instance Vars */
	private final String network;
	private final int port;
	private final String ident;
    private final String password;
	private Socket s;
	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * Create IRC object and connects to network
	 * 
	 * @param network
	 *            - irc network to connect to
	 * @param port
	 *            - irc port
	 * @param ident
	 *            - irc username/identity
	 */
	public IRCore(Config config) {
		this.network = config.getNetwork();
		this.port = Integer.parseInt(config.getPort());
		this.ident = config.getIdent();
        this.password = config.getIdentpassword();
		s = connect();
	}

	/**
	 * Create socket and connect to objects network/port
	 */
	private Socket connect() {
		try {
			s = new Socket(network, port);
			s.setKeepAlive(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			setIn(new BufferedReader(new InputStreamReader(s.getInputStream())));
			out = new BufferedWriter(
					new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		sendMsgQueitly("NICK " + ident);
		sendMsgQueitly("USER " + ident + " " + network + " bla :" + ident);
		sendMsgQueitly("PRIVMSG NickServ :identify " + password);

		return s;
	}

	/**
	 * Closes this network socket and input/output steams
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		sendMsg("QUIT :System Hault");
		s.close();
		System.exit(0);
	}

	/**
	 * Private method to send irc messages to the socket
	 * 
	 * Adds '\r\n' to the end of messages.
	 * 
	 * @param msg
	 *            - The msg to be sent
	 */
	private void sendMsg(String msg) {
		msg = msg + "\r\n";
        System.out.println("Message: " + msg);
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private method to send irc messages to the socket
	 * 
	 * Adds '\r\n' to the end of messages.
     *
     * Not printed to console
	 * 
	 * @param msg
	 *            - The msg to be sent
	 */
	private void sendMsgQueitly(String msg) {
		msg = msg + "\r\n";
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send a notice to the specified user
	 */
	public void sendNotice(String user, String msg) {
		String tmpMsg = "NOTICE " + user + " :" + msg;
		sendMsg(tmpMsg);
	}

	/**
	 * Send a message to the specified channel and user.
	 * 
	 * @param channel
	 *            - The #channel the message is sent to
	 * @param user
	 *            - The user who should receive the message
	 * @param msg
	 *            - The contents of the msg to be sent
	 */
	public void sendMsgTo(String channel, String user, String msg) {
		String tmpMsg = "PRIVMSG " + channel + " :" + user + ": " + msg;
		sendMsg(tmpMsg);
	}

	/**
	 * Send a query/private message to user
	 * 
	 * @param user
	 *            - The user to query
	 * @param msg
	 *            - The query message
	 */
	public void sendPrivMsgTo(String user, String msg) {
		String tmpMsg = "PRIVMSG " + user + " :" + msg;
		sendMsg(tmpMsg);
	}

    /**
     * Send message to channel
     */
    public void sendMsgToChan(String channel, String msg) {
        sendMsg("PRIVMSG " + channel + " :" + msg);
    }

	/**
	 * Join specified channel on this network
	 * 
	 * @param chan
	 *            - Channel to join (#example)
	 */
	public void joinChannel(String chan) {
		String tmp = "JOIN " + chan;
		sendMsg(tmp);
	}

	/**
	 * Part specified channel on this network
	 * 
	 * @param chan
	 *            - Channel to part (#example)
	 */
	public void partChannel(String chan) {
		String tmp = "PART " + chan;
		sendMsg(tmp);
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public BufferedReader getIn() {
		return in;
	}

    /**
     * PONG the specified server
     */
    public void doPong(String server) {
        sendMsg("PONG " + server);
    }
}
