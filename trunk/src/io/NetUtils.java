package io;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;

import math.BitMath;
import math.LabelMath;
import math.ec.ECCurve;
import math.ec.ECPoint;


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



public class NetUtils {
	
	public static void writeLabel(byte[] label, DataOutputStream dos) throws IOException{
		dos.write(label);
	}
	
	public static byte[] readLabel(DataInputStream dis) throws IOException{
		byte[] input = new byte[LabelMath.LABEL_BYTESIZE];
		dis.readFully(input);
		return input;
	}
	
	
	public static void writeBigInteger(BigInteger m, int byteCount, DataOutputStream dos) throws IOException{
		byte[] mb = m.toByteArray();
		byte[] full_m;
		if(mb.length == byteCount){
			full_m = mb;
		}else{
			full_m = new byte[byteCount];
			if(mb.length<byteCount){
				System.arraycopy(mb, 0, full_m, byteCount-mb.length, mb.length);
			}else{
				System.arraycopy(mb, mb.length-byteCount, full_m, 0, byteCount);
			}
		}
		dos.write(full_m);
	}

	
	public static BigInteger readBigInteger(int byteCount, DataInputStream dis) throws IOException{
		byte[] buf = new byte[byteCount];
		dis.readFully(buf);
		return new BigInteger(1, buf);
	}
	
	public static void writeBigInteger(BigInteger m, DataOutputStream dos) throws IOException{
		int l = m.bitLength();
		dos.writeInt(l);
		writeBigInteger(m, BitMath.byteCount(l), dos);
	}
	
	public static BigInteger readBigInteger(DataInputStream dis) throws IOException{
		return readBigInteger(BitMath.byteCount(dis.readInt()), dis);
	}
	
	public static void writeBigIntegerArray(BigInteger[] array, DataOutputStream dos) throws IOException{
		dos.writeInt(array.length);
		for(BigInteger a : array){
			writeBigInteger(a, dos);
		}
	}
	
	public static BigInteger[] readBigIntegerArray(DataInputStream dis) throws IOException{
		int l = dis.readInt();
		BigInteger[] ret = new BigInteger[l];
		for(int i=0; i<l; i++){
			ret[i] = readBigInteger(dis);
		}
		return ret;
	}
	
	public static void writeBigIntegerArray2D(BigInteger[][] array, int elementByteLength, DataOutputStream dos) throws IOException{
		dos.writeInt(array.length);
		dos.writeInt(array[0].length);
		for(BigInteger[] ar : array){
			for(BigInteger a : ar){
				writeBigInteger(a, elementByteLength, dos);
			}
		}
	}
	
	public static BigInteger[][] readBigIntegerArray2D(int elementByteCount, DataInputStream dis) throws IOException{
		int l1 = dis.readInt();
		int l2 = dis.readInt();
		BigInteger[][] ret = new BigInteger[l1][l2];
		for(int i=0; i<l1; i++){
			for(int j=0; j<l2; j++){
				ret[i][j] = readBigInteger(elementByteCount, dis);
			}
		}
		return ret;
	}
	
	public static void writeECPoint(ECPoint p, DataOutputStream dos) throws IOException{
		byte[] enc = p.getEncoded();
		dos.write(enc.length);
		dos.write(enc);
	}
	
	public static ECPoint readECPoint(ECCurve curve, DataInputStream dis) throws IOException{
		int len = dis.read();
		byte[] enc = new byte[len];
		dis.readFully(enc);
		return curve.decodePoint(enc);
	}

}
