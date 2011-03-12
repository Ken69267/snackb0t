/**
 * Copyright (C) 2009-2011 Kenneth Prugh
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

import config.Config;

/**
 * @author Kenneth Prugh
 * 
 */
public class snackb0t {
    /*
     * Backend
     */
    private final Config config = Config.getInstance();
    private final IRCore irc = new IRCore(config);
    private final MessageParser MessageParserEngine = new MessageParser(irc);

    public static void main(String[] args) {
        snackb0t m = new snackb0t();
        m.initSnack();
    }

    public void initSnack() {
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
                MessageParserEngine.parseInput(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
                /* Fix that logging disaster */
                System.exit(1);
            }
        }
    }
}

