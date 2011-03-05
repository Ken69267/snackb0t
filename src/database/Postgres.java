/**
 * Postgres.java
 *
 * Copyright (C) 2009 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import config.Config;

public class Postgres {

	private Connection db;

	public Postgres(Config config) {
		try {
			Class.forName("org.postgresql.Driver");
			db = DriverManager.getConnection(config.getDbpath(),config.getDbuser(), config.getDbpass());
			final String DBEncoding = "UNICODE";
			PreparedStatement statement = db
					.prepareStatement("SET CLIENT_ENCODING TO '" + DBEncoding + "'");
			statement.execute();
			statement.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return Connection to postgresql
	 */
	public Connection getConnection() {
		return db;
	}
}
