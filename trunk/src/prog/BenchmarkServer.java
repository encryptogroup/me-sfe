package prog;

import gc.GarbledCircuitCreator;
import io.BenchmarkDummyReader;
import io.CircuitReader;
import io.NetUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import ot.OTExtSenderLowerMemAES;

import math.LabelMath;

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


public class BenchmarkServer extends ProgramServer {

	CircuitReader reader;
	
	public BenchmarkServer() throws IOException {
		super();
		registers = new byte[2][];
		inverted = new boolean[2];
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
			generateAndExchangeInputLabels();
			iem.stop(i);
			otm.start(i);
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(reader, registers, inverted, dos);
			otm.stop(i);
			reader.reset();
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		
	}
	
	private void generateAndExchangeInputLabels() throws Exception{
		BigInteger myInput = new BigInteger(1, new Random());
		registers[0] = LabelMath.randomLabel();
		if(myInput.testBit(0)){
			NetUtils.writeLabel(LabelMath.conjugate(registers[0]), dos);
		}else{
			NetUtils.writeLabel(registers[0], dos);
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[1][];
		byte[] label = LabelMath.randomLabel();
		registers[1] = label;
		zeroMsgs[0] = label;
		otSender.execProtocol(zeroMsgs);
	}
	
	private void runOTMeasurement() throws Exception{
		int r = 1000;
		byte[][] zeroMsgs;
		Measurement ot10 = StopWatch.measurement("OT_10");
		ot10.start();
		for(int j=0; j<r; j++){		
			zeroMsgs = new byte[10][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otSender.execProtocol(zeroMsgs);
		}
		ot10.stop();
		Measurement ot100 = StopWatch.measurement("OT_100");
		ot100.start();
		for(int j=0; j<r; j++){
			zeroMsgs = new byte[100][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otSender.execProtocol(zeroMsgs);
		}
		ot100.stop();
		Measurement ot1000 = StopWatch.measurement("OT_1000");
		ot1000.start();
		for(int j=0; j<r; j++){
			zeroMsgs = new byte[1000][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otSender.execProtocol(zeroMsgs);
		}
		ot1000.stop();
		Measurement ot10000 = StopWatch.measurement("OT_10000");
		ot10000.start();
		for(int j=0; j<r; j++){
			zeroMsgs = new byte[10000][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otSender.execProtocol(zeroMsgs);
		}
		ot10000.stop();
	}
	
	private void runOTAESMeasurement() throws Exception{
		OTExtSenderLowerMemAES otaesSender = new OTExtSenderLowerMemAES(LabelMath.LABEL_BYTESIZE*8, dis, dos);
		int r = 1000;
		byte[][] zeroMsgs;
		/*
		Measurement ot10 = StopWatch.measurement("OT_10");
		ot10.start();
		for(int j=0; j<r; j++){		
			zeroMsgs = new byte[10][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otaesSender.execProtocol(zeroMsgs);
		}
		ot10.stop();
		Measurement ot100 = StopWatch.measurement("OT_100");
		ot100.start();
		for(int j=0; j<r; j++){
			zeroMsgs = new byte[100][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otaesSender.execProtocol(zeroMsgs);
		}
		ot100.stop();
		Measurement ot1000 = StopWatch.measurement("OT_1000");
		ot1000.start();
		for(int j=0; j<r; j++){
			zeroMsgs = new byte[1000][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otaesSender.execProtocol(zeroMsgs);
		}
		ot1000.stop(); */
		Measurement ot10000 = StopWatch.measurement("OT_10000");
		ot10000.start();
		for(int j=0; j<r; j++){
			zeroMsgs = new byte[10000][];
			for(int i=0; i<zeroMsgs.length; i++){
				zeroMsgs[i] = LabelMath.randomLabel();
			}
			otaesSender.execProtocol(zeroMsgs);
		}
		ot10000.stop();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int numberOfGates = Integer.parseInt(args[0]);
		int repetitions = Integer.parseInt(args[1]);
		new BenchmarkServer().run(repetitions, numberOfGates);

	}

}
