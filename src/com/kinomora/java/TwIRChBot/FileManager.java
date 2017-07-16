package com.kinomora.java.TwIRChBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.IOException;

public class FileManager {
	// Global instance variables
	private String workingDirectory;
	/**
	 * Empty constructor.
	 **/
	public FileManager(String dir) {
		this.workingDirectory = dir;
	}

	/**
	 * Prints a new text line to a file using
	 * <tt>PrintWriter.println(string);</tt>
	 * 
	 * @param file_name
	 *            The name and extension of the file to be created.
	 * @param line_to_write
	 *            The line of text to append to the end of the file.
	 **/
	public void writeToFile(String file_name, String line_to_write) throws IOException {
		FileWriter write = new FileWriter(workingDirectory + file_name, true);
		PrintWriter print_line = new PrintWriter(write);
		print_line.println(line_to_write);
		print_line.close();
	}

	/**
	 * Prints a text line to a file with formatting options using
	 * <tt>PrintWriter.printf(string);</tt>
	 * 
	 * @param file_name
	 *            The name and extension of the file to be created.
	 * @param line_to_write
	 *            The line of text to append to the end of the file.
	 **/
	public void logToFile(String file_name, String line_to_write) throws IOException {
		FileWriter write = new FileWriter(workingDirectory + file_name, true);
		PrintWriter print_line = new PrintWriter(write);
		print_line.printf(line_to_write);
		print_line.close();
	}

	/**
	 * Creates a file at the location specified in file_name
	 * 
	 * @param file_name
	 *            The name and extension of the file to be created.
	 **/
	public void createFile(String file_name) throws IOException {
		File file = new File(workingDirectory + file_name);
		file.createNewFile();
	}

	/**
	 * Deletes a file with the specified name at the speficied location.
	 *
	 * @param file_name
	 *            The name and extension of the file to be deleted.
	 **/
	public void deleteFile(String file_name) throws IOException {
		File file = new File(workingDirectory + file_name);
		file.delete();
	}

	/**
	 * Reads in a specified file to a string, adding <tt>delimiter</tt> between
	 * each line, ignoring lines beginning with <tt>commenter</tt>.
	 * To read an entire file, ignoring no lines, simply pass "" into <tt>commenter</tt>
	 * 
	 * @param file_name
	 *            The name and extension of the file to be created.
	 * @param commenter
	 *            The character, or series of characters, a line that should be
	 *            ignored will begin with.
	 * @param delimiter
	 *            The character, or series of characters, to insert between each
	 *            line read in
	 * @return A string with the contents of the file, inserting
	 *         <tt>delimiter</tt> between each new line.
	 **/
	public String readFile(String file_name, String commenter, String delimiter) throws FileNotFoundException {
		File file = new File(workingDirectory + file_name);
		String fileContents = "";
		String currentLine = "";
		Scanner scanner = new Scanner(file);

		// While the file has another line to read
		while (scanner.hasNextLine()) {
			// Copy that line
			currentLine = scanner.nextLine();
			// And if that line doesn't begin with "commenter"
			if (!currentLine.substring(0, commenter.length()).equals(commenter)) {
				// Add it to the final return
				fileContents += currentLine + delimiter;
			}
		}
		scanner.close();

		// Return the file as a string
		return fileContents;
	}

	@SuppressWarnings("resource")
	public String readFileStartAtLine(String file_name, int line_num) throws FileNotFoundException {
		File file = new File(workingDirectory + file_name);
		Scanner scanner = new Scanner(file);

		// Skip to the line passed into the method
		for (int i = 0; i < line_num; i++) {
			scanner.nextLine();
		}

		// Read the line if comments aren't present
		if (scanner.hasNextLine() && scanner.nextLine().contains("##")) {
			return scanner.nextLine();
		}

		scanner.close();
		return null;
	}
}
