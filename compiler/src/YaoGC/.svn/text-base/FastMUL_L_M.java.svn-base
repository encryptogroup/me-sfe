package YaoGC;

public class FastMUL_L_M extends CompositeCircuit {

	public static final int BREAK_THRESHOLD = 10;
	
	private int max, l, k;
	
	public FastMUL_L_M(int l, int k) throws Exception{
		super(l+k, l+k, 1, "FastMUL_"+l+"_"+k);
		if( l<k) throw new Exception("FastMUL requires the first input to be greater or equal to the second input!");
		max = l;
		this.l = l;
		this.k = k;
	}

	protected void createSubCircuits() throws Exception {
		if(max<=BREAK_THRESHOLD){
			subCircuits[0] = new MUL_L_M(l, k);
		}else{
			subCircuits[0] = new Karatsuba(l, k);
		}
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<l+k; i++){
			inputWires[i].connectTo(subCircuits[0].inputWires, i);
		}

	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, l+k);
	}

}
