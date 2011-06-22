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

package be.okno.tik.tak.server.processing;

import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.commons.model.Tik;
import be.okno.tik.tak.server.Launcher;
import be.okno.tik.tak.server.processing.WindClockLogger.ClockRequestStatus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonSyntaxException;

public class WindClockProtocol {

	// Error messages.
	private static final String E_JSON = "JSON syntax error while processing client input.";
	private static final String E_RUNTIME = "Runtime error while processing client input.";
	private static final String E_REGISTER = "An error occured registering the current TAK client.";
	private static final String E_TIK = "An error occured receiving a tik from the current TAK client.";

	private Clock clock;
	private JsonStreamParser parser;
	private WindTimeProtocol wtp;
	private WindClockLogger wcLogger;
	private List<MetaDataDefinition> mdDefs;
	private int nbMdDefs;

	public WindClockProtocol(InputStreamReader isReader) {
		parser = new JsonStreamParser(isReader);
		wcLogger = new WindClockLogger();
		wtp = new WindTimeProtocol();
	}

	private boolean onClock() {
		boolean result;

		wcLogger.logClock(clock, ClockRequestStatus.LOGON_REQUEST, true);

		mdDefs = clock.getMetaDataDefinitions();
		nbMdDefs = mdDefs == null ? 0 : mdDefs.size();

		wcLogger.logClock(clock, ClockRequestStatus.LOGON_XMPPCON, false);
		result = wtp.onClock(clock);
		// log & return
		wcLogger.logClock(clock, !result ? ClockRequestStatus.LOGON_FAILURE
				: ClockRequestStatus.LOGON_SUCCESS, true);
		return result;
	}


	private boolean onTik(Tik tik) {

		boolean result = true;

		tik.setIdClock(clock.getIdClock());
		wcLogger.logTik(ClockRequestStatus.TIK_REQUEST, tik, false);

		//result = checkTikMetaDataValues(tik.getMetaDataValues());

		if (result) {
			wcLogger.logTik(ClockRequestStatus.TIK_XMPPSEND, tik, false);
			result = wtp.onTik(tik);
		}
		wcLogger.logTik(result ? ClockRequestStatus.TIK_SUCCESS
				: ClockRequestStatus.TIK_FAILURE, tik, true);

		return result;
	}

	public void run() {
		JsonElement element;
		Gson gson = new Gson();

		try {
			if (parser.hasNext()) {
				element = parser.next();

				clock = (Clock) gson.fromJson(element, Clock.class);
				if (onClock()) {

					Tik tik;
					while (parser.hasNext()) {
						element = parser.next();
						tik = (Tik) gson.fromJson(element, Tik.class);
						if (!onTik(tik)) {
							Launcher.getLogger().log(Level.SEVERE, E_TIK);
							break;
						}
					}
				} else {
					Launcher.getLogger().log(Level.SEVERE, E_REGISTER);
				}
			}
		} catch (JsonSyntaxException jse) {
			Launcher.getLogger().log(Level.SEVERE, E_JSON, jse);
		} catch (RuntimeException rte) {
			Launcher.getLogger().log(Level.SEVERE, E_RUNTIME, rte);
		}
	}
}
