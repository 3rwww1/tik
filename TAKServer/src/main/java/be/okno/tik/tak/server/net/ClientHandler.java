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
import java.net.Socket;
import java.util.logging.Level;

import be.okno.tik.tak.server.Launcher;
import be.okno.tik.tak.server.processing.WindClockProtocol;

public class ClientHandler implements Runnable {

	// Client socket.
	Socket clientSocket;
	
	// Generic messages.
	private static final String M_CLTCON = "Incoming TAK client connection.";
	
	// Error messages.
	private static final String E_CLTIO = "I/O error while processing TAK client connection.";
	private static final String E_CLOSE = "I/O error while closing TAK client connection.";
	
	// Network messages.
	private static final byte[] N_CONACK = { 'O', 'K', '\r', '\n' };

	public ClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {

		InputStream is = null;
		
		try {
			is = clientSocket.getInputStream();
			InputStreamReader isReader = new InputStreamReader(is);
			Launcher.getLogger().info(M_CLTCON);
			clientSocket.getOutputStream().write(N_CONACK);

			new WindClockProtocol(isReader).run();
		} catch (IOException e) {
			Launcher.getLogger().log(Level.SEVERE, E_CLTIO, e);
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				Launcher.getLogger().log(Level.SEVERE, E_CLOSE, e);
			}
		}
	}
}