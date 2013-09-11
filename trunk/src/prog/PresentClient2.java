package prog;

import gc.GarbledCircuitEvaluator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;

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


public class PresentClient2 extends ProgramClient{
	
	public static final int ROUNDS = PresentServer.ROUNDS;
	private byte[][] keysRegisters;
	private byte[][] tmp;
	CircuitReader addRoundKeyCircuitReader;
	CircuitReader presentRoundCircuitReader;
	private BigInteger[] inputs;
	public static final int[] PBOX = PresentServer2.PBOX;
	
	
	public PresentClient2(BigInteger[] inputs) throws Exception {
		super();
		this.inputs = inputs;
		registers = new byte[128][];
		tmp = new byte[64][];
		addRoundKeyCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentAddRoundKey.bmec"), true);
		presentRoundCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentRound.bmec"), true);
	}

	
	public void run(int repetitions) throws Exception{
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		BigInteger ciphertext;
		
		for(int i=0; i<repetitions; i++){
			receiveInputLabels(inputs[i]);
			
			for(int round=0; round<ROUNDS-1; round++){
				//load round key
				for(int j=0; j<64; j++){
					registers[64+j] = keysRegisters[round*64+j];
				}
				
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(presentRoundCircuitReader, registers, dis);
				
				//Permutation
				for(int j=0; j<64; j++){
					tmp[j] = registers[PBOX[j]];
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
			ciphertext = GarbledCircuitEvaluator.receiveOutputDescriptionTable(addRoundKeyCircuitReader, registers, dis);
			System.out.println("CipherText: " + ciphertext.toString(16));
		}
		
		cleanup();
		System.out.println("Done");
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
		
		PresentClient2 client = new PresentClient2(inputs);
		client.run(1000);
		
	}
	
}
