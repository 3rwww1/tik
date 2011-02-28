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

package be.okno.tik.tak.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import be.okno.tik.tak.server.Launcher;

public class Acceptor implements Runnable {

	// Static configuration values.
	private static final int C_TAKPORT = 30223;
	
	// Error messages.
	private static final String E_LISTEN = "Error setting up TAK server listen socket on port: "
			+ C_TAKPORT + ".";
	private static final String E_ACCEPT = "Accept failed.";
	private static final String E_CLOSE = "I/O error while closing server socket.";

	// Info messages.
	private static final String I_LISTEN = "Server started listening on port: "
			+ C_TAKPORT + ".";
	private static final String I_ACCEPT = "Server now accepting clients on port:"
			+ C_TAKPORT + ".";

	// Client thread pool.
	private Executor threadPool = Executors.newCachedThreadPool();
	
	// Server socket.
	private ServerSocket serverSocket = null;
	
	// This variable is set to true if the server should be running, false otherwise.
	private volatile boolean running = true;
	
	public Acceptor() {

		try {
			serverSocket = new ServerSocket(C_TAKPORT);
		} catch (IOException e) {
			Launcher.getLogger().log(Level.SEVERE, E_LISTEN, e);
			Launcher.exitOnError();
		}
		Launcher.getLogger().info(I_LISTEN);
	}

	public void setRunning(boolean running) {
		this.running = false;
	}

	public void run() {
		while (running) {
			try {
				Launcher.getLogger().info(I_ACCEPT);
				Socket clientSocket = serverSocket.accept();
				threadPool.execute(new ClientHandler(clientSocket));
			} catch (IOException e) {
				Launcher.getLogger().log(Level.SEVERE, E_ACCEPT, e);
				Launcher.exitOnError();
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			Launcher.getLogger().log(Level.SEVERE, E_CLOSE, e);
			Launcher.exitOnError();
		}
	}
}
