package comp;

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

public interface InputOutputDescription {
	
	public boolean[] getInputWireOwners();
	
	public boolean[] getOutputWireOwners();
	
	public int getNumberOfInputWires();
	
	public int getNumberOfOutputWires();
	
}
