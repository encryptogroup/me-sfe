package YaoGC;

/**
 * A Half_Subtrator.
 * Inputs: two bits x and y
 * Outputs: one bit s = x-y mod2, one bit b = (!x+y)/2 (the borrow bit)
 */

public class HALF_SUB_2_2 extends CompositeCircuit {

	private final static int XOR = 0;
	private final static int AND = 1;
	private final static int NOT = 2;

	public final static int X = 0;
	public final static int Y = 1;
	public final static int S = 0;
	public final static int BOUT = 1;
	
	public HALF_SUB_2_2() {
		super(2, 2, 3, "HALF_SUB_2_2");
	}
	
	protected void createSubCircuits() throws Exception {
		subCircuits[XOR] = new XOR_2_1();
		subCircuits[AND] = AND_2_1.newInstance();
		subCircuits[NOT] = new NOT_1_1();
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() {
		inputWires[X].connectTo(subCircuits[XOR].inputWires, 0);
		inputWires[X].connectTo(subCircuits[NOT].inputWires, 0);
		inputWires[Y].connectTo(subCircuits[XOR].inputWires, 1);
		inputWires[Y].connectTo(subCircuits[AND].inputWires, 1);
		subCircuits[NOT].outputWires[0].connectTo(subCircuits[AND].inputWires, 0);
	}

	@Override
	protected void defineOutputWires() {
		outputWires[S] = subCircuits[XOR].outputWires[0];
		outputWires[BOUT] = subCircuits[AND].outputWires[0];
	}

}
