package prog;

import java.io.File;
import java.util.Scanner;

import math.LabelMath;

import io.CircuitReader;
import io.NetUtils;
import gc.GarbledCircuitCreator;
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


public class AESServer extends ProgramServer {

	protected final static int Nb = 4; // words in a block, always 4 for now
	private short[] w; // the expanded key
	protected static int Nk; // key length in words
	protected static int Nr; // number of rounds, = Nk + 6

	private final static short[] SBox = { // SubBytes table
	0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
			0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4,
			0x72, 0xc0, 0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1,
			0x71, 0xd8, 0x31, 0x15, 0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12,
			0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, 0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0,
			0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, 0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc,
			0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, 0xd0, 0xef, 0xaa, 0xfb,
			0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8, 0x51, 0xa3,
			0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
			0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d,
			0x19, 0x73, 0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14,
			0xde, 0x5e, 0x0b, 0xdb, 0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3,
			0xac, 0x62, 0x91, 0x95, 0xe4, 0x79, 0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9,
			0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, 0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6,
			0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, 0x70, 0x3e, 0xb5, 0x66,
			0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, 0xe1, 0xf8,
			0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
			0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54,
			0xbb, 0x16 };

	private final static short[] Rcon = { 1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, 216, 171, 77,
			154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145 };

	protected final static short[] ShiftRows = new short[] { 0, 5, 10, 15, 4, 9, 14, 3, 8, 13, 2, 7,
			12, 1, 6, 11 };

	private byte[][] keysRegisters;
	private byte[][] sboxRegisters;
	private boolean[] sboxInverted;
	private byte[][] tmp;
	private boolean[] tmpInv;
	CircuitReader addRoundKeyCircuitReader;
	CircuitReader sBoxCircuitReader;
	CircuitReader mixColumnsCircuitReader;

	public AESServer(short[] key, boolean newVersion) throws Exception {
		super();
		Nk = key.length / Nb;
		Nr = Nk + 6;	
		w = new short[4 * Nb * (Nr + 1)]; // room for expanded key
		keyExpansion(key);
		registers = new byte[256][];
		inverted = new boolean[256];
		addRoundKeyCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AESAddRoundKey.bmec"), true);
		if(newVersion){
			sBoxCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AES_SBox.bmec"), true);
		}else{
			sBoxCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AES_SBox_old.bmec"), true);
		}
		mixColumnsCircuitReader = CircuitReader.getInstance(new File("circuits/aes/AESMixColumns.bmec"), true);
		sboxRegisters = new byte[sBoxCircuitReader.getNumberOfRegisters()][];
		sboxInverted = new boolean[sBoxCircuitReader.getNumberOfRegisters()];
		tmp = new byte[mixColumnsCircuitReader.getNumberOfRegisters()][];
		tmpInv = new boolean[mixColumnsCircuitReader.getNumberOfRegisters()];
		dos.writeInt(Nr);
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
			generateAndExchangeInputLabels();
			iem.stop(i);
			
			// load round key
			for (int j = 0; j < 128; j++) {
				registers[128 + j] = keysRegisters[j];
				inverted[128 + j] = false;
			}
			// AddRoundKey
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(addRoundKeyCircuitReader,
					registers, inverted, dos);

			
			for (int round = 1; round < Nr; round++) {
				// SubBytes
				for (int r = 0; r < 16; r++) {
					for (int j = 0; j < 8; j++) {
						sboxRegisters[j] = registers[r * 8 + j];
						sboxInverted[j] = inverted[r * 8 + j];
					}
					GarbledCircuitCreator.createAndSendGarbledCircuitLabels(sBoxCircuitReader,
							sboxRegisters, sboxInverted, dos);
					for (int j = 0; j < 8; j++) {
						registers[r * 8 + j] = sboxRegisters[sboxOutputMapping[j]];
						inverted[r * 8 + j] = sboxInverted[sboxOutputMapping[j]];
					}
				}
				// ShiftRows
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 8; k++) {
						tmp[j * 8 + k] = registers[ShiftRows[j] * 8 + k];
						tmpInv[j * 8 + k] = inverted[ShiftRows[j] * 8 + k];
					}
				}
				// MixColumns
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(mixColumnsCircuitReader,
						tmp, tmpInv, dos);
				for (int j = 0; j < 128; j++) {
					registers[j] = tmp[mixColumnsOutputMapping[j]];
					inverted[j] = tmpInv[mixColumnsOutputMapping[j]];
				}
				// load round key
				for (int j = 0; j < 128; j++) {
					registers[128 + j] = keysRegisters[round * 128 + j];
					inverted[128 + j] = false;
				}
				// AddRoundKey
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(addRoundKeyCircuitReader,
						registers, inverted, dos);
			} 

			// SubBytes
			for (int r = 0; r < 16; r++) {
				for (int j = 0; j < 8; j++) {
					sboxRegisters[j] = registers[r * 8 + j];
					sboxInverted[j] = inverted[r * 8 + j];
				}
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(sBoxCircuitReader,
						sboxRegisters, sboxInverted, dos);
				for (int j = 0; j < 8; j++) {
					registers[r * 8 + j] = sboxRegisters[sboxOutputMapping[j]];
					inverted[r * 8 + j] = sboxInverted[sboxOutputMapping[j]];
				}
			}
			// ShiftRows
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 8; k++) {
					tmp[j * 8 + k] = registers[ShiftRows[j] * 8 + k];
					tmpInv[j * 8 + k] = inverted[ShiftRows[j] * 8 + k];
				}
			}
			for(int j=0; j<128; j++){
				registers[j] = tmp[j];
				inverted[j] = tmpInv[j];
			}
			// Add last round key
			for (int j = 0; j < 128; j++) {
				registers[128 + j] = keysRegisters[Nr * 128 + j];
				inverted[128 + j] = false;
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
		keysRegisters = new byte[(Nr + 1) * 128][];
		for (int i = 0; i < Nr + 1; i++) {
			for (int j = 0; j < 128; j++) {
				keysRegisters[i * 128 + j] = LabelMath.randomLabel();
				if (testBit(w, i * 128 + j)) {
					NetUtils.writeLabel(LabelMath.conjugate(keysRegisters[i * 128 + j]), dos);
				} else {
					NetUtils.writeLabel(keysRegisters[i * 128 + j], dos);
				}
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[Nb * 32][];
		for (int i = 0; i < zeroMsgs.length; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[i] = label;
			inverted[i] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}

	private boolean testBit(short[] w, int pos) {
		int wi = pos / 8;
		int idx = pos % 8;
		return ((w[wi] & (1 << idx)) == 0) ? false : true;
	}

	// KeyExpansion: expand key, byte-oriented code, but tracks words
	private void keyExpansion(short[] key) {
		short[] temp = new short[4];

		// first just copy key to w
		int j = 0;
		while (j < 4 * Nk) {
			w[j] = key[j++];
		}

		// here j == 4*Nk;
		int i;
		while (j < 4 * Nb * (Nr + 1)) {
			i = j / 4; // j is always multiple of 4 here

			// handle everything word-at-a time, 4 bytes at a time
			for (int iTemp = 0; iTemp < 4; iTemp++)
				temp[iTemp] = w[j - 4 + iTemp];
			if (i % Nk == 0) {
				short ttemp, tRcon;
				short oldtemp0 = temp[0];
				for (int iTemp = 0; iTemp < 4; iTemp++) {
					if (iTemp == 3)
						ttemp = oldtemp0;
					else
						ttemp = temp[iTemp + 1];
					if (iTemp == 0)
						tRcon = Rcon[i / Nk - 1];
					else
						tRcon = 0;
					temp[iTemp] = (short) (SBox[ttemp & 0xff] ^ tRcon);
				}
			} else if (Nk > 6 && (i % Nk) == 4) {
				for (int iTemp = 0; iTemp < 4; iTemp++)
					temp[iTemp] = SBox[temp[iTemp] & 0xff];
			}
			for (int iTemp = 0; iTemp < 4; iTemp++)
				w[j + iTemp] = (short) (w[j - 4 * Nk + iTemp] ^ temp[iTemp]);
			j = j + 4;
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
		short[] key = { 0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6, 0xab, 0xf7, 0x15, 0x88,
			0x09, 0xcf, 0x4f, 0x3c };
		AESServer server = new AESServer(key, newVersion);
		server.run(repetitions);
		
		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.close();

	}

}
