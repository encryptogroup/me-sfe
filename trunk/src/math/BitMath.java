package math;


/***************************************************************************************************
*
*
* This file is part of ME-SFE, a secure two-party computation framework.
*
* Copyright (c) 2012 - 2013 Wilko Henecka and Thomas Schneider
*
* ME-SFE is free software; you can redistribute it and/or modify it under the terms of the
* GNU General Public License as published by the Free Software Foundation; either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with this program.  
* If not, see <http://www.gnu.org/licenses/>.
*
* Getting Source ==============
*
* Source for this application is maintained at code.google.com, a repository for free software
* projects.
*
* For details, please see http://code.google.com/p/me-sfe/
*/


public class BitMath {

	/**
	 * returns the bit length of the integer i
	 */
	public static int bitLength(int i){
		return ceil_log(i+1);
	}
	
	public static int[] ceil_log_with_n(int i) {
		// return p,n with p = min_k(i<=2**k) and n = 2**p
		int power = 0;
		int n = 1;
		do {
			n *= 2;
			power++;
		} while (n < i);
		return new int[] { power, n };
	}
	
	public static int ceil_log(int i) {
		// return p with p = min_k(i<=2**k)
		return ceil_log_with_n(i)[0];
	}
	
	/**
	 * returns the number of bytes needed to fit in numberOfBits
	 */
	public static int byteCount(int numberOfBits){
		return (numberOfBits+7)/8;
	}
}
