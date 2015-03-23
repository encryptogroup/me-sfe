package YaoGC;

/**
 * computes x>y
 * 
 * input: first l-wires: x
 * 		  next l wires: y
 * output: one wire: 1 if x>y
 * 					 0    else
 */

public class GT_2L_1_succ extends CompositeCircuit {

	private final int L;

	public GT_2L_1_succ(int l) {
		super(2 * l, 1, l, "GT_" + (2 * l) + "_1");
		L = l;
	}

	protected void createSubCircuits() throws Exception {
		for (int i = 0; i < L; i++) {
			subCircuits[i] = new GT_3_1();
		}

		super.createSubCircuits();
	}

	protected void connectWires() {
		inputWires[0].connectTo(subCircuits[0].inputWires, GT_3_1.X);
		inputWires[L].connectTo(subCircuits[0].inputWires, GT_3_1.Y);

		for (int i = 1; i < L; i++) {
			inputWires[i].connectTo(subCircuits[i].inputWires, GT_3_1.X);
			inputWires[i+L].connectTo(subCircuits[i].inputWires, GT_3_1.Y);
			subCircuits[i - 1].outputWires[0].connectTo(subCircuits[i].inputWires, GT_3_1.C);
		}
	}

	protected void defineOutputWires() {
		outputWires[0] = subCircuits[L - 1].outputWires[0];
	}

	protected void fixInternalWires() {
		subCircuits[0].inputWires[GT_3_1.C].fixWire(0);
	}


}
