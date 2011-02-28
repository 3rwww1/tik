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

package be.okno.tik.tak.commons.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * Set the default localization to English, and the default timezone to UTC. 
 * 
 * @author erwan
 *
 */
public class LocaleManager {
	
	// Constant objects and values.
	private final static String C_DATEPATTERN = "yyyyMMDD-HHmmss-SSS";
	private final static Locale C_LOCALE = Locale.ENGLISH;
	private final static String C_TZSTR = "UTC";
	private final static TimeZone C_TZ = TimeZone.getTimeZone(C_TZSTR);
	
	static {
		TimeZone.setDefault(C_TZ);
		Locale.setDefault(C_LOCALE);
	}

	private LocaleManager()  {
		throw new AssertionError();
	}
	
	public static final SimpleDateFormat getDateFormatter() {
		return new SimpleDateFormat(C_DATEPATTERN, C_LOCALE);
	}
}
