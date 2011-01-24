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

package be.okno.tik.tak.commons.model;

public class MetaDataDefinition {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column mddef.id_mddef
	 * @mbggenerated
	 */
	private Integer idMddef;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column mddef.name
	 * @mbggenerated
	 */
	private String name;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column mddef.type
	 * @mbggenerated
	 */
	private String type;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column mddef.id_mddef
	 * @return  the value of mddef.id_mddef
	 * @mbggenerated
	 */
	public Integer getIdMddef() {
		return idMddef;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column mddef.id_mddef
	 * @param idMddef  the value for mddef.id_mddef
	 * @mbggenerated
	 */
	public void setIdMddef(Integer idMddef) {
		this.idMddef = idMddef;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column mddef.name
	 * @return  the value of mddef.name
	 * @mbggenerated
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column mddef.name
	 * @param name  the value for mddef.name
	 * @mbggenerated
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column mddef.type
	 * @return  the value of mddef.type
	 * @mbggenerated
	 */
	public String getType() {
		return type;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column mddef.type
	 * @param type  the value for mddef.type
	 * @mbggenerated
	 */
	public void setType(String type) {
		this.type = type;
	}
}