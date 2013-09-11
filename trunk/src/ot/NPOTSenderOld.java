// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import io.NetUtils;

import java.math.*;
import java.security.SecureRandom;
import java.io.*;

import math.BitMath;


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


public class NPOTSenderOld extends Sender {

	private static SecureRandom rnd = new SecureRandom();
	private static final int certainty = 80;

	private final static int qLength = 160;
	private final static int pLength = 1248;

	private BigInteger p, q, g, C, r;
	private BigInteger Cr, gr;

	public NPOTSenderOld(int numOfPairs, int msgBitLength, DataInputStream in, DataOutputStream out)
			throws Exception {
		super(numOfPairs, msgBitLength, in, out);

		initialize();
	}

	public void execProtocol(BigInteger[][] msgPairs) throws Exception {
		super.execProtocol(msgPairs);

		step1();
	}

	private void initialize() throws Exception {
		File keyfile = new File("NPOTKey_" + pLength);
		if (keyfile.exists()) {
			try{
				readKeyFile(keyfile);
			}catch(Exception e){
				System.out.println("Error reading Keyfile. Creating a new one...");
				generateKey();
				writeKeyFile(keyfile);
			}

		} else {
			generateKey();
			writeKeyFile(keyfile);
		}
		
		NetUtils.writeBigInteger(C, dos);
		NetUtils.writeBigInteger(p, dos);
		NetUtils.writeBigInteger(q, dos);
		NetUtils.writeBigInteger(g, dos);
		NetUtils.writeBigInteger(gr, dos); 
		dos.writeInt(msgBitLength);
		dos.flush();
		
	}

	private void step1() throws Exception {
		BigInteger[] pk0 = NetUtils.readBigIntegerArray(dis);
		BigInteger[] pk1 = new BigInteger[numOfPairs];
		BigInteger[][] msg = new BigInteger[numOfPairs][2];

		for (int i = 0; i < numOfPairs; i++) {
			pk0[i] = pk0[i].modPow(r, p);
			pk1[i] = Cr.multiply(pk0[i].modInverse(p)).mod(p);

			msg[i][0] = Cipher.encrypt(pk0[i], msgPairs[i][0], msgBitLength);
			msg[i][1] = Cipher.encrypt(pk1[i], msgPairs[i][1], msgBitLength);
		}

		NetUtils.writeBigIntegerArray2D(msg, BitMath.byteCount(msgBitLength), dos);
		dos.flush();
	}
	
	private void readKeyFile(File keyfile) throws Exception{
		FileInputStream fin = new FileInputStream(keyfile);
		ObjectInputStream fois = new ObjectInputStream(fin);
		C = (BigInteger) fois.readObject();
		p = (BigInteger) fois.readObject();
		q = (BigInteger) fois.readObject();
		g = (BigInteger) fois.readObject();
		gr = (BigInteger) fois.readObject();
		r = (BigInteger) fois.readObject();
		Cr = (BigInteger) fois.readObject();
		fois.close();
	}
	
	private void generateKey(){
		BigInteger rr;	
		do{
			q = new BigInteger(qLength, certainty, rnd);
			rr = new BigInteger(pLength - q.bitLength(), rnd);
			p = q.multiply(rr).add(BigInteger.ONE);
		}while(!p.isProbablePrime(certainty));
		do{
			g = (new BigInteger(qLength, rnd)).mod(q);
		}while(g.modPow(BigInteger.valueOf(2), p).equals(BigInteger.ONE));
		
		g = g.modPow(BigInteger.valueOf(2), p);
		r = (new BigInteger(qLength, rnd)).mod(q);
		gr = g.modPow(r, p);
		BigInteger tmp = (new BigInteger(qLength, rnd)).mod(q);
		C = tmp.modPow(tmp, p);
		Cr = C.modPow(r, p);
	}
	
	private void writeKeyFile(File keyfile) throws Exception{
		FileOutputStream fout = new FileOutputStream(keyfile);
		ObjectOutputStream foos = new ObjectOutputStream(fout);

		foos.writeObject(C);
		foos.writeObject(p);
		foos.writeObject(q);
		foos.writeObject(g);
		foos.writeObject(gr);
		foos.writeObject(r);
		foos.writeObject(Cr);
		foos.flush();
		foos.close();
	}
}