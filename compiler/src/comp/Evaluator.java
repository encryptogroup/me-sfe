package comp;


import java.math.BigInteger;
import java.util.Random;

import crypt.Present;

import YaoGC.BarrelShifter;
import YaoGC.BitMask;
import YaoGC.Circuit;
import YaoGC.EQUALS_2L_1;
import YaoGC.FastMUL_L_M;
import YaoGC.GT_2L_1;
import YaoGC.GT_2L_1_succ;
import YaoGC.HALF_SUB_2_2;
import YaoGC.MAXMIN_L;
import YaoGC.MUL_L_M;
import YaoGC.NOT_1_1;
import YaoGC.RandomFromRangeN_0_L;
import YaoGC.RandomFromRange_0_L;
import YaoGC.RandomFromRange_1_L;
import YaoGC.RandomScale;
import YaoGC.SUB_2L_L;
import YaoGC.State;
import YaoGC.PRESENTcomponents.PresentFull;
import YaoGC.PSIcomponents.PSI;
import YaoGC.PSIcomponents.WOI;

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

public class Evaluator {

	public static void evaluateCircuit(Circuit c, State inputState){
		State outputState = c.startExecuting(inputState);
		BigInteger output = BigInteger.ZERO;
		for(int i=0; i<outputState.getWidth(); i++){
			if(outputState.wires[i].value == 1){
				output = output.setBit(i);
			}
		}
		System.out.println("Output: " + output.toString(2));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Circuit c = new FastMUL_L_M(128, 128);
		c.build();
		
		BigInteger x = BigInteger.valueOf(35);
		BigInteger y = BigInteger.valueOf(125);
		State input = new State(x, 128);
		input = State.concatenate(input, new State(y,128));
		evaluateCircuit(c, input);
		
	}

}
