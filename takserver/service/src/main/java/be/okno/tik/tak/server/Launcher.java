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

import static be.okno.tik.tak.commons.util.Constants.C_COL;
import static be.okno.tik.tak.commons.util.Constants.C_SP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.okno.tik.tak.server.net.Acceptor;
import be.okno.tik.tak.server.processing.WindTimeProtocol;

/**
 * Entry point class for TAKServer.
 * 
 * Starts logging, load configuration from user-defined and default properties files, and start server
 * 
 * 
 * TODO Daemonization.
 * 
 * @author erwan
 */
public class Launcher {

	// Constant objects and values.
	private static final Properties C_PROPERTIES = new Properties();
	private static final Logger C_LOGGER = Logger.getLogger(Launcher.class
			.getName());
	private static final int C_EXITFAIL = 1;

	// Resources names.
	private static final String R_DEFPROPS = "takserver-defaults.properties";
	
	// Files paths.
	private static final String F_PROPS = "takserver.properties";

	// Properties keys.
	private static final String K_LOGFILE = "log.file";
	
	// Info messages.
	private static final String I_NOTFNDPROPFILE = "TAK server user-defined properties file not found, keeping default settings.";
	private static final String I_PROPLIST = "Listing TAK server properties.";
	
	// Error messages.
	private static final String E_SECLOGFILE = "Security exception while opening log file.";
	private static final String E_IOLOGFILE = "I/O error while opening log file.";
	private static final String E_IODEFPROPFILE = "I/O error while reading TAK server default (jar-embedded) properties file.";
	private static final String E_NOTFNDDEFPROPFILE = "TAK server default (jar-embedded) properties file not found.";
	private static final String E_IOPROPFILE = "I/O error while reading TAK server user-defined properties file.";

	private static final String E_EXIT = "Exiting application.";

	private static final void startLogging() {
		try {
			FileHandler log = new FileHandler(getProperty(K_LOGFILE));
			getLogger().addHandler(log);

		} catch (SecurityException e) {
			getLogger().log(Level.SEVERE, E_SECLOGFILE, e);
			exitOnError();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, E_IOLOGFILE, e);
			exitOnError();
		}
	}
	private static final void loadConfiguration() {
		
		try {
			C_PROPERTIES.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(R_DEFPROPS));
		} catch (FileNotFoundException e) {
			getLogger().log(Level.SEVERE, E_NOTFNDDEFPROPFILE, e);
			exitOnError();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, E_IODEFPROPFILE, e);
			exitOnError();
		}
	
		try {
			C_PROPERTIES.load(new FileInputStream(F_PROPS));
		} catch (FileNotFoundException e) {
			getLogger().log(Level.INFO, I_NOTFNDPROPFILE);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, E_IOPROPFILE, e);
			exitOnError();
		}
		
		Set<Entry<Object, Object>> propSet = C_PROPERTIES.entrySet();

		
		getLogger().log(Level.INFO, I_PROPLIST);
		for (Entry<Object, Object> entry : propSet) {
			getLogger().log(Level.INFO, entry.getKey().toString() + C_SP + C_COL + C_SP + entry.getValue().toString());
		}

	}

	public static final String getProperty(String key) {
		return C_PROPERTIES.getProperty(key);
	}

	public static final Logger getLogger() {
		return C_LOGGER;
	}

	public static final void exitOnError() {
		getLogger().severe(E_EXIT);
		System.exit(C_EXITFAIL);
	}

	public static final void main(String[] args) {
		loadConfiguration();
		startLogging();
		WindTimeProtocol.loadProperties();
		new Thread(new Acceptor()).run();
	}
}
