package prog;

import gc.GarbledCircuitCreator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

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


public class SingleCircuitProgServer extends ProgramServer {

	CircuitReader reader;
	int[] creatorInputRegisters;
	int[] evaluatorInputRegisters;
	
	public SingleCircuitProgServer(File circuitFile) throws Exception {
		super();
		reader = CircuitReader.getInstance(circuitFile, false);
		creatorInputRegisters = reader.getCreatorInputRegisters();
		evaluatorInputRegisters = reader.getEvaluatorInputRegisters();
		registers = new byte[reader.getNumberOfRegisters()][];
		inverted = new boolean[reader.getNumberOfRegisters()];
	}
	
	public void run(int repetitions, BigInteger[] inputs) throws Exception{
		if(inputs == null){
			inputs = new BigInteger[repetitions];
			for(int i=0; i<repetitions; i++){
				inputs[i] = new BigInteger(creatorInputRegisters.length, new Random());
			}
		}
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("exchangeInputLabels", repetitions);
		MeasurementSeries gcm = StopWatch.measurementSeries("createAndSendGarbledCircuitLabels", repetitions);
		
		for(int i=0; i<repetitions; i++){
			//System.out.println("Inputs: " + inputs[i].toString(2));
			otm.start(i);
			iem.start(i);
			generateAndExchangeInputLabels(inputs[i]);
			iem.stop(i);
			gcm.start(i);
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(reader, registers, inverted, dos);
			GarbledCircuitCreator.createAndSendOutputDescriptionTable(reader, registers, inverted, dos);
			gcm.stop(i);
			interpretOutputLabels();
			otm.stop(i);
			reader.reset();
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(gcm.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		cleanup();
	}
	
	private void interpretOutputLabels() throws Exception{
		int[] outRegs = reader.getCreatorOutputRegisters();
		if(outRegs.length > 0){
			byte[] label;
			BigInteger output = BigInteger.ZERO;
			for(int i=0; i<outRegs.length; i++){
				label = NetUtils.readLabel(dis);
				if(!Arrays.equals(registers[outRegs[i]], label)){
					if(Arrays.equals(registers[outRegs[i]], LabelMath.conjugate(label))){
						output = output.setBit(i);
					}else{
						throw new Exception("Bad label encountered!");
					}
				}
			}
			System.out.println("Output: " + output);
		}
	}
	
	private void generateAndExchangeInputLabels(BigInteger input) throws Exception{
		for(int i=0; i<creatorInputRegisters.length; i++){
			registers[creatorInputRegisters[i]] = LabelMath.randomLabel();
			inverted[creatorInputRegisters[i]] = false;
			if(input.testBit(i)){
				NetUtils.writeLabel(LabelMath.conjugate(registers[creatorInputRegisters[i]]), dos);
			}else{
				NetUtils.writeLabel(registers[creatorInputRegisters[i]], dos);
			}
		}
		dos.flush();
		if(evaluatorInputRegisters.length > 0){
			byte[][] zeroMsgs = new byte[evaluatorInputRegisters.length][];
			for(int i=0; i<evaluatorInputRegisters.length; i++){
				byte[] label = LabelMath.randomLabel();
				registers[evaluatorInputRegisters[i]] = label;
				inverted[evaluatorInputRegisters[i]] = false;
				zeroMsgs[i] = label;
			}
			otSender.execProtocol(zeroMsgs);
		}
	}
	
	public static void main(String[] args) throws Exception{
		File cFile = new File(args[0]);
		int repetitions = Integer.parseInt(args[1]);
		SingleCircuitProgServer server = new SingleCircuitProgServer(cFile);
		server.run(repetitions, null);
		
		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.close();
	}

}
