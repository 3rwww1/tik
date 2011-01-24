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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import be.okno.tik.tak.server.net.Acceptor;

/**
 * Bootstrapping
 * 
 */
public class BootStrap {

	private static final Logger LOGGER = Logger.getLogger(BootStrap.class
			.getName());
	private static final int EXIT_FAILURE = 1;

	private static final void startLogging() {
		try {
			FileHandler log = new FileHandler("log/be.okno.tik.tak.server");
			getLogger().addHandler(log);
		} catch (SecurityException e) {
			e.printStackTrace();
			exitOnError();
		} catch (IOException e) {
			e.printStackTrace();
			exitOnError();
		}
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static void exitOnError() {
		System.exit(EXIT_FAILURE);
	}

	public static final void main(String[] args) {
		
		startLogging();
		new Thread(new Acceptor()).run();
	}
}
