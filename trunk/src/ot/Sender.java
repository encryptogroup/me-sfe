// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.math.*;
import java.io.*;


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


public abstract class Sender {
    protected int numOfPairs;
    protected int msgBitLength;
    protected BigInteger[][] msgPairs;

    protected DataInputStream dis;
    protected DataOutputStream dos;

    public Sender(int numOfPairs, int msgBitLength, 
		  DataInputStream in, DataOutputStream out) {
	this.numOfPairs = numOfPairs;
	this.msgBitLength = msgBitLength;
	dis = in;
	dos = out;
    }

    public void execProtocol(BigInteger[][] msgPairs) throws Exception {
	if (msgPairs.length != numOfPairs)
	    throw new Exception("Message pair length error: " + 
				msgPairs.length + " != " + numOfPairs);

	this.msgPairs = msgPairs;
    }
}