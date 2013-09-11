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


public class MeasurementSeries {

	private int nofMeasurements;
	private String name;
	private Measurement[] measurements;
	
	public MeasurementSeries(String name, int noOfMeasurements, CountingOutputStream cos, CountingInputStream cis){
		this.nofMeasurements = noOfMeasurements;
		this.name = name;
		measurements = new Measurement[noOfMeasurements];
		for(int i=0; i<noOfMeasurements; i++){
			measurements[i] = new Measurement(name + "_" + i, cos, cis);
		}
	}
	
	public void start(int measurement){
		measurements[measurement].start();
	}
	
	public void pause(int measurement){
		measurements[measurement].pause();
	}
	
	public void resume(int measurement){
		measurements[measurement].resume();
	}
	
	public void stop(int measurement){
		System.out.print("Iteration " + measurement + ": ");
		measurements[measurement].stop();	
	}
	
	public String SeriesAverage(){
		long time=0;
		double out=0, in=0;
		
		for(Measurement m : measurements){
			time += m.getElapsedTime();
			out += m.getTotalOutByteCount();
			in += m.getTotalInByteCount();
		}
		time = time/nofMeasurements;
		out = out/(nofMeasurements*1024);
		in = in/(nofMeasurements*1024);
		
		return String.format("Average %s: %dms (out: %.2fkb, in: %.2fkb)", name, time, out, in);
	}
}
