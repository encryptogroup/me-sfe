// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import io.NetUtils;

import java.math.*;
import java.io.*;

import math.BitMath;

import java.util.concurrent.*;

import java.security.SecureRandom;

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


class ThreadInitialize implements Runnable {
        int numOfChoices, offset;
        BigInteger p,q,g,gr,C;
        SecureRandom rnd;
        BigInteger[] gk, C_over_gk, keys;
        
        ThreadInitialize(int numOfChoices, int offset, BigInteger p, BigInteger q, BigInteger g, BigInteger gr, BigInteger C, BigInteger[] gk, BigInteger[] C_over_gk, BigInteger[] keys) {
                this.numOfChoices = numOfChoices;
                this.offset = offset;
                this.p = p;
                this.q = q;
                this.g = g;
                this.gr = gr;
                this.C = C;
                this.gk = gk;
                this.C_over_gk = C_over_gk;
                this.keys = keys;
                rnd = new SecureRandom();
        }

        public void run() {
                for (int i = 0; i < numOfChoices; i++) {
			BigInteger k = (new BigInteger(q.bitLength(), rnd)).mod(q);
			gk[offset+i] = g.modPow(k, p);
			C_over_gk[offset+i] = C.multiply(gk[offset+i].modInverse(p)).mod(p);
			keys[offset+i] = gr.modPow(k, p);
		}
        }
}


class ThreadStep2 implements Runnable {
        int numOfChoices, offset, msgBitLength;
        BigInteger[] keys, msg, data;
        BigInteger choices;
        ThreadStep2(int numOfChoices, int offset, BigInteger[] keys, BigInteger[] msg, int msgBitLength, BigInteger[] data, BigInteger choices) {
                this.numOfChoices = numOfChoices;
                this.offset = offset;
                this.msgBitLength = msgBitLength;
                this.keys = keys;
                this.msg = msg;
                this.data = data;                
                this.choices = choices;
        }

        public void run() {
                ConcurrentCipher myCipher = new ConcurrentCipher();
                for (int i = 0; i < numOfChoices; i++) {
                        if(choices.testBit(offset+i)){
				data[offset+i] = myCipher.decrypt(keys[offset+i], msg[2*offset+2*i+1], msgBitLength);
			}else{
				data[offset+i] = myCipher.decrypt(keys[offset+i], msg[2*offset+2*i+0], msgBitLength);
			}
                }
        }
}

public class NPOTReceiverMultiThreading extends Receiver {
	//private static SecureRandom rnd = new SecureRandom();

	private static final boolean READALL_THEN_STARTALL = true; // first read all data then start all threads
	
	private int msgBitLength;
	private BigInteger p, q, g, C;
	private BigInteger gr;

	private BigInteger[] gk, C_over_gk;
	//private BigInteger[][] pk;

	private BigInteger[] keys;
	private int byteCount;
	private int msgByteCount;

	private int nrOfProcessors = Runtime.getRuntime().availableProcessors();
	
	public NPOTReceiverMultiThreading(int numOfChoices, DataInputStream in, DataOutputStream out)
			throws Exception {
		super(numOfChoices, in, out);
		initialize();
	}
	
	public NPOTReceiverMultiThreading(int numOfChoices, DataInputStream in, DataOutputStream out, int nOfThreads)
			throws Exception {
		super(numOfChoices, in, out);
		this.nrOfProcessors = nOfThreads;
		initialize();
	}

	public void execProtocol(BigInteger choices) throws Exception {
		super.execProtocol(choices);

		step1();
		step2();
	}

	private void initialize() throws Exception {

		C = NetUtils.readBigInteger(dis);
		p = NetUtils.readBigInteger(dis);
		q = NetUtils.readBigInteger(dis);
		g = NetUtils.readBigInteger(dis);
		gr = NetUtils.readBigInteger(dis); 
		msgBitLength = dis.readInt();
		byteCount = BitMath.byteCount(NPOTSenderMultiThreading.pLength);
		msgByteCount = BitMath.byteCount(msgBitLength);

		gk = new BigInteger[numOfChoices];
		C_over_gk = new BigInteger[numOfChoices];
		keys = new BigInteger[numOfChoices];
		
		long start = System.currentTimeMillis();

		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(nrOfProcessors, nrOfProcessors, 1000, TimeUnit.MILLISECONDS, queue);

		// run threads
		for (int i = 0; i < nrOfProcessors; i++) {
		        executor.execute(new ThreadInitialize(numOfChoices/nrOfProcessors, i*numOfChoices/nrOfProcessors, p, q, g, gr, C, gk, C_over_gk, keys));
                }
                executor.shutdown();
                try {
			executor.awaitTermination(100, TimeUnit.SECONDS);
		} catch (Exception e) {
		}
		
		//System.out.print("Time: "); System.out.println(System.currentTimeMillis() - start);
		/* Sequential version
		for (int i = 0; i < numOfChoices; i++) {
			BigInteger k = (new BigInteger(q.bitLength(), rnd)).mod(q);
			gk[i] = g.modPow(k, p);
			C_over_gk[i] = C.multiply(gk[i].modInverse(p)).mod(p);
			keys[i] = gr.modPow(k, p);
		}*/
	}

	private void step1() throws Exception {
		for (int i = 0; i < numOfChoices; i++) {
			if( choices.testBit(i)){
				NetUtils.writeBigInteger(C_over_gk[i], byteCount, dos);
			}else{
				NetUtils.writeBigInteger(gk[i], byteCount, dos);
			}
		}
		dos.flush();
	}

	private void step2() throws Exception {
		//BigInteger[][] msg = NetUtils.readBigIntegerArray2D(BitMath.byteCount(msgBitLength), dis);
		
		data = new BigInteger[numOfChoices];


		long start = System.currentTimeMillis();

		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(nrOfProcessors, nrOfProcessors, 1000, TimeUnit.MILLISECONDS, queue);
		BigInteger msg[] = new BigInteger[2*numOfChoices];

		if(READALL_THEN_STARTALL) {		
                        // Read Data
                        for (int i = 0; i < 2*numOfChoices; i++) {
                                msg[i] = NetUtils.readBigInteger(msgByteCount, dis);
                        }
                        // Run threads
                        for (int i = 0; i < nrOfProcessors; i++) {
                                executor.execute(new ThreadStep2(numOfChoices/nrOfProcessors, i*numOfChoices/nrOfProcessors, keys, msg, msgBitLength, data, choices));
                        }
                } else {
                        for (int i = 0; i < nrOfProcessors; i++) {
                                for (int j = 0; j < 2*numOfChoices/nrOfProcessors; j++) {
                                        msg[2*(numOfChoices/nrOfProcessors)*i+j] = NetUtils.readBigInteger(msgByteCount, dis);
                                }
                                executor.execute(new ThreadStep2(numOfChoices/nrOfProcessors, i*numOfChoices/nrOfProcessors, keys, msg, msgBitLength, data, choices));
                        }
                }
                
                // Collect threads
                executor.shutdown();
                try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (Exception e) {
		}

		/* Sequential version
                for (int i = 0; i < numOfChoices; i++) {
			BigInteger msg0, msg1;
			msg0 = NetUtils.readBigInteger(msgByteCount, dis);
			msg1 = NetUtils.readBigInteger(msgByteCount, dis);
			if(choices.testBit(i)){
				data[i] = Cipher.decrypt(keys[i], msg1, msgBitLength);
			}else{
				data[i] = Cipher.decrypt(keys[i], msg0, msgBitLength);
			}
		}
		*/
		
		
		//System.out.print("Time2: "); System.out.println(System.currentTimeMillis() - start);

	}
}
