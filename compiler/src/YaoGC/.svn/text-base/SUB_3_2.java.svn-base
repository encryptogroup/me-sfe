package YaoGC;

/**
 * A full-subtractor.
 * 3 bits input: x, y and a borrow bit b_in.
 * 2 bits output: s and a borrow bit b_out.
 * outputs s = x-y-b_in and a b_out=1 if y+b_in>x
 */
public class SUB_3_2 extends CompositeCircuit {

	private final static int XOR0 = 0;
	private final static int XOR1 = 1;
	private final static int XOR2 = 2;
	private final static int XOR3 = 3;
	private final static int AND0 = 4;
	private final static int NOT = 5;

	public final static int X = 0;
	public final static int Y = 1;
	public final static int BIN = 2;
	public final static int S = 0;
	public final static int BOUT = 1;

	public SUB_3_2() {
		super(3, 2, 6, "SUB_3_2");
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[XOR0] = new XOR_2_1();
		subCircuits[XOR1] = new XOR_2_1();
		subCircuits[XOR2] = new XOR_2_1();
		subCircuits[XOR3] = new XOR_2_1();
		subCircuits[AND0] = AND_2_1.newInstance();
		subCircuits[NOT] = new NOT_1_1();

		super.createSubCircuits();
	}

	@Override
	protected void connectWires() {
		Circuit xor0 = subCircuits[XOR0];
		Circuit xor1 = subCircuits[XOR1];
		Circuit xor2 = subCircuits[XOR2];
		Circuit xor3 = subCircuits[XOR3];
		Circuit and0 = subCircuits[AND0];
		Circuit not = subCircuits[NOT];

		inputWires[BIN].connectTo(xor0.inputWires, 0);
		and0.outputWires[0].connectTo(xor0.inputWires, 1);
		
		inputWires[X].connectTo(xor1.inputWires, 0);
		inputWires[BIN].connectTo(xor1.inputWires, 1);

		inputWires[Y].connectTo(xor2.inputWires, 0);
		inputWires[BIN].connectTo(xor2.inputWires, 1);

		inputWires[X].connectTo(xor3.inputWires, 0);
		xor2.outputWires[0].connectTo(xor3.inputWires, 1);

		xor1.outputWires[0].connectTo(not.inputWires, 0);
		not.outputWires[0].connectTo(and0.inputWires, 0);
		xor2.outputWires[0].connectTo(and0.inputWires, 1);
	    }

	@Override
	protected void defineOutputWires() {
		outputWires[S] = subCircuits[XOR3].outputWires[0];
		outputWires[BOUT] = subCircuits[XOR0].outputWires[0];
	}

}
