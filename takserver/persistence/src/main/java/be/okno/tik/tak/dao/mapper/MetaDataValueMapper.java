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

import be.okno.tik.tak.commons.model.MetaDataValue;
import org.apache.ibatis.annotations.Param;

public interface MetaDataValueMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table mdval
	 * @mbggenerated
	 */
	int deleteByPrimaryKey(@Param("idMddef") Integer idMddef,
			@Param("idTik") Long idTik);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table mdval
	 * @mbggenerated
	 */
	int insert(MetaDataValue record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table mdval
	 * @mbggenerated
	 */
	int insertSelective(MetaDataValue record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table mdval
	 * @mbggenerated
	 */
	MetaDataValue selectByPrimaryKey(@Param("idMddef") Integer idMddef,
			@Param("idTik") Long idTik);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table mdval
	 * @mbggenerated
	 */
	int updateByPrimaryKeySelective(MetaDataValue record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table mdval
	 * @mbggenerated
	 */
	int updateByPrimaryKey(MetaDataValue record);
	
}