package YaoGC;

/**
 * Adds two l-bit numbers x,y and a carry bit c.
 * Output (l+1 bits) z = x+y+c
 * InputWires:
 * -first l wires: x
 * -next l wires: y
 * -the 2l+1th wire: c 
 */

public class ADD_2LplusCarry_Lplus1 extends CompositeCircuit {
	private final int L;

	public ADD_2LplusCarry_Lplus1(int l) {
		super(2 * l + 1, l + 1, l, "ADD_" + (2 * l) + "+carry_" + (l + 1));

		L = l;
	}

	protected void createSubCircuits() throws Exception {
		for (int i = 0; i < L; i++)
			subCircuits[i] = new ADD_3_2();

		super.createSubCircuits();
	}

	protected void connectWires() {
		inputWires[X(0)].connectTo(subCircuits[0].inputWires, ADD_3_2.X);
		inputWires[Y(0)].connectTo(subCircuits[0].inputWires, ADD_3_2.Y);
		inputWires[2*L].connectTo(subCircuits[0].inputWires, ADD_3_2.CIN); //the carry bit input

		for (int i = 1; i < L; i++) {
			inputWires[X(i)].connectTo(subCircuits[i].inputWires, ADD_3_2.X);
			inputWires[Y(i)].connectTo(subCircuits[i].inputWires, ADD_3_2.Y);
			subCircuits[i - 1].outputWires[ADD_3_2.COUT].connectTo(
					subCircuits[i].inputWires, ADD_3_2.CIN);
		}
	}

	protected void defineOutputWires() {
		for (int i = 0; i < L; i++)
			outputWires[i] = subCircuits[i].outputWires[ADD_3_2.S];
		outputWires[L] = subCircuits[L - 1].outputWires[ADD_3_2.COUT];
	}

	protected void fixInternalWires() {
	
	}

	private int X(int i) {
		return i;
	}

	private int Y(int i) {
		return L+i;
	}

}