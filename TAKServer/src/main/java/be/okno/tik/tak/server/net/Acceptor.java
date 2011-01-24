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

import be.okno.tik.tak.server.BootStrap;

public class Acceptor implements Runnable {
	private Executor threadPool = Executors.newCachedThreadPool();
	private ServerSocket serverSocket = null;
	private volatile boolean running = true;
	private static final int SERVER_PORT = 30223;

	public Acceptor() {

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			BootStrap
					.getLogger()
					.log(Level.SEVERE,
							"Error starting listening on server socket, port: " + 30223,
							e);
			BootStrap.exitOnError();
		}
		BootStrap.getLogger().info(
				"Server started listening on port: " + SERVER_PORT);
	}

	public void setRunning(boolean running) {
		this.running = false;
	}

	public void run() {
		while (running) {
			try {
				BootStrap.getLogger().info("Accepting clients");
				Socket clientSocket = serverSocket.accept();
				threadPool.execute(new ClientHandler(clientSocket));
			} catch (IOException e) {
				BootStrap.getLogger().log(Level.SEVERE, "Accept failed", e);
				BootStrap.exitOnError();
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			BootStrap.getLogger().log(Level.SEVERE,
					"Error closing server socket", e);
			BootStrap.exitOnError();
		}
	}
}
