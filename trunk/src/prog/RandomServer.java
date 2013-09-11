package prog;

import gc.GarbledCircuitCreator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;

import math.LabelMath;


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


public class RandomServer extends ProgramServer {

	private CircuitReader maxminReader, nextBitMaskReader, randomReader,greaterThanReader, addReader;
	private int length;
	private byte[][] minLabels;
	private boolean[] minInverted;
	private byte[][] maxminusminLabels;
	private boolean[] maxminusminInverted;
	private byte[][] maskLabels;
	private boolean[] maskInverted;
	private byte[][] testRegisters;
	private boolean[] testInverted;
	
	
	public RandomServer(int length) throws Exception {
		super();
		this.length = length;
		minLabels = new byte[length][];
		minInverted = new boolean[length];
		maxminusminLabels = new byte[length][];
		maxminusminInverted = new boolean[length];
		maskLabels = new byte[length][];
		maskInverted = new boolean[length];
		testRegisters = new byte[2*length][];
		testInverted = new boolean[2*length];
		String circuitFolder = "circuits/SecureScaling/";
		maxminReader = CircuitReader.getInstance(new File(circuitFolder + "MAXMIN_"+length+".bmec"), true);
		nextBitMaskReader = CircuitReader.getInstance(new File(circuitFolder + "NextBitMask_"+length+".bmec"), true);
		randomReader = CircuitReader.getInstance(new File(circuitFolder + "MaskedRandomness_"+length+".bmec"), true);
		greaterThanReader = CircuitReader.getInstance(new File(circuitFolder + "GreaterThan_"+length+".bmec"), true);	
		addReader = CircuitReader.getInstance(new File(circuitFolder + "ADD_"+length+".bmec"), true);
		registers = new byte[maxminReader.getNumberOfRegisters()][];
		inverted = new boolean[maxminReader.getNumberOfRegisters()];
	}

	public void run(int repetitions) throws Exception {
		precompute();
		BigInteger min, max;
		Random rnd = new Random();
		
		for(int i=0; i<repetitions; i++){
			/*
			// random min max generation
			min = BigInteger.ONE.shiftLeft(length-1).subtract(new BigInteger(length-1, rnd));
			max = BigInteger.ONE.shiftLeft(length-1).add(new BigInteger(length-1, rnd));
			*/
			//fixed min max
			min = BigInteger.ZERO;
			max = BigInteger.ONE.shiftLeft(length-1);
			
			//find range by intersection of rangeA and rangeB
			generateAndExchangeInputLabels(min, max);
			System.out.println("Iteration " + i + ": [" + min.toString() + ", " + max.toString()+"]");
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(maxminReader, registers, inverted, dos);
			int[] outregs = maxminReader.getCreatorOutputRegisters();
			for(int j=0; j<length; j++){
				minLabels[j] = registers[outregs[j]];
				minInverted[j] = inverted[outregs[j]];
				maxminusminLabels[j] = registers[outregs[j+length]];
				maxminusminInverted[j] = inverted[outregs[j+length]];
			}
			dos.flush();
			//compute the next bitmask
			for(int j=0; j<length; j++){
				registers[j] = maxminusminLabels[j];
				inverted[j] = maxminusminInverted[j];
				inverted[j+length] = false;
			}
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(nextBitMaskReader, registers, inverted, dos);
			for(int j=0; j<length; j++){
				maskLabels[j] = registers[j];
				maskInverted[j] = inverted[j];
			}
			
			// get a random value
			do{
				generateAndExchangeRandomness();
				for(int j=0; j<length; j++){
					registers[2*length+j] = maskLabels[j];
					inverted[2*length+j] = maskInverted[j];
				}
				for(int j=3*length; j<inverted.length; j++){
					inverted[j] = false;
				}
				GarbledCircuitCreator.createAndSendGarbledCircuitLabels(randomReader, registers, inverted, dos);

			}while(greaterThan());
			
			//add min to random value
			for(int j=0; j<length; j++){
				registers[j+length] = minLabels[j];
				inverted[j+length] = minInverted[j];
			}
			GarbledCircuitCreator.createAndSendGarbledCircuitLabels(addReader, registers, inverted, dos);
			//send ODT or receive OutputLabels
			GarbledCircuitCreator.createAndSendOutputDescriptionTable(addReader, registers, inverted, dos);

		}
		
		cleanup();
	}
	
	private boolean greaterThan() throws Exception{
		
		for(int i=0; i<length; i++){
			testRegisters[i] = registers[i];
			testInverted[i] = inverted[i];
			testRegisters[i+length] = maxminusminLabels[i];
			testInverted[i+length] = maxminusminInverted[i];
		}
		GarbledCircuitCreator.createAndSendGarbledCircuitLabels(greaterThanReader, testRegisters, testInverted, dos);
		GarbledCircuitCreator.createAndSendOutputDescriptionTable(greaterThanReader, testRegisters, testInverted, dos);
		return dis.readBoolean();
	}
	
	private void generateAndExchangeInputLabels(BigInteger min, BigInteger max) throws Exception{
		int[] inpregs = maxminReader.getCreatorInputRegisters();
		for(int i=0; i<length; i++){
			registers[inpregs[i]] = LabelMath.randomLabel();
			inverted[inpregs[i]] = false;
			if(min.testBit(i)){
				NetUtils.writeLabel(LabelMath.conjugate(registers[inpregs[i]]), dos);
			}else{
				NetUtils.writeLabel(registers[inpregs[i]], dos);
			}
			registers[inpregs[i+length]] = LabelMath.randomLabel();
			inverted[inpregs[i+length]] = false;
			if(max.testBit(i)){
				NetUtils.writeLabel(LabelMath.conjugate(registers[inpregs[i+length]]), dos);
			}else{
				NetUtils.writeLabel(registers[inpregs[i+length]], dos);
			}
		}
		dos.flush();
		inpregs = maxminReader.getEvaluatorInputRegisters();
		byte[][] zeroMsgs = new byte[2*length][];
		for (int i = 0; i < zeroMsgs.length; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[inpregs[i]] = label;
			inverted[inpregs[i]] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}
	
	private void generateAndExchangeRandomness() throws Exception{
		BigInteger r = new BigInteger(length, new Random());
		for(int i=0; i<length; i++){
			registers[i] = LabelMath.randomLabel();
			inverted[i] = false;
			if(r.testBit(i)){
				NetUtils.writeLabel(LabelMath.conjugate(registers[i]), dos);
			}else{
				NetUtils.writeLabel(registers[i], dos);
			}
		}
		dos.flush();
		byte[][] zeroMsgs = new byte[length][];
		for (int i = 0; i < zeroMsgs.length; i++) {
			byte[] label = LabelMath.randomLabel();
			registers[i+length] = label;
			inverted[i+length] = false;
			zeroMsgs[i] = label;
		}
		otSender.execProtocol(zeroMsgs);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		if(args == null || args.length != 2){
			throw new Exception("RandomServer has to be called with two parameter: bitlength and repetitions");
		}
		new RandomServer(Integer.parseInt(args[0])).run(Integer.parseInt(args[1]));

	}
}
