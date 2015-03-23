// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

public class AddRoundKey extends CompositeCircuit {

    public AddRoundKey() {
	super(256, 128, 128, "AddRoundKey");
    }


    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < 128; i++) 
	    subCircuits[i] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 128; i++) {
	    inputWires[X(i)].connectTo(subCircuits[i].inputWires, 0);
	    inputWires[Y(i)].connectTo(subCircuits[i].inputWires, 1);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < 128; i++)
	    outputWires[i] = subCircuits[i].outputWires[0];
    }

    private static int X(int i) {
	return i + 128;
    }

    private static int Y(int i) {
	return i;
    }
}