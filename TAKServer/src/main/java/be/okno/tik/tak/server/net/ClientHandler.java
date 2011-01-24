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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;

import be.okno.tik.tak.server.BootStrap;
import be.okno.tik.tak.server.processing.WindClockProtocol;

public class ClientHandler implements Runnable {

	Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {

		InputStream is = null;
		try {
			is = clientSocket.getInputStream();
			InputStreamReader isReader = new InputStreamReader(is);
			BootStrap.getLogger().info("CLIENT LOGGED IN");
			
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
			pw.println("CONNECTION ACCEPTED");
			
			new WindClockProtocol(isReader).run();
		} catch (IOException e) {
			BootStrap.getLogger().log(Level.SEVERE, "ERROR: Client I/O", e);
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				BootStrap.getLogger().log(Level.SEVERE,
						"ERROR: Client close connection", e);
			}
		}

	}
}