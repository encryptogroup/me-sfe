package ot;

import io.NetUtils;

import java.math.*;
import java.io.*;

import math.ec.ECPoint;
import math.ec.ECCurve;

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

public class ECNPOTReceiver extends Receiver {
	private static SecureRandom rnd = new SecureRandom();

	private int msgBitLength;
	private ECPoint G, C, rG;
	private ECPoint[] PKs;
	private byte[][] keys;

	public ECNPOTReceiver(int numOfChoices, DataInputStream in,
			DataOutputStream out) throws Exception {
		super(numOfChoices, in, out);

		initialize();
	}

	public void execProtocol(BigInteger choices) throws Exception {
		super.execProtocol(choices);

		step1();
		step2();
	}

	private void initialize() throws Exception {
		//String EC_SPEC = (String) ois.readObject();
		//ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(EC_SPEC);
		ECCurve curve = ECCurve.getCurve(dis.readUTF());
		BigInteger order = curve.getOrder();
		G = curve.getGenerator();
		C = NetUtils.readECPoint(curve, dis);
		rG = NetUtils.readECPoint(curve, dis);
		msgBitLength = dis.readInt();

		PKs = new ECPoint[numOfChoices];
		keys = new byte[numOfChoices][];

		BigInteger k;
		for (int i = 0; i < numOfChoices; i++) {
			k = new BigInteger(order.bitLength(), rnd);
			PKs[i] = G.multiply(k);
			keys[i] = rG.multiply(k).getEncoded();
		}
	}

	private void step1() throws Exception {
		ECPoint PK0;
		for (int i = 0; i < numOfChoices; i++) {
			if( choices.testBit(i) ){
				//PK0s[i] = C.subtract(PKs[i]).getEncoded();
				PK0 = C.subtract(PKs[i]);
				
			}else{
				//PK0s[i] = PKs[i].getEncoded();
				PK0 = PKs[i];
			}
			NetUtils.writeECPoint(PK0, dos);
		}
		dos.flush();
	}

	private void step2() throws Exception {
		//BigInteger[][] msg = (BigInteger[][]) ois.readObject();
		data = new BigInteger[numOfChoices];
		BigInteger msg0, msg1;
		for (int i = 0; i < numOfChoices; i++) {
			msg0 = NetUtils.readBigInteger(dis);
			msg1 = NetUtils.readBigInteger(dis);
			if(choices.testBit(i)){
				data[i] = Cipher.decrypt(keys[i],msg1, msgBitLength);
			}else{
				data[i] = Cipher.decrypt(keys[i], msg0, msgBitLength);
			}
		}
	}
}
