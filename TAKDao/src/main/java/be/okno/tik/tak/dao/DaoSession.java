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

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.dao.mapper.ClockMapper;

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
	
	// MyBatis SQL session.
	private SqlSession sqlSession;
	
	// MyBatis clock mapping.
	private ClockMapper mapper;

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
			props.load(reader);
		} catch (FileNotFoundException e) {
			System.err.println(E_NOTFNDCONFILE);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println(E_IOCONFFILE);
			e.printStackTrace();
			System.exit(1);
		}
		
		Set<Entry<Object, Object>> propSet = props.entrySet();
		
		System.err.println(I_PROPLIST);
		for (Entry<Object, Object> entry : propSet) {
			System.err.println(entry.getKey().toString() + C_SP + C_COL + C_SP + entry.getValue().toString());
		}
		Configuration cnf = new Configuration();
		Environment env;
		TransactionFactory trf = new JdbcTransactionFactory();
		DataSource ds = new UnpooledDataSourceFactory().getDataSource();
		env = new Environment("TAK", trf, ds);
		
		cnf.setEnvironment(env);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(cnf);
		sqlSession = sqlSessionFactory.openSession();
		mapper = sqlSession.getMapper(ClockMapper.class);
	}

	public static synchronized DaoSession getInstance() {
		return instance;
	}

	public synchronized Clock getClockById(Integer idClock) {
		return mapper.selectByPrimaryKey(idClock);
	}

	public synchronized List<Clock> getAllClocks() {
		return mapper.selectAllClocks();
	}

	public synchronized boolean updateClock(Clock clock) {
//		ClockMapper mapper = sqlSession.getMapper(ClockMapper.class);
//		int result = mapper.updateByPrimaryKey(clock);
		return true;
	}

	@Override
	public void finalize() {
		sqlSession.close();
	}
}
