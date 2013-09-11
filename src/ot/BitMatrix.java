// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.math.*;
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


public class BitMatrix {
	private int rows;
	private int cols;
	private byte[][] data;
	private int rowByteCount;
	
	public BitMatrix(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
		this.rowByteCount = byteCount(cols);
		data = new byte[rows][rowByteCount];
	}
	
	public void initialize(SecureRandom rnd){
		for(int i=0; i<rows; i++){
			rnd.nextBytes(data[i]);
		}
	}

	
	/* returns a transposed BitMatrix */
	public BitMatrix transpose(){
		BitMatrix t = new BitMatrix(cols, rows);
		byte[] rowData;
		int newByteIdx, bytePos, byteIdx;
		for(int r=0; r<rows; r++){
			rowData = data[r];
			newByteIdx = r/8;
			for(int c=0; c<cols; c++){
				bytePos = c % 8;
				byteIdx = c / 8;  
				if((rowData[byteIdx] & 1<<bytePos) != 0){ //then bit_{r,c} is set
					t.data[c][newByteIdx] ^= 1<<(r%8);
				}
			}
		}	
		return t;
	}
	
	
	public byte[] getColumnData(int column){
		byte[] ret = new byte[byteCount(rows)];
		int byteIdx = column / 8;
		int bytePos = column % 8;
		int idx, pos;
		for(int i=0; i<rows; i++){
			idx = i/8;
			pos = i%8;
			ret[idx] = setBit(ret[idx], pos, bitVal(data[i][byteIdx], bytePos));
		}
		return ret;
	}
	
	public byte[] getRowData(int row){
		return data[row];
	}
	
	public void setRowData(int row, byte[] rowData){
		if(rowData.length == rowByteCount ){
			data[row] = rowData;
		}else{
			if(rowData.length < rowByteCount){
				data[row] = new byte[rowByteCount];
				System.arraycopy(rowData, 0, data[row], rowByteCount-rowData.length, rowData.length);
			}else{
				System.arraycopy(rowData, rowData.length-rowByteCount, data[row], 0, rowByteCount);
			}
		}
	}
	
	public void setRowData(int row, BigInteger rowData){
		byte[] rdata = rowData.toByteArray();
		if(rdata.length == rowByteCount ){
			data[row] = rdata;
		}else{
			if(rdata.length < rowByteCount){
				byte[] tmp = new byte[rowByteCount];
				System.arraycopy(rdata, 0, tmp, rowByteCount-rdata.length, rdata.length);
				data[row] = tmp;
			}else{
				byte[] tmp = new byte[rowByteCount];
				System.arraycopy(rdata, rdata.length-rowByteCount, tmp, 0, rowByteCount);
				data[row] = tmp;
			}
		}
	}
	
	private static int byteCount(int bits){
		return (bits+7)/8;
	}
	
	private static int bitVal(byte b, int pos){
		return (b & 1<<pos) == 0 ? 0:1;
	}
	
	private static byte setBit(byte b, int pos, int val){
		byte res;
		if(val == 0){
			res = (byte)(b & (255 ^ 1<<pos));
		}else{
			res = (byte)(b | 1<<pos);
		}
		return res;
	}
	

}
