/**
 * SDate.java
 *
 * Copyright (C) 2009 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package modules;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Return the current time and date
 * 
 * @author ken
 * 
 */
public class SDate {

	public String getDate() {
		Date cDate;
		SimpleDateFormat formatter;
		Locale cLocale = new Locale("en", "US");

		formatter = new SimpleDateFormat("hh:mma EEE MMM d, yyyy z", cLocale);
		cDate = new Date();
		return formatter.format(cDate);
	}
}
