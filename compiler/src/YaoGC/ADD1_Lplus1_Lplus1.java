// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

/**
 * Adds one bit y to an l-bit number x. 
 * Output (l+1 bits) z = x+y 
 * InputWires: 
 *   -first wire: y 
 *   -the second to l+1th wires: y
 */

public class ADD1_Lplus1_Lplus1 extends CompositeCircuit {
    private final int L;

    public ADD1_Lplus1_Lplus1(int l) {
	super(l+1, l+1, l, "ADD1_" + (l+1) + "_" + (l+1));
	L = l;
    }

    protected void createSubCircuits() throws Exception {
    	for(int i=0; i<L; i++){
    		subCircuits[i] = new HALF_ADD_2_2();
    	}
	super.createSubCircuits();
    }

    protected void connectWires() {
    	inputWires[0].connectTo(subCircuits[0].inputWires, HALF_ADD_2_2.Y); //the y bit
    	for(int i=0; i<L; i++){ //that's x
    		inputWires[i+1].connectTo(subCircuits[i].inputWires, HALF_ADD_2_2.X);
    	}
    	//chain the carry bits

		for (int i = 0; i < L-1; i++)
		    subCircuits[i].outputWires[HALF_ADD_2_2.COUT].connectTo(subCircuits[i+1].inputWires, HALF_ADD_2_2.Y);
	    }

    protected void defineOutputWires() {
    	for(int i=0; i<L; i++){
    		outputWires[i] = subCircuits[i].outputWires[HALF_ADD_2_2.S];
    	}
    	outputWires[L] = subCircuits[L-1].outputWires[HALF_ADD_2_2.COUT];
    }

    protected void fixInternalWires() {

    }
}