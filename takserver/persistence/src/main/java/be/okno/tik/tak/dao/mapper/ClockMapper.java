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

package be.okno.tik.tak.dao.mapper;

import java.util.List;

import be.okno.tik.tak.commons.model.Clock;

public interface ClockMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock
	 * @mbggenerated
	 */
	int deleteByPrimaryKey(Integer idClock);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock
	 * @mbggenerated
	 */
	int insert(Clock record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock
	 * @mbggenerated
	 */
	int insertSelective(Clock record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock
	 * @mbggenerated
	 */
	Clock selectByPrimaryKey(Integer idClock);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock
	 * @mbggenerated
	 */
	int updateByPrimaryKeySelective(Clock record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock
	 * @mbggenerated
	 */
	int updateByPrimaryKey(Clock record);

	List<Clock> selectAllClocks();
	
	Clock selectClockByName(String name);
	
	int insertNewClock(Clock clock);
}