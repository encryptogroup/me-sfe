package YaoGC;

/**
 * given input x this circuit outputs 2^y - 1 with 2^(y-1)-1 < x <= 2^y-1
 * or in words: all bits of x are set to one but the zeros above are left untouched.
 * 
 * input: l-wires: x
 * output: l-wires: 2^y-1
 *
 */
public class NextBitMask extends CompositeCircuit {

	private int l;
	
	public NextBitMask(int l) {
		super(l, l, l-1, "NextBitMask_"+l);
		this.l = l;
	}

	protected void createSubCircuits() throws Exception {
		for(int i=0; i<l-1; i++){
			subCircuits[i] = OR_2_1.newInstance();
		}
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<l-1; i++){
			inputWires[i].connectTo(subCircuits[i].inputWires, 0);
		}
		inputWires[l-1].connectTo(subCircuits[l-2].inputWires, 1);
		for(int i=0; i<l-2; i++){
			subCircuits[i+1].outputWires[0].connectTo(subCircuits[i].inputWires, 1);
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<l-1; i++){
			outputWires[i] = subCircuits[i].outputWires[0];
		}
		outputWires[l-1] = inputWires[l-1];
	}

}
