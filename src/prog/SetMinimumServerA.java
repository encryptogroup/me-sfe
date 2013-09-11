package prog;

import gc.GarbledCircuitCreator;
import io.CircuitReader;
import io.NetUtils;
import io.SetMinimumGenerator;

import java.math.BigInteger;
import java.util.Random;

import math.LabelMath;

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


public class SetMinimumServerA extends ProgramServer {

	CircuitReader min;
	static Random rnd = new Random();
	int elements, bitlen;

	public SetMinimumServerA(int n, int l) throws Exception {
		super();
		min = new SetMinimumGenerator(n, l);
		registers = new byte[min.getNumberOfRegisters()][];
		inverted = new boolean[min.getNumberOfRegisters()];
		elements = n;
		bitlen = l;
	}

	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();

		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("exchangeInputLabels", repetitions);

		for (int i = 0; i < repetitions; i++) {
			otm.start(i);
			iem.resume(i);
			generateAndExchangeInputLabels();
			iem.pause(i);
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(min, registers,
					inverted, dos);
			
			GarbledCircuitCreator.createAndSendOutputDescriptionTable(min, registers, inverted, dos);
			otm.stop(i);
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		cleanup();
		System.out.println("Done");
	}

	private void generateAndExchangeInputLabels() throws Exception {
		// we've got random inputs
		BigInteger input = new BigInteger((elements/2)*bitlen, rnd);
		
		for (int j = 0; j < (elements/2)*bitlen; j++) {
			registers[j] = LabelMath.randomLabel();
			inverted[j] = false;
			if (input.testBit(j)) {
				NetUtils.writeLabel(LabelMath.conjugate(registers[j]), dos);
			} else {
				NetUtils.writeLabel(registers[j], dos);
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[(elements/2)*bitlen][];
		for (int i = 0; i < (elements/2)*bitlen; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[i + (elements/2)*bitlen] = label;
			inverted[i + (elements/2)*bitlen] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		SetMinimumServerA server = new SetMinimumServerA(1000000, 20);
		server.run(1000);
	}

}
