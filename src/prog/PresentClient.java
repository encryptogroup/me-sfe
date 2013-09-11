package prog;

import gc.GarbledCircuitEvaluator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;

import util.StopWatch;


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


public class PresentClient extends ProgramClient{
	
	public static final int ROUNDS = PresentServer.ROUNDS;
	private byte[][] keysRegisters;
	private byte[][] sboxRegisters;
	private byte[][] tmp;
	CircuitReader addRoundKeyCircuitReader;
	CircuitReader sBoxCircuitReader;
	private BigInteger[] inputs;
	public static final int[] PBOX = PresentServer.PBOX;
	
	
	public PresentClient(BigInteger[] inputs) throws Exception {
		super();
		this.inputs = inputs;
		registers = new byte[128][];
		tmp = new byte[64][];
		addRoundKeyCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentAddRoundKey.bmec"), true);
		sBoxCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentSBox16.bmec"), true);
		sboxRegisters = new byte[sBoxCircuitReader.getNumberOfRegisters()][];
	}

	
	public void run(int repetitions) throws Exception{
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		int[] sboxMapping = sBoxCircuitReader.getCreatorOutputRegisters();
		
		for(int i=0; i<repetitions; i++){
			System.out.println("Repetition: " + i);
			receiveInputLabels(inputs[i]);
			//printInputLabels(i);
			//System.exit(0);
			for(int round=0; round<ROUNDS-1; round++){
				//load round key
				for(int j=0; j<64; j++){
					registers[64+j] = keysRegisters[round*64+j];
				}
				//AddRoundKey
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(addRoundKeyCircuitReader, registers, dis);
				
				//SBox
				System.arraycopy(registers, 0, sboxRegisters, 0, 64);
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(sBoxCircuitReader, sboxRegisters, dis);
				for(int j=0; j<64; j++){
					registers[j] = sboxRegisters[sboxMapping[j]];
				}
				
				/**
				for(int r=0; r<16; r++){
					for(int j=0; j<4; j++){
						sboxRegisters[j] = registers[r*4+j];
					}
					GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(sBoxCircuitReader, sboxRegisters, dis);
					for(int j=0; j<4; j++){
						registers[r*4+j] = sboxRegisters[j];
					}
				}
				**/
				//Permutation
				for(int j=0; j<64; j++){
					tmp[PBOX[j]] = registers[j];
				}
				for(int j=0; j<64; j++){
					registers[j] = tmp[j];
				}
			}
			
			//Add last round key
			for(int j=0; j<64; j++){
				registers[64+j] = keysRegisters[(ROUNDS-1)*64 + j];
			}
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(addRoundKeyCircuitReader, registers, dis);
			//receiveODT
			System.out.println("Ciphertext: " + GarbledCircuitEvaluator.receiveOutputDescriptionTable(addRoundKeyCircuitReader, registers, dis).toString(16));
		}
		cleanup();
		System.out.println("Done");
	}
	
	void printInputLabels(int i){
		System.out.println("Input: " + inputs[i]);
		for(int j=0; j<64; j++){
			System.out.println("Reg. "+j+": "+ Arrays.toString(registers[j]));
		}
	}
	
	private void receiveInputLabels(BigInteger input) throws Exception{
		//receive all the round keys
		keysRegisters = new byte[ROUNDS*64][];
		for(int i=0; i<ROUNDS*64; i++){
			keysRegisters[i] = NetUtils.readLabel(dis);
		}
		//receive the labels for my input
		byte[][] data = otReceiver.execProtocol(input, 64);
		for(int i=0; i<64; i++){
			registers[i] = data[i];
			//System.out.println(i+": " + Arrays.toString(data[i]));
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		BigInteger[] inputs = new BigInteger[1000];
		for(int i=0; i<1000; i++){
			inputs[i] = BigInteger.valueOf(i);
		}
		
		
		PresentClient client = new PresentClient(inputs);
		client.run(1000);
		
	}
	
}
