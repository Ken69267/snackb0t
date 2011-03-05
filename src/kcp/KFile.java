/**
 * KFile.java
 *
 * Copyright (C) 2009 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package kcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for basic file operations
 * 
 * @author Kenneth Prugh
 */
public class KFile {
	private final File file;

	BufferedWriter out;

	/**
	 * Creates a new KFile object
	 * 
	 * @param file
	 *            - A File object
	 */
	public KFile(File file) {
		this.file = file;
	}

	/**
	 * Creates a new KFile object
	 * 
	 * @param filepath
	 *            - Path to file
	 */
	public KFile(String filepath) {
		this.file = new File(filepath);
	}

	/**
	 * Reads the file and returns the contents as a String
	 * 
	 * @return - String
	 * @throws IOException
	 */
	public String read() throws IOException {
		final StringBuilder sb = new StringBuilder();

		BufferedReader in = new BufferedReader(new FileReader(file));

		String line = null;

		while ((line = in.readLine()) != null) {
			sb.append(line);
			sb.append(System.getProperty("line.separator"));
		}

		in.close();

		return sb.toString();
	}

	/**
	 * Reads the file and returns the contents as a List of all the lines in the
	 * file
	 * 
	 * @return List
	 * @throws IOException
	 */
	public List<String> readlines() throws IOException {
		List<String> lines = new ArrayList<String>();

		BufferedReader in = new BufferedReader(new FileReader(file));

		String line = null;

		while ((line = in.readLine()) != null) {
			lines.add((line + System.getProperty("line.separator")));
		}

		in.close();

		return lines;
	}

	/**
	 * Writes the line to the file as-is
	 * 
	 * @param line
	 *            - The line of text to write
	 * @param append
	 *            - True if we should append to the file
	 * @throws IOException
	 */
	public void write(String line, boolean append) throws IOException {
		out = new BufferedWriter(new FileWriter(file, append));

		out.write(line);
	}

	/**
	 * Closes the KFile writer from KFile.write()
	 * 
	 * @throws IOException
	 */
	public void closeWrite() throws IOException {
		out.close();
	}
}