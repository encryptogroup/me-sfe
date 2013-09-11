package prog;

import java.io.File;
import java.math.BigInteger;


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


public class PresentClient3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		SingleCircuitProgClient client = new SingleCircuitProgClient(new File("circuits/present/Present_full.bmec"));
		int reps = 1000;
		BigInteger[] inputs = new BigInteger[reps];
		for(int i=0; i<reps; i++){
			inputs[i] = BigInteger.valueOf(i);
		}
		client.run(reps, inputs);

	}

}
