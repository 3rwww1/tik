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

import be.okno.tik.tak.commons.model.RelClockMetaDataDefinition;
import org.apache.ibatis.annotations.Param;

public interface RelClockMetaDataDefinitionMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock2mddef
	 * @mbggenerated
	 */
	int deleteByPrimaryKey(@Param("idClock") Integer idClock,
			@Param("idMddef") Integer idMddef);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock2mddef
	 * @mbggenerated
	 */
	int insert(RelClockMetaDataDefinition record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table clock2mddef
	 * @mbggenerated
	 */
	int insertSelective(RelClockMetaDataDefinition record);
}