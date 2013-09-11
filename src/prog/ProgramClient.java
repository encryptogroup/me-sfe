package prog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ot.OTExtReceiverLowerMem;


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


public class ProgramClient {
	
	protected DataInputStream dis;
	protected DataOutputStream dos;
	private Socket socket;
	protected OTExtReceiverLowerMem otReceiver;
	protected byte[][] registers;
	
	public ProgramClient() throws IOException{
		this("localhost", 7777);
	}
	
	public ProgramClient(String serverName, int serverPort) throws IOException{
		initializeNetIO(serverName, serverPort);
	}
	
	private void initializeNetIO(String serverIPname, int serverPort) throws IOException{
		socket = new java.net.Socket(serverIPname, serverPort);
		dis = new DataInputStream(new BufferedInputStream(socket.getInputStream(), ProgramServer.BUF_SIZE));
		dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(),ProgramServer.BUF_SIZE));
	}
	
	private void initializeOT() throws Exception{
		otReceiver = new OTExtReceiverLowerMem(dis, dos);
	}
	
	protected void cleanup(){
		try {dos.close();} catch (IOException e) {}
		try {dis.close();} catch (IOException e) {}
		try {socket.close();} catch (IOException e) {}
	}
	
	protected void initializeRegisters(int numOfRegisters){
		registers = new byte[numOfRegisters][];
	}
	
	public void precompute() throws Exception{
		initializeOT();
	}
	

}
