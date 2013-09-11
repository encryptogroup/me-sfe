package io;

import java.io.File;


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


public abstract class CircuitReader {
	/* abstract base class for circuit reader */
	
	public static CircuitReader getInstance(File file, boolean cachingEnabled) throws Exception{
		String filename = file.getName();
		String fileExtension = filename.substring(filename.lastIndexOf('.'));
		if(".mec".equals(fileExtension)){
			return new MECCircuitReader(file);
		}else{
			if(".bmec".equals(fileExtension)){
				return new BMECCircuitReader(file, cachingEnabled);
			}else{
				throw new Exception("Format '"+fileExtension+"' is an unsupported circuit description format!");
			}
		}
	}
	
	public abstract int getNumberOfRegisters();
	public abstract int getNumberOfGates();
	public abstract int[] getCreatorInputRegisters();
	public abstract int[] getEvaluatorInputRegisters();
	public abstract int[] getCreatorOutputRegisters();
	public abstract int[] getEvaluatorOutputRegisters();
	//start over and read again.
	public abstract void reset();
	public abstract int[] getNextGate();
}
