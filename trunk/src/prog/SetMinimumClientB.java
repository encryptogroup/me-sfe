package prog;

import gc.GarbledCircuitEvaluator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;

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


public class SetMinimumClientB extends ProgramClient {

	CircuitReader min2, min3;
	static Random rnd = new Random();

	public SetMinimumClientB() throws Exception {
		super();
		min2 = CircuitReader.getInstance(new File("circuits/setMinimum/MIN_20.bmec"), true);
		min3 = CircuitReader.getInstance(new File("circuits/setMinimum/MIN_3_20.bmec"), true);
		registers = new byte[min3.getNumberOfRegisters()][];
	}

	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();


		for (int i = 0; i < repetitions; i++) {
			receiveInputLabels();
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(min2, registers, dis);
			
			for (int j = 0; j < 499999; j++) {
				receiveInputLabels2();
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(min3, registers, dis);
			}
			GarbledCircuitEvaluator.receiveOutputDescriptionTable(min3, registers, dis);
		}
		cleanup();
		System.out.println("Done");
	}

	private void receiveInputLabels() throws Exception {
		for (int j = 0; j < 20; j++) {
			registers[j] = NetUtils.readLabel(dis);
		}
		// we've got random inputs
		BigInteger input = new BigInteger(20, rnd);
		// receive the labels for my input
		byte[][] data = otReceiver.execProtocol(input, 20);
		for (int i = 0; i < 20; i++) {
			registers[i+20] = data[i];
		}
	}
	
	private void receiveInputLabels2() throws Exception {
		for (int j = 0; j < 20; j++) {
			registers[20 + j] = NetUtils.readLabel(dis);
		}
		// we've got random inputs
		BigInteger input = new BigInteger(20, rnd);
		// receive the labels for my input
		byte[][] data = otReceiver.execProtocol(input,20);
		for (int i = 0; i < 20; i++) {
			registers[i+40] = data[i];
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SetMinimumClientB client = new SetMinimumClientB();
		client.run(1000);

	}

}
