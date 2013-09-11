package prog;

import gc.GarbledCircuitCreator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
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


public class SetMinimumServerB extends ProgramServer {

	CircuitReader min2, min3;
	static Random rnd = new Random();
	//byte[][] tmp;
	//boolean[] tmpInv;

	public SetMinimumServerB() throws Exception {
		super();
		min2 = CircuitReader.getInstance(new File("circuits/setMinimum/MIN_20.bmec"), true);
		min3 = CircuitReader.getInstance(new File("circuits/setMinimum/MIN_3_20.bmec"), true);
		registers = new byte[min3.getNumberOfRegisters()][];
		inverted = new boolean[min3.getNumberOfRegisters()];
		//tmp = new byte[20][];
		//tmpInv = new boolean[20];
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
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(min2, registers,
					inverted, dos);
			//System.arraycopy(registers, 0, tmp, 0, 20);
			//System.arraycopy(inverted, 0, tmpInv, 0, 20);
			
			for (int j = 0; j < 499999; j++) {
				iem.resume(i);
				generateAndExchangeInputLabels2();
				iem.pause(i);
				//System.arraycopy(tmp, 0, registers, 500*20, 20);
				//System.arraycopy(tmpInv, 0, inverted, 500*20, 20);
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(min3, registers,
							inverted, dos);
				//System.arraycopy(registers, 0, tmp, 0, 20);
				//System.arraycopy(inverted, 0, tmpInv, 0, 20);
			}
			GarbledCircuitCreator.createAndSendOutputDescriptionTable(min3, registers, inverted, dos);
			otm.stop(i);
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		cleanup();
		System.out.println("Done");
	}

	private void generateAndExchangeInputLabels() throws Exception {
		// we've got random inputs
		BigInteger input = new BigInteger(20, rnd);
		for (int j = 0; j < 20; j++) {
			registers[j] = LabelMath.randomLabel();
			inverted[j] = false;
			if (input.testBit(j)) {
				NetUtils.writeLabel(LabelMath.conjugate(registers[j]), dos);
			} else {
				NetUtils.writeLabel(registers[j], dos);
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[20][];
		for (int i = 0; i < 20; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[i + 20] = label;
			inverted[i + 20] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}
	
	private void generateAndExchangeInputLabels2() throws Exception {
		// we've got random inputs
		BigInteger input = new BigInteger(20, rnd);
		for (int j = 0; j < 20; j++) {
			registers[20+j] = LabelMath.randomLabel();
			inverted[20+j] = false;
			if (input.testBit(j)) {
				NetUtils.writeLabel(LabelMath.conjugate(registers[20+j]), dos);
			} else {
				NetUtils.writeLabel(registers[20+j], dos);
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[20][];
		for (int i = 0; i < 20; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[i + 40] = label;
			inverted[i + 40] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		SetMinimumServerB server = new SetMinimumServerB();
		server.run(1000);
	}

}
