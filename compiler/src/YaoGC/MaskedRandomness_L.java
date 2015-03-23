package YaoGC;

/**
 * 
 * Inputs:
 *  - first l bits: randomness from one party
 *  - next l bits: randomness from other party
 *  - last l bits: selection mask
 */

public class MaskedRandomness_L extends CompositeCircuit {

	private int l;
	
	public MaskedRandomness_L(int l) {
		super(3*l, l, l+1, "Random");
		this.l = l;
	}
	
	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new XOR_2L_L(l); // r = r_a xor r_b
		for(int i=0; i<l; i++){
			subCircuits[1+i] = AND_2_1.newInstance();
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<l; i++){
			inputWires[i].connectTo(subCircuits[0].inputWires, i);
			inputWires[l+i].connectTo(subCircuits[0].inputWires, l+i);
			inputWires[2*l+i].connectTo(subCircuits[1+i].inputWires, 0);
			subCircuits[0].outputWires[i].connectTo(subCircuits[1+i].inputWires, 1);
		}

	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<l; i++){
			outputWires[i] = subCircuits[1+i].outputWires[0];
		}

	}

}
