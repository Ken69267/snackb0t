/**
 * LastSpoke.java
 *
 * Copyright (C) 2009 Kenneth Prugh
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

import database.Postgres;

public class LastSpoke {

	private Connection db;
	private final SDate dEngine = new SDate();
	private PreparedStatement prepStmt;
	private ResultSet rs;

	public LastSpoke(Postgres db) {
		/*
		 * Initialize the postgresql connection
		 */
		this.db = db.getConnection();
	}

	/**
	 * Records the last spoken line for the specified user
	 * 
	 * @param user
	 * @param chan
	 * @param msg
	 */
	public void logLine(String user, String chan, String msg) {
		String date = dEngine.getDate();
		try {
			String selectStatement = "SELECT nick FROM lastspoke where nick = ?";
			prepStmt = db.prepareStatement(selectStatement);
			prepStmt.setString(1, user);

			rs = prepStmt.executeQuery();
			if (rs.next()) {
				// nick is present
				selectStatement = "UPDATE lastspoke SET channel = ?, message = ?, sdate = ? WHERE nick = ?";
				prepStmt = db.prepareStatement(selectStatement);
				prepStmt.setString(4, user);
				prepStmt.setString(1, chan);
				prepStmt.setString(2, msg);
				prepStmt.setString(3, date);
				prepStmt.executeUpdate();
				rs.close();
				prepStmt.close();
			} else {
				selectStatement = "INSERT INTO lastspoke VALUES ( ?, ?, ?, ? )";
				prepStmt = db.prepareStatement(selectStatement);
				prepStmt.setString(1, user);
				prepStmt.setString(2, chan);
				prepStmt.setString(3, msg);
				prepStmt.setString(4, date);
				prepStmt.execute();
				rs.close();
				prepStmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (SQLException e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * Returns the last known line from the specified user
	 * 
	 * @param user
	 */
	public String getLastSpoke(String user) {
		String chan;
		String msg;
		String date;
		String result = "Unknown";

		try {
			String selectStatement = "SELECT channel,message,sdate FROM lastspoke WHERE nick = ?";
			PreparedStatement prepStmt;
			prepStmt = db.prepareStatement(selectStatement);
			prepStmt.setString(1, user);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				chan = rs.getString(1);
				msg = rs.getString(2);
				date = rs.getString(3);
				result = user + " was last seen saying on " + chan + ": \""
						+ msg + "\" on " + date;
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
