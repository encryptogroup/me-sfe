package prog;

import gc.GarbledCircuitEvaluator;
import io.BenchmarkDummyReader;
import io.CircuitReader;
import io.NetUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import ot.OTExtReceiverLowerMemAES;

import util.Measurement;
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


public class BenchmarkClient extends ProgramClient {

	CircuitReader reader;
	
	public BenchmarkClient() throws IOException {
		super();
		registers = new byte[2][];
	}

	public void run(int repetitions, int numberOfGates) throws Exception{
		reader = new BenchmarkDummyReader(numberOfGates);
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		//runOTMeasurement();
		//runOTAESMeasurement();
		
		runNonLinearGatesMeasurement(repetitions);
		
		cleanup();
		
	}
	
	private void runNonLinearGatesMeasurement(int repetitions) throws Exception{
		
		MeasurementSeries otm = StopWatch.measurementSeries("evalNonLinearGates", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("exchangeInputLabels", repetitions);
		
		for(int i=0; i<repetitions; i++){		
			iem.start(i);
			receiveInputLabels();
			iem.stop(i);
			otm.start(i);
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(reader, registers, dis);
			otm.stop(i);
			reader.reset();
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		
		
	}
	
	private void runOTMeasurement() throws Exception{
		int r = 1000;
		Measurement ot10 = StopWatch.measurement("OT_10");
		Random rnd = new Random();
		BigInteger input;
		byte[][] data;
		ot10.start();
		for(int i=0; i<r; i++){			
			input = new BigInteger(10,rnd);
			data = otReceiver.execProtocol(input, 10);
		}
		ot10.stop();
		Measurement ot100 = StopWatch.measurement("OT_100");
		ot100.start();
		for(int i=0; i<r; i++){		
			input = new BigInteger(100,rnd);
			data = otReceiver.execProtocol(input, 100);
		}
		ot100.stop();
		Measurement ot1000 = StopWatch.measurement("OT_1000");
		ot1000.start();
		for(int i=0; i<r; i++){
			input = new BigInteger(1000,rnd);
			data = otReceiver.execProtocol(input, 1000);
		}
		ot1000.stop();
		Measurement ot10000 = StopWatch.measurement("OT_10000");
		ot10000.start();
		for(int i=0; i<r; i++){
			input = new BigInteger(10000,rnd);
			data = otReceiver.execProtocol(input, 10000);
		}
		ot10000.stop();
	}
	
	private void runOTAESMeasurement() throws Exception{
		OTExtReceiverLowerMemAES otReceiver = new OTExtReceiverLowerMemAES(dis, dos);
		int r = 1000;
		Random rnd = new Random();
		BigInteger input;
		byte[][] data;
		/*
		Measurement ot10 = StopWatch.measurement("OT_10");
		ot10.start();
		for(int i=0; i<r; i++){			
			input = new BigInteger(10,rnd);
			data = otReceiver.execProtocol(input, 10);
		}
		ot10.stop();
		Measurement ot100 = StopWatch.measurement("OT_100");
		ot100.start();
		for(int i=0; i<r; i++){		
			input = new BigInteger(100,rnd);
			data = otReceiver.execProtocol(input, 100);
		}
		ot100.stop();
		Measurement ot1000 = StopWatch.measurement("OT_1000");
		ot1000.start();
		for(int i=0; i<r; i++){
			input = new BigInteger(1000,rnd);
			data = otReceiver.execProtocol(input, 1000);
		}
		ot1000.stop(); */
		Measurement ot10000 = StopWatch.measurement("OT_10000");
		ot10000.start();
		for(int i=0; i<r; i++){
			input = new BigInteger(10000,rnd);
			data = otReceiver.execProtocol(input, 10000);
		}
		ot10000.stop();
	}
	
	
	private void receiveInputLabels() throws Exception{

		registers[0] = NetUtils.readLabel(dis);
		//receive the labels for my input
		BigInteger myInput = new BigInteger(1,new Random());
		byte[][] data = otReceiver.execProtocol(myInput, 1);
		registers[1] = data[0];
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int numberOfGates = Integer.parseInt(args[0]);
		int repetitions = Integer.parseInt(args[1]);
		new BenchmarkClient().run(repetitions, numberOfGates);

	}

}
