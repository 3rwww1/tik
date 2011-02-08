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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ibatis.io.Resources;

import be.okno.tik.tak.server.net.Acceptor;
import be.okno.tik.tak.server.processing.WindTimeProtocol;

/**
 * Entry point class for TAKServer.
 * 
 * TODO Daemonization.
 * 
 */
public class Launcher {

	// Configuration and logging classes.
	private static final Properties CONFIG = new Properties();
	private static final Logger LOGGER = Logger.getLogger(Launcher.class
			.getName());
	
	// Exit error code.
	private static final int EXIT_FAILURE = 1;
	
	// Configuration file informations. 
	private static final String CONF_PROPFILE = "takserver.properties";
	
	// Log file informations.
	private static final String KEY_LOGFILE = "log.file.severe";
	private static final String DEFAULT_LOGFILE = "log/takserver.log";
	
	// Error messages.
	private static final String WARN_DEFLOGFILE = "Log file location is not defined in configuration file, writing log to default location: " + DEFAULT_LOGFILE + ".";
	private static final String ERR_SEC_LOGFILE = "Security exception while opening log file.";
	private static final String ERR_IO_LOGFILE = "I/O error while opening log file.";
	private static final String ERR_IO_CONFFILE = "I/O error while opening configuration file.";
	private static final String ERR_NOTFOUND_CONFFILE = "Configuration file not found.";
	private static final String ERR_EXIT = "Exiting application.";
	
	private static final void startLogging() {
		try {
			String logFile;
			
			if ((logFile = getProperty(KEY_LOGFILE)) == null) {
				logFile = DEFAULT_LOGFILE;
				getLogger().warning(WARN_DEFLOGFILE);
			}
			FileHandler log = new FileHandler(logFile);
			getLogger().addHandler(log);
			
		} catch (SecurityException e) {
			getLogger().log(Level.SEVERE, ERR_SEC_LOGFILE, e);
			exitOnError();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, ERR_IO_LOGFILE, e);
			exitOnError();
		}
	}

	private static final void loadConfiguration() {
		try {
			CONFIG.load(Resources.getResourceAsReader(CONF_PROPFILE));
		} catch (FileNotFoundException e) {
			getLogger().log(Level.SEVERE, ERR_NOTFOUND_CONFFILE, e);
			exitOnError();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, ERR_IO_CONFFILE, e);
			exitOnError();
		}
	}

	public static final String getProperty(String key) {
		return CONFIG.getProperty(key);
	}

	public static final Logger getLogger() {
		return LOGGER;
	}

	public static final void exitOnError() {
		getLogger().severe(ERR_EXIT);
		System.exit(EXIT_FAILURE);
	}

	public static final void main(String[] args) {
		loadConfiguration();
		startLogging();
		WindTimeProtocol.loadProperties();
		new Thread(new Acceptor()).run();
	}
}
