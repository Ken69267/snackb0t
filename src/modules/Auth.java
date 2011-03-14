/**
 * Auth.java
 *
 * Copyright (C) 2011 Kenneth Prugh
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

public class Auth {
    private final Connection pg = Postgres.getInstance().getConnection();
	private PreparedStatement prepStmt;
	private ResultSet rs;

    public boolean isAuthenticated(String ident, String host) {
        boolean auth = false;
		try {
			String selectStatement = "SELECT host FROM auth where ident = ?";
			prepStmt = pg.prepareStatement(selectStatement);
			prepStmt.setString(1, ident);

			rs = prepStmt.executeQuery();
			if (rs.next()) {
				// nick is present
                // test against user
                String sqlhost = rs.getString(1);
                if (host.equals(sqlhost)) {
                    auth = true;
                }
            }
		} catch (SQLException e) {
			e.printStackTrace();
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

        return auth;
    }
}
