package util;

import java.util.HashMap;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.input.CountingInputStream;


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


public class StopWatch {

	//the collection of measurements
	private static HashMap<String, Measurement> measurements = new HashMap<String, Measurement>();
	private static HashMap<String, MeasurementSeries> measurementSeries = new HashMap<String, MeasurementSeries>();
	
	/* returns the Measurement denoted by 'measurement'
	 * if the measurement doesn't exist yet, it will be created.
	 */
	public static Measurement measurement(String measurement){
		Measurement m = measurements.get(measurement);
		if(m == null){
			m = new Measurement(measurement, cos, cis);
			measurements.put(measurement, m);
		}
		return m;
	}
	
	public static MeasurementSeries measurementSeries(String measurementSeries, int iterations){
		MeasurementSeries m = StopWatch.measurementSeries.get(measurementSeries);
		if(m == null){
			m = new MeasurementSeries(measurementSeries, iterations, cos, cis);
			StopWatch.measurementSeries.put(measurementSeries, m);
		}
		return m;		
	}
	
	private static long startTime = 0;
	private static long lastSnapshotTime = 0;
	private static long stopTime = 0;

	private static double startCOScount = 0;
	private static double stopCOScount = 0;
	private static double lastCOScount = 0;
	private static double startCIScount = 0;
	private static double stopCIScount = 0;
	private static double lastCIScount = 0;
	public static CountingOutputStream cos = null;
	public static CountingInputStream cis = null;

	public static void startTimeStamp() {
		startTime = lastSnapshotTime = System.currentTimeMillis();
		if (cos != null)
			lastCOScount = startCOScount = cos.getByteCount() / 1024.0;
		if (cis != null)
			lastCIScount = startCIScount = cis.getByteCount() / 1024.0;
	}

	public static void stopTimeStamp() {
		stopTime = System.currentTimeMillis();
		long totalRunningTime = stopTime - startTime;
		if (cos != null)
			stopCOScount = cos.getByteCount() / 1024.0;
		if (cis != null)
			stopCIScount = cis.getByteCount() / 1024.0;
		System.out.println("Total running time (ms): " + totalRunningTime + "("
				+ (stopCOScount - startCOScount) + ", "
				+ (stopCIScount - startCIScount) + ")");
	}

	// what are start() and stop() for?
	public static void start() {
		lastSnapshotTime = startTime = System.currentTimeMillis();

		if (cos != null)
			lastCOScount = startCOScount = cos.getByteCount() / 1024.0;
		if (cis != null)
			lastCIScount = startCIScount = cis.getByteCount() / 1024.0;

		System.out.println("Program starting time (ms): " + startTime + " ("
				+ startCOScount + ", " + startCIScount + ")");
	}

	public static void stop() {
		lastSnapshotTime = stopTime = System.currentTimeMillis();

		if (cos != null)
			lastCOScount = startCOScount = cos.getByteCount() / 1024.0;
		if (cis != null)
			lastCIScount = startCIScount = cis.getByteCount() / 1024.0;

		System.out.println("Program stopping time (ms): " + stopTime + " ("
				+ stopCOScount + ", " + stopCIScount + ")");
	}

	public static void pointTimeStamp(String point) {
		System.out.println("Time (ms) " + point + ": " + getElapsedTime()
				+ " (" + getOutputCounter() + ", " + getInputCounter() + ")");
	}

	public static void taskTimeStamp(String task) {
		System.out.println("Elapsed time (ms) on " + task + ": "
				+ getSegmentedElapsedTime() + " (" + getOutputUsage() + ", "
				+ getInputUsage() + ")");
	}

	public static long getElapsedTime() {
		long elapsed;
		lastSnapshotTime = System.currentTimeMillis();
		elapsed = lastSnapshotTime - startTime;

		return elapsed;
	}

	public static double getOutputCounter() {
		if (cos == null)
			return 0;

		lastCOScount = cos.getByteCount() / 1024.0;
		return lastCOScount - startCOScount;
	}

	public static double getInputCounter() {
		if (cis == null)
			return 0;

		lastCIScount = cis.getByteCount() / 1024.0;
		return lastCIScount - startCIScount;
	}

	public static long getSegmentedElapsedTime() {
		long elapsed;
		long snapshotTime = System.currentTimeMillis();
		elapsed = snapshotTime - lastSnapshotTime;
		lastSnapshotTime = snapshotTime;

		return elapsed;
	}

	public static double getOutputUsage() {
		if (cos == null)
			return 0;

		double used;
		double currentCount = cos.getByteCount() / 1024.0;
		used = currentCount - lastCOScount;
		lastCOScount = currentCount;

		return used;
	}

	public static double getInputUsage() {
		if (cis == null)
			return 0;

		double used;
		double currentCount = cis.getByteCount() / 1024.0;
		used = currentCount - lastCIScount;
		lastCIScount = currentCount;

		return used;
	}
	
}