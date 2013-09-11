// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

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


public class OTExtReceiverLowerMemAES {
	private static SecureRandom rnd = new SecureRandom();
	private static final BigInteger MASK = BigInteger.ONE.shiftLeft(
			OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION).subtract(
			BigInteger.ONE);
	private int k1;
	private int k2;
	private int msgBitLength;
	private int msgByteLength;

	private Sender snder;
	private BitMatrix T;
	private BigInteger[][] keyPairs;
	protected BigInteger choices;
	protected DataInputStream dis;
	protected DataOutputStream dos;

	public OTExtReceiverLowerMemAES(DataInputStream in,
			DataOutputStream out) throws Exception {
		this.dis = in;
		this.dos = out;
		initialize();
	}

	public byte[][] execProtocol(BigInteger choices, int numOfChoices) throws Exception {

		byte[] zeroMsg, oneMsg;
		byte[] zeroCph, oneCph; 
		byte[] choicesb;
		byte[] y0 = new byte[msgByteLength], y1 = new byte[msgByteLength];
		byte[][] data = new byte[numOfChoices][];
		int sigma;
		BitMatrix Tt;

		int iterations = (numOfChoices - 1)
				/ OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION + 1;
		int elementsPerIteration = OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION;

		T = new BitMatrix(k1, elementsPerIteration);

		for (int i = 0; i < iterations; i++) {
			if (i == iterations - 1) {
				elementsPerIteration = numOfChoices - i
						* OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION;
				T = new BitMatrix(k1, elementsPerIteration);
			}
			T.initialize(rnd);

			choicesb = LabelMath.toByteArray(
							choices.shiftRight(i* OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION).and(MASK), 
							BitMath.byteCount(OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION));
			for (int j = 0; j < k1; j++) {
				zeroMsg = T.getRowData(j);
				oneMsg = LabelMath.xor(T.getRowData(j), choicesb);
				zeroCph = CipherAES.encrypt(i, 2*j,
						zeroMsg, elementsPerIteration, OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION);
				oneCph = CipherAES.encrypt(i, 2*j+1,
						oneMsg, elementsPerIteration, OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION);
				dos.write(zeroCph);
				dos.write(oneCph);
			}
			
			dos.flush();
			Tt = T.transpose();

			for (int j = 0; j < elementsPerIteration; j++) {
				dis.readFully(y0);
				dis.readFully(y1);
				sigma = choices.testBit(i
						* OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION
						+ j) ? 1 : 0;
				data[i * OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION + j] = Cipher
						.decrypt(i*OTExtSenderLowerMemAES.MAX_NUM_OF_PAIRS_PER_ITERATION+j, Tt.getRowData(j), sigma == 0 ? y0 : y1,
								msgBitLength);
			}
		}
		return data;
	}

	private void initialize() throws Exception {
		k1 = dis.readInt();
		k2 = dis.readInt();
		msgBitLength = dis.readInt();
		msgByteLength = (msgBitLength - 1) / 8 + 1;

		snder = new NPOTSenderMultiThreading(k1, k2, dis, dos);

		keyPairs = new BigInteger[k1][2];
		for (int i = 0; i < k1; i++) {
			keyPairs[i][0] = new BigInteger(k2, rnd);
			keyPairs[i][1] = new BigInteger(k2, rnd);
		}

		CipherAES.init(k1,k2,keyPairs);

		snder.execProtocol(keyPairs);
	}
}
