package YaoGC;


/**
 * picks a random number out of the range 0 .. (2^n)-1
 * These are all possible n-bit integer.
 * 
 * inputs:
 *  - first l bits: n
 *  - next 2^l bits: r_a randomness from party A
 *  - last 2^l bits: r_b randomness form party B
 * outputs:
 *  - 2^l bits: r uniformly at random out of {0, 1, ..., (2^n)-1}
 *
 */
public class RandomFromRange_0_L extends CompositeCircuit {

	private int l, lpow;
	
	public RandomFromRange_0_L(int l) {
		super(l + 2*((1<<l)-1), (1<<l)-1, (1<<l)-1+2, "RandomFromRange_0_" + l);
		this.l = l;
		this.lpow = (1<<l)-1;
	}
	
	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new XOR_2L_L(lpow); // r = r_a xor r_b
		subCircuits[1] = new BitMask(lpow);
		for(int i=0; i<lpow; i++){
			subCircuits[2+i] = AND_2_1.newInstance();
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		// n
		for(int i=0; i<l; i++){
			inputWires[i].connectTo(subCircuits[1].inputWires, i);
		}
		// r_a and r_b
		for(int i=0; i<lpow; i++){
			inputWires[l+i].connectTo(subCircuits[0].inputWires, i);
			inputWires[l+lpow+i].connectTo(subCircuits[0].inputWires, lpow+i);
		}
		for(int i=0; i<lpow; i++){
			subCircuits[1].outputWires[i].connectTo(subCircuits[2+i].inputWires, 0);
			subCircuits[0].outputWires[i].connectTo(subCircuits[2+i].inputWires, 1);
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<lpow; i++){
			outputWires[i] = subCircuits[2+i].outputWires[0];
		}
	}

}
