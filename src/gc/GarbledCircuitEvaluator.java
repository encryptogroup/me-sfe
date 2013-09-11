package gc;

import io.CircuitReader;
import io.NetUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

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


public class GarbledCircuitEvaluator extends GarbledCircuitConstants {

	private static int gateIDX = 0;

	public static BigInteger receiveOutputDescriptionTable(CircuitReader reader, byte[][] registers, DataInputStream dis) throws IOException{
		int[] outRegs = reader.getEvaluatorOutputRegisters();
		if(outRegs.length < 1){
			return null;
		}
		byte[] outputZero, outputOne;
		BigInteger output = BigInteger.ZERO;
		for(int i=0; i<outRegs.length; i++){
			outputZero = NetUtils.readLabel(dis);
			outputOne = NetUtils.readLabel(dis);
			if(Arrays.equals(registers[outRegs[i]], outputZero)){
			}else{
				if(Arrays.equals(registers[outRegs[i]], outputOne)){
					output = output.setBit(i);
				}else{
					System.out.println("Puu! Output doesn't match the ODT");
				}
			}
		}
		return output;
	}

	
	public static void receiveAndEvaluateCircuitLabels(CircuitReader reader, byte[][] registers, DataInputStream dis) throws IOException {

		byte[][] garbledTable = new byte[4][LabelMath.LABEL_BYTESIZE];
		int pbl, pbr; // permutation bits of the left and right input
		byte[] rightInput, leftInput;
		int[] gate;

		for (int i = 0; i < reader.getNumberOfGates(); i++) {
			gate = reader.getNextGate();
			switch (gate[TRUTH_TABLE]) {
			case 6: // free XOR
			case 9: // free XNOR
				leftInput = registers[gate[LEFT_INPUT_REGISTER]];
				rightInput = registers[gate[RIGHT_INPUT_REGISTER]];

				registers[gate[OUTPUT_REGISTER]] = LabelMath.xor(registers[gate[LEFT_INPUT_REGISTER]],
								registers[gate[RIGHT_INPUT_REGISTER]]);
				break;
			default:
				leftInput = registers[gate[LEFT_INPUT_REGISTER]];
				rightInput = registers[gate[RIGHT_INPUT_REGISTER]];

				pbl = LabelMath.testBit(leftInput, 0)?1:0;
				pbr = LabelMath.testBit(rightInput, 0)?1:0;
				
				garbledTable[1] = NetUtils.readLabel(dis);
				garbledTable[2] = NetUtils.readLabel(dis);
				garbledTable[3] = NetUtils.readLabel(dis);
	
				registers[gate[OUTPUT_REGISTER]] = LabelMath.encrypt(leftInput, rightInput, gateIDX, garbledTable[pbr+(pbl<<1)]);
			}
			gateIDX++;
		}
		reader.reset();
	}

}
