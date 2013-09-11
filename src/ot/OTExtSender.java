// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;


import java.util.*;
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


public class OTExtSender {
	static final class SecurityParameter {
		public static final int k1 = 80; // number of columns in T
		public static final int k2 = 80;
	}

	private static SecureRandom rnd = new SecureRandom();
	private Receiver rcver;
	private BigInteger s;
	private BigInteger[] keys;
	protected DataInputStream dis;
    protected DataOutputStream dos;
    protected int numOfPairs;
    protected int msgBitLength;

	public OTExtSender(int numOfPairs, int msgBitLength, DataInputStream in,
			DataOutputStream out) throws Exception {
		this.dis = in;
		this.dos = out;
		this.numOfPairs = numOfPairs;
		this.msgBitLength = msgBitLength;
		initialize();
	}

	public void execProtocol(byte[][] zeroMsgs) throws Exception {

		byte[][] zeroCphs = new byte[SecurityParameter.k1][BitMath.byteCount(numOfPairs)];
		byte[][] oneCphs = new byte[SecurityParameter.k1][BitMath.byteCount(numOfPairs)];
		for(int i=0; i<SecurityParameter.k1; i++){
			dis.readFully(zeroCphs[i]);
			dis.readFully(oneCphs[i]);
		}
		BitMatrix Q = new BitMatrix(SecurityParameter.k1, numOfPairs);

		for (int i = 0; i < SecurityParameter.k1; i++) {
			if (s.testBit(i)){
				Q.setRowData(i, Cipher.decrypt(keys[i].toByteArray(), oneCphs[i], numOfPairs));
			}else{
				Q.setRowData(i, Cipher.decrypt(keys[i].toByteArray(), zeroCphs[i], numOfPairs));
			}
		}

		BitMatrix tQ = Q.transpose();

		byte[] y0, y1;
		byte[] sb = LabelMath.toByteArray(s, BitMath.byteCount(SecurityParameter.k1));
		for (int i = 0; i < numOfPairs; i++) {
			y0 = Cipher.encrypt(i, tQ.getRowData(i), zeroMsgs[i], msgBitLength);
			y1 = Cipher.encrypt(i, LabelMath.xor(tQ.getRowData(i),sb), LabelMath.conjugate(zeroMsgs[i]), msgBitLength);
			dos.write(y0);
			dos.write(y1);
		}		
		dos.flush();
	}

	private void initialize() throws Exception {
		dos.writeInt(SecurityParameter.k1);
		dos.writeInt(SecurityParameter.k2);
		dos.writeInt(msgBitLength);
		dos.flush();
		rcver = new NPOTReceiverOld(SecurityParameter.k1, dis, dos);
		s = new BigInteger(SecurityParameter.k1, rnd);
		rcver.execProtocol(s);
		keys = rcver.getData();
	}
}
