package YaoGC;


/**
 * 
 * inputs:
 *  - first l bits: min_a
 *  - next  l bits: min_b
 *  - next  l bits: max_a
 *  - last  l bits: max_b
 *  
 *  outputs:
 *  - first l bits: max(min_a, min_b)
 *  - next  l bits: min(max_a, max_b) - max(min_a, min_b)
 */
public class MAXMIN_L extends CompositeCircuit {

	private int l;
	private static final int MAX = 0;
	private static final int MIN = 1;
	private static final int SUB = 2;
	private static final int MASK = 3;
	
	public MAXMIN_L(int l) {
		super(4*l, 2*l, 3, null);
		this.l = l;
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[MAX] = new MAX_2L_L(l);
		subCircuits[MIN] = new MIN_2L_L(l);
		subCircuits[SUB] = new SUB_2L_L(l);
		//subCircuits[MASK] = new NextBitMask(l);
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<l; i++){
			inputWires[i].connectTo(subCircuits[MAX].inputWires, i);
			inputWires[i+l].connectTo(subCircuits[MAX].inputWires, i+l);
		}
		for(int i=0; i<l; i++){
			inputWires[i+2*l].connectTo(subCircuits[MIN].inputWires, i);
			inputWires[i+3*l].connectTo(subCircuits[MIN].inputWires, i+l);
		}
		for(int i=0; i<l; i++){
			subCircuits[MIN].outputWires[i].connectTo(subCircuits[SUB].inputWires, i);
			subCircuits[MAX].outputWires[i].connectTo(subCircuits[SUB].inputWires, i+l);
		}

	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[MAX].outputWires, 0, outputWires, 0, l);
		System.arraycopy(subCircuits[SUB].outputWires, 0, outputWires, l, l);
	}

}
