package prog;

import gc.GarbledCircuitEvaluator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;

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


public class RandomClient extends ProgramClient {

	private CircuitReader maxminReader, nextBitMaskReader, randomReader,greaterThanReader, addReader;
	private int length;
	private byte[][] minLabels;
	private byte[][] maxminusminLabels;
	private byte[][] maskLabels;
	private byte[][] testRegisters;

	
	
	public RandomClient(int length) throws Exception {
		super();
		this.length = length;
		minLabels = new byte[length][];
		maxminusminLabels = new byte[length][];
		maskLabels = new byte[length][];
		testRegisters = new byte[2*length][];
		String circuitFolder = "circuits/SecureScaling/";
		maxminReader = CircuitReader.getInstance(new File(circuitFolder + "MAXMIN_"+length+".bmec"), true);
		nextBitMaskReader = CircuitReader.getInstance(new File(circuitFolder + "NextBitMask_"+length+".bmec"), true);
		randomReader = CircuitReader.getInstance(new File(circuitFolder + "MaskedRandomness_"+length+".bmec"), true);
		greaterThanReader = CircuitReader.getInstance(new File(circuitFolder + "GreaterThan_"+length+".bmec"), true);	
		addReader = CircuitReader.getInstance(new File(circuitFolder + "ADD_"+length+".bmec"), true);
		registers = new byte[maxminReader.getNumberOfRegisters()][];
	}

	public void run(int repetitions) throws Exception {
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		int tries = 0;
		
		BigInteger min, max;
		Random rnd = new Random();
		
		for(int i=0; i<repetitions; i++){
			/*
			//random min max generation
			min = BigInteger.ONE.shiftLeft(length-1).subtract(new BigInteger(length-1, rnd));
			max = BigInteger.ONE.shiftLeft(length-1).add(new BigInteger(length-1, rnd));
			*/
			min = BigInteger.ZERO;
			max = BigInteger.ONE.shiftLeft(length-1);
			otm.start(i);
			//find range by intersection of rangeA and rangeB
			receiveInputLabels(min, max);
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(maxminReader, registers, dis);
			int[] outregs = maxminReader.getCreatorOutputRegisters();
			for(int j=0; j<length; j++){
				minLabels[j] = registers[outregs[j]];
				maxminusminLabels[j] = registers[outregs[j+length]];
			}
			// compute the next bitmask
			for(int j=0; j<length; j++){
				registers[j] = maxminusminLabels[j];
			}
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(nextBitMaskReader, registers, dis);
			for(int j=0; j<length; j++){
				maskLabels[j] = registers[j];
			}
			
			// get a random value
			tries = 0;
			do{
				generateAndExchangeRandomness();
				for(int j=0; j<length; j++){
					registers[2*length+j] = maskLabels[j];
				}
				GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(randomReader, registers, dis);
				tries++;
			}while(greaterThan());
			
			//add min to random value
			for(int j=0; j<length; j++){
				registers[j+length] = minLabels[j];
			}
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(addReader, registers, dis);
			//receive ODT
			BigInteger output = GarbledCircuitEvaluator.receiveOutputDescriptionTable(addReader, registers, dis);
			System.out.println("random number: " + output.toString() + " out of ["+min.toString()+", "+max.toString()+"]");
			System.out.println(tries + " tries.");
			otm.stop(i);
		}
		System.out.println(otm.SeriesAverage());
		cleanup();
	}
	
	private boolean greaterThan() throws Exception{
		
		for(int i=0; i<length; i++){
			testRegisters[i] = registers[i];
			testRegisters[i+length] = maxminusminLabels[i];
		}
		GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(greaterThanReader, testRegisters, dis);
		BigInteger res = GarbledCircuitEvaluator.receiveOutputDescriptionTable(greaterThanReader, testRegisters, dis);
		if(res.intValue() == 1){
			dos.writeBoolean(true);
		}else{
			dos.writeBoolean(false);
		}
		dos.flush();
		return res.intValue() == 1;
	}
	
	private void receiveInputLabels(BigInteger min, BigInteger max) throws Exception{
		int[] inpregs = maxminReader.getCreatorInputRegisters();
		for(int i=0; i<length; i++){
			registers[inpregs[i]] = NetUtils.readLabel(dis);
			registers[inpregs[i+length]] = NetUtils.readLabel(dis);
		}
		BigInteger input = min.add(max.shiftLeft(length)); 
		inpregs = maxminReader.getEvaluatorInputRegisters();
		byte[][] data = otReceiver.execProtocol(input, 2*length);
		for (int i = 0; i < data.length; i++) {
			registers[inpregs[i]] = data[i];
		}
	}
	
	private void generateAndExchangeRandomness() throws Exception{
		BigInteger r = new BigInteger(length, new Random());
		for(int i=0; i<length; i++){
			registers[i] = NetUtils.readLabel(dis);
		}
		byte[][] data = otReceiver.execProtocol(r, length);
		for (int i = 0; i < data.length; i++) {
			registers[i+length] = data[i];
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		if(args == null || args.length != 2){
			throw new Exception("RandomClient has to be called with to parameter: bitlength and repetitions");
		}
		new RandomClient(Integer.parseInt(args[0])).run(Integer.parseInt(args[1]));

	}

}
