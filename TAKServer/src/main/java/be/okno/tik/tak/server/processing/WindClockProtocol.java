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
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.commons.model.MetaDataValue;
import be.okno.tik.tak.commons.model.Tik;
import be.okno.tik.tak.dao.DaoSession;
import be.okno.tik.tak.server.BootStrap;
import be.okno.tik.tak.server.processing.WindClockLogger.ClockRequestStatus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonSyntaxException;

public class WindClockProtocol {

	private Map<Integer, MetaDataDefinition> mdDefsMap;
	private int nbMdDefs;
	private Clock clock;
	private JsonStreamParser parser;
	private WindTimeProtocol wtp;
	
	private WindClockLogger wcLogger;

	public WindClockProtocol(InputStreamReader isReader) {
		parser = new JsonStreamParser(isReader);
		mdDefsMap = new TreeMap<Integer, MetaDataDefinition>();
		wcLogger = new WindClockLogger();
	}

	private boolean onClock() {
		boolean result;
		List<MetaDataDefinition> mdDefs = clock.getMetaDataDefinitions();

		for (MetaDataDefinition mdDef : mdDefs) {
			mdDefsMap.put(mdDef.getIdMddef(), mdDef);
		}
		wcLogger.setMetaDataDefintionsMap(mdDefsMap);
		
		wcLogger.logClock(clock, ClockRequestStatus.LOGON_REQUEST, true);

		// invoke blocking DAO call to retrieve clock data
		Clock dbClock = DaoSession.getInstance().getClockById(
				clock.getIdClock());

		if (dbClock != null && dbClock.getIdClock().equals(clock.getIdClock())) {
			// the clock was found by the DAO, and are consistent.
			if (!(dbClock.equals(clock))) {
				// the connected clock data is different from DAO clock data
				// invoke blocking DAO call to update clock data
				DaoSession.getInstance().updateClock(dbClock);
			}
			wcLogger.logClock(clock, ClockRequestStatus.LOGON_PERSIST, false);
			// login agent on XMPP server
			wcLogger.logClock(clock, ClockRequestStatus.LOGON_XMPPCON, false);
			// Everything OK
			result = true;
		} else {
			// clock was not found by the DAO or the IDs were inconsistent
			result = false;
		}

		// log & return
		wcLogger.logClock(clock, !result ? ClockRequestStatus.LOGON_FAILURE
				: ClockRequestStatus.LOGON_SUCCESS, true);
		return result;
	}

	private boolean checkTikMetaDataValues(List<MetaDataValue> mdVals) {

		boolean result = true;

		int nbMdVals = mdVals == null ? 0 : mdVals.size();

		if (nbMdVals != nbMdDefs) {
			// the number of meta data values is inconsistent
			result = false;
		} else if (mdVals != null){
			// the number of meta data values is consistent

			for (MetaDataValue mdVal : mdVals) {
			
				if (mdVal.getIdMddef() == null
						|| mdDefsMap.get(mdVal.getIdMddef()) == null) {
					// a meta data id is not correct
				
					result = false;
					break;
				}
			}
		}
		return result;
	}

	private boolean onTik(Tik tik) {

		boolean result = true;
		wcLogger.logTik(ClockRequestStatus.TIK_REQUEST, tik, false);
		List<MetaDataValue> mdVals = tik.getMetaDataValues();

		if (tik.getIdClock() == clock.getIdClock()) {
			// the clock id is consistent
			result = checkTikMetaDataValues(mdVals);

		} else {
			// the clock id is not consistent
			result = false;
		}

		wcLogger.logTik(result ? ClockRequestStatus.TIK_SUCCESS : ClockRequestStatus.TIK_FAILURE, tik, true);
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
						if (onTik(tik))
							;
					}
				} else {
					BootStrap.getLogger().log(Level.SEVERE,
							"ERROR: Registering clock");
				}
			}
		} catch (JsonSyntaxException jse) {
			BootStrap.getLogger().log(Level.SEVERE, "ERROR: JSON syntax", jse);
		} catch (RuntimeException rte) {
			BootStrap.getLogger().log(Level.SEVERE, "ERROR : Runtime", rte);
		}
	}
}
