package YaoGC;

/**
 * computes x = x_l + 2^n * x_h
 * inputs:
 *  - first 2^l -1 wires: x_l
 *  - next  2^l -1 wires: x_h
 *  - last  l wires: n
 * outputs:
 *  - 2^l -1 wires: x
 */

public class Concatenate_L extends CompositeCircuit {

	private int l, exp;
	private static final int SHIFT = 0;
	private static final int ADD = 1;
	
	public Concatenate_L(int l) {
		super(l+2*((1<<l)-1), (1<<l)-1, 2, "Concatenate");
		this.l = l;
		exp = (1<<l)-1;
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[SHIFT] = new BarrelShifter(exp);
		subCircuits[ADD] = new ADD_2L_L(exp);
		super.createSubCircuits();
	}
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<exp; i++){
			inputWires[i].connectTo(subCircuits[ADD].inputWires, i);
		}
		for(int i=0; i<exp+l; i++){
			inputWires[exp+i].connectTo(subCircuits[SHIFT].inputWires, i);
		}
		for(int i=0; i<exp; i++){
			subCircuits[SHIFT].outputWires[i].connectTo(subCircuits[ADD].inputWires, exp+i);
		}

	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[ADD].outputWires, 0, outputWires, 0, exp);

	}

}
