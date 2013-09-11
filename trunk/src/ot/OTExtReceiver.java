// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.math.*;
import java.io.*;

import math.BitMath;
import math.LabelMath;

import java.security.SecureRandom;

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


public class OTExtReceiver {
	private static SecureRandom rnd = new SecureRandom();

	private int k1;
	private int k2;
	private int msgBitLength;

	private Sender snder;
	private BitMatrix T;
	private BigInteger[][] keyPairs;
	protected BigInteger choices;
    protected int numOfChoices;
    protected DataInputStream dis;
    protected DataOutputStream dos;

	public OTExtReceiver(int numOfChoices, DataInputStream in,
			DataOutputStream out) throws Exception {
		this.dis = in;
		this.dos = out;
		this.numOfChoices = numOfChoices;
		initialize();
	}

	public byte[][] execProtocol(BigInteger choices) throws Exception {
		
		byte[][] zeroMsgs = new byte[k1][];
		byte[][] oneMsgs = new byte[k1][];
		byte[][] zeroCphs = new byte[k1][];
		byte[][] oneCphs = new byte[k1][];
		byte[] choicesb = LabelMath.toByteArray(choices, BitMath.byteCount(numOfChoices));

		for (int i = 0; i < k1; i++) {
			zeroMsgs[i] = T.getRowData(i);
			oneMsgs[i] = LabelMath.xor(T.getRowData(i), choicesb);
			
			zeroCphs[i] = Cipher.encrypt(keyPairs[i][0].toByteArray(), zeroMsgs[i],
					numOfChoices);
			oneCphs[i] = Cipher.encrypt(keyPairs[i][1].toByteArray(), oneMsgs[i],
					numOfChoices);
		}
		for(int i=0; i<k1; i++){
			dos.write(zeroCphs[i]);
			dos.write(oneCphs[i]);
		}
		dos.flush();
		int bytelength;

		BitMatrix tT = T.transpose();

		
		bytelength = (msgBitLength - 1) / 8 + 1;
		byte[] y0 = new byte[bytelength], y1 = new byte[bytelength];
		byte[][] data = new byte[numOfChoices][];
		for (int i = 0; i < numOfChoices; i++) {
			dis.readFully(y0);
			dis.readFully(y1);
			data[i] = Cipher.decrypt(i, tT.getRowData(i), choices.testBit(i) ? y1 : y0, msgBitLength);
		}
		return data;
	}

	private void initialize() throws Exception {
		k1 = dis.readInt();
		k2 = dis.readInt();
		msgBitLength = dis.readInt();

		//StopWatch.measurement("NPOTSender creation").start();
		snder = new NPOTSenderOld(k1, k2, dis, dos);
		//StopWatch.measurement("NPOTSender creation").stop();
		T = new BitMatrix(k1, numOfChoices);
		T.initialize(rnd);

		keyPairs = new BigInteger[k1][2];
		for (int i = 0; i < k1; i++) {
			keyPairs[i][0] = new BigInteger(k2, rnd);
			keyPairs[i][1] = new BigInteger(k2, rnd);
		}
		
		//StopWatch.measurement("NPOTSender.execProtocol").start();
		snder.execProtocol(keyPairs);
		//StopWatch.measurement("NPOTSender.execProtocol").stop();
	}
}
