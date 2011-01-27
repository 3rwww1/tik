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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.dao.DaoSession;

public class StressingTiks {

	public void stressingTiksLauncher() {

		ExecutorService executor = Executors.newCachedThreadPool();
		List<Clock> clocks;

		Random rng = new Random(System.currentTimeMillis());
		clocks = DaoSession.getInstance().getAllClocks();
		for (Clock clock : clocks) {
			List<MetaDataDefinition> mdDefs = new ArrayList<MetaDataDefinition>();
			
			MetaDataDefinition mdDef = new MetaDataDefinition();

			mdDef.setIdMddef(1);
			mdDef.setName("orientation");
			mdDef.setType("float");

			mdDefs.add(mdDef);

			mdDef = new MetaDataDefinition();

			mdDef.setIdMddef(2);
			mdDef.setType("long");
			mdDef.setName("windspeed");

			mdDefs.add(mdDef);

			int minRateMillis = rng.nextInt(4000 / rng.nextInt(50));
			int maxRateMillis = minRateMillis
					+ rng.nextInt((8000 / rng.nextInt(20)) - minRateMillis);

			clock.setMetaDataDefinitions(mdDefs);
			executor.execute(new TestClock(clock, maxRateMillis, minRateMillis,
					false, rng));
		}
	}

	public static void main(String[] args) {
		new StressingTiks().stressingTiksLauncher();
	}
}