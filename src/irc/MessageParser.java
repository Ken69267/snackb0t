/**
 * Copyright (C) 2009-2012 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package irc;

import java.io.IOException;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modules.Auth;
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
import modules.Faq;
import modules.ChannelFilter;
import modules.Todo;
import database.Postgres;

public class MessageParser {
    private IRCore irc;

    private final Postgres pg = Postgres.getInstance();

    private final IRCCtcp ctcpEngine = new IRCCtcp();

    private final Snack snackEngine = new Snack(pg);
    private final Weather weatherEngine = new Weather(pg);
    private final GBug bugEngine = new GBug();
    private final M8ball m8Engine = new M8ball();
    private final Help helpEngine = new Help();
    private final Dice diceEngine = new Dice();
    private final BanUser banEngine = new BanUser();
    private final SDate dateEngine = new SDate();
    private final LastSpoke lastSpokeEngine = new LastSpoke(pg);
    private final Metadata metaEngine = new Metadata();
    private final calc calcEngine = new calc();
    private final Todo todoEngine = new Todo();

    public MessageParser(IRCore irc) {
        this.irc = irc;
    }

    protected void parseInput(String input) {
        System.out.println(input);
        /*
         * Server messages
         */
        if (input.matches("PING.*")) {
            irc.doPong(input.split(":")[1]);
            return;
        }

        /*
         * User Messages
         */
        String type = input.split(" ")[1];

        Message m = new Message(input);

        /*
         * Handle the msgobj
         */
        if (type.equals("PRIVMSG")) {
            //log the message
            lastSpokeEngine.logLine(m.user, m.channel, m.msg);

            // Ignore banned users from this point on
            if (banEngine.isBanned(m.user)) {
                return;
            }

            /*
             * Handle CTCP {ugly}
             */
			if (m.msg.startsWith(IRCCtcp.MARKER) && !m.msg.contains("ACTION")) {
				irc.sendNotice(m.user, ctcpEngine.parseCTCP(m.msg));
			} else if (m.isCommand()) {
                /*
                 * Its a user command
                 */

                if (m.isAdmin()) {
                    authenticatedCommand(m);
                }

                /* This is where we can enforce per channel command filtering.
                 * However, it's going to be ugly as it will be comparing the
                 * message vs say, !jDisable !snack to disable !snacks
                 */
                if (ChannelFilter.isCommandFiltered(m))
                {
                    // filter message
                }
                else
                {
                    parseCommand(m);
                }
            }

            /*
             * Since its a user message, send it through the passive reader
             */
            passiveCommand(m);
        }
        else {
            System.out.println("UNHANDLED MSG: " + m);
        }
    }

    protected void authenticatedCommand(final Message m) {
        if (m.command.equals("jsay")) {
            irc.sendMsgToChan(m.channel, m.msg.substring(5).trim());
        }

        if (m.command.equals("jpartChannel")) {
            irc.partChannel(m.msg.substring(13).trim());
        }

        if (m.command.equals("jjoinChannel")) {
            irc.joinChannel(m.msg.substring(13).trim());
        }

        if (m.command.equals("jQuitIRC")) {
            try {
                irc.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (m.command.equals("jDeleteSnack")) {
            irc.sendMsgTo(m.channel, m.user, snackEngine.deleteSnack(m.msg));
        }

        if (m.command.equals("jbanUser")) {
            banEngine.banUser(m.msg.substring(9).trim());
        }

        if (m.command.equals("junbanUser")) {
            banEngine.unbanUser(m.msg.substring(11).trim());
        }

        if (m.command.equals("junfilter")) {
            irc.sendMsgTo(m.channel, m.user, ChannelFilter.disableFilter(m));
        }

        if (m.command.equals("jfilter")) {
            irc.sendMsgTo(m.channel, m.user, ChannelFilter.enableFilter(m));
        }
    }

    private void passiveCommand(final Message m) {

        if (m.msg.matches(".*bug [#0-9]+.*")) {
            String bugIDmsg = m.msg;
            Pattern pattern = Pattern.compile("bug [#0-9]+");

            Matcher matcher = pattern.matcher(bugIDmsg);

            while (matcher.find()) {
                String bugString = matcher.group();
                pattern = Pattern.compile("[0-9]+");
                matcher = pattern.matcher(bugString);

                while (matcher.find()) {
                    String r = bugEngine.getBug(matcher.group());
                    irc.sendMsgTo(m.channel, m.user, r);
                }
            }
        }
    }

    private void parseCommand(final Message m) {
        if (m.command.equals("snack")) {
            irc.sendMsgTo(m.channel, m.user, snackEngine.getSnack());
        }

        if (m.command.equals("newsnack")) {
            irc.sendMsgTo(m.channel, m.user, snackEngine.addSnack(m.msg, m.user));
        }

        if (m.command.equals("weather")) {
            irc.sendMsgTo(m.channel, m.user,
                    weatherEngine.getWeather(m.user, m.msg));
        }

        if (m.command.equals("forecast")) {
            List<String> results = weatherEngine.getForecast(m.user, m.msg);
            for (String cast : results) {
                irc.sendMsgTo(m.channel, m.user, cast);
            }
        }

        if (m.command.equals("calc")) {
            irc.sendMsgTo(m.channel, m.user,
                    calcEngine.getCalculation(m.msg, m.user));
        }


        if (m.command.equals("8ball")) {
            irc.sendMsgTo(m.channel, m.user, m8Engine.getAnswer());
        }

        if (m.command.equals("roll")) {
            irc.sendMsgTo(m.channel, m.user, diceEngine.rollDie(m.msg));
        }

        if (m.command.equals("help")) {
            List<String> results = helpEngine.displayHelp();
            for (String text : results) {
                irc.sendPrivMsgTo(m.user, text);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (m.command.equals("vote")) {
            irc.sendMsgTo(m.channel, m.user, snackEngine.voteSnack(m.msg, m.user));
        }

        if (m.command.equals("date")) {
            irc.sendMsgTo(m.channel, m.user, dateEngine.getDate());
        }

        /* Exploitable!
        if (m.command.equals("meta")) {
            irc.sendMsgTo(m.channel, m.user, metaEngine.getMetadata(m.msg));
        }
        */

        // Ask NickServ when the m.user was last seen
        if (m.command.equals("seen")) {
            irc.sendMsgTo(m.channel, m.user,
                    lastSpokeEngine.getLastSpoke(m.msg.substring(5).trim()));
        }

        if (m.command.equals("lastspoke")) {
            irc.sendMsgTo(m.channel, m.user,
                    lastSpokeEngine.getLastSpoke(m.msg.substring(10).trim()));
        }

        if (m.command.equals("faq")) {
            irc.sendMsgTo(m.channel, m.user, Faq.parseInput(m));
        }

        if (m.command.equals("todo")) {
            irc.sendMsgTo(m.channel, m.user, todoEngine.parseTodo(m));
        }

        if (m.command.equals("done")) {
            irc.sendMsgTo(m.channel, m.user, todoEngine.deleteTodo(m));
        }

		/* Doesn't really work
        if (m.command.equals("convert")) {
            irc.sendMsgTo(m.channel, m.user, gcalc.convert(m.msg));
        }
		*/
    }
}
