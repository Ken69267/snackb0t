/**
 * Dice.java
 *
 * Copyright (C) 2009 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package modules;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simulates rolling a die and returns the value rolled
 * 
 * @author ken
 * 
 */
public class Dice {
	final private Random r;

	public Dice() {
		r = new Random();
	}

	public String rollDie(String msg) {
		final int valueRolled;

		Pattern pattern = Pattern.compile("d\\d{1,}");

		Matcher matcher = pattern.matcher(msg);

		try {
			if (matcher.find()) { // d20 etc. was provided
				String dString = matcher.group();

				pattern = Pattern.compile("\\d{1,}");
				matcher = pattern.matcher(dString.trim());

				matcher.find();

				int d = Integer.parseInt(matcher.group());
				if (d > 0) {
					valueRolled = r.nextInt(d) + 1;
				} else {
					valueRolled = 0;
				}
			} else {
				valueRolled = r.nextInt(6) + 1;
			}

			return String.valueOf(valueRolled);
		} catch (NumberFormatException e) {
			return "0";
		}
	}

	public static void main(String[] args) {
		Dice d = new Dice();
		System.out.println(d.rollDie("!roll d20000000000000000000000000"));
		// for (int i = 0; i < 100; i++) {
		// System.out.println(d.rollDie("!roll d20"));
		// }
	}
}
