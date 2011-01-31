/* 
 * This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 */

package be.okno.tik.tak.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import be.okno.tik.tak.server.net.Acceptor;

/**
 * Entry point class for TAKServer
 * 
 * TODO daemonization
 * 
 */
public class Launcher {

	private static final Properties CONFIG = new Properties();
	private static final Logger LOGGER = Logger.getLogger(Launcher.class
			.getName());
	private static final int EXIT_FAILURE = 1;

	private static String CONF_LOGFILE = "log/takserver.log";
	private static final String CONF_PROPFILE = "takserver.properties";
	
	private static final void startLogging() {
		try {
			FileHandler log = new FileHandler(CONF_LOGFILE);
			getLogger().addHandler(log);
		} catch (SecurityException e) {
			e.printStackTrace();
			exitOnError();
		} catch (IOException e) {
			e.printStackTrace();
			exitOnError();
		}
	}

	private static final void loadConfiguration() {
		try {
			CONFIG.load(new FileInputStream(CONF_PROPFILE));
			
			String logFile = CONFIG.getProperty("log_file");
			if (logFile != null) {
				CONF_LOGFILE = logFile;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exitOnError();
		} catch (IOException e) {
			exitOnError();
			e.printStackTrace();
		}
	}

	public static final Object getConfigurationValue(String key) {
		return null;
	}

	public static final Logger getLogger() {
		return LOGGER;
	}

	public static void exitOnError() {
		System.exit(EXIT_FAILURE);
	}

	public static final void main(String[] args) {
		loadConfiguration();
		startLogging();
		new Thread(new Acceptor()).run();
	}
}
