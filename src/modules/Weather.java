/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import database.Postgres;

/* Main module for Weather related commands
 * Holds Weather location in postgresql
 * Logic for Forecast or conditions
 * depending on location
 */
public class Weather {
    private String username;
    private String location;
    private Connection db;
    private WeatherConditions WC;
    private Forecast FC;

    public Weather(Postgres db) {
        this.db = db.getConnection();
        WC = new WeatherConditions();
        FC = new Forecast();
    }

    public String getLocation() {
        String locationResult = null;
        ResultSet rs = null;
        PreparedStatement prepStmt = null;

        try { 
            String selectStatement = "SELECT location FROM weather WHERE name = ? ";
            prepStmt = db.prepareStatement(selectStatement);
            prepStmt.setString(1, username);
            rs = prepStmt.executeQuery();

            /* Location is not given */
            if (location.equals("")) {
                if (rs.next()) {
                    // location is present
                    locationResult = rs.getString(1);
                } else {
                    // location isn't present, unknown and unprovided
                    throw new NullPointerException();
                }
            } else {
                /* Location given */
                if (rs.next()) {
                    // user is in database, update the location
                    selectStatement = "UPDATE weather SET location = ? WHERE name = ? ";
                    prepStmt = db.prepareStatement(selectStatement);
                    prepStmt.setString(1, location);
                    prepStmt.setString(2, username);
                    prepStmt.execute();
                    locationResult = location;
                } else {
                    // user is not in database, add user
                    selectStatement = "INSERT INTO weather (name, location) VALUES( ? , ? )";
                    prepStmt = db.prepareStatement(selectStatement);
                    prepStmt.setString(1, username);
                    prepStmt.setString(2, location);
                    prepStmt.execute();
                    locationResult = location;
                }
            }
        } catch (SQLException e) {
            return "Database accident";
        } finally {
            if (prepStmt != null) {
                try { 
                    prepStmt.close();
                } catch (SQLException e) {}
            }
            if (rs != null) {
                try { 
                    rs.close();
                } catch (SQLException e) {}
            }
        }
        return locationResult;
    }

    public String getWeather(String username, String location) {
        this.username = username;
        this.location = location.substring(8).trim().replaceAll(" ", "%20");

        String result;

        try {
            result = getLocation();
        } catch (NullPointerException e) {
            return "Location unknown in database";
        }

        return WC.getWeather(result);
    }

    public List<String> getForecast(String username, String location) {
        this.username = username;
        this.location = location.substring(9).trim();

        String result;

        try {
            result = getLocation();
        } catch (NullPointerException e) {
            List<String> err = new ArrayList<String>();
            err.add("Location not known");
            return err;
        }

        return FC.getForecast(result);
    }

    /* XML helper */
    public static String getValueForTag(Element docEle, String tag) {
        String tagValue;
        try {
            NodeList nl = docEle.getElementsByTagName(tag);

            Element el = (Element) nl.item(0);
            tagValue = el.getFirstChild().getNodeValue();
        } catch (Exception e) {
            // e.printStackTrace();
            tagValue = "unknown";
        }
        return tagValue;
    }
}
