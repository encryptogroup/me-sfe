
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


public class ECNPOTSender extends Sender {

	private static SecureRandom rnd = new SecureRandom();

	private BigInteger r;
	static String EC_SPEC = "secp160r1";
	private ECPoint G, C, rG, rC;
	private ECCurve curve;

	public ECNPOTSender(int numOfPairs, int msgBitLength, DataInputStream in,
			DataOutputStream out) throws Exception {
		super(numOfPairs, msgBitLength, in, out);

		//StopWatch.pointTimeStamp("right before ECNPOT public key generation");
		initialize();
		//StopWatch.taskTimeStamp("ECNPOT public key generation");
	}

	public void execProtocol(BigInteger[][] msgPairs) throws Exception {
		super.execProtocol(msgPairs);

		step1();
	}

	private void initialize() throws Exception {
		
		BigInteger order = null;
		File keyfile = new File("ECNPOTKey_"+EC_SPEC);
		if (keyfile.exists()) {
		    FileInputStream fin = new FileInputStream(keyfile);
		    ObjectInputStream fois = new ObjectInputStream(fin);
		    curve = ECCurve.getCurve((String)fois.readObject());
		    C = curve.decodePoint((byte[])fois.readObject());
		    r = (BigInteger)fois.readObject();
		    rG = curve.decodePoint((byte[])fois.readObject());
		    rC = curve.decodePoint((byte[])fois.readObject());
		    order = curve.getOrder();
		    G = curve.getGenerator();
		    fois.close();
		    fin.close();

		}else{				
			curve = ECCurve.getCurve(EC_SPEC);
			G = curve.getGenerator();
			order = curve.getOrder();
			//generate C
			BigInteger k = new BigInteger(order.bitLength(), rnd);
			C = G.multiply(k);
			//generate rG
			r = new BigInteger(order.bitLength(), rnd);
			rG = G.multiply(r);
			//generate rC
			rC = C.multiply(r);
			
			
			FileOutputStream fout = new FileOutputStream(keyfile);
		    ObjectOutputStream foos = new ObjectOutputStream(fout);
		    foos.writeObject(EC_SPEC);
		    foos.writeObject(C.getEncoded());
		    foos.writeObject(r);
		    foos.writeObject(rG.getEncoded());
		    foos.writeObject(rC.getEncoded());
		    foos.close();
		    fout.close();
		}
		//oos.writeObject(EC_SPEC);
		dos.writeUTF(EC_SPEC);
		NetUtils.writeECPoint(C, dos);
		NetUtils.writeECPoint(rG, dos);
		dos.writeInt(msgBitLength);
		dos.flush();
		
	}

	private void step1() throws Exception {
		ECPoint PK0;
		ECPoint rPK0; //mnn
		ECPoint rPK1; //mnn
		BigInteger msg;
		for(int i=0; i<numOfPairs; i++){
			PK0 = NetUtils.readECPoint(curve, dis);
			rPK0 = PK0.multiply(r);
			rPK1 = rC.subtract(rPK0);
			msg =  Cipher.encrypt(rPK0.getEncoded(), msgPairs[i][0], msgBitLength);
			NetUtils.writeBigInteger(msg, dos);
			msg = Cipher.encrypt(rPK1.getEncoded(), msgPairs[i][1], msgBitLength);
			NetUtils.writeBigInteger(msg, dos);
		}
		
		dos.flush();
	}
}