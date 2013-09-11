package prog;

import gc.GarbledCircuitEvaluator;
import io.CircuitReader;
import io.NetUtils;
import io.SetMinimumGenerator;

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


public class SetMinimumClientA extends ProgramClient {

	CircuitReader min;
	static Random rnd = new Random();
	int elements, bitlen;

	public SetMinimumClientA(int n, int l) throws Exception {
		super();
		min = new SetMinimumGenerator(n, l);
		registers = new byte[min.getNumberOfRegisters()][];
		elements = n;
		bitlen = l;
	}

	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();


		for (int i = 0; i < repetitions; i++) {
			receiveInputLabels();
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(min, registers, dis);
			GarbledCircuitEvaluator.receiveOutputDescriptionTable(min, registers, dis);
		}
		cleanup();
		System.out.println("Done");
	}

	private void receiveInputLabels() throws Exception {
		for (int j = 0; j < (elements*bitlen)/2; j++) {
			registers[j] = NetUtils.readLabel(dis);
		}
		// we've got random inputs
		BigInteger input = new BigInteger((elements*bitlen)/2, rnd);
		// receive the labels for my input
		byte[][] data = otReceiver.execProtocol(input, (elements*bitlen)/2);
		for (int i = 0; i < (elements*bitlen)/2; i++) {
			registers[i+(elements*bitlen)/2] = data[i];
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SetMinimumClientA client = new SetMinimumClientA(1000000,20);
		client.run(1000);

	}

}
