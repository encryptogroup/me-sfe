package YaoGC;

import math.BitMath;


/**
 * Computes the Hamming distance between the to l-bit inputs x and y
 * InputWires:
 * -first l wires: x
 * -next l wires: y
 * OutputWires:
 * bitLength(l) wires: bitCount(x XOR y)
 */

public class HAMMING_DISTANCE_2L extends CompositeCircuit {

	private int L;
	
	public HAMMING_DISTANCE_2L(int inDegree) {
		super(2*inDegree, BitMath.bitLength(inDegree), 2, "Hamming_Dist_2*"+inDegree);
		this.L = inDegree;
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new XOR_2L_L(L);
		subCircuits[1] = new HAMMING_WEIGHT(L);
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<L; i++){
			inputWires[i].connectTo(subCircuits[0].inputWires, i);					//input x
			inputWires[i+L].connectTo(subCircuits[0].inputWires, i+L);//input y
		}
		for(int i=0; i<L; i++){
			subCircuits[0].outputWires[i].connectTo(subCircuits[1].inputWires, i);  //output of XOR into HAMMING_WEIGHT
		}
	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[1].outputWires, 0, outputWires, 0, outDegree);
	}

}
