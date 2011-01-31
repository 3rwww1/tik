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

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.dao.mapper.ClockMapper;

public class DaoSession {

	static DaoSession instance = new DaoSession();

	private SqlSessionFactory sqlSessionFactory;
	private static final String CONF_DAOFILE = "DaoSessionConfig.xml";
	private SqlSession sqlSession;
	private ClockMapper mapper;

	private DaoSession() {

		Reader reader;
		try {
			reader = Resources.getResourceAsReader(CONF_DAOFILE);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			System.err.println("Can't read Dao configuration file");
			e.printStackTrace();
			System.exit(1);
		}
		sqlSession = sqlSessionFactory.openSession();
		mapper = sqlSession.getMapper(ClockMapper.class);
	}

	public static DaoSession getInstance() {
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
