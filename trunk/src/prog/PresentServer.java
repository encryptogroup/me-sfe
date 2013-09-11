package prog;

import gc.GarbledCircuitCreator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

import math.LabelMath;

import util.MeasurementSeries;
import util.StopWatch;

import crypt.Present;


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


public class PresentServer extends ProgramServer {

	private BigInteger[] roundKeys;
	private byte[][] keysRegisters;
	private byte[][] sboxRegisters;
	private boolean[] sboxInverted;
	private byte[][] tmp;
	boolean[] invTmp;
	public static final int ROUNDS = 32;
	CircuitReader addRoundKeyCircuitReader;
	CircuitReader sBoxCircuitReader;
	public static final int[] PBOX = new int[] { 0, 16, 32, 48, 1, 17, 33, 49, 2, 18, 34, 50, 3,
		19, 35, 51, 4, 20, 36, 52, 5, 21, 37, 53, 6, 22, 38, 54, 7, 23, 39, 55, 8, 24, 40, 56,
		9, 25, 41, 57, 10, 26, 42, 58, 11, 27, 43, 59, 12, 28, 44, 60, 13, 29, 45, 61, 14, 30,
		46, 62, 15, 31, 47, 63 };
	
	public PresentServer(BigInteger key) throws Exception {
		super();
		roundKeys = new Present(key).getRoundKeys();
		registers = new byte[128][];
		inverted = new boolean[128];
		sboxRegisters = new byte[7][];
		sboxInverted = new boolean[7];
		tmp = new byte[64][];
		invTmp = new boolean[64];
		addRoundKeyCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentAddRoundKey.bmec"), true);
		sBoxCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentSBox16.bmec"), true);
		sboxRegisters = new byte[sBoxCircuitReader.getNumberOfRegisters()][];
		sboxInverted = new boolean[sBoxCircuitReader.getNumberOfRegisters()];
	}
	
	public void run(int repetitions) throws Exception{
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("exchangeInputLabels", repetitions);
		
		int[] sBoxMappings = sBoxCircuitReader.getCreatorOutputRegisters();
		
		for(int i=0; i<repetitions; i++){
			otm.start(i);
			iem.start(i);
			generateAndExchangeInputLabels();
			iem.stop(i);
			
			for(int round=0; round<ROUNDS-1; round++){
				//load round key
				for(int j=0; j<64; j++){
					registers[64+j] = keysRegisters[round*64+j];
					inverted[64+j] = false;
				}
				//AddRoundKey
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(addRoundKeyCircuitReader, registers, inverted, dos);
				
				//SBox
				System.arraycopy(registers, 0, sboxRegisters, 0, 64);
				System.arraycopy(inverted, 0, sboxInverted, 0, 64);
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(sBoxCircuitReader, sboxRegisters, sboxInverted, dos);
				for(int j=0; j<64; j++){
					registers[j] = sboxRegisters[sBoxMappings[j]];
					inverted[j] = sboxInverted[sBoxMappings[j]];
				}
				/**
				for(int r=0; r<16; r++){
					for(int j=0; j<4; j++){
						sboxRegisters[j] = registers[r*4+j];
						sboxInverted[j] = inverted[r*4+j];
					}
					GarbledCircuitCreator.createAndSendGarbledCircuitLabels(sBoxCircuitReader, sboxRegisters, sboxInverted, dos);
					for(int j=0; j<4; j++){
						registers[r*4+j] = sboxRegisters[j];
						inverted[r*4+j] = sboxInverted[j];
					}
				}**/
				//Permutation
				for(int j=0; j<64; j++){
					tmp[PBOX[j]] = registers[j];
					invTmp[PBOX[j]] = inverted[j];
				}
				for(int j=0; j<64; j++){
					registers[j] = tmp[j];
					inverted[j] = invTmp[j];
				}
			} 
			//Add last round key
			for(int j=0; j<64; j++){
				registers[64+j] = keysRegisters[(ROUNDS-1)*64 + j];
				inverted[64+j] = false;
			}
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(addRoundKeyCircuitReader, registers, inverted, dos);
			//generateAndSendODT
			GarbledCircuitCreator.createAndSendOutputDescriptionTable(addRoundKeyCircuitReader, registers, inverted, dos);
			otm.stop(i);
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		cleanup();
		System.out.println("Done");
	}

	private void generateAndExchangeInputLabels() throws Exception{
		keysRegisters = new byte[ROUNDS*64][];
		for(int i=0; i<ROUNDS; i++){
			for(int j=0; j<64; j++){
				keysRegisters[i*64 + j] = LabelMath.randomLabel();
				if( roundKeys[i].testBit(j) ){
					NetUtils.writeLabel(LabelMath.conjugate(keysRegisters[i*64 + j]), dos);
				}else{
					NetUtils.writeLabel(keysRegisters[i*64 + j], dos);
				}
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[64][];
		for(int i=0; i<64; i++){
			byte[] label = LabelMath.randomLabel();
			registers[i] = label;
			inverted[i] = false;
			zeroMsgs[i] = label;
			//System.out.println(i+": " + Arrays.toString(label));
		}
		otSender.execProtocol(zeroMsgs);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		PresentServer server = new PresentServer(BigInteger.ZERO);
		server.run(1000);
		
		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.close();
	}

}
