// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

public class SBox extends CompositeCircuit {
    private static final byte Inv = 0;
    private static final byte Aff = 1;

    public SBox() {
	super(8, 8, 2, "SBox");
    }


    protected void createSubCircuits() throws Exception {
	subCircuits[Inv] = new Inverse_GF256();
	subCircuits[Aff] = new AffineTransform();

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 8; i++) {
	    inputWires[i].connectTo(subCircuits[Inv].inputWires, i);
	    
	    subCircuits[Inv].outputWires[i].connectTo(subCircuits[Aff].inputWires, i);
	}
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[Aff].outputWires, 0, outputWires, 0, 8);
    }

}