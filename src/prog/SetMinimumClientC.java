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


public class SetMinimumClientC extends ProgramClient {

	CircuitReader min500, min2;
	static Random rnd = new Random();
	byte[][] tmp;

	public SetMinimumClientC() throws Exception {
		super();
		min500 = CircuitReader.getInstance(new File("circuits/setMinimum/MIN_500_20.bmec"), true);
		min2 = CircuitReader.getInstance(new File("circuits/setMinimum/MIN_20.bmec"), true);
		registers = new byte[min500.getNumberOfRegisters()][];
		tmp = new byte[20][];
	}

	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();


		for (int i = 0; i < repetitions; i++) {
			receiveInputLabels();
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(min500, registers, dis);
			System.arraycopy(registers, 0, tmp, 0, 20);
			
			for (int j = 0; j < 1999; j++) {
				receiveInputLabels();
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(min500, registers, dis);
				System.arraycopy(tmp, 0, registers, 20, 20);
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(min2, registers, dis);
				System.arraycopy(registers, 0, tmp, 0, 20);
			}
			GarbledCircuitEvaluator.receiveOutputDescriptionTable(min2, registers, dis);
		}
		cleanup();
		System.out.println("Done");
	}

	private void receiveInputLabels() throws Exception {
		for (int i = 0; i < 250; i++) {
			for (int j = 0; j < 20; j++) {
				registers[i * 20 + j] = NetUtils.readLabel(dis);
			}
		}
		// we've got random inputs
		BigInteger input = new BigInteger(250 * 20, rnd);
		// receive the labels for my input
		byte[][] data = otReceiver.execProtocol(input, 250*20);
		for (int i = 0; i < 250*20; i++) {
			registers[i+250*20] = data[i];
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SetMinimumClientC client = new SetMinimumClientC();
		client.run(1000);

	}

}
