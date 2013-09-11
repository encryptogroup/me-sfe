package crypt;

import java.math.BigInteger;

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



public class Present {
	/* Implementation of the PRESENT cipher */

	private static final BigInteger[] SBox = new BigInteger[] { BigInteger.valueOf(12),
			BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(11),
			BigInteger.valueOf(9), BigInteger.valueOf(0), BigInteger.valueOf(10),
			BigInteger.valueOf(13), BigInteger.valueOf(3), BigInteger.valueOf(14),
			BigInteger.valueOf(15), BigInteger.valueOf(8), BigInteger.valueOf(4),
			BigInteger.valueOf(7), BigInteger.valueOf(1), BigInteger.valueOf(2) };

	private static final BigInteger[] SBox_inv = new BigInteger[] { BigInteger.valueOf(5),
			BigInteger.valueOf(14), BigInteger.valueOf(15), BigInteger.valueOf(8),
			BigInteger.valueOf(12), BigInteger.valueOf(1), BigInteger.valueOf(2),
			BigInteger.valueOf(13), BigInteger.valueOf(11), BigInteger.valueOf(4),
			BigInteger.valueOf(6), BigInteger.valueOf(3), BigInteger.valueOf(0),
			BigInteger.valueOf(7), BigInteger.valueOf(9), BigInteger.valueOf(10) };

	private static final int[] PBox = new int[] { 0, 16, 32, 48, 1, 17, 33, 49, 2, 18, 34, 50, 3,
			19, 35, 51, 4, 20, 36, 52, 5, 21, 37, 53, 6, 22, 38, 54, 7, 23, 39, 55, 8, 24, 40, 56,
			9, 25, 41, 57, 10, 26, 42, 58, 11, 27, 43, 59, 12, 28, 44, 60, 13, 29, 45, 61, 14, 30,
			46, 62, 15, 31, 47, 63 };

	private static final int[] PBox_inv = new int[] { 0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44,
			48, 52, 56, 60, 1, 5, 9, 13, 17, 21, 25, 29, 33, 37, 41, 45, 49, 53, 57, 61, 2, 6, 10,
			14, 18, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58, 62, 3, 7, 11, 15, 19, 23, 27, 31, 35,
			39, 43, 47, 51, 55, 59, 63 };

	private static final BigInteger MASK4 = BigInteger.ONE.shiftLeft(4).subtract(BigInteger.ONE);

	private int rounds;
	private BigInteger[] roundKeys;

	public Present(BigInteger key, int rounds) throws Exception {
		this.rounds = rounds;
		// check key
		if (key.bitLength() > 128) {
			throw new Exception("Key too big. It has to be either 80 or 128 bit long.");
		}
		if (key.bitLength() > 80) {
			roundKeys = generateRoundkeys128(key, rounds);
		} else {
			roundKeys = generateRoundkeys80(key, rounds);
		}
	}

	public Present(BigInteger key) throws Exception {
		this(key, 32);
	}

	private BigInteger[] generateRoundkeys80(BigInteger key, int rounds) {
		BigInteger[] roundKeys = new BigInteger[rounds];
		BigInteger tmpKey = key;
		BigInteger mask19 = BigInteger.ONE.shiftLeft(19).subtract(BigInteger.ONE);
		BigInteger mask76 = BigInteger.ONE.shiftLeft(76).subtract(BigInteger.ONE);

		for (int i = 1; i <= rounds; i++) {
			roundKeys[i - 1] = tmpKey.shiftRight(16);
			// 1. shift
			tmpKey = tmpKey.and(mask19).shiftLeft(61).add(tmpKey.shiftRight(19));
			// 2. SBox
			tmpKey = SBox[tmpKey.shiftRight(76).intValue()].shiftLeft(76).add(tmpKey.and(mask76));
			// 3. Salt
			tmpKey = tmpKey.xor(BigInteger.valueOf(i).shiftLeft(15));
		}
		return roundKeys;
	}

	private BigInteger[] generateRoundkeys128(BigInteger key, int rounds) {
		BigInteger[] roundKeys = new BigInteger[rounds];
		BigInteger tmpKey = key;
		BigInteger mask67 = BigInteger.ONE.shiftLeft(67).subtract(BigInteger.ONE);
		BigInteger mask120 = BigInteger.ONE.shiftLeft(120).subtract(BigInteger.ONE);

		for (int i = 1; i <= rounds; i++) {
			roundKeys[i - 1] = tmpKey.shiftRight(64);
			// 1. shift
			tmpKey = tmpKey.and(mask67).shiftLeft(61).add(tmpKey.shiftRight(67));
			// 2. SBox
			tmpKey = SBox[tmpKey.shiftRight(124).intValue()]
					.shiftLeft(124)
					.add(SBox[(tmpKey.shiftRight(120).and(BigInteger.valueOf(15))).intValue()]
							.shiftLeft(120)).add(tmpKey.and(mask120));
			// 3. Salt
			tmpKey = tmpKey.xor(BigInteger.valueOf(i).shiftLeft(62));
		}
		return roundKeys;
	}

	public BigInteger encrypt(BigInteger message) {

		BigInteger state = message;
		for (int i = 0; i < rounds-1; i++) {
			state = addRoundKey(state, roundKeys[i]);
			state = sBoxLayer(state);
			state = pLayer(state);
		}
		return addRoundKey(state, roundKeys[rounds-1]);
	}

	public BigInteger decrypt(BigInteger cipher) {
		BigInteger state = cipher;
		for (int i = 0; i < rounds - 1; i++) {
			state = addRoundKey(state, roundKeys[rounds - 1 - i]);
			state = pLayer_dec(state);
			state = sBoxLayer_dec(state);
		}

		return addRoundKey(state, roundKeys[0]);
	}

	private BigInteger addRoundKey(BigInteger state, BigInteger roundKey) {
		return state.xor(roundKey);
	}

	private BigInteger sBoxLayer(BigInteger state) {
		BigInteger output = BigInteger.ZERO;
		for (int i = 0; i < 16; i++) {
			output = output.add(SBox[state.shiftRight(i * 4).and(MASK4).intValue()]
					.shiftLeft(i * 4));
		}
		return output;
	}

	private BigInteger sBoxLayer_dec(BigInteger state) {
		BigInteger output = BigInteger.ZERO;
		for (int i = 0; i < 16; i++) {
			output = output.add(SBox_inv[state.shiftRight(i * 4).and(MASK4).intValue()]
					.shiftLeft(i * 4));
		}
		return output;
	}

	private BigInteger pLayer(BigInteger state) {
		BigInteger output = BigInteger.ZERO;
		for (int i = 0; i < 64; i++) {
			if (state.testBit(i)) {
				output = output.setBit(PBox[i]);
			}
		}
		return output;
	}

	private BigInteger pLayer_dec(BigInteger state) {
		BigInteger output = BigInteger.ZERO;
		for (int i = 0; i < 64; i++) {
			if (state.testBit(i)) {
				output = output.setBit(PBox_inv[i]);
			}
		}
		return output;
	}
	
	public BigInteger[] getRoundKeys(){
		return roundKeys;
	}
	
}
