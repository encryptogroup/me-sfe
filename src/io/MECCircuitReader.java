package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


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


public class MECCircuitReader extends CircuitReader {
	
	
	private File file;
	private Scanner scanner;
	private boolean header_read = false;
	//private static final Pattern GATE_PATTERN = Pattern.compile(",|;");
	
	private int numberOfRegisters;
	private int numberOfGates;
	private int[] creatorInputRegisters = new int[0];
	private int[] evaluatorInputRegisters = new int[0];
	private int[] creatorOutputRegisters = new int[0];
	private int[] evaluatorOutputRegisters = new int[0];
	
	private String token;
	
	protected MECCircuitReader(File file) throws FileNotFoundException{
		this.file = file;
		scanner = new Scanner(new BufferedReader(new FileReader(file)));
	}
	
	private void readHeader(){
		try{
			creatorInputRegisters = readIntArray("inputscreator:");
			evaluatorInputRegisters = readIntArray("inputsevaluator:");
			creatorOutputRegisters = readIntArray("outputscreator:");
			evaluatorOutputRegisters = readIntArray("outputsevaluator:");
			numberOfRegisters = readInt("numberofregisters:");
			numberOfGates = readInt("numberofgates:");
			header_read = true;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private int[] readIntArray(String label) throws Exception{
		String token = nextToken();
		int[] res;
		if(!token.trim().toLowerCase().equals(label)){
			throw new Exception("Unknown Format. Found: '" + token + "', expected: '" + label + "'");
		}
		token = scanner.nextLine().trim();
		if(token.length() > 0){
			String[] inputs = token.split(",");
			res = new int[inputs.length];
			for(int i=0; i<inputs.length; i++){
				res[i] = Integer.parseInt(inputs[i].trim());
			}
		}else{
			res = new int[0];
		}
		return res;
	}
	
	private int readInt(String label) throws Exception{
		String token = nextToken();
		if(!token.trim().toLowerCase().equals(label)){
			throw new Exception("Unknown Format. Found: '" + token + "', expected: '" + label + "'");
		}
		int ret = scanner.nextInt();
		scanner.nextLine();
		return ret;
	}
	
	private String nextToken(){
		token = scanner.next();
		while(token.startsWith("//")){
			scanner.nextLine();
			token = scanner.next();
		}
		return token;
	}
	
	@Override
	public int getNumberOfRegisters() {
		if(!header_read){
			readHeader();
		}
		return numberOfRegisters;
	}

	@Override
	public int[] getCreatorInputRegisters() {
		if(!header_read){
			readHeader();
		}
		return creatorInputRegisters;
	}

	@Override
	public int[] getEvaluatorInputRegisters() {
		if(!header_read){
			readHeader();
		}
		return evaluatorInputRegisters;
	}

	@Override
	public int[] getCreatorOutputRegisters() {
		if(!header_read){
			readHeader();
		}
		return creatorOutputRegisters;
	}

	@Override
	public int[] getEvaluatorOutputRegisters() {
		if(!header_read){
			readHeader();
		}
		return evaluatorOutputRegisters;
	}

	@Override
	public void reset() {
		try{
			scanner.close();
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			readHeader();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public int[] getNextGate() {
		if(!header_read){
			readHeader();
		}
		String[] tmp = nextToken().split(";|,");
		int[] res = new int[tmp.length];
		for(int i=0; i<tmp.length; i++){
			res[i] = Integer.parseInt(tmp[i]);
		}
		return res;
	}

	@Override
	public int getNumberOfGates() {
		if(!header_read){
			readHeader();
		}
		return numberOfGates;
	}

}
