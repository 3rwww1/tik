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

import java.util.List;

public class Clock {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column clock.id_clock
	 * 
	 * @mbggenerated
	 */
	private Integer idClock;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column clock.name
	 * 
	 * @mbggenerated
	 */
	private String name;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column clock.longitude
	 * 
	 * @mbggenerated
	 */
	private Double longitude;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column clock.latitude
	 * 
	 * @mbggenerated
	 */
	private Double latitude;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to
	 * the database column clock.altitude
	 * 
	 * @mbggenerated
	 */
	private Double altitude;

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column clock.id_clock
	 * 
	 * @return the value of clock.id_clock
	 * @mbggenerated
	 */
	public Integer getIdClock() {
		return idClock;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column clock.id_clock
	 * 
	 * @param idClock
	 *            the value for clock.id_clock
	 * @mbggenerated
	 */
	public void setIdClock(Integer idClock) {
		this.idClock = idClock;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column clock.name
	 * 
	 * @return the value of clock.name
	 * @mbggenerated
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column clock.name
	 * 
	 * @param name
	 *            the value for clock.name
	 * @mbggenerated
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column clock.longitude
	 * 
	 * @return the value of clock.longitude
	 * @mbggenerated
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column clock.longitude
	 * 
	 * @param longitude
	 *            the value for clock.longitude
	 * @mbggenerated
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column clock.latitude
	 * 
	 * @return the value of clock.latitude
	 * @mbggenerated
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column clock.latitude
	 * 
	 * @param latitude
	 *            the value for clock.latitude
	 * @mbggenerated
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the
	 * value of the database column clock.altitude
	 * 
	 * @return the value of clock.altitude
	 * @mbggenerated
	 */
	public Double getAltitude() {
		return altitude;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the
	 * value of the database column clock.altitude
	 * 
	 * @param altitude
	 *            the value for clock.altitude
	 * @mbggenerated
	 */
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	private List<MetaDataDefinition> metaDataDefinitions;

	public void setMetaDataDefinitions(
			List<MetaDataDefinition> metaDatadefinitions) {
		this.metaDataDefinitions = metaDatadefinitions;
	}

	public List<MetaDataDefinition> getMetaDataDefinitions() {
		return metaDataDefinitions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idClock == null) ? 0 : idClock.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clock other = (Clock) obj;
		if (idClock == null) {
			if (other.idClock != null)
				return false;
		} else if (!idClock.equals(other.idClock))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}