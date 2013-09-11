package io;

import java.util.Arrays;


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


public class SetMinimumGenerator extends CircuitReader {
	
	/* generates a SetMinimum circuit of arbitrary size */

	private int elements, bitlen;
	private int curPos = 0;
	private int curElement = 1;
	private int count, count2, count3;
	private int numRegisters;
	private int a,b,c,d;
	
	public SetMinimumGenerator(int n, int l){
		elements = n;
		bitlen = l;
		numRegisters = elements*bitlen + 2;
	}
	
	@Override
	public int getNumberOfRegisters() {
		return numRegisters;
	}

	@Override
	public int getNumberOfGates() {
		return (elements-1)*((7*bitlen)-2);
	}

	@Override
	public int[] getCreatorInputRegisters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getEvaluatorInputRegisters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getCreatorOutputRegisters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getEvaluatorOutputRegisters() {
		int[] ret = new int[bitlen];
		for(int i=0; i<bitlen; i++){
			ret[i] = i;
		}
		return ret;
	}

	@Override
	public void reset() {
		curPos = 0;
		curElement = 1;
	}

	@Override
	public int[] getNextGate() {
		if(curPos == 0){
			switch(count){
			case 0:
				count++;
				return new int[]{numRegisters-2, curElement*bitlen, 0, 1};
			case 1:
				count++;
				return new int[]{numRegisters-2, curElement*bitlen, numRegisters-2, 6};
			case 2:
				count++;
				return new int[]{numRegisters-1, curPos+1, numRegisters-2, 6};
			case 3:
				count = 0;
				curPos++;			
				return new int[]{0, curElement*bitlen, 0, 6};
			}
		}else{
			if(curPos == bitlen-1){
				switch (count) {
				case 0:
				case 2:
					a = numRegisters-2; b = curElement*bitlen + curPos; c = numRegisters-2; d = 6;
					count++;
					break;
				case 1:
					a = numRegisters-2; b = numRegisters-2; c = numRegisters-1; d = 1;
					count++;
					break;
				case 3:
					if(count2<bitlen-1){
						switch(count3){
						case 0:
							a = count2; b=numRegisters-2; c=count2; d=1;
							count3++; break;
						case 1:
							a = count2; b=curElement*bitlen+count2; c=count2; d=6;
							count3 = 0; count2++; break;
						}
					}else{
						switch(count3){
						case 0:
						case 2:
							a = count2; b=curElement*bitlen+count2; c=count2; d=6;
							if(count3 == 2){
								count3 = 0;
								count2 = 0;
								count = 0;
								curElement++;
								curPos=0;
							}else{
								count3++;
							}
							break;
						case 1:
							a = count2; b=numRegisters-2; c=count2; d=1;
							count3++; break;
						}
					}
					break;
				}
				return new int[]{a,b,c,d};

			}else{ //all others
				switch (count) {
				case 0:
				case 2:
					count++;
					return new int[]{numRegisters-2, curElement*bitlen + curPos, numRegisters-2, 6};				
				case 1:
					count++;
					return new int[]{numRegisters-2, numRegisters-2, numRegisters-1, 1};
				case 3:
					count++;
					return new int[]{numRegisters-1, curPos+1, numRegisters-2, 6};
				case 4:
					int a,b;
					a = curPos;
					b = curElement*bitlen + curPos;
					count = 0;
					curPos++;			
					return new int[]{a, b, a, 6};
				default:
					System.out.println("Oh, oh. This should never happen");
					break;
				}
			}
		}
		System.out.println("Oh, oh. This should never happen, too");
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SetMinimumGenerator gen = new SetMinimumGenerator(3, 20);
		System.out.println("regs: " + gen.getNumberOfRegisters() +", gates: " + gen.getNumberOfGates());
		for(int i=0; i<gen.getNumberOfGates(); i++){
			System.out.println(Arrays.toString(gen.getNextGate()));
		}

	}

}
