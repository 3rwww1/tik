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

package be.okno.tik.tak.dao;

import static be.okno.tik.tak.commons.util.Constants.C_COL;
import static be.okno.tik.tak.commons.util.Constants.C_SP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.MetaDataDefinition;
import be.okno.tik.tak.commons.model.MetaDataValue;
import be.okno.tik.tak.commons.model.RelClockMetaDataDefinition;
import be.okno.tik.tak.commons.model.Tik;
import be.okno.tik.tak.dao.mapper.ClockMapper;
import be.okno.tik.tak.dao.mapper.MetaDataDefinitionMapper;
import be.okno.tik.tak.dao.mapper.MetaDataValueMapper;
import be.okno.tik.tak.dao.mapper.RelClockMetaDataDefinitionMapper;
import be.okno.tik.tak.dao.mapper.TikMapper;

public class DaoSession {

	private static final String F_DAOPROPS = "takdao.properties";

	// Resources names.
	private static final String R_DAOCONF = "DaoSessionConfig.xml";
	private static final String R_DEFDAOPROPS = "takdao-defaults.properties";

	// Info messages.
	private static final String I_NOTFNDPROPFILE = "TAK DAO user-defined properties file not found, keeping default settings.";
	private static final String I_PROPLIST = "Listing TAK DAO properties.";

	// Error messages.
	private static final String E_IOCONFFILE = "I/O error while opening MyBatis XML configuration file";
	private static final String E_NOTFNDCONFILE = "MyBatis XML configuration file not found.";
	private static final String E_IODEFPROPFILE = "I/O error while opening TAK DAO default (jar-embedded) properties file.";
	private static final String E_NOTFNDDEFPROPFILE = "TAK DAO default (jar-embedded) properties file not found.";
	private static final String E_IOPROPFILE = "I/O error while reading TAK DAO user-defined properties file.";

	// Singleton instance.
	private static DaoSession instance = new DaoSession();

	// MyBatis SQL session factory.
	private static SqlSessionFactory sqlSessionFactory;

	// time counter
	private static long msTiks = System.currentTimeMillis();

	// tik counter
	private static int nbTiks = 0;

	// MyBatis SQL session.
	private SqlSession sqlSession;

	// MyBatis clock mapping.
	private ClockMapper clockMapper;

	// MyBatis meta data definition mapping object.
	private MetaDataDefinitionMapper mdDefMapper;

	// MyBatis meta data values mapping object.
	private MetaDataValueMapper mdValMapper;

	// MyBatis TIK mapping object.
	private TikMapper tikMapper;

	// MyBatis meta data definition / clock relation mapping object
	private RelClockMetaDataDefinitionMapper clkMdDefMapper;

	private DaoSession() {
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(R_DEFDAOPROPS));
		} catch (FileNotFoundException e) {
			System.err.println(E_NOTFNDDEFPROPFILE);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println(E_IODEFPROPFILE);
			e.printStackTrace();
			System.exit(1);
		}

		try {
			props.load(new FileInputStream(F_DAOPROPS));
		} catch (FileNotFoundException e) {
			System.err.println(I_NOTFNDPROPFILE);
		} catch (IOException e) {
			System.err.println(E_IOPROPFILE);
			e.printStackTrace();
			System.exit(1);
		}

		Reader reader;
		try {
			reader = Resources.getResourceAsReader(R_DAOCONF);

			Set<Entry<Object, Object>> propSet = props.entrySet();

			System.err.println(I_PROPLIST);
			for (Entry<Object, Object> entry : propSet) {
				System.err.println(entry.getKey().toString() + C_SP + C_COL
						+ C_SP + entry.getValue().toString());
			}

			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,
					props);
			sqlSession = sqlSessionFactory.openSession();
			clockMapper = sqlSession.getMapper(ClockMapper.class);
			mdDefMapper = sqlSession.getMapper(MetaDataDefinitionMapper.class);
			mdValMapper = sqlSession.getMapper(MetaDataValueMapper.class);
			tikMapper = sqlSession.getMapper(TikMapper.class);
			clkMdDefMapper = sqlSession
					.getMapper(RelClockMetaDataDefinitionMapper.class);
		} catch (FileNotFoundException e) {
			System.err.println(E_NOTFNDCONFILE);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println(E_IOCONFFILE);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static synchronized DaoSession getInstance() {
		return instance;
	}

	public synchronized List<Clock> getAllClocks() {
		return clockMapper.selectAllClocks();
	}

	public synchronized Clock getClockByName(Clock clock) {
		return clockMapper.selectClockByName(clock.getName());
	}

	public synchronized void newClock(Clock clock) {
		clockMapper.insertNewClock(clock);
		System.err.println("NEW CLOCK ID : " + clock.getIdClock());

		if (clock.getMetaDataDefinitions() != null) {

			for (MetaDataDefinition mdDef : clock.getMetaDataDefinitions()) {
				MetaDataDefinition dbMdDef = mdDefMapper
						.selectByNameAndType(mdDef);
				if (dbMdDef == null) {
					newMetaDataDefinition(mdDef);

				} else {
					mdDef = dbMdDef;
				}
				RelClockMetaDataDefinition clkMdDef = new RelClockMetaDataDefinition();

				clkMdDef.setIdMddef(mdDef.getIdMddef());
				clkMdDef.setIdClock(clock.getIdClock());
				clkMdDefMapper.insert(clkMdDef);
			}
		}
		sqlSession.commit(true);
	}

	public synchronized Set<MetaDataDefinition> getMetaDataDefinitionsForClock(
			Clock clock) {
		return null;
	}

	public synchronized void newTik(Tik tik) {
		tikMapper.insertWithIdClock(tik);
		System.err.println("NEW TIK ID : " + tik.getIdTik());
		if (tik.getMetaDataValues() != null) {
			for (MetaDataValue mdVal : tik.getMetaDataValues()) {
				mdVal.setIdTik(tik.getIdTik());
				mdValMapper.insert(mdVal);
			}
		}
		if (++nbTiks == 10 || System.currentTimeMillis() - msTiks > 1500) {
			nbTiks = 0;
			msTiks = System.currentTimeMillis();
			sqlSession.commit();
		}
	}

	private void newMetaDataDefinition(MetaDataDefinition metaDataDefinition) {
		mdDefMapper.insertNewMetaDataDefinition(metaDataDefinition);
		System.err.println("NEW MDDEF ID : " + metaDataDefinition.getIdMddef());
	}

	@Override
	public void finalize() {
		sqlSession.close();
	}
}
