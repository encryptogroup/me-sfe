package prog;

import gc.GarbledCircuitCreator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;

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


public class PresentServer2 extends ProgramServer {

	private BigInteger[] roundKeys;
	private byte[][] keysRegisters;
	private byte[][] tmp;
	boolean[] invTmp;
	public static final int ROUNDS = 32;
	CircuitReader presentRoundCircuitReader;
	CircuitReader addRoundKeyCircuitReader;

/*
	protected static final int[] PBOX = new int[] { 64, 19, 35, 51, 2, 17, 33, 49, 0, 14, 30, 46,
			1, 16, 32, 48, 7, 23, 39, 55, 5, 21, 37, 53, 3, 18, 34, 50, 4, 20, 36, 52, 11, 27, 43,
			59, 9, 25, 41, 57, 6, 22, 38, 54, 8, 24, 40, 56, 15, 31, 47, 63, 13, 29, 45, 61, 10,
			26, 42, 58, 12, 28, 44, 60 };
*/	
	protected static final int[] PBOX = new int[]{ 64, 7, 11, 15, 19, 23, 27, 31, 35, 39, 43, 47, 51, 55, 59, 63, 2, 5, 9, 13, 17, 21, 25, 29, 33, 37, 41, 45, 49, 53, 57, 61, 0, 3, 6, 
	10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58, 1, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 54, 60};

	public PresentServer2(BigInteger key) throws Exception {
		super();
		roundKeys = new Present(key).getRoundKeys();
		registers = new byte[128][];
		inverted = new boolean[128];
		tmp = new byte[64][];
		invTmp = new boolean[64];
		presentRoundCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentRound.bmec"),
				true);
		addRoundKeyCircuitReader = CircuitReader.getInstance(new File("circuits/present/PresentAddRoundKey.bmec"), true);
	}

	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("exchangeInputLabels", repetitions);

		for (int i = 0; i < repetitions; i++) {
			otm.start(i);
			iem.start(i);
			generateAndExchangeInputLabels();
			iem.stop(i);

			
			for (int round = 0; round < ROUNDS - 1; round++) {
				// load round key
				for (int j = 0; j < 64; j++) {
					registers[64 + j] = keysRegisters[round * 64 + j];
					inverted[64 + j] = false;
				}
				//execute round (AddRoundKey + SBox)
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(presentRoundCircuitReader, registers, inverted, dos);
				// Permutation
			
				for (int j = 0; j < 64; j++) {
					tmp[j] = registers[PBOX[j]];
					invTmp[j] = inverted[PBOX[j]];
				}
				for (int j = 0; j < 64; j++) {
					registers[j] = tmp[j];
					inverted[j] = invTmp[j];
				}
				
			}
			// Add last round key
			for (int j = 0; j < 64; j++) {
				registers[64 + j] = keysRegisters[(ROUNDS - 1) * 64 + j];
				inverted[64 + j] = false;
			}
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(addRoundKeyCircuitReader,
					registers, inverted, dos);
			// generateAndSendODT
			GarbledCircuitCreator.createAndSendOutputDescriptionTable(addRoundKeyCircuitReader,
					registers, inverted, dos);
			otm.stop(i);
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		
		cleanup();
		System.out.println("Done");
	}

	private void generateAndExchangeInputLabels() throws Exception {
		keysRegisters = new byte[ROUNDS * 64][];
		for (int i = 0; i < ROUNDS; i++) {
			for (int j = 0; j < 64; j++) {
				keysRegisters[i * 64 + j] = LabelMath.randomLabel();
				if (roundKeys[i].testBit(j)) {
					NetUtils.writeLabel(LabelMath.conjugate(keysRegisters[i * 64 + j]), dos);
				} else {
					NetUtils.writeLabel(keysRegisters[i * 64 + j], dos);
				}
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[64][];
		for (int i = 0; i < 64; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[i] = label;
			inverted[i] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		PresentServer2 server = new PresentServer2(BigInteger.ZERO);
		server.run(1000);
	}

}
