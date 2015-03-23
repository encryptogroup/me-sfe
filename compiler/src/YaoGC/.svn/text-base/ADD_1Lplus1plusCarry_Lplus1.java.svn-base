package YaoGC;

/**
 * Adds one l-bit number x, one bit y and a carry bit c. 
 * Output (l+1 bits) z = x+y+c 
 * InputWires: 
 *   -first l wires: x 
 *   -the l+1th wire: y 
 *   -the l+2th wire: c
 */

public class ADD_1Lplus1plusCarry_Lplus1 extends CompositeCircuit {
	private final int L;

	public ADD_1Lplus1plusCarry_Lplus1(int l) {
		super(l + 2, l + 1, l, "ADD1_" + (l + 2) + "_" + (l + 1));
		L = l;
	}

	protected void createSubCircuits() throws Exception {
		
		subCircuits[0] = new ADD_3_2();
		for(int i=1; i<L; i++){
			subCircuits[i] = new HALF_ADD_2_2();
		}
		super.createSubCircuits();
	}

	protected void connectWires() {
		//x
		for (int i = 0; i < L; i++){
			inputWires[i].connectTo(subCircuits[i].inputWires, 0);
		}
		//y
		inputWires[L].connectTo(subCircuits[0].inputWires, ADD_3_2.Y);
		//c
		inputWires[L+1].connectTo(subCircuits[0].inputWires, ADD_3_2.CIN);
		//chain the carry bits
		for(int i=0; i<L-1; i++){
			subCircuits[i].outputWires[1].connectTo(subCircuits[i+1].inputWires, 1);
			
		}
		
	}

	protected void defineOutputWires() {
		for(int i=0; i<L; i++){
			outputWires[i] = subCircuits[i].outputWires[0];
		}
		outputWires[L] = subCircuits[L-1].outputWires[1]; //and the last carry bit
	}

}