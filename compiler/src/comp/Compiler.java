package comp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import YaoGC.Circuit;
import YaoGC.FastMUL_L_M;
import YaoGC.Wire;

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

public class Compiler {
	
	/* This compiler converts a Boolean circuit, defined in the FastGC framework format (see http://www.mightbeevil.org/framework/),
	 * to the mec format used in the me-sfe framework.
	 * It creates an ascii text file in mec format. But beware, each gate gets assigned a unique ID, and, as pointed out in the paper, 
	 * execution can be significantly more efficient, if we only hold the 'working set' in memory.
	 * To identify these 'working sets' an additional step is necessary, as implemented in the Optimizer class.
	 * Furthermore, for efficiency reasons it is advisable to convert the 'mec'-file into the binary 'bmec' format. See the 
	 * MEC_To_BMEC_Converter.py Python script.
	 * 
	 * See also example in main() on how to use this compiler.
	 */
	
	
	private static void printHeaderLinePair(String nameTrue, String nameFalse, Wire[] wires, boolean[] wireOwners, PrintWriter pw){
		StringBuilder sbt = new StringBuilder(), sbf = new StringBuilder();
		for(int i=0; i<wireOwners.length; i++){
			if(wireOwners[i]){
				if(sbt.length() != 0)
					sbt.append(',');
				sbt.append(wires[i].getRegisterNum());
			}else{
				if(sbf.length() != 0)
					sbf.append(',');
				sbf.append(wires[i].getRegisterNum());
			}
		}
		
		pw.print(nameTrue);
		pw.println(sbt.toString());
		pw.print(nameFalse);
		pw.println(sbf.toString());
	}
	
	public static void compileCircuit(Circuit c, File destFile, InputOutputDescription inOutDesc) throws Exception{
		//maybe test if circuit is built
		if(c.inputWires.length != inOutDesc.getNumberOfInputWires()){
			throw new Exception("Number of input wires of the circuit " + c.getName() + " doesn't match the given number of inputs: " + inOutDesc.getNumberOfInputWires());
		}
		if(c.outputWires.length != inOutDesc.getNumberOfOutputWires()){
			throw new Exception("Number of output wires of the circuit " + c.getName() + " doesn't match the given number of outputs: " + inOutDesc.getNumberOfOutputWires());
		}
		File tmpFile = File.createTempFile("mecComp", null);
		PrintWriter pw = new PrintWriter(tmpFile);
		Wire[] inputWires = c.inputWires;
		printHeaderLinePair("inputsCreator: ", "inputsEvaluator: ", inputWires, inOutDesc.getInputWireOwners(), pw);
		c.printCircuit(pw);
		pw.close();
		BufferedReader br = new BufferedReader(new FileReader(tmpFile));
		pw = new PrintWriter(destFile);
		pw.println(br.readLine());
		pw.println(br.readLine());
		Wire[] outputWires = c.outputWires;
		printHeaderLinePair("outputsCreator: ", "outputsEvaluator: ", outputWires, inOutDesc.getOutputWireOwners(), pw);
		pw.println("numberOfRegisters: " + Wire.getRegisterCount());
		pw.println("numberOfGates: " + c.getNumberOfGates());
		String line;
		while((line=br.readLine()) != null ){
			pw.println(line);
		}
		pw.close();
		br.close();
		tmpFile.delete();	
		System.out.println("Compiliation done.");
	}

	
	public static void main(String[] args) throws Exception{
		//Example: fast multiplication of two 128 bit integers
		//define circuit
		Circuit circuit = new FastMUL_L_M(128, 128);
		//build circuit
		circuit.build();
		//that's where we want to save it to.
		File destFile = new File("FastMUL_128_128.mec");
		//now, since we can't infer which inputs belong to which party from the circuit, we need to provide the compiler with this information.
		//This is done with an InputOutputDescription object. The SimpleInputOutputDescription(m,n,o,p) Class assigns the first m input wires to
		//the creator, the next m input wires to the evaluator, the first o output wires to the creator, and the next p output wires to the
		//evaluator.
		//In out example: both parties provide a 128 bit input, the creator gets a 256 bit output and the evaluator nothing.
		SimpleInputOutputDescription inOutDescription = new SimpleInputOutputDescription(128, 128, 256, 0);
		//now compile
		compileCircuit(circuit, destFile, inOutDescription);
		System.out.println("Compilation finished.");
		System.out.println("linear gates: " + circuit.getNumberOfLinearGates() + ", nonlinear gates: " + circuit.getNumberOfNonlinearGates());
		System.out.println("Don't forget to run the optimizer.");
			
	}

}
