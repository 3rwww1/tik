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
import java.util.Random;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.dao.DaoSession;

public class SampleClock {

	public void duplicateClocksLauncher() {

		int minSleepMillis = 200;
		int maxSleepMillis = 3000;
		Clock clock = DaoSession.getInstance().getClockById(1);
		List<MetaDataDefinition> mdDefs = new ArrayList<MetaDataDefinition>();
		MetaDataDefinition mdDef = new MetaDataDefinition();

		mdDef.setName("orientation");
		mdDef.setType("float");
		mdDefs.add(mdDef);

		clock.setMetaDataDefinitions(mdDefs);
		
		Random rng = new Random(System.currentTimeMillis());
		new Thread(new TestClock(clock, maxSleepMillis, minSleepMillis, true, rng)).run();
	}
	
	public static void main(String... args) {
		new SampleClock().duplicateClocksLauncher(); 
	}
}
