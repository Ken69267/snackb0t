/**
 * BanUser.java
 *
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package modules;

import java.util.HashSet;
import java.util.Set;

/**
 * This class temporarily bans a user from passing commands to snackb0t
 * 
 * @author ken
 */
public class BanUser {
	private Set<String> bannedUsers = new HashSet<String>();

	/**
	 * Add the user to the banList and start the timer thread for unbanning
	 * 
	 * @param user
	 */
	public void banUser(final String user) {
		// Add to banlist
		System.out.println("Banning: " + user);
		bannedUsers.add(user);
	}

    public void unbanUser(final String user) {
        bannedUsers.remove(user);
        System.out.println("Unbanned: " + user);
    }

	public boolean isBanned(String user) {
		if (bannedUsers.contains(user)) {
			// is banned
			System.out.println(user + " is banned");
			return true;
		} else {
			return false;
		}
	}
}
