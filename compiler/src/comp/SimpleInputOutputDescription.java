package comp;

import java.util.Arrays;


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
* For details, please see https://github.com/encryptogroup/me-sfe
*/

public class SimpleInputOutputDescription implements InputOutputDescription{

	private boolean[] inputWireOwners, outputWireOwners;
	
	public SimpleInputOutputDescription(int numberOfCreatorInputWires, int numberOfEvaluatorInputWires, int numberOfCreatorOutputWires, int numberOfEvaluatorOutputWires){
		inputWireOwners = new boolean[numberOfCreatorInputWires+numberOfEvaluatorInputWires];
		Arrays.fill(inputWireOwners, 0, numberOfCreatorInputWires, true);
		outputWireOwners = new boolean[numberOfCreatorOutputWires+numberOfEvaluatorOutputWires];
		Arrays.fill(outputWireOwners, 0, numberOfCreatorOutputWires, true);
	}
	
	public boolean[] getInputWireOwners(){
		return inputWireOwners;
	}
	
	public boolean[] getOutputWireOwners(){
		return outputWireOwners;
	}
	
	
	public int getNumberOfInputWires(){
		return inputWireOwners.length;
	}
	
	public int getNumberOfOutputWires(){
		return outputWireOwners.length;
	}
	
	
	public static void main(String[] args){
		SimpleInputOutputDescription iod = new SimpleInputOutputDescription(13, 20, 0, 5);
		System.out.println(Arrays.toString(iod.getInputWireOwners()));
		System.out.println(Arrays.toString(iod.getOutputWireOwners()));
	}
}
