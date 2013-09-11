package io;


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


public class BenchmarkDummyReader extends CircuitReader {

	private int numberOfGates;
	private int[] gate = new int[]{0,0,1,1}; //0 = 0 and 1
	
	public BenchmarkDummyReader(int numberOfGates){
		this.numberOfGates = numberOfGates;
	}
	
	@Override
	public int getNumberOfRegisters() {
		return 2;
	}

	@Override
	public int getNumberOfGates() {
		return numberOfGates;
	}

	@Override
	public int[] getCreatorInputRegisters() {
		return new int[]{0};
	}

	@Override
	public int[] getEvaluatorInputRegisters() {
		return new int[]{1};
	}

	@Override
	public int[] getCreatorOutputRegisters() {
		return new int[]{0};
	}

	@Override
	public int[] getEvaluatorOutputRegisters() {
		return new int[]{};
	}

	@Override
	public void reset() {

	}

	@Override
	public int[] getNextGate() {
		return gate;
	}

}
