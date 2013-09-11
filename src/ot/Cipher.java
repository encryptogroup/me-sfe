// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.math.BigInteger;
import java.security.*;

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


public final class Cipher {
	private static int digestBytes;
	
	private static MessageDigest sha1 = null;
	
	static {
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
			digestBytes = sha1.getDigestLength();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static byte[] encrypt(int j, byte[] key, byte[] msg, int msgLength){
		return LabelMath.xor(msg, getPaddingBytesOfLength(j, key, msgLength));
	}
	
	public static byte[] decrypt(int j, byte[] key, byte[] cipher, int msgLength){
		return encrypt(j, key, cipher, msgLength);
	}
	
	public static byte[] encrypt(byte[] key, byte[] msg, int msgLength){
		return LabelMath.xor(msg, getPaddingBytesOfLength(key, msgLength));
	}
	
	public static byte[] decrypt(byte[] key, byte[] cph, int cphLength){
		return encrypt(key, cph, cphLength);
	}
	
	public static BigInteger encrypt(BigInteger key, BigInteger msg,
			int msgLength) {
		return msg.xor(new BigInteger(1, getPaddingBytesOfLength(key.toByteArray(), msgLength)));
	}
	
	public static BigInteger decrypt(BigInteger key, BigInteger cph,
			int cphLength) {
		return cph.xor(new BigInteger(1, getPaddingBytesOfLength(key.toByteArray(), cphLength)));
	}
	
	public static BigInteger encrypt(byte[] key, BigInteger msg, int msgLength){
		return msg.xor(new BigInteger(1, getPaddingBytesOfLength(key, msgLength)));
	}
	
	public static BigInteger decrypt(byte[] key, BigInteger cph, int cphLength){
		return cph.xor(new BigInteger(1, getPaddingBytesOfLength(key, cphLength)));
	}
	

	private static byte[] getPaddingBytesOfLength(byte[] key, int padLength){
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
	}
	
	private static byte[] getPaddingBytesOfLength(int j, byte[] key, int padLength){
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
	}
	
}