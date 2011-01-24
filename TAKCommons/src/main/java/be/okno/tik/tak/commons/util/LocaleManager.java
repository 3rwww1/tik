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

public class LocaleManager {
	
	private final static String pattern = "yyyyMMDD-HHmmss-SSS";
	private final static Locale locale = Locale.ENGLISH;
	private final static String strTimeZone = "UTC";
	private final static TimeZone timeZone = TimeZone.getTimeZone(strTimeZone);
	static {
		TimeZone.setDefault(timeZone);
		Locale.setDefault(locale);
	}
	
	private LocaleManager()  {
		
	}
	
	public static final SimpleDateFormat getDateFormatter() {
		return new SimpleDateFormat(pattern, locale);
	}
}
