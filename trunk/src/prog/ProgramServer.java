package prog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import math.LabelMath;

import ot.OTExtSenderLowerMem;


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


public class ProgramServer {
	public static final int BUF_SIZE = 9000; //The buffer size for the network streams
	
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	protected OTExtSenderLowerMem otSender;
	protected int repetitions = 1;
	protected byte[][] registers;
	protected boolean[] inverted;
	
	public ProgramServer(int port) throws IOException{
		initializeNetIO(port);
	}
	
	public ProgramServer() throws IOException{
		this(7777);
	}
	
	private void initializeOT() throws Exception{
		otSender = new OTExtSenderLowerMem(LabelMath.LABEL_BYTESIZE*8, dis, dos);
	}
	
	protected void precompute() throws Exception{
		initializeOT();
	}
	
	protected void cleanup(){
		try {dos.close();} catch (IOException e) {}
		try {dis.close();} catch (IOException e) {}
		try {clientSocket.close();} catch (IOException e) {}
		try {serverSocket.close();} catch (IOException e) {}
	}
	
	private void initializeNetIO(int port) throws IOException{
		serverSocket = new ServerSocket(port); // create socket and bind to port
		System.out.println("waiting for client to connect");
		clientSocket = serverSocket.accept(); // wait for client to connect
		System.out.println("client has connected");
		dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream(),BUF_SIZE));
		dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream(), BUF_SIZE));
		dos.flush();
	}
	
}
