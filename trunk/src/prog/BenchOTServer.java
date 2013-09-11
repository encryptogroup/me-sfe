package prog;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import ot.ECNPOTReceiver;
import ot.NPOTReceiverMultiThreading;
import ot.Receiver;


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


public class BenchOTServer extends ProgramServer{

	public BenchOTServer() throws IOException {
		super();
		
	}
	
	public void run(int repetitions) throws Exception{
		MeasurementSeries otst = StopWatch.measurementSeries("OT singleThread", repetitions);
		MeasurementSeries otmt1 = StopWatch.measurementSeries("OT multiThread1", repetitions);
		MeasurementSeries otmt2 = StopWatch.measurementSeries("OT multiThread2", repetitions);
		MeasurementSeries otmt3 = StopWatch.measurementSeries("OT multiThread4", repetitions);
		MeasurementSeries otec = StopWatch.measurementSeries("OT EC", repetitions);

		for(int i=0; i<repetitions; i++){
			otst.start(i);
			runOTST();
			otst.stop(i);
			otmt1.start(i);
			runOTMT(1);
			otmt1.stop(i);
			otmt2.start(i);
			runOTMT(2);
			otmt2.stop(i);
			otmt3.start(i);
			runOTMT(4);
			otmt3.stop(i);
			otec.start(i);
			runOTEC();
			otec.stop(i);
		}
		System.out.println(otst.SeriesAverage());
		System.out.println(otmt1.SeriesAverage());
		System.out.println(otmt2.SeriesAverage());
		System.out.println(otmt3.SeriesAverage());
		System.out.println(otec.SeriesAverage());
		cleanup();
	}
	
	private Receiver rcver;
	private BigInteger s;
	private BigInteger[] keys;
	private Random rnd = new Random();
	
	private void runOTST() throws Exception{
		rcver = new ot.NPOTReceiverOld(80, dis, dos);
		s = new BigInteger(80, rnd);
		rcver.execProtocol(s);
		keys = rcver.getData();
	}
	
	private void runOTMT(int nOfThreads) throws Exception{
		
		rcver = new NPOTReceiverMultiThreading(80, dis, dos, nOfThreads);
		s = new BigInteger(80, rnd);
		rcver.execProtocol(s);
		keys = rcver.getData();
	}
	
	private void runOTEC() throws Exception{
		rcver = new ECNPOTReceiver(80, dis, dos);
		s = new BigInteger(80, rnd);
		rcver.execProtocol(s);
		keys = rcver.getData();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new BenchOTServer().run(100);

	}

}
