/**
 * Copyright (C) 2008 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package modules;

import java.util.Random;

/**
 * @author ken
 * 
 */
public class M8ball {
	private String[] answerArray;
	private Random rgen = new Random();

	public M8ball() {
		initArray();
	}

	private void initArray() {
		answerArray = new String[20];
		answerArray[0] = "As I see it, yes";
		answerArray[1] = "Ask again later";
		answerArray[2] = "Better not tell you now";
		answerArray[3] = "Cannot predict now";
		answerArray[4] = "Concentrate and ask again";
		answerArray[5] = "Don't count on it";
		answerArray[6] = "It is certain";
		answerArray[7] = "It is decidedly so";
		answerArray[8] = "Most likely";
		answerArray[9] = "My reply is no";
		answerArray[10] = "My sources say no";
		answerArray[11] = "Outlook good";
		answerArray[12] = "Outlook not so good";
		answerArray[13] = "Reply hazy, try again";
		answerArray[14] = "Signs point to yes";
		answerArray[15] = "Very doubtful";
		answerArray[16] = "Without a doubt";
		answerArray[17] = "Yes";
		answerArray[18] = "Yes - definitely";
		answerArray[19] = "You may rely on it";
	}

	public String getAnswer() {
		int answerid = rgen.nextInt(answerArray.length);
		String result = answerArray[answerid];
		return result;
	}

	/*
	 * public static void main(String[] args) { M8ball m8 = new M8ball();
	 * System.out.println(m8.getAnswer());
	 * 
	 * }
	 */

}
