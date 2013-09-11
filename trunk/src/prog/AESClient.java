package prog;

import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;

import gc.GarbledCircuitEvaluator;
import util.MeasurementSeries;
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


public class AESClient extends ProgramClient {

	final static int Nb = AESServer.Nb;
	static int Nr;
	private short[] msg;
	private byte[][] keysRegisters;
	private byte[][] sboxRegisters;
	private byte[][] tmp;
	private CircuitReader addRoundKeyCircuitReader, sBoxCircuitReader, mixColumnsCircuitReader;
	private static short[] ShiftRows = AESServer.ShiftRows;
	
	public AESClient(short[] msg, boolean newVersion) throws Exception{
		super();
		this.msg = msg;
		registers = new byte[256][];	
		addRoundKeyCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AESAddRoundKey.bmec"), true);
		if(newVersion){
			sBoxCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AES_SBox.bmec"), true);
		}else{
			sBoxCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AES_SBox_old.bmec"), true);
		}
		mixColumnsCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AESMixColumns.bmec"), true);
		sboxRegisters = new byte[sBoxCircuitReader.getNumberOfRegisters()][];
		tmp = new byte[mixColumnsCircuitReader.getNumberOfRegisters()][];
		Nr = dis.readInt();
	}
	
	
	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("exchangeInputLabels", repetitions);

		int[] sboxOutputMapping = sBoxCircuitReader.getEvaluatorOutputRegisters();
		int[] mixColumnsOutputMapping = mixColumnsCircuitReader.getCreatorOutputRegisters();

		for (int i = 0; i < repetitions; i++) {
			otm.start(i);
			iem.start(i);
			receiveInputLabels(msg);
			iem.stop(i);
			
			// load round key
			for (int j = 0; j < 128; j++) {
				registers[128 + j] = keysRegisters[j];
			}
			// AddRoundKey
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(addRoundKeyCircuitReader, registers, dis);

			
			for (int round = 1; round < Nr; round++) {
				// SubBytes
				for (int r = 0; r < 16; r++) {
					for (int j = 0; j < 8; j++) {
						sboxRegisters[j] = registers[r * 8 + j];
					}
					GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(sBoxCircuitReader, sboxRegisters, dis);
					for (int j = 0; j < 8; j++) {
						registers[r * 8 + j] = sboxRegisters[sboxOutputMapping[j]];
					}
				}
				// ShiftRows
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 8; k++) {
						tmp[j * 8 + k] = registers[ShiftRows[j] * 8 + k];
					}
				}
				// MixColumns
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(mixColumnsCircuitReader, tmp, dis);
				for (int j = 0; j < 128; j++) {
					registers[j] = tmp[mixColumnsOutputMapping[j]];
				}
				// load round key
				for (int j = 0; j < 128; j++) {
					registers[128 + j] = keysRegisters[round * 128 + j];
				}
				// AddRoundKey
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(addRoundKeyCircuitReader, registers, dis);
			} 

			// SubBytes
			for (int r = 0; r < 16; r++) {
				for (int j = 0; j < 8; j++) {
					sboxRegisters[j] = registers[r * 8 + j];
				}
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(sBoxCircuitReader, sboxRegisters, dis);
				for (int j = 0; j < 8; j++) {
					registers[r * 8 + j] = sboxRegisters[sboxOutputMapping[j]];
				}
			}
			// ShiftRows
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 8; k++) {
					tmp[j * 8 + k] = registers[ShiftRows[j] * 8 + k];
				}
			}
			for(int j=0; j<128; j++){
				registers[j] = tmp[j];
			}
			// Add last round key
			for (int j = 0; j < 128; j++) {
				registers[128 + j] = keysRegisters[Nr * 128 + j];
			}
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(addRoundKeyCircuitReader, registers, dis);
			// generateAndSendODT
			BigInteger output = GarbledCircuitEvaluator.receiveOutputDescriptionTable(addRoundKeyCircuitReader, registers, dis);
			System.out.println("Output: " + output.toString(16));
			otm.stop(i);
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		cleanup();
		System.out.println("Done");
	}
	
	private void receiveInputLabels(short[] msg) throws Exception{
		//receive all the round keys
		keysRegisters = new byte[(Nr + 1) * 128][];
		for(int i=0; i<keysRegisters.length; i++){
			keysRegisters[i] = NetUtils.readLabel(dis);
		}
		//receive the labels for my input
		BigInteger m = BigInteger.ZERO;
		for (int i = 0; i < 16; i++)
		    m = m.shiftLeft(8).xor(BigInteger.valueOf(msg[15-i]));
		byte[][] data = otReceiver.execProtocol(m, Nb * 32);
		for(int i=0; i<128; i++){
			registers[i] = data[i];
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		boolean newVersion = true;
		int repetitions = 1000;
		try{
			if(args.length < 2){
				System.out.println("expecting arguments: newVersion('true'/'false') repetitions(int)");
				System.exit(0);
			}
			newVersion = Boolean.parseBoolean(args[0]);
			repetitions = Integer.parseInt(args[1]);
		}catch(Exception e){
			System.out.println("error parsing: " + args[0] +"! expecting 'true'/'false'");
		}
		short[] msg = { 0x32, 0x43, 0xf6, 0xa8, 0x88, 0x5a, 0x30, 0x8d, 0x31, 0x31, 0x98, 0xa2,
				0xe0, 0x37, 0x07, 0x34 };
		AESClient client = new AESClient(msg, newVersion);
		client.run(repetitions);

	}

}
