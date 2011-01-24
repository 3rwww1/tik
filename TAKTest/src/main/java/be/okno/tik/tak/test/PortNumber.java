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

package be.okno.tik.tak.test;

public class PortNumber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String portStrTab[] = {"TIK", "TAK"};
		
		for (int i = 0, len = portStrTab.length; i < len; ++i)
		{
			computePortNumberFromString(portStrTab[i]);
		}
	}

	private static void computePortNumberFromString(String portStr) {

		byte[] portStrBytes = portStr.getBytes();
		

		StringBuilder sb = new StringBuilder(6);

		for (byte portStrByte : portStrBytes) {

			sb.append(Byte.toString(portStrByte));
		}
		String portBytesStr = sb.toString();
		int portBytesStrIntValue = Integer.parseInt(portBytesStr);
		int portMod = 49152 - 1024;
		int portNbr = (portBytesStrIntValue % portMod) + 1024;
		System.out.println(portNbr);
	}
}
