package YaoGC;

/**
 * An l-bit Subtrator circuit.
 * computes z = x-y with x,y,z are l-bit integer
 * inputs:
 *  - first l bits: x
 *  - next l bits: y
 */

public class SUB_2L_L extends CompositeCircuit {

	private final int l;

	public SUB_2L_L(int l) {
		super(2 * l, l, l+1, "SUB_" + (2 * l) + "_" + l);
		this.l = l;
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new HALF_SUB_2_2();
		for (int i = 1; i < l-1; i++)
			subCircuits[i] = new SUB_3_2();
		subCircuits[l-1] = new XOR_2_1();
		subCircuits[l] = new XOR_2_1();
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		inputWires[0].connectTo(subCircuits[0].inputWires, HALF_SUB_2_2.X);
		inputWires[l].connectTo(subCircuits[0].inputWires, HALF_SUB_2_2.Y);
		for(int i=1; i<l-1; i++){
			inputWires[i].connectTo(subCircuits[i].inputWires, SUB_3_2.X);
			inputWires[l+i].connectTo(subCircuits[i].inputWires, SUB_3_2.Y);
			subCircuits[i-1].outputWires[SUB_3_2.BOUT].connectTo(subCircuits[i].inputWires, SUB_3_2.BIN);
		}
		subCircuits[l-2].outputWires[SUB_3_2.BOUT].connectTo(subCircuits[l].inputWires, 0);
		inputWires[l-1].connectTo(subCircuits[l].inputWires, 1);
		subCircuits[l].outputWires[0].connectTo(subCircuits[l-1].inputWires, 0);
		inputWires[2*l-1].connectTo(subCircuits[l-1].inputWires, 1);
		
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<l; i++){
			outputWires[i] = subCircuits[i].outputWires[SUB_3_2.S];
		}

	}

}
