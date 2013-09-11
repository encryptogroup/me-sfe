package gc;

import io.CircuitReader;
import io.NetUtils;

import java.io.DataOutputStream;
import java.io.IOException;

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


public class GarbledCircuitCreator extends GarbledCircuitConstants {
	
	private static int gateIDX = 0;
	
	public static void createAndSendOutputDescriptionTable(CircuitReader reader, byte[][] registers, boolean[] inverted, DataOutputStream dos) throws IOException{
		int[] evalOutputs = reader.getEvaluatorOutputRegisters();
		for(int reg : evalOutputs){
			if(inverted[reg]){
				NetUtils.writeLabel(LabelMath.conjugate(registers[reg]), dos);
				NetUtils.writeLabel(registers[reg], dos);
			}else{
				NetUtils.writeLabel(registers[reg], dos);
				NetUtils.writeLabel(LabelMath.conjugate(registers[reg]), dos);
			}
		}
		dos.flush();
	}

	
	public static void createAndSendGarbledCircuitLabels(CircuitReader reader, byte[][] registers, boolean[] inverted, DataOutputStream dos) throws IOException{
		int[] gate;
		byte[][] newLabelPair = new byte[2][LabelMath.LABEL_BYTESIZE];
		byte[][] garbledTable = new byte[4][LabelMath.LABEL_BYTESIZE];
		byte[][] rightInput = new byte[2][LabelMath.LABEL_BYTESIZE], leftInput = new byte[2][LabelMath.LABEL_BYTESIZE];
		
		for(int i=0; i<reader.getNumberOfGates(); i++){
			gate = reader.getNextGate();
			switch(gate[TRUTH_TABLE]){
			case 6: //free XOR
				registers[gate[OUTPUT_REGISTER]] = LabelMath.xor(registers[gate[LEFT_INPUT_REGISTER]], registers[gate[RIGHT_INPUT_REGISTER]]);
				inverted[gate[OUTPUT_REGISTER]] = inverted[gate[LEFT_INPUT_REGISTER]] ^ inverted[gate[RIGHT_INPUT_REGISTER]];
				break;
			case 9: //free XNOR
				registers[gate[OUTPUT_REGISTER]] = LabelMath.xor(registers[gate[LEFT_INPUT_REGISTER]], registers[gate[RIGHT_INPUT_REGISTER]]);
				inverted[gate[OUTPUT_REGISTER]] = inverted[gate[LEFT_INPUT_REGISTER]] ^ !inverted[gate[RIGHT_INPUT_REGISTER]];
				break;
			default:
				leftInput[0] = registers[gate[LEFT_INPUT_REGISTER]];
				rightInput[0] = registers[gate[RIGHT_INPUT_REGISTER]];
				leftInput[1] = LabelMath.conjugate(leftInput[0]);
				rightInput[1] = LabelMath.conjugate(rightInput[0]);
				
				int pbl = LabelMath.testBit(leftInput[0], 0)?1:0;  //left permutation bit
				int pbr = LabelMath.testBit(rightInput[0], 0)?1:0; //right permutation bit

					
				int ipbl = pbl;		//the permutation bit has to be inverted if the input[0] stands for 1
				if(inverted[gate[LEFT_INPUT_REGISTER]]){
					ipbl = 1-ipbl;
				}
				int ipbr = pbr;
				if(inverted[gate[RIGHT_INPUT_REGISTER]]){
					ipbr = 1 - ipbr;
				}
				
				//create the labels for the output
				int eval = (gate[TRUTH_TABLE] & (8>>(ipbr+(ipbl<<1)))) > 0 ? 1:0;
				newLabelPair[eval] = LabelMath.kdf(leftInput[pbl], rightInput[pbr], gateIDX);
				//newLabelPair[eval] = LabelMath.xor(leftInput[pbl], rightInput[pbr]);
				newLabelPair[1-eval] = LabelMath.conjugate(newLabelPair[eval]);
				registers[gate[OUTPUT_REGISTER]] = newLabelPair[0];
				inverted[gate[OUTPUT_REGISTER]] = false;
				
				//sort them into the garbled table
				garbledTable[((0^ipbl)<<1) + 0^ipbr] = newLabelPair[(gate[TRUTH_TABLE] & 8)>>3];
				garbledTable[((0^ipbl)<<1) + 1^ipbr] = newLabelPair[(gate[TRUTH_TABLE] & 4)>>2];
				garbledTable[((1^ipbl)<<1) + 0^ipbr] = newLabelPair[(gate[TRUTH_TABLE] & 2)>>1];
				garbledTable[((1^ipbl)<<1) + 1^ipbr] = newLabelPair[(gate[TRUTH_TABLE] & 1)];
				
				//encrypt the garbled table
				if (pbl != 0 || pbr != 0){
					garbledTable[((0^pbl)<<1) + 0^pbr] = LabelMath.encrypt(leftInput[0], rightInput[0], gateIDX, garbledTable[((0^pbl)<<1) + 0^pbr]);
				}
				if (pbl != 0 || pbr != 1){
					garbledTable[((0^pbl)<<1) + 1^pbr] = LabelMath.encrypt(leftInput[0], rightInput[1], gateIDX, garbledTable[((0^pbl)<<1) + 1^pbr]);
				}
				if (pbl != 1 || pbr != 0){
					garbledTable[((1^pbl)<<1) + 0^pbr] = LabelMath.encrypt(leftInput[1], rightInput[0], gateIDX, garbledTable[((1^pbl)<<1) + 0^pbr]);
				}
				if (pbl != 1 || pbr != 1){
					garbledTable[((1^pbl)<<1) + 1^pbr] = LabelMath.encrypt(leftInput[1], rightInput[1], gateIDX, garbledTable[((1^pbl)<<1) + 1^pbr]);
				}
				
				//send the garbled table
				for(int j=1; j<4; j++){
					NetUtils.writeLabel(garbledTable[j], dos);
				}	
			}
			gateIDX++;
		}
		if(dos != null){
			dos.flush();
		}
		reader.reset();
	}
	

}
