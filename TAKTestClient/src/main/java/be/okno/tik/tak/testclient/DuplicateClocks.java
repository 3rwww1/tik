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

import java.util.ArrayList;
import java.util.List;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.dao.DaoSession;

public class DuplicateClocks {

	public void duplicateClocksLauncher() {

		int minSleepMillis = 1900;
		int maxSleepMillis = 2000;
		Clock clock = DaoSession.getInstance().getClockById(1);
		List<MetaDataDefinition> mdDefs = new ArrayList<MetaDataDefinition>();
		MetaDataDefinition mdDef = new MetaDataDefinition();

		mdDef.setName("orientation");
		mdDef.setType("float");
		mdDefs.add(mdDef);

		clock.setMetaDataDefinitions(mdDefs);
		new Thread(new TestClock(clock, maxSleepMillis, minSleepMillis)).run();
		System.out.println("here we go");
		new Thread(new TestClock(clock, maxSleepMillis, minSleepMillis)).run();
		System.out.println("here we go again");
	}
	
	public static void main(String... args) {
		new DuplicateClocks().duplicateClocksLauncher(); 
	}
}
