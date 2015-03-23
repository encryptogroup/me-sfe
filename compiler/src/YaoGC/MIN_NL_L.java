package YaoGC;

public class MIN_NL_L extends CompositeCircuit {

	private int n;
	private int l;
	
	public MIN_NL_L(int n, int l) {
		super(n*l, l, n-1, "MIN_"+n+"*"+l);
		this.n = n;
		this.l = l;
	}
	
	protected void createSubCircuits() throws Exception {
		for(int i=0; i<n-1; i++){
			subCircuits[i] = new MIN_2L_L(l);
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<l; i++){
			inputWires[i].connectTo(subCircuits[0].inputWires, i);
		}
		for(int i=1; i<n; i++){
			for(int j=0; j<l; j++){
				inputWires[i*l + j].connectTo(subCircuits[i-1].inputWires, j+l);
			}
		}
		for(int i=0; i<n-2; i++){
			for(int j=0; j<l; j++){
				subCircuits[i].outputWires[j].connectTo(subCircuits[i+1].inputWires, j);
			}
		}
	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[n-2].outputWires, 0, outputWires, 0, l);
	}

}
