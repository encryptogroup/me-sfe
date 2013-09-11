package io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


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


public class BMECCircuitReader extends CircuitReader {
	/* reads circuit files in bmec format */
	
	private int numberOfRegisters;
	private int numberOfGates;
	private int[] creatorInputRegisters = new int[0];
	private int[] evaluatorInputRegisters = new int[0];
	private int[] creatorOutputRegisters = new int[0];
	private int[] evaluatorOutputRegisters = new int[0];
	
	private File file;
	BufferedInputStream in;
	byte[] inBytes = new byte[4];
	private int headerBytes = 0;
	private boolean cachingEnabled;
	private int[][] cache;
	private int gateCounter = 0;
	
	protected BMECCircuitReader(File file) throws FileNotFoundException{
		this(file, false);
	}
	
	protected BMECCircuitReader(File file, boolean cachingEnabled) throws FileNotFoundException{
		this.file = file;
		this.cachingEnabled = cachingEnabled;
		this.in = new BufferedInputStream(new FileInputStream(file));
		readHeader();
		if(cachingEnabled){
			cache = new int[numberOfGates][];
			for(int i=0; i<numberOfGates; i++){
				cache[i] = readNextGate();
			}
			try{
				in.close();
			}catch(IOException e){}
		}
	}
	
	private void readHeader(){
		int n = nextInt();
		headerBytes += 4 + 4*n;
		creatorInputRegisters = new int[n];
		for(int i=0; i<n; i++){
			creatorInputRegisters[i] = nextInt();
		}
		n = nextInt();
		headerBytes += 4 + 4*n;
		evaluatorInputRegisters = new int[n];
		for(int i=0; i<n; i++){
			evaluatorInputRegisters[i] = nextInt();
		}
		n = nextInt();
		headerBytes += 4 + 4*n;
		creatorOutputRegisters = new int[n];
		for(int i=0; i<n; i++){
			creatorOutputRegisters[i] = nextInt();
		}
		n = nextInt();
		headerBytes += 4 + 4*n;
		evaluatorOutputRegisters = new int[n];
		for(int i=0; i<n; i++){
			evaluatorOutputRegisters[i] = nextInt();
		}
		numberOfRegisters = nextInt();
		numberOfGates = nextInt();
		headerBytes += 8;
	}
	
	/*
	 * reads the next 4 bytes and converts them into an integer (big-endian byte order)
	 */
	private int nextInt(){
		int ret = 0;
		try{
			in.read(inBytes);
			ret = ((inBytes[0] & 0xff) << 24) | ((inBytes[1] & 0xff) << 16) | ((inBytes[2] & 0xff) << 8) | (inBytes[3] & 0xff);
		}catch(Exception e){
			System.out.println("O-oh!");
			e.printStackTrace();
			System.exit(0);
		}
		return ret;
	}
	
	@Override
	public int getNumberOfRegisters() {
		return numberOfRegisters;
	}

	@Override
	public int getNumberOfGates() {
		return numberOfGates;
	}

	@Override
	public int[] getCreatorInputRegisters() {
		return creatorInputRegisters;
	}

	@Override
	public int[] getEvaluatorInputRegisters() {
		return evaluatorInputRegisters;
	}

	@Override
	public int[] getCreatorOutputRegisters() {
		return creatorOutputRegisters;
	}

	@Override
	public int[] getEvaluatorOutputRegisters() {
		return evaluatorOutputRegisters;
	}

	@Override
	public void reset() {
		if(cachingEnabled){
			gateCounter = 0;
		}else{
			try {
				in.close();
				in = new BufferedInputStream(new FileInputStream(file));
				in.skip(headerBytes);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
	}
	
	private int[] readNextGate() {
		int[] ret = new int[4];
		for(int i=0; i<4; i++){
			ret[i] = nextInt();
		}
		return ret;
	}
	
	@Override
	public int[] getNextGate() {
		if(cachingEnabled){
			return cache[gateCounter++];
		}else{
			return readNextGate();
		}
	}
	
}
