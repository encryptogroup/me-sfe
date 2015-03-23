package comp;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.File;
import io.CircuitReader;

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

public class Optimizer {
	
	/* Identifies the working set of a circuit. 
	 */
	
	
	public void minimizeRegisters(CircuitReader reader, String filename){
		/* 	first we create an array 'last seen' holding the highest gate number where the current gate output is used as input,
			circuit outputs get a special value '-1'
		*/
		System.out.println("analysing circuit...");
		int[] lastSeen = new int[reader.getNumberOfGates()+reader.getCreatorInputRegisters().length+reader.getEvaluatorInputRegisters().length];
		int[] inputRegisters = reader.getCreatorInputRegisters();
		//inputs
		System.arraycopy(inputRegisters, 0, lastSeen, 0, inputRegisters.length);
		int curPos = inputRegisters.length;
		int numberOfGates = reader.getNumberOfGates();
		inputRegisters = reader.getEvaluatorInputRegisters();
		System.arraycopy(inputRegisters, 0, lastSeen, curPos, inputRegisters.length);
		curPos += inputRegisters.length;
		//gates
		int end = curPos + numberOfGates;
		while(curPos < end){
			int[] gate = reader.getNextGate();
			assert(curPos == gate[0]);
			lastSeen[curPos] = curPos;
			lastSeen[gate[1]] = gate[0];
			lastSeen[gate[2]] = gate[0];
			curPos++;
		}
		//outputs
		int[] outputRegisters = reader.getCreatorOutputRegisters();
		for(int i : outputRegisters){
			lastSeen[i] = -1;
		}
		outputRegisters = reader.getEvaluatorOutputRegisters();
		for(int i : outputRegisters){
			lastSeen[i] = -1;
		}
		
		/*	next step is to build a replacement table.
		 * 	values are: -1 if no replacement value has been found yet, otherwise it is the register index
		 */
		System.out.println("generating replacement table...");
		//we start with the inputs
		int[] replaceTable = new int[lastSeen.length];
		Arrays.fill(replaceTable, -1);
		inputRegisters = reader.getCreatorInputRegisters();
		System.arraycopy(inputRegisters, 0, replaceTable, 0, inputRegisters.length);
		int nInputs = inputRegisters.length;
		inputRegisters = reader.getEvaluatorInputRegisters();
		System.arraycopy(inputRegisters, 0, replaceTable, nInputs, inputRegisters.length);
		nInputs += inputRegisters.length;
		
		Pool pool = new Pool(nInputs);
		for(int i=0; i<end; i++){
			
			// get current replacement value
			int curVal = replaceTable[i];
			if(curVal == -1){
				curVal = pool.next(i);
				replaceTable[i] = curVal;
			}
			
			// we have to check if this is an output gate. Because then there can't be any replacing
			if( lastSeen[i] == -1){
				continue;
			}
			
			int lsi = lastSeen[i];
			if(replaceTable[lsi] == -1){
				replaceTable[lsi] = curVal;
			}else{
				//this is already replaced. Now we have to put this register index in a pool to use later...
				pool.put(curVal, lsi);
			}
		}
		lastSeen = null;
		/* Last step: putting it all together.
		 */
		System.out.println("replacing registers...");
		reader.reset();
		try{
			PrintWriter pw = new PrintWriter(new File(filename));
			printHeaderLine("inputsCreator", reader.getCreatorInputRegisters(), pw);
			printHeaderLine("inputsEvaluator", reader.getEvaluatorInputRegisters(), pw);
			int[] registers = reader.getCreatorOutputRegisters();
			for(int i=0; i<registers.length; i++){
				registers[i] = replaceTable[registers[i]];
			}
			printHeaderLine("outputsCreator", registers, pw);
			registers = reader.getEvaluatorOutputRegisters();
			for(int i=0; i<registers.length; i++){
				registers[i] = replaceTable[registers[i]];
			}
			printHeaderLine("outputsEvaluator", registers, pw);
			printHeaderLine("numberOfRegisters", pool.nextAvailableRegister, pw);
			printHeaderLine("numberOfGates", numberOfGates, pw);
			int[] gate;
			for(int i=0; i<numberOfGates; i++){
				gate = reader.getNextGate();
				pw.printf("%d;%d,%d;%d\n", replaceTable[gate[0]], replaceTable[gate[1]], replaceTable[gate[2]], gate[3]);
			}
			pw.close();
		}catch(Exception e){
			
		}
		System.out.println("done");
	}
	
	private void printHeaderLine(String element, int value, PrintWriter pw){
		pw.print(element);
		pw.print(": ");
		pw.println(value);
	}
	
	private void printHeaderLine(String element, int[] values, PrintWriter pw){
		pw.print(element);
		pw.print(": ");
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for(int el : values){
			if(first){
				sb.append(el);
				first = false;
			}else{
				sb.append(',');
				sb.append(el);
			}
		}
		pw.println(sb.toString());
	}
	
	public static void main(String[] args) throws Exception{
	
		Optimizer opt = new Optimizer();
		opt.minimizeRegisters(CircuitReader.getInstance(new File("FMUL_128_128.mec"), true), "FMUL_min.mec");
	}

}

class Pool{
	LinkedList<int[]> list = new LinkedList<int[]>();
	int nextAvailableRegister = -1;
	Pool(int nextAvailableRegister){
		this.nextAvailableRegister = nextAvailableRegister;
	}
	
	int next(int currentPos){
		for(int i=0; i<list.size(); i++){
			int[] el = list.get(i);
			if(el[1] <= currentPos){
				list.remove(i);
				return el[0];
			}
		}
		// we end up here if we can't find anything suitable in the list
		return nextAvailableRegister++;
		
	}
	
	void put(int registerID, int holdUntil){
		list.addFirst(new int[]{registerID, holdUntil});
	}
}