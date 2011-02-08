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
	private Executor threadPool = Executors.newCachedThreadPool();
	private ServerSocket serverSocket = null;
	private volatile boolean running = true;
	private static final int takPort = 30223;

	private static final String ERR_LISTEN = "Setting up TAK server listen socket on port: "
			+ takPort + ".";
	private static final String ERR_ACCEPT = "Accept failed.";
	private static final String ERR_IO = "I/O error while closing server socket.";
	
	private static final String INFO_LISTEN = "Server started listening on port: "
			+ takPort + ".";
	private static final String INFO_ACCEPT = "Server now accepting clients.";

	public Acceptor() {

		try {
			serverSocket = new ServerSocket(takPort);
		} catch (IOException e) {
			Launcher.getLogger().log(Level.SEVERE, ERR_LISTEN, e);
			Launcher.exitOnError();
		}
		Launcher.getLogger().info(INFO_LISTEN);
	}

	public void setRunning(boolean running) {
		this.running = false;
	}

	public void run() {
		while (running) {
			try {
				Launcher.getLogger().info(INFO_ACCEPT);
				Socket clientSocket = serverSocket.accept();
				threadPool.execute(new ClientHandler(clientSocket));
			} catch (IOException e) {
				Launcher.getLogger().log(Level.SEVERE, ERR_ACCEPT, e);
				Launcher.exitOnError();
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			Launcher.getLogger().log(Level.SEVERE,
					ERR_IO, e);
			Launcher.exitOnError();
		}
	}
}
