package YaoGC;

/**
 * return 1 if x != 0 and 0 if x == 0
 * inputs:
 * l-bits: x
 * outputs:
 * 1 bit: x != 0
 */

public class NEQ_ZERO extends CompositeCircuit {

	private int l;
	
	public NEQ_ZERO(int l) {
		super(l, 1, l-1, "NEQ_ZERO_"+l);
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
		inputWires[0].connectTo(subCircuits[0].inputWires, 0);
		for(int i=1; i<l; i++){
			inputWires[i].connectTo(subCircuits[i-1].inputWires, 1);
		}
		for(int i=0; i<l-2; i++){
			subCircuits[i].outputWires[0].connectTo(subCircuits[i+1].inputWires, 0);
		}
	}

	@Override
	protected void defineOutputWires() {
		outputWires[0] = subCircuits[l-2].outputWires[0];

	}

}
