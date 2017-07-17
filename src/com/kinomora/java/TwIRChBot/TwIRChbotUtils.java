/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kinomora.java.TwIRChBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jibble.pircbot.*;

public class TwIRChbotUtils extends PircBot {

	// Imported when instantiated
	private final String version;
	private final String directory;
	private String currentChannel;

	// Printed onJoin if not empty
	private String loginMessage = "";

	// Multi-twitch partner if not empty
	public String multi = "";

	// Used in various methods to hold a message
	public String tmpMsg = "";

	// Whether or not I have checked the current running version of the bot in
	// the specific chat
	boolean checked = false;

	// This is an instance of the ChatFilter class to be used later
	ChatFilter filter;

	// This variable will enable or disable chat filtering (true = enabled)
	boolean chatFilter = true;

	// Array lists containing relative information
	public List<String> Admins = new ArrayList<>(); // People with swords
	public List<String> importedCommands = new ArrayList<>();
	public List<String> importedActions = new ArrayList<>();
	public List<String> importedHelp = new ArrayList<>();

	public TwIRChbotUtils(String botName, String version, String directory) {
		this.setName(botName);
		this.version = version;
		this.directory = directory;
	}

	public void setChannel(String channel) {
		if (channel.contains("#")) {
			this.currentChannel = channel.substring(1);
		} else
			this.currentChannel = channel;
	}

	public String getChannel() {
		return this.currentChannel;
	}

	public void onJoin(String channel, String sender, String login, String hostname) {
		// Clear out old data
		Admins.clear();
		importedCommands.clear();
		importedActions.clear();
		importedHelp.clear();

		// Create an instance of the ChatFilter class to check for links
		filter = new ChatFilter();

		// Greeting, if added, is sent to chat.
		if (!loginMessage.equals("")) {
			sendBotMessage(channel, loginMessage);
		}

		// Load the commands text document into the bot
		try {
			loadCommandsFile();
		} catch (FileNotFoundException e) {
			sendBotMessage("", "No commands file found!");
			e.printStackTrace();
		}

		// Send a CAP REQ to be able to use twitch commands
		sendRawLine("CAP REQ :twitch.tv/commands");

		// Ask for the admins list
		sendMessage(channel, ".mods");

		// Add the broadcaster to the admins list
		Admins.add(channel.substring(1, channel.length()));

		// Fix the stupid channel shit
		this.currentChannel = channel;
	}

	public void username(String username) {
		this.setName(username);
	}

	/*
	 * admins(String[]) - method
	 * 
	 * @param String[] - A string array containing the names of viewers who have
	 * "OP" status on twitch and display in the "mods" list when the command
	 * /mods is executed on twitch
	 * 
	 * This method is called from the "main" runnable class (TwIRChbot.java) to
	 * pass the mod list back into the "handling" (TwIRChbotExta.java) class
	 * where it can be used to decide who can use certain commands
	 */
	public void admins(String[] admins) {
		for (int i = 0; i < admins.length; i++) {
			Admins.add(admins[i]);
		}
	}

	public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		// Avoid other notices. Whoops.
		if (notice.contains("The moderators of this room")) {
			// Make each admin have a `, ` before their name
			String temp = notice.replace("The moderators of this room are: ", "");

			// While the `temp` string isn't empty, cycle through and add each
			// user to the `Admins` list
			while (!temp.isEmpty()) {
				if (temp.contains(",")) {
					Admins.add(temp.substring(0, temp.indexOf(',')).trim());
				} else {
					Admins.add(temp.trim());
					break;
				}
				temp = temp.substring(temp.indexOf(',') + 2);
			}
			// Add all mods to the admins list in the ChatFilter class
			filter.updateAdminsList(Admins);
		}
	}

	// Chat monitoring
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		TwIRChbot.updateChat(sender, message);
		/*** Chat Filtering ***/
		if (chatFilter) {
			if (!filter.permittedToPostLink(sender, message)) {
				sendBotMessage(channel, ".timeout " + sender + " 1");
				sendBotMessage(channel, "@" + sender
						+ " Sorry, that link you posted wasn't permitted! Please ask before posting a link, you may need to message a moderator.");
			}
		}

		// Excessive chat length
		if (message.length() > 400) {
			sendBotMessage(channel, ".timeout " + sender + " 1");
			sendBotMessage(channel, "@" + sender
					+ " Please don't post such long messages. Try breaking it up if you've got a lot to say.");
		} else

		/***
		 * Commands only available to Kinomora
		 ***/
		if (sender.equalsIgnoreCase("kinomora")) {
			// Usage: !version
			if (message.equalsIgnoreCase("!version")) {
				if (!checked) {
					sendBotMessage(channel, ".w kinomora " + version);
					checked = true;
				}
			}
		}

		/***
		 * Commands only available to the broadcaster
		 ***/
		if (isBroadcaster(sender)) {
			if (message.startsWith("!mod ") && message.length() > 5) {
				filter.addTempAdmin(message.substring(5));
				Admins.add(message.substring(5));
			} else if (message.startsWith("!unmod ") && message.length() > 7) {
				filter.tempRemoveAdmin(message.substring(7));
				Admins.remove(message.substring(7));
			} else if (message.equals("!mod")) {
				sendBotMessage(sender, "I think you're using that command wrong! Do !mod <user> or !unmod <user>.");
			}
		}

		/***
		 * Commands only available to admins (and broadcaster)
		 ***/
		if (isAdmin(sender)) {
			// Set the multi-stream
			if (message.contains("!setmulti")) {
				if (message.equalsIgnoreCase("!setmulti")) {
					multi = "";
				} else
					multi = message.substring(message.indexOf(' ') + 1);
			} else
			// Reload the commands file
			if (message.equalsIgnoreCase("!reload")) {
				importedCommands.clear();
				importedActions.clear();
				importedHelp.clear();
				try {
					loadCommandsFile();
					sendBotMessage(channel, "Commands reloaded!");
				} catch (FileNotFoundException e) {
					sendBotMessage("", "No commands file found!");
					e.printStackTrace();
				}
			} else

			// Adding a command
			// Usage: !addcmd $c<command> $a<action> $h[help]
			if (message.contains("!addcmd") && message.contains("$c") && message.contains("$a")) {
				/*** Add the command to the commands List<String> ***/
				if (message.substring(message.indexOf("$c") + 2, message.indexOf("$a") - 1).contains("!")) {
					// Add it if an "!" was present
					importedCommands.add(message.substring(message.indexOf("$c") + 2, message.indexOf("$a") - 1));
				} else {
					// Add an "!" to the beginning of the command if needed
					importedCommands.add("!" + message.substring(message.indexOf("$c") + 2, message.indexOf("$a") - 1));
				}

				/*** Add the action to the actions List<String> ***/
				if (message.contains("$h")) {
					importedActions.add(message.substring(message.indexOf("$a") + 2, message.indexOf("$h") - 1));
				} else {
					importedActions.add(message.substring(message.indexOf("$a") + 2));
				}

				/*** Check if a help action was included, add it if it were ***/
				if (message.contains("$h")) {
					// Help WAS defined
					importedHelp.add(message.substring(message.indexOf("$h") + 2));
				} else
					// Help was NOT defined
					importedHelp.add("Not defined.");
			} else if (message.equalsIgnoreCase("!addcmd") | message.equalsIgnoreCase("!addcommand")) {
				sendBotMessage(channel,
						"Are you use you're using that command right? Try: !addcmd $c<command> $a<response> $h[help]");
			}
		}

		// Temp everyone command//
		if (message.equalsIgnoreCase("!mods")) {
			String temp = "The users with admin powers in this channel are: ";
			for (String user : Admins) {
				temp += user + ", ";
			}
			sendBotMessage(channel, temp.substring(0, temp.length() - 2));
		} else

		/*** Commands available to everyone ***/
		// Check if they tried to list the commands first
		if (message.equalsIgnoreCase("!") || message.equalsIgnoreCase("!commands") || message.equalsIgnoreCase("!cmds")
				|| message.equalsIgnoreCase("!help")) {
			for (String str : importedCommands) {
				tmpMsg += str + ", ";
			}
			tmpMsg += ("and " + message);
			sendBotMessage(channel, "The public commands for this channel are: " + tmpMsg);
			tmpMsg = "";
		} else

		// Main message checking
		if (message.contains("!") && importedCommands.size() > 0 && importedActions.size() > 0) {
			for (int i = 0; i < importedCommands.size(); i++) {
				if (message.equalsIgnoreCase(importedCommands.get(i))) {
					// Check to replace <multi>
					if (importedActions.get(i).contains("<multi>")) {
						if (multi.equals("")) {
							sendBotMessage(channel, channel.substring(1) + " isn't current streaming with anyone else TheFeels");
							break;
						} else {
							sendBotMessage(channel, importedActions.get(i).replace("<multi>", multi));
						}
					} else {
						// If there's nothing to replace, just respond normally
						sendBotMessage(channel, importedActions.get(i));
					}
				}
			}
		} else

		/*** Help for commands available to everyone ***/
		// Check if they tried to list the help commands
		if (message.equalsIgnoreCase("?commands") || message.equalsIgnoreCase("?cmds")
				|| message.equalsIgnoreCase("?help")) {
			sendBotMessage(channel, "Displays all commands available for anyone to use in chat.");
		} else

		// Main message checking
		if (message.contains("?") && importedHelp.size() > 0 && importedCommands.size() > 0) {
			for (int i = 0; i < importedCommands.size(); i++) {
				if (message.equals("?" + importedCommands.get(i).substring(1))) {
					sendBotMessage(channel, importedHelp.get(i));
				}
			}
		} else

		/*** Automatic chat responses ***/
		if (message.equalsIgnoreCase("^")) {
			sendBotMessage(channel, "^");
		}
	}

	public void onPart(String channel, String sender, String login, String hostname) {
		filter.admins.clear();
	}

	/**
	 * Sends a message to Twitch while updating the chat pane in the main GUI
	 * 
	 * @param channel
	 *            - The name of the channel to send the message to. If "" is
	 *            sent, only the chat pane receives the message.
	 * @param message
	 *            - The actual message to push to twitch and the GUI chat pane.
	 **/
	private void sendBotMessage(String channel, String message) {
		if (!channel.equals("")) {
			sendMessage(channel, message);
			TwIRChbot.updateChat(this.getNick(), message);
		} else {
			TwIRChbot.updateChat("[SYSTEM]", message);
		}
	}

	private void loadCommandsFile() throws FileNotFoundException {
		// Declare the file and scanner objects
		File commandsFile = new File(directory + "commands.txt");
		Scanner scanner;

		// Try to open the file, if it exists
		try {
			scanner = new Scanner(commandsFile);
			String currentLine = "";

			// Loop through the file and import each
			// line into "currentLine" for processing
			while (scanner.hasNextLine()) {
				currentLine = scanner.nextLine();
				if (!currentLine.contains("##") && !currentLine.isEmpty() && currentLine.contains("!")
						&& currentLine.contains(" ")) {
					// Sorts out the "command" portion and adds it to the
					// arraylist of it's type
					importedCommands.add(currentLine.substring(currentLine.indexOf("!"), currentLine.indexOf("</a>")));
					// Sorts out the "action" portion and adds it to the
					// arraylist of it's type
					importedActions
							.add(currentLine.substring(currentLine.indexOf("</a>") + 4, currentLine.indexOf("</h>")));
					// Sorts out the "help" portion and adds it to the
					// arraylist of it's type
					importedHelp.add(currentLine.substring(currentLine.indexOf("</h>") + 4));
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// Catch the stack-trace of the error
			e.printStackTrace();
		}
	}

	public void saveCommand(File file, String command, String action) {
		// Save commands here
	}

	public boolean isAdmin(String user) {
		return Admins.contains(user);
	}

	public boolean isBroadcaster(String user) {
		return user.equalsIgnoreCase(currentChannel.substring(1));
	}
}
