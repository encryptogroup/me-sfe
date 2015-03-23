package YaoGC;


/**
 * inputs:
 *  - first n bits: min_a
 *  - next n bits: max_a
 *  - next n bits: min_b
 *  - next n bits: max_b
 *  - next ... all the randomness
 * outputs:
 *  - 2^n -1 bits: r uniformly at random out of {2^(max(min_a,min_b)), ..., 2^(min(max_a,max_b))-1}
 *
 */

public class RandomScale extends CompositeCircuit {

	private int n;
	private int npow;
	
	private static final int MAX = 0;
	private static final int MIN = 1;
	private static final int RANDOM_0 = 2;
	private static final int SUB = 3;
	private static final int RANDOM_1 = 4;
	private static final int SHIFT = 5;
	private static final int ADD = 6;
	
	public RandomScale(int n) {
		super(4*n+6*((1<<n)-1), (1<<n)-1, 7, "RandomScale_"+n);
		this.n = n;
		npow = (1<<n)-1;
	}
	
	protected void createSubCircuits() throws Exception {
		subCircuits[MAX] = new MAX_2L_L(n);
		subCircuits[MIN] = new MIN_2L_L(n);
		subCircuits[RANDOM_0] = new RandomFromRange_0_L(n);
		subCircuits[SUB] = new SUB_2L_L(n);
		subCircuits[RANDOM_1] = new RandomFromRange_1_L(n);
		subCircuits[SHIFT] = new BarrelShifter(npow);
		subCircuits[ADD] = new ADD_2L_L(npow);
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		//all the inputs that go into MAX and MIN
		for(int i=0; i<n; i++){
			inputWires[i].connectTo(subCircuits[MAX].inputWires, i);
			inputWires[2*n+i].connectTo(subCircuits[MAX].inputWires, n+i);
			inputWires[n+i].connectTo(subCircuits[MIN].inputWires, i);
			inputWires[3*n+i].connectTo(subCircuits[MIN].inputWires, n+i);
		}
		for(int i=0; i<n; i++){
			subCircuits[MAX].outputWires[i].connectTo(subCircuits[RANDOM_0].inputWires, i);
		}
		for(int i=0; i<npow; i++){
			inputWires[n+i].connectTo(subCircuits[RANDOM_0].inputWires, n+i);
			inputWires[n+3*npow+i].connectTo(subCircuits[RANDOM_0].inputWires, n+npow+i);
		}
		for(int i=0; i<n; i++){
			subCircuits[MIN].outputWires[i].connectTo(subCircuits[SUB].inputWires, i);
			subCircuits[MAX].outputWires[i].connectTo(subCircuits[SUB].inputWires, n+i);
		}
		for(int i=0; i<n; i++){
			subCircuits[SUB].outputWires[i].connectTo(subCircuits[RANDOM_1].inputWires, i);
		}
		for(int i=0; i<npow; i++){
			inputWires[n+npow+i].connectTo(subCircuits[RANDOM_1].inputWires, n+i);
			inputWires[n+2*npow+i].connectTo(subCircuits[RANDOM_1].inputWires, n+npow+i);
			inputWires[n+4*npow+i].connectTo(subCircuits[RANDOM_1].inputWires, n+2*npow+i);
			inputWires[n+5*npow+i].connectTo(subCircuits[RANDOM_1].inputWires, n+3*npow+i);
		}
		for(int i=0; i<npow; i++){
			subCircuits[RANDOM_1].outputWires[i].connectTo(subCircuits[SHIFT].inputWires, i);
		}
		for(int i=0; i<n; i++){
			subCircuits[MAX].outputWires[i].connectTo(subCircuits[SHIFT].inputWires, npow+i);
		}
		for(int i=0; i<npow; i++){
			subCircuits[RANDOM_0].outputWires[i].connectTo(subCircuits[ADD].inputWires, i);
			subCircuits[SHIFT].outputWires[i].connectTo(subCircuits[ADD].inputWires, npow+i);
		}
	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[ADD].outputWires, 0, outputWires, 0, npow);

	}

}
