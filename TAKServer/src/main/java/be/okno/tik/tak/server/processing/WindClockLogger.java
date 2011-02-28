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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.commons.model.MetaDataValue;
import be.okno.tik.tak.commons.model.Tik;
import be.okno.tik.tak.commons.util.LocaleManager;
import be.okno.tik.tak.server.Launcher;

public class WindClockLogger {
	
	private StringBuilder sb;
	private SimpleDateFormat fmt;
	private Map<Integer, MetaDataDefinition> mdDefsMap;
	
	protected enum ClockRequestStatus {
		/** Log on request */
		LOGON_REQUEST,
		/** Log on persistence */
		LOGON_PERSIST, 
		/** Log on XMMP connection */
		LOGON_XMPPCON, 
		/** Log on success */
		LOGON_SUCCESS, 
		/** Log on failure */
		LOGON_FAILURE, 
		/** TIK request */ 
		TIK_REQUEST, 
		/** TIK success */ 
		TIK_SUCCESS, 
		/** TIK persistence */
		TIK_PERSIST,
		/** TIK XMPP send */
		TIK_XMPPSEND, 
		/** TIK failure */
		TIK_FAILURE;
	}
	
	protected WindClockLogger() {
		fmt = LocaleManager.getDateFormatter();
		sb = new StringBuilder();
	}

	protected void setMetaDataDefintionsMap(Map<Integer, MetaDataDefinition> mdDefsMap) {
		this.mdDefsMap = mdDefsMap;
	}
	
	private void logMetaDataDefinitions(List<MetaDataDefinition> mdDefs) {
		
		int nbMdDefs = mdDefs == null ? 0 : mdDefs.size(); 
		
		if (nbMdDefs != 0) {
			sb.append(" WITH ");
			sb.append(nbMdDefs);
			sb.append(" METADATAS:");

			for (MetaDataDefinition metaDataDefinition : mdDefs) {
				sb.append("\n\t");
				sb.append("META name=[");
				sb.append(metaDataDefinition.getName());
				sb.append("], type=[");
				sb.append(metaDataDefinition.getType());
				sb.append("]");
			}
		} else
			sb.append(" WITH NO METADATAS");
	}
	
	private void logMetaDataValues(List<MetaDataValue> mdVals) {

		int nbMdVals;

		if (mdVals != null && (nbMdVals = mdVals.size()) != 0) {
			sb.append(" WITH ");
			sb.append(nbMdVals);
			sb.append(" METADATA VALUES:");
			for (MetaDataValue mdVal : mdVals) {

				MetaDataDefinition mdDef = mdDefsMap.get(mdVal.getIdMddef());
				sb.append("\n\t");
				sb.append("META name=[");
				sb.append(mdDef.getName());
				sb.append("],type=[");
				sb.append(mdDef.getType());
				sb.append("],value=[");
				sb.append(mdVal.getValue());
				sb.append("]");
			}
		} else {
			sb.append(" WITH NO METADATA VALUES");
		}
	}
	
	public void logClock(Clock clock, ClockRequestStatus status, boolean logMdDefs) {
		sb.append(status);
		sb.append(" clock name=[");
		sb.append(clock.getName());
		sb.append("],id=[");
		sb.append(clock.getIdClock());
		sb.append("]");
		if (logMdDefs) {
			logMetaDataDefinitions(clock.getMetaDataDefinitions());
		}
		Launcher.getLogger().info(sb.toString());
		sb.delete(0, sb.length());
	}

	public void logTik(ClockRequestStatus status, Tik tik, boolean logMdVals) {
		sb.append(status);
		sb.append(" tik clockId=[");
		sb.append(tik.getIdClock());
		sb.append("],arrivalTime=[");
		sb.append(fmt.format(new Date()));
		sb.append("]");
		if (logMdVals)
			logMetaDataValues(tik.getMetaDataValues());
		Launcher.getLogger().info(sb.toString());
		sb.delete(0, sb.length());
	}
}
