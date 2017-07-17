package com.kinomora.java.TwIRChBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatFilter {

	public List<String> permittedSites = new ArrayList<>();
	public List<String> identifiedTLDs = new ArrayList<>();
	public List<String> admins = new ArrayList<>();

	public ChatFilter() {
		//Clear old channel data
		permittedSites.clear();
		identifiedTLDs.clear();
		
		//Update the lists
		populateTLDs();
		populateSites();
	}

	public boolean permittedToPostLink(String sender, String message) {
		for (String user : admins) {
			if (sender.equals(user)) {
				// User is an admin/broadcaster
				return true;
			}
		}
		// User was not an admin/broadcaster
		return testForTLD(message);
	}

	public void updateAdminsList(List<String> importedAdmins) {
		admins.clear();
		for (String user : importedAdmins) {
			admins.add(user);
		}
		System.out.println("Added " + admins.size() + " users to the Admins list.");
	}

	public boolean addTempAdmin(String user) {
		admins.add(user);
		return true;
	}

	public boolean tempRemoveAdmin(String user) {
		if (admins.contains(user)) {
			admins.remove(user);
			return true;
		}
		return false;
	}

	private boolean testForTLD(String message) {
		for (String item : identifiedTLDs) {
			if (message.contains(item)) {
				// Identified a TLD, process for permitted domain.
				return testForDomain(message);
			}
		}
		// Did not contain an identified URL
		return true;
	}

	private boolean testForDomain(String message) {
		for (String item : permittedSites) {
			if (message.contains(item)) {
				// Identified permitted domain
				return true;
			}
		}
		// No permitted domain was identified
		return false;
	}

	public void populateSites() {
		// Load the websites.txt file
		// Declare the file and scanner objects
		File websitesFile = new File(TwIRChbot.directory + "websites.txt");
		Scanner scanner;
		int i = 0;

		// Try to open the file, if it exists
		try {
			scanner = new Scanner(websitesFile);
			String currentLine = "";

			// Loop through the file and import each line into "currentLine" for
			// processing
			while (scanner.hasNextLine()) {
				currentLine = scanner.nextLine();
				if (!currentLine.contains("##") && !currentLine.isEmpty()){
					permittedSites.add(currentLine);
					i++;
				}
			}
			System.out.println("Added " + i + " permitted websites.");
			scanner.close();
		} catch (FileNotFoundException e) {
			// Catch the stack-trace of the error
			e.printStackTrace();
		}
	}

	public void populateTLDs() {
		identifiedTLDs.add(".com"); // commercial
		identifiedTLDs.add(".net"); // network
		identifiedTLDs.add(".org"); // organization
		identifiedTLDs.add(".info"); // info

		identifiedTLDs.add(".ad"); // Generic Country Code Top Level Domains
		identifiedTLDs.add(".as");
		identifiedTLDs.add(".bz");
		identifiedTLDs.add(".cc");
		identifiedTLDs.add(".cd");
		identifiedTLDs.add(".co");
		identifiedTLDs.add(".dj");
		identifiedTLDs.add(".fm");
		identifiedTLDs.add(".io");
		identifiedTLDs.add(".la");
		identifiedTLDs.add(".ly");
		identifiedTLDs.add(".me");
		identifiedTLDs.add(".ms");
		identifiedTLDs.add(".nu");
		identifiedTLDs.add(".sc");
		identifiedTLDs.add(".sr");
		identifiedTLDs.add(".su");
		identifiedTLDs.add(".tv");
		identifiedTLDs.add(".tk");
		identifiedTLDs.add(".ws");

		identifiedTLDs.add(".us"); // Country Codes
		identifiedTLDs.add(".de");
		identifiedTLDs.add(".ca");
		identifiedTLDs.add(".au");
		identifiedTLDs.add(".eu");
		identifiedTLDs.add(".nz");
		identifiedTLDs.add(".uk");
		identifiedTLDs.add(".gg");
		identifiedTLDs.add(".tk");

		identifiedTLDs.add(".xxx"); // Other
		identifiedTLDs.add(".hot");
		identifiedTLDs.add(".adult");
		identifiedTLDs.add(".bot");
		identifiedTLDs.add(".cam");
		identifiedTLDs.add(".coffee");
		identifiedTLDs.add(".dev");
		identifiedTLDs.add(".eco");
		identifiedTLDs.add(".fail");
		identifiedTLDs.add(".fast");
		identifiedTLDs.add(".fit");
		identifiedTLDs.add(".game");
		identifiedTLDs.add(".help");
		identifiedTLDs.add(".hot");
		identifiedTLDs.add(".live");
		identifiedTLDs.add(".lol");
		identifiedTLDs.add(".meet");
		identifiedTLDs.add(".moe");
		identifiedTLDs.add(".one");
		identifiedTLDs.add(".ooo");
		identifiedTLDs.add(".pics");
		identifiedTLDs.add(".pictures");
		identifiedTLDs.add(".porn");
		identifiedTLDs.add(".sex");
		identifiedTLDs.add(".sexy");
		identifiedTLDs.add(".site");
		identifiedTLDs.add(".wtf");
		identifiedTLDs.add(".xyz");
	}

}
