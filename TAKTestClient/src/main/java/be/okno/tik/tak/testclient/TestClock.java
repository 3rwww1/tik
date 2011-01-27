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

package be.okno.tik.tak.testclient;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.Tik;

import com.google.gson.Gson;

public class TestClock implements Runnable {

	private Clock clock;
	private int maxSleepMillis;
	private int minSleepMillis;
	private boolean verbose;

	private Random rng;

	public TestClock(Clock clock, int maxSleepMillis, int minSleepMillis,
			boolean verbose, Random rng) {

		this.rng = rng;
		this.clock = clock;
		this.maxSleepMillis = maxSleepMillis;
		this.minSleepMillis = minSleepMillis;
		this.verbose = verbose;
	}

	public void run() {
		Gson gson = new Gson();
		try {
			Socket socket = new Socket("localhost", 30223);

			OutputStreamWriter osWriter = new OutputStreamWriter(
					socket.getOutputStream());

			String msg = gson.toJson(clock);

			if (verbose) {
				System.out.println("JSON MSG:\n" + msg);
			}

			osWriter.write(msg);
			osWriter.flush();

			Tik tik = new Tik();
			tik.setIdClock(clock.getIdClock());

			int diffRate = maxSleepMillis - minSleepMillis;

			while (true) {
				int rate = rng.nextInt(diffRate) + minSleepMillis;
				try {
					Thread.sleep(rate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				msg = gson.toJson(tik);
				if (verbose) {
					System.out.println("JSON MSG:\n" + msg);
				}
				osWriter.write(msg);
				osWriter.flush();
			}

		} catch (UnknownHostException e) {
			System.err.println("ERROR: Unknown Host");
		} catch (IOException e) {
			System.err.println("ERROR: IO Exception");
		}
	}
}
