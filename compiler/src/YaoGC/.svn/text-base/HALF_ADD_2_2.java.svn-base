package YaoGC;

/**
 * A Half_Adder.
 * Inputs: two bits x and y
 * Outputs: one bit s = x+y mod2, one bit c = (x+y)/2 (the carry bit)
 */
public class HALF_ADD_2_2 extends CompositeCircuit {
	private final static int XOR = 0;
	private final static int AND = 1;

	public final static int X = 0;
	public final static int Y = 1;
	public final static int S = 0;
	public final static int COUT = 1;

	public HALF_ADD_2_2() {
		super(2, 2, 2, "HALF_ADD_2_2");
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[XOR] = new XOR_2_1();
		subCircuits[AND] = AND_2_1.newInstance();
		super.createSubCircuits();
	}

	protected void connectWires() {
		inputWires[X].connectTo(subCircuits[XOR].inputWires, 0);
		inputWires[X].connectTo(subCircuits[AND].inputWires, 0);
		inputWires[Y].connectTo(subCircuits[XOR].inputWires, 1);
		inputWires[Y].connectTo(subCircuits[AND].inputWires, 1);
	}

	protected void defineOutputWires() {
		outputWires[S] = subCircuits[XOR].outputWires[0];
		outputWires[COUT] = subCircuits[AND].outputWires[0];
	}
}