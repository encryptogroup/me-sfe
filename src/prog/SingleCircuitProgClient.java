package prog;

import gc.GarbledCircuitEvaluator;
import io.CircuitReader;
import io.NetUtils;

import java.io.File;
import java.io.IOException;
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


public class SingleCircuitProgClient extends ProgramClient {

	protected CircuitReader reader;
	protected int[] creatorInputRegisters;
	protected int[] evaluatorInputRegisters;
	
	public SingleCircuitProgClient(File circuitFile) throws Exception {
		super();
		reader = CircuitReader.getInstance(circuitFile, false);
		creatorInputRegisters = reader.getCreatorInputRegisters();
		evaluatorInputRegisters = reader.getEvaluatorInputRegisters();
		registers = new byte[reader.getNumberOfRegisters()][];
	}
	
	public void run(int repetitions, BigInteger[] inputs) throws Exception{
		if(inputs == null){
			inputs = new BigInteger[repetitions];
			for(int i=0; i<repetitions; i++){
				inputs[i] = new BigInteger(evaluatorInputRegisters.length, new Random());
				for(int j=0; j<10; j++){
					inputs[i] = inputs[i].clearBit(1023+j);
				}
			}
		}
		StopWatch.measurement("Precomputation").start();
		precompute();
		StopWatch.measurement("Precomputation").stop();
		
		MeasurementSeries otm = StopWatch.measurementSeries("onlineTime", repetitions);
		MeasurementSeries iem = StopWatch.measurementSeries("receivingInputLabels", repetitions);
		MeasurementSeries gcm = StopWatch.measurementSeries("receiveAndEvaluateGarbledCircuitLabels", repetitions);
		
		for(int i=0; i<repetitions; i++){
			otm.start(i);
			iem.start(i);
			receiveInputLabels(inputs[i]);
			iem.stop(i);
			gcm.start(i);
			GarbledCircuitEvaluator.receiveAndEvaluateCircuitLabels(reader, registers, dis);
			BigInteger output = GarbledCircuitEvaluator.receiveOutputDescriptionTable(reader, registers, dis);
			
//			System.out.println("Input: " + inputs[i].toString(10));
			
			if(output != null){
				System.out.println("Output: "+output.toString(16));
			}
			sendCreatorOutputLabels();
			gcm.stop(i);
			otm.stop(i);
			reader.reset();
		}
		System.out.println(iem.SeriesAverage());
		System.out.println(gcm.SeriesAverage());
		System.out.println(otm.SeriesAverage());
		cleanup();
	}
	
	private void sendCreatorOutputLabels() throws IOException{
		int[] cor = reader.getCreatorOutputRegisters();
		if(cor.length > 0){
			for(int reg : cor){
				NetUtils.writeLabel(registers[reg], dos);
			}
			dos.flush();
		}
	}
	
	private void receiveInputLabels(BigInteger input) throws Exception{
		for(int reg : creatorInputRegisters){
			registers[reg] = NetUtils.readLabel(dis);
		}
		if(evaluatorInputRegisters.length > 0){
			byte[][] data = otReceiver.execProtocol(input, evaluatorInputRegisters.length);
			for(int i=0; i<evaluatorInputRegisters.length; i++){
				registers[evaluatorInputRegisters[i]] = data[i];
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		File cFile = new File(args[0]);
		int repetitions = Integer.parseInt(args[1]);
		SingleCircuitProgClient client = new SingleCircuitProgClient(cFile);
		client.run(repetitions, null);
	}

}
