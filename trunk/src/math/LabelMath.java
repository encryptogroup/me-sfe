package math;

import java.math.BigInteger;
import java.security.MessageDigest;
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

public class LabelMath {

	private static byte[] R;
	private static MessageDigest sha1 = null;
	public static final int LABEL_BYTESIZE = 11;
	private static SecureRandom rnd = new SecureRandom();

	static {
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
			initR();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static byte[] xor(byte[] x, byte[] y) {
		byte[] ret = new byte[x.length];
		for (int i = 0; i < x.length; i++) {
			ret[i] = (byte) (x[i] ^ y[i]);
		}
		return ret;
	}
	
	/* inline xor: x = x^y
	 */
	public static void ixor(byte[] x, byte[] y) {
		for (int i = 0; i < x.length; i++) {
			x[i] = (byte) (x[i] ^ y[i]);
		}
	}

	public static boolean testBit(byte[] x, int bitIndex) {
		int byteIndex = bitIndex / 8;
		bitIndex = bitIndex % 8;
		return (x[byteIndex] & (1 << bitIndex)) > 0;
	}
	
	public static byte[] getLastBits(byte[] x, int numOfBits){
		byte[] res = new byte[x.length];
		int bitcount = 8;
		for(int i=0; i<x.length; i++){
			if(bitcount <= numOfBits){
				res[i] = x[i];
			}else{
				int lastBits = numOfBits % 8;
				byte mask = 0;
				for(int j=0; j<lastBits; j++){
					mask += 1<<j;
				}
				res[i] = (byte)(x[i] & mask);
				break;
			}
			bitcount += 8;
		}
		return res;
	}
	
	//returns the 'numOfBytes' least significant bytes of BigInteger x
	public static byte[] toByteArray(BigInteger x, int numOfBytes){
		byte[] res = new byte[numOfBytes];;
		byte[] xb = x.toByteArray();
		if(xb.length == numOfBytes){		
			for(int i=0; i<xb.length; i++){
				res[numOfBytes-1-i] = xb[i];
			}
		}else{
			if(xb.length < numOfBytes){
				for(int i=0; i<xb.length; i++){
					res[xb.length-1-i] = xb[i];
				}
			}else{
				for(int i=0; i<numOfBytes; i++){
					res[i] = xb[xb.length-1-i];
				}
			}
		}
		return res;
	}

	/*
	 * creates a random label
	 */
	public static byte[] randomLabel(){
		byte[] label = new byte[LABEL_BYTESIZE];
		rnd.nextBytes(label);
		return label;
	}
	
	public static byte[] conjugate(byte[] x) {
		return xor(x, R);
	}
	
	public static void initR(){
		R = randomLabel();
		if(!testBit(R, 0)){
			R[0] += 1;
		}
	}
	
	static byte[] getR(){
		return R;
	}

	public static byte[] kdf(byte[] key1, byte[] key2, int k) {
		sha1.update(key1);
		sha1.update(key2);
		sha1.update(intToByteArray(k));
		byte[] buf = new byte[LABEL_BYTESIZE];
		byte[] digest = sha1.digest();
		System.arraycopy(digest, 0, buf, 0, LABEL_BYTESIZE);
		return buf;
	}
	
	public static BigInteger kdf(BigInteger key, int length){
		sha1.update(key.toByteArray());
		return new BigInteger(1, sha1.digest()).and(BigInteger.ONE.shiftLeft(length).subtract(BigInteger.ONE));
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8),
				(byte) value };
	}
	
	public static byte[] encrypt(byte[] key1, byte[] key2, int k, byte[] message) {
		byte[] key = kdf(key1, key2, k);
		ixor(key, message);
		return key;
	}
}
