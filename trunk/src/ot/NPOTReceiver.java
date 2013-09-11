// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import io.NetUtils;

import java.math.*;
import java.io.*;

import math.BitMath;

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


public class NPOTReceiver extends Receiver {
	private static SecureRandom rnd = new SecureRandom();

	private int msgBitLength;
	private BigInteger p, q, g, C;
	private BigInteger gr;

	private BigInteger[] gk, C_over_gk;

	private BigInteger[] keys;
	private int byteCount;
	private int msgByteCount;

	public NPOTReceiver(int numOfChoices, DataInputStream in, DataOutputStream out)
			throws Exception {
		super(numOfChoices, in, out);
		initialize();
	}

	public void execProtocol(BigInteger choices) throws Exception {
		super.execProtocol(choices);

		step1();
		step2();
	}

	private void initialize() throws Exception {

		C = NetUtils.readBigInteger(dis);
		p = NetUtils.readBigInteger(dis);
		q = NetUtils.readBigInteger(dis);
		g = NetUtils.readBigInteger(dis);
		gr = NetUtils.readBigInteger(dis); 
		msgBitLength = dis.readInt();
		byteCount = BitMath.byteCount(NPOTSender.pLength);
		msgByteCount = BitMath.byteCount(msgBitLength);

		gk = new BigInteger[numOfChoices];
		C_over_gk = new BigInteger[numOfChoices];
		keys = new BigInteger[numOfChoices];


		for (int i = 0; i < numOfChoices; i++) {
			BigInteger k = (new BigInteger(q.bitLength(), rnd)).mod(q);
			gk[i] = g.modPow(k, p);
			C_over_gk[i] = C.multiply(gk[i].modInverse(p)).mod(p);
			keys[i] = gr.modPow(k, p);
		}

	}

	private void step1() throws Exception {
			
		BigInteger[][] pk = new BigInteger[numOfChoices][2];
		BigInteger[] pk0 = new BigInteger[numOfChoices];
		for (int i = 0; i < numOfChoices; i++) {
			int sigma = choices.testBit(i) ? 1 : 0;
			pk[i][sigma] = gk[i];
			pk[i][1 - sigma] = C_over_gk[i];

			pk0[i] = pk[i][0];
		}

		NetUtils.writeBigIntegerArray(pk0, dos);
		dos.flush();
	}

	private void step2() throws Exception {

		BigInteger msg0;
		BigInteger msg1;
		
		data = new BigInteger[numOfChoices];
		for (int i = 0; i < numOfChoices; i++) {
			msg0 = NetUtils.readBigInteger(msgByteCount, dis);
			msg1 = NetUtils.readBigInteger(msgByteCount, dis);
			if(choices.testBit(i)){
				data[i] = Cipher.decrypt(keys[i], msg1, msgBitLength);
			}else{
				data[i] = Cipher.decrypt(keys[i], msg0, msgBitLength);
			}
		}
	}
}
