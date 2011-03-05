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
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modules.BanUser;
import modules.Dice;
import modules.GBug;
import modules.Help;
import modules.LastSpoke;
import modules.M8ball;
import modules.Metadata;
import modules.SDate;
import modules.Snack;
import modules.Weather;
import modules.calc;
import modules.gcalc;
import config.Config;
import database.Postgres;

/**
 * @author Kenneth Prugh
 * 
 */
public class snackb0t {
    /*
     * Backend
     */
    protected static IRCore irc;

    private static final Config config = new Config();

    /*
     * Database
     */
    private final Postgres pg = new Postgres(config);
    /*
     * Modules
     */
    private final Snack snackEngine = new Snack(pg);
    private final Weather weatherEngine = new Weather(pg);
    private final GBug bugEngine = new GBug();
    private final M8ball m8Engine = new M8ball();
    private final Help helpEngine = new Help();
    private final Dice diceEngine = new Dice();
    // private final Seen seenEngine = new Seen();
    // private final Links linksEngine = new Links();
    private final BanUser banEngine = new BanUser();
    private final SDate dateEngine = new SDate();
    private final LastSpoke lastSpokeEngine = new LastSpoke(pg);
    private final Metadata metaEngine = new Metadata();
    private final calc calcEngine = new calc();
    protected final IRCCtcp ctcpEngine = new IRCCtcp(config);
    private final MessageParser MessageParserEngine = new MessageParser();

    public static void main(String[] args) {
        snackb0t m = new snackb0t();
        irc = new IRCore(config);

        Thread joinChannelsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* Wait 20 seconds then join channels */
                    Thread.sleep(20000);
                    irc.joinChannel(config.getChannels());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        joinChannelsThread.start();

        final BufferedReader in = irc.getIn();

        while (true) {
            try {
                m.MessageParserEngine.parseInput(m, in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
                /* Fix that logging disaster */
                System.exit(1);
            }
        }
    }

    /*
     * Handle commands that may need to listen passively or to freenode directly
     */
    protected void passiveInput(Message m) {
        final String user = m.user;
        final String channel = m.channel;
        final String msg = m.msg;
        // Scan for response to !seen [hackish]
        // seenEngine.checkSeen(msg, irc);

        // Send to Links scanner
        // linksEngine.scanLinks(msg);

        // LastSpoke
        lastSpokeEngine.logLine(user, "#" + channel, msg);

        if (msg.matches(".*bug [#0-9]+.*")) {
            String bugIDmsg = msg;
            Pattern pattern = Pattern.compile("bug [#0-9]+");

            Matcher matcher = pattern.matcher(bugIDmsg);

            while (matcher.find()) {
                String bugString = matcher.group();
                pattern = Pattern.compile("[0-9]+");
                matcher = pattern.matcher(bugString);

                while (matcher.find()) {
                    String r = bugEngine.getBug(matcher.group());
                    irc.sendMsgTo("#" + channel, user, r);
                }
            }
        }

    }

    // Handle everything else
    protected void parseCommand(Message m) {
        final String user = m.user;
        final String channel = m.channel;
        final String msg = m.msg;

        /**
         * Core Commands and Private commands
         */
        if (m.command.equals("jpartChannel")) {
            irc.partChannel(msg.substring(13).trim());
        }

        if (m.command.equals("jjoinChannel")) {
            irc.joinChannel(msg.substring(13).trim());
        }

        if (m.command.equals("jQuitIRC")) {
            try {
                irc.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (m.command.equals("jAnnihilateSnack")) {
            irc.sendPrivMsgTo(user, snackEngine.deleteSnack());
        }

        if (m.command.equals("jbanUser")) {
            banEngine.banUser(msg.substring(9).trim());
        }

        if (m.command.equals("junbanUser")) {
            banEngine.unbanUser(msg.substring(11).trim());
        }

        /*
         * Module Commands
         */
        // if (msg.equalsIgnoreCase("jtest")) {
        // irc.sendMsgTo("#" + channel, user, Test.hello_world(tmp));
        // }

        if (banEngine.isBanned(user)) {
            return;
        }

        if (m.command.equals("snack")) {
            irc.sendMsgTo("#" + channel, user, snackEngine.getSnack());
        }

        if (m.command.equals("newsnack")) {
            irc.sendMsgTo("#" + channel, user, snackEngine.addSnack(msg));
        }

        if (m.command.equals("weather")) {
            irc.sendMsgTo("#" + channel, user,
                    weatherEngine.getWeather(user, msg));
        }

        if (m.command.equals("forecast")) {
            List<String> results = weatherEngine.getForecast(user, msg);
            for (String cast : results) {
                irc.sendMsgTo("#" + channel, user, cast);
            }
        }

        if (m.command.equals("calc")) {
            irc.sendMsgTo("#" + channel, user,
                    calcEngine.getCalculation(msg, user));
        }


        if (m.command.equals("8ball")) {
            irc.sendMsgTo("#" + channel, user, m8Engine.getAnswer());
        }

        if (m.command.equals("roll")) {
            irc.sendMsgTo("#" + channel, user, diceEngine.rollDie(msg));
        }

        if (m.command.equals("help")) {
            List<String> results = helpEngine.displayHelp();
            for (String text : results) {
                irc.sendPrivMsgTo(user, text);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (m.command.equals("vote")) {
            irc.sendMsgTo("#" + channel, user, snackEngine.voteSnack(msg, user));
        }

        if (m.command.equals("date")) {
            irc.sendMsgTo("#" + channel, user, dateEngine.getDate());
        }

        /* Exploitable!
        if (m.command.equals("meta")) {
            irc.sendMsgTo("#" + channel, user, metaEngine.getMetadata(msg));
        }
        */

        // Ask NickServ when the user was last seen
        if (m.command.equals("seen")) {
            // seenEngine.askSeen(msg, user, channel, irc);
            irc.sendMsgTo("#" + channel, user,
                    lastSpokeEngine.getLastSpoke(msg.substring(5).trim()));
        }

        if (m.command.equals("lastspoke")) {
            irc.sendMsgTo("#" + channel, user,
                    lastSpokeEngine.getLastSpoke(msg.substring(10).trim()));
        }

        if (m.command.equals("convert")) {
            irc.sendMsgTo("#" + channel, user, gcalc.convert(msg));
        }
    }
}
