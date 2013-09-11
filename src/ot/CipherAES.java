// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.math.BigInteger;

import javax.crypto.spec.*;
import java.security.SecureRandom;

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


public final class CipherAES {
	private static javax.crypto.Cipher[] ciphers = null;
	
	private static int blockBytes = 0;

	private static byte[] msg;

	public static void init(int nkeys, int k2, BigInteger[] keys) {
		try {
			ciphers = new javax.crypto.Cipher[nkeys];
			byte[] key = new byte[16];
			for (int i=0; i<nkeys; i++) {
				byte[] ikey = keys[i].toByteArray();
				System.arraycopy(key,0,ikey,0,ikey.length);
				SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
				ciphers[i] = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
				ciphers[i].init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec);
			}
			blockBytes = ciphers[0].getBlockSize();
			msg = new byte[blockBytes];
		} catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                }
	}

	public static void init(int nkeypairs, int k2, BigInteger[][] keypairs) {
		BigInteger[] keys=new BigInteger[2*nkeypairs];
		for(int i=0; i<nkeypairs; i++) {
			keys[2*i]=keypairs[i][0];
			keys[2*i+1]=keypairs[i][1];
		}
		init(2*nkeypairs,k2,keys);
	}

	public static byte[] encrypt(int j, int keynum, byte[] msg, int msgLength, int periteration){
		return LabelMath.xor(msg, getPaddingBytesOfLength(j, keynum, msgLength, periteration));
	}
	
	public static byte[] decrypt(int j, int keynum, byte[] cipher, int msgLength, int periteration){
		return encrypt(j, keynum, cipher, msgLength, periteration);
	}
	
	private static byte[] getPaddingBytesOfLength(int j, int keynum, int padLength, int periteration){
		int padBytes = BitMath.byteCount(padLength);
		int iterations = padBytes / blockBytes;
		byte[] pad = new byte[padBytes];
		int val = j*periteration;
		for (int i=0; i<iterations; i++) {
			int v = val+i;
			
			msg[0] = (byte) (v >>> 24);
			msg[1] = (byte) (v >>> 16);
			msg[2] = (byte) (v >>> 8);
			msg[3] = (byte) v;

			byte[] tmp = ciphers[keynum].update(msg);

			int len = (i==iterations-1) ? (padBytes % blockBytes) : blockBytes;
			System.arraycopy(tmp, 0, pad, i*blockBytes, len);
		}

		return pad;

		/*
		sha1.update(LabelMath.intToByteArray(j));
		sha1.update(key);
		int padBytes = BitMath.byteCount(padLength);
		byte[] pad = new byte[padBytes];
		int i;
		byte[] tmp;
		for(i=0; i<padBytes/digestBytes; i++){
			tmp = sha1.digest();
			System.arraycopy(tmp, 0, pad, i*digestBytes, digestBytes);
			sha1.update(tmp);
		}
		tmp = LabelMath.getLastBits(sha1.digest(), padLength % digestBytes);
		System.arraycopy(tmp, 0, pad, i*digestBytes, padBytes%digestBytes);	
		return pad;
		*/
	}


	/* === TEST CODE === */
	public static String asHex (byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}


	public static void main(String[] args) throws Exception {
		System.out.println("Starting Test...");

		int k1=80;
		int k2=80;

		SecureRandom rnd = new SecureRandom();
		BigInteger[][] keyPairs = new BigInteger[k1][2];
		for (int i = 0; i < k1; i++) {
			keyPairs[i][0] = new BigInteger(k2, rnd);
			keyPairs[i][1] = new BigInteger(k2, rnd);
		}

		CipherAES.init(k1,k2,keyPairs);

		int numOfChoices = 129;
		int MAX_NUM_OF_PAIRS_PER_ITERATION = 128; // 8192

		int iterations = (numOfChoices - 1) / MAX_NUM_OF_PAIRS_PER_ITERATION + 1;
		int elementsPerIteration = MAX_NUM_OF_PAIRS_PER_ITERATION;

		BitMatrix T = new BitMatrix(k1, elementsPerIteration);

		for(int i=0; i<iterations; i++) {
			if (i==iterations - 1) {
				elementsPerIteration = numOfChoices - i * MAX_NUM_OF_PAIRS_PER_ITERATION;
				T = new BitMatrix(k1, elementsPerIteration);
			}
			T.initialize(rnd);

			for (int j=0; j<k1; j++) {
				System.out.print("i: ");
				System.out.print(i);
				System.out.print(" j: ");
				System.out.print(j);
				System.out.print(" ");
			
				byte[] zeroMsg = T.getRowData(j);
				byte[] zeroCph = CipherAES.encrypt(i, 2*j, zeroMsg, elementsPerIteration, MAX_NUM_OF_PAIRS_PER_ITERATION);
				
				System.out.println(asHex(zeroCph));

				byte[] decrypted = CipherAES.decrypt(i, 2*j, zeroCph, elementsPerIteration, MAX_NUM_OF_PAIRS_PER_ITERATION);
				System.out.println();
				System.out.println(asHex(zeroMsg));
				System.out.println(asHex(decrypted));
			}
		}
	}
}
