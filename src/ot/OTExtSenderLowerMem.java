//Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.security.SecureRandom;
import java.math.*;
import java.io.*;

import math.BitMath;
import math.LabelMath;


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


public class OTExtSenderLowerMem {
	static class SecurityParameter {
		public static final int k1 = 80; // number of columns in T
		public static final int k2 = 80;
	}

	public static final int MAX_NUM_OF_PAIRS_PER_ITERATION = 8192;

	private static SecureRandom rnd = new SecureRandom();
	private BigInteger s;
	private BigInteger[] keys;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected int msgBitLength;

	public OTExtSenderLowerMem(int msgBitLength,
			DataInputStream in, DataOutputStream out) throws Exception {
		this.dis = in;
		this.dos = out;
		this.msgBitLength = msgBitLength;
		initialize();
	}

	public void execProtocol(byte[][] zeroMsgs) throws Exception {
		int numOfPairs = zeroMsgs.length;
		int iterations = (numOfPairs - 1) / MAX_NUM_OF_PAIRS_PER_ITERATION + 1;
		int elementsPerIteration = MAX_NUM_OF_PAIRS_PER_ITERATION;
		byte[] zeroCph = new byte[BitMath.byteCount(elementsPerIteration)];
		byte[] oneCph = new byte[BitMath.byteCount(elementsPerIteration)];
		BitMatrix Q = new BitMatrix(SecurityParameter.k1, elementsPerIteration);
		byte[] columnData;
		byte[] y0, y1;
		byte[] sb = LabelMath.toByteArray(s, BitMath
				.byteCount(SecurityParameter.k1));

		for (int i = 0; i < iterations; i++) {
			if (i == iterations - 1) {
				elementsPerIteration = numOfPairs - i
						* MAX_NUM_OF_PAIRS_PER_ITERATION;
				Q = new BitMatrix(SecurityParameter.k1, elementsPerIteration);
				zeroCph = new byte[BitMath.byteCount(elementsPerIteration)];
				oneCph = new byte[BitMath.byteCount(elementsPerIteration)];
			}
			
			for (int j = 0; j < SecurityParameter.k1; j++) {
				dis.readFully(zeroCph);
				dis.readFully(oneCph);
				if (s.testBit(j)) {
					Q.setRowData(j, Cipher.decrypt(i, keys[j].toByteArray(),
							oneCph, elementsPerIteration));
				} else {
					Q.setRowData(j, Cipher.decrypt(i, keys[j].toByteArray(),
							zeroCph, elementsPerIteration));
				}			
			}

			for (int j = 0; j < elementsPerIteration; j++) {
				columnData = Q.getColumnData(j);
				y0 = Cipher.encrypt(i*MAX_NUM_OF_PAIRS_PER_ITERATION+j, columnData, zeroMsgs[i
						* MAX_NUM_OF_PAIRS_PER_ITERATION + j], msgBitLength);
				y1 = Cipher.encrypt(i*MAX_NUM_OF_PAIRS_PER_ITERATION+j, LabelMath.xor(columnData, sb),
						LabelMath.conjugate(zeroMsgs[i
								* MAX_NUM_OF_PAIRS_PER_ITERATION + j]),
						msgBitLength);
				dos.write(y0);
				dos.write(y1);
			}

			dos.flush();
		}
	}

	private void initialize() throws Exception {
		dos.writeInt(SecurityParameter.k1);
		dos.writeInt(SecurityParameter.k2);
		dos.writeInt(msgBitLength);
		dos.flush();

		Receiver rcver = new NPOTReceiverMultiThreading(SecurityParameter.k1, dis, dos);
		s = new BigInteger(SecurityParameter.k1, rnd);
		rcver.execProtocol(s);
		keys = rcver.getData();
	}
}
