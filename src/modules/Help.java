/**
 * Copyright (C) 2008 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package modules;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ken
 * 
 */
public class Help {
	final List<String> helpArray = new ArrayList<String>();

	/**
	 * Display Help Contents
	 * 
	 * If module is supplied it will give that specific modules help
	 */
	public Help() {
		helpArray.add("Welcome to snackb0t 2.1's help system");
		helpArray
				.add("Features and bugs report here: http://kenp.homelinux.net/mantisbt/");
		helpArray
				.add("A list of commands and how to use them can be found here: http://www.gentoo-pr0n.org/snackb0t:snackb0t#list_of_commands");
		helpArray.add("I serve Ken69267");
	}

	public List<String> displayHelp() {
		return helpArray;
	}

}
