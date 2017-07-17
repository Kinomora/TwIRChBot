/*
 * Copyright (c) 2010, Oracle. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Oracle nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */// </editor-fold> 
package com.kinomora.java.TwIRChBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

import org.jibble.pircbot.IrcException;

public class TwIRChbot extends javax.swing.JFrame {
	private static final long serialVersionUID = (long) Math.random();

	// GUI variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JScrollPane jScrollPane1;
	private static javax.swing.JTextArea chat;
	private javax.swing.JButton updateButton;
	private javax.swing.JTextField channelIn;
	private javax.swing.JTextField usernameIn;
	private javax.swing.JPasswordField oauthIn;
	private javax.swing.JTextField sendMessage;

	// Used variables
	public static String directory = "./ChatbotFiles/";
	public String currentChannel = "";
	public String channel = "";
	public String oauth = "";
	public String username = "TwitchChatBot";
	public boolean loadLogin = false;
	public static String h;
	public static String m;
	public static String s;
	public static String version = "0.11.0.0-ALPHA";

	// Objects

	public TwIRChbotUtils bot = new TwIRChbotUtils(username, version, directory);

	// Files
	FileManager file;
	static File log;
	static String Log = "latest.log";
	static File login;
	static String Login = "login.txt";
	static File commands;
	static String Commands = "commands.txt";
	static File currency;
	static String Currency = "currency.txt";
	static File websites;
	static String Websites = "websites.txt";
	static File readme;
	static String Readme = "readme.txt";


	// Initialization Constructor
	public TwIRChbot() {

		// Create a directory to hold the files we will be using
		File dir = new File(directory);
		dir.mkdirs();

		// Create FileManager instances for the files to be managed
		file = new FileManager(directory);

		// Make sure all the files exist before starting the program
		manageFiles();

		// Try to load in stored login
		loadLogin();

		// Initialize the GUI components
		initComponents();
	}

	// Constructs the GUI
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		channelIn = new javax.swing.JTextField();
		usernameIn = new javax.swing.JTextField();
		oauthIn = new javax.swing.JPasswordField();
		jScrollPane1 = new javax.swing.JScrollPane();
		chat = new javax.swing.JTextArea();
		sendMessage = new javax.swing.JTextField();
		updateButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1.setText("Channel");

		jLabel2.setText("Username");

		jLabel3.setText("Oauth");

		chat.setEditable(false);
		chat.setColumns(20);
		chat.setRows(5);
		jScrollPane1.setViewportView(chat);

		sendMessage.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				sendMessageKeyPressed(evt);
			}
		});

		updateButton.setText("Update and Reconnect");
		updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				updateButtonMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(updateButton, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jScrollPane1)
						.addGroup(
								layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup().addComponent(jLabel1).addGap(0,
														90, Short.MAX_VALUE))
												.addComponent(channelIn))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel2).addComponent(usernameIn,
														javax.swing.GroupLayout.PREFERRED_SIZE, 152,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel3).addComponent(oauthIn,
														javax.swing.GroupLayout.PREFERRED_SIZE, 165,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addComponent(sendMessage))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1).addComponent(jLabel2).addComponent(jLabel3))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(channelIn, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(usernameIn, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(oauthIn, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(updateButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sendMessage, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));

		pack();

		// Set values
		if (loadLogin) {
			this.channelIn.setText(channel);
			this.usernameIn.setText(username);
			this.oauthIn.setText(oauth);
			loadLogin = false;
		}
	}

	private void sendMessageKeyPressed(java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && !sendMessage.getText().equals("")
				&& bot.isConnected()) {
			// sendMessage(sendMessage.getText());
			updateChat(username, sendMessage.getText());
			bot.sendMessage(channel, sendMessage.getText());
			sendMessage.setText("");
		}
	}

	private void updateButtonMouseClicked(java.awt.event.MouseEvent evt) {
		// Update the values in memory
		updateConnectValues();

		// Connect to twitch and the server
		connect();
	}

	@SuppressWarnings("deprecation")
	private void updateConnectValues() {
		channel = this.channelIn.getText();
		username = this.usernameIn.getText();
		oauth = this.oauthIn.getText();
	}

	private void connect() {
		// Prepare the bot for connecting
		bot.username(username);
		bot.setVerbose(true);

		// If we were connected to begin with, part the channel
		if (channel.equals("")) {
			chat.append("Channel cannot be empty!\n");
		} else if (username.equals("")) {
			chat.append("Username cannot be empty!\n");
		} else if (!bot.isConnected() && !username.equals("") && !channel.equals("")) {
			try {
				bot.connect("irc.chat.twitch.tv", 6667, oauth);
				chat.append("Connected to Twitch IRC as " + username + "!\n");
				currentChannel = channel;
			} catch (IOException | IrcException e) {
				chat.append("Error. Could not connect.\n");
				e.printStackTrace();
			}
			if (bot.isConnected()) {
				// Connect to the specified channel
				bot.joinChannel(channel);
				chat.append("Connected to " + channel + "'s IRC room!\n");
			}
			// STOP CONNECTING MULTIPLE TIMES TO EACH CHANNEL ON DISCONNECT +
			// RECONNECT
		} else {
			if (currentChannel.equals(channel)) {
				chat.append("You can't leave and rejoin the same channel!\n");
			} else {
				chat.append("Disconnecting from channel " + currentChannel + "\n");
				bot.partChannel(currentChannel);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chat.append("Connecting to channel " + channel + "\n");
				currentChannel = channel;
				bot.joinChannel(currentChannel);
			}
		}
	}

	public static void updateChat(String sender, String message) {
		chat.append("[" + getTime() + "] " + sender + ": " + message + "\n");
	}

	@SuppressWarnings("deprecation")
	private static String getTime() {
		if (Calendar.getInstance().getTime().getHours() < 10) {
			h = "0" + Calendar.getInstance().getTime().getHours();
		} else {
			h = "" + Calendar.getInstance().getTime().getHours();
		}
		if (Calendar.getInstance().getTime().getMinutes() < 10) {
			m = "0" + Calendar.getInstance().getTime().getMinutes();
		} else {
			m = "" + Calendar.getInstance().getTime().getMinutes();
		}
		if (Calendar.getInstance().getTime().getSeconds() < 10) {
			s = "0" + Calendar.getInstance().getTime().getSeconds();
		} else {
			s = "" + Calendar.getInstance().getTime().getSeconds();
		}
		return (h + ":" + m + ":" + s);
	}

	private void manageFiles() {
		// Delete the old "latest.log" so a new one can replace it
		log = new File(directory + Log);
		log.delete();

		// Generate a login file if one doesn't exist
		login = new File(directory + Login);
		try {
			login.createNewFile();
		} catch (IOException e) {
			System.out.println("login.txt already exists!");
			e.printStackTrace();
		}

		// Generate a commands file if one doesn't exist
		commands = new File(directory + Commands);
		try {
			commands.createNewFile();
			if (!commands.exists()) {
				System.out.println("Could not find commands.txt! Creating..");
				generateCommandsFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Generate a currency file if one doesn't exist
		currency = new File(directory + Currency);
		try {
			currency.createNewFile();
			if (!currency.exists()) {
				System.out.println("Could not find currency.txt! Creating..");
				generateCurrencyFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generate a websites file if one doesn't exist
		websites = new File(directory + Websites);
		try {
			websites.createNewFile();
			if (!websites.exists()) {
				System.out.println("Could not find websites.txt! Creating..");
				generateWebSitesFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generate a readme file if one doesn't exist
		readme = new File(directory + Readme);
		try {
			readme.createNewFile();
			if (!readme.exists()) {
				System.out.println("Could not find readme.txt! Creating..");
				generateReadMeFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateCommandsFile() {
		try {
			file.writeToFile(Commands, "## To create a commented line, put ## at the beginning of the line\n");
			file.writeToFile(Commands,
					"## NOTE: *ANY* line with two ## will NOT be read in by the bot, \'one\' # is fine, however.\n");
			file.writeToFile(Commands, "##\n");
			file.writeToFile(Commands, "## --Variables--\n");
			file.writeToFile(Commands, "## Use @sender to reference the command-executer.\n");
			file.writeToFile(Commands,
					"## Use <first>, <second>, and <third> to import respective words into the bot.\n");
			file.writeToFile(Commands, "##        ^--<x> will take the word AFTER the [x] space.\n");
			file.writeToFile(Commands,
					"## Use <last> to import the LAST word starting from the LAST space when the command is executed into the bot.\n");
			file.writeToFile(Commands,
					"## Example: \"!shoutout KinoBot Nightbot Kinomora\" when the command is listed as \"!shoutout Hey check out <last>!\" will return \"Hey! Check out Kinomora!\"\n");
			file.writeToFile(Commands, "##\n");
			file.writeToFile(Commands, "## To create a mod-only command, begin the command with \"(m)\".\n");
			file.writeToFile(Commands, "## To create an admin-only command, being the command with \"(a)\".\n");
			file.writeToFile(Commands, "## To create a broadcaster-only command, begin the command with \"(b)\".\n");
			file.writeToFile(Commands, "##\n");
			file.writeToFile(Commands, "## --Examples--\n");
			file.writeToFile(Commands, "## !sayhi Hello!\n");
			file.writeToFile(Commands, "## !purgeme /timeout @sender 1\n");
			file.writeToFile(Commands, "##---Bans the sender for 1 second (effectively purging them)\n");
			file.writeToFile(Commands, "## (m)!tempban <first> <second>\n");
			file.writeToFile(Commands,
					"##---MOD-ONLY: Bans the <FIRST> for <SECOND> amount of seconds (if <first> is \"Kinomora\" and <second> is 60, you would \"tempban\" Kinomora for 10 seconds from chatting)\n");
			file.writeToFile(Commands, "## (a)!checkban <first>\n");
			file.writeToFile(Commands,
					"##---ADMIN-ONLY: Checks the bannedusers.txt for <FIRST> and lists their banned reason if one is listed.\n");
			file.writeToFile(Commands, "## (b)!shoutout Hey! Check out this cool person www.twitch.tv/<last>\n");
			file.writeToFile(Commands,
					"##---BROADCASTER-ONLY: Sends \"Hey! Check out this cool person www.twitch.tv/<LAST>\" in chat, where <LAST> is the last word sent with the command.\n");
		} catch (IOException IOErr) {
			// TODO Auto-generated catch block
			IOErr.printStackTrace();
		}
	}

	private void generateCurrencyFile() {
		try {
			file.writeToFile(Currency, "##Cannot Be Empty\n");
			file.writeToFile(Currency, "##Lines with \"##\" within them are ignored.\n");
		} catch (IOException IOErr) {
			// TODO Auto-generated catch block
			IOErr.printStackTrace();
		}
	}

	private void generateWebSitesFile() {
		try {
			file.writeToFile(Websites, "##Cannot Be Empty\n");
			file.writeToFile(Websites, "##Lines with \"##\" within them are ignored.\n");
			file.writeToFile(Websites, "##You may edit this file, the bot will not timeout a user who posts a message containing a link to one of these websites.\n");
			file.writeToFile(Websites, "imgur\n");
			file.writeToFile(Websites, "twitch\n");
			file.writeToFile(Websites, "beam\n");
			file.writeToFile(Websites, "google\n");
			file.writeToFile(Websites, "reddit\n");
			file.writeToFile(Websites, "discord\n");
			file.writeToFile(Websites, "prntscr\n");
			file.writeToFile(Websites, "gyazo\n");
			file.writeToFile(Websites, "strawpoll\n");
			file.writeToFile(Websites, "screenshot\n");
			file.writeToFile(Websites, "kadgar\n");
			file.writeToFile(Websites, "multi\n");
			file.writeToFile(Websites, "socialblade\n");
			file.writeToFile(Websites, "twitchalerts\n");
			file.writeToFile(Websites, "streamlabs\n");
			file.writeToFile(Websites, "streamjar\n");
			file.writeToFile(Websites, "streamable\n");
			file.writeToFile(Websites, "youtube\n");
		} catch (IOException IOErr) {
			IOErr.printStackTrace();
		}
	}

	private void generateReadMeFile() {
		try {
			file.writeToFile(Readme, "Thank you for downloading version " + version + " of TwIRChBot.\n");
			file.writeToFile(Readme, "I'm sure you're ready to get it up and running, so I'll keep this short.\n");
			file.writeToFile(Readme,
					"Make sure you have permission to use this bot in the chat you're connecting to.\n");
			file.writeToFile(Readme, "The only \"hard wired\" commands are:\n");
			file.writeToFile(Readme, "!addcmd\n");
			file.writeToFile(Readme, "!mods\n");
			file.writeToFile(Readme, "!help/!cmds/!commands/!\n");
			file.writeToFile(Readme, "!mods\n");
			file.writeToFile(Readme, "*!version\n");
			file.writeToFile(Readme, "\n");
			file.writeToFile(Readme,
					"*Please note. I do not wish to give myself undue power in anyones stream, however\n");
			file.writeToFile(Readme,
					"in order for me to be able to track the development and spread of this bot, as well\n");
			file.writeToFile(Readme,
					"as ensure you are protected against potential exploits in any ceratin version, I have\n");
			file.writeToFile(Readme,
					"allowed only myself to use the command !version in any chat ONLY ONCE. This command\n");
			file.writeToFile(Readme,
					"causes the bot to whisper me the version which it is running on. Nothing else will\n");
			file.writeToFile(Readme,
					"happen and no other person can use this command under any other circumstance.\n\n");
			file.writeToFile(Readme, "My sincerest gratitude,\n");
			file.writeToFile(Readme, "Kinomora\n");
		} catch (IOException IOErr) {
			IOErr.printStackTrace();
		}
	}

	private void loadLogin() {
		if (login.exists()) {
			Scanner scanner;
			try {
				scanner = new Scanner(login);
				if (scanner.hasNextLine()) {
					channel = scanner.nextLine().substring(9);
					username = scanner.nextLine().substring(10);
					oauth = scanner.nextLine().substring(10);
					loadLogin = true;
				}
			} catch (FileNotFoundException e) {
				System.out.println("No login data found, please create the file.");
			}
		}
	}

	public static void main(String args[]) {
		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TwIRChbot().setVisible(true);
			}
		});
	}
}
