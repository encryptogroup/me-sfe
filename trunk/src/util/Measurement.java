package util;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;


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


/**
 * Use with StopWatch.
 * Create a measurement:
 *   StopWatch.measurement(measurementName);
 * 
 * Static access to the measurement:
 * Start the measurement:
 *   StopWatch.measurement(measurementName).start();
 * Stop the measurement:
 *   StopWatch.measurement(measurementName).stop();
 * ...
 */

public class Measurement {
	private String name;
	//for time
	private long startTime = 0;
	private long totalTime = 0;
	//for communication costs
	private double startCOScount = 0;
	private double totalCOScount = 0;
	private double startCIScount = 0;
	private double totalCIScount = 0;
	private boolean paused = false;
	
	private CountingOutputStream cos = null;
	private CountingInputStream cis = null;
	
	protected Measurement(String name, CountingOutputStream cos, CountingInputStream cis) {
		this.name = name;
		this.cos = cos;
		this.cis = cis;
	}
	
	public void start(){
		startTime = System.currentTimeMillis();
		if(cos != null)
			startCOScount = cos.getByteCount();
		if(cis != null)
			startCIScount = cis.getByteCount();
	}
	
	public void pause(){
		paused = true;
		totalTime += System.currentTimeMillis()-startTime;
		if(cos != null)
			totalCOScount += cos.getByteCount()-startCOScount;
		if(cis != null)
			totalCIScount += cis.getByteCount()-startCIScount;
	}
	
	public void resume(){
		paused = false;
		startTime = System.currentTimeMillis();
		if(cos != null)
			startCOScount = cos.getByteCount();
		if(cis != null)
			startCIScount = cis.getByteCount();
	}
	
	public void stop(){
		if(!paused)
			totalTime += System.currentTimeMillis()-startTime;
		if(cos != null)
			totalCOScount += cos.getByteCount()-startCOScount;
		if(cis != null)
			totalCIScount += cis.getByteCount()-startCIScount;
		
		System.out.printf("%s %dms (out: %.2fkb, in: %.2fkb)\n", name, totalTime, totalCOScount/1024, totalCIScount/1024);
	}
	
	public void splitTime(String splitTimeName){
		long split = System.currentTimeMillis() - startTime;
		double splitCOScount = 0;
		double splitCIScount = 0;
		if(cos != null)
			splitCOScount = cos.getByteCount() - startCOScount;
		if(cis != null)
			splitCIScount = cis.getByteCount() - startCIScount;
		System.out.printf("%s.%s: %dms (out: %.2fkb, in: %.2fkb)\n", name, splitTimeName, split, splitCOScount/1024, splitCIScount/1024);
	}
	
	public long getElapsedTime(){
		return totalTime;
	}
	
	public double getTotalOutByteCount(){
		return totalCOScount;
	}
	
	public double getTotalInByteCount(){
		return totalCIScount;
	}
	

}
