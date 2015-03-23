package YaoGC;

public class EQUALS_2L_1 extends CompositeCircuit {

	private int L;
	public EQUALS_2L_1(int L){
		super(2*L, 1, 2*L-1, "EQUALS");
		this.L = L;
	}
	
	protected void createSubCircuits() throws Exception {
		for( int i=0; i<L; i++){
			subCircuits[i] = new XNOR_2_1();
		}
		for (int i=0; i<L-1; i++){
			subCircuits[L+i] = AND_2_1.newInstance();
		}
		super.createSubCircuits();
	}
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<L; i++){
			inputWires[i].connectTo(subCircuits[i].inputWires, 0);
			inputWires[L+i].connectTo(subCircuits[i].inputWires, 1);
		}
		subCircuits[0].outputWires[0].connectTo(subCircuits[L].inputWires, 0);
		for(int i=0; i<L-1; i++){
			subCircuits[i+1].outputWires[0].connectTo(subCircuits[L+i].inputWires, 1);
		}
		for(int i=0; i<L-2; i++){
			subCircuits[L+i].outputWires[0].connectTo(subCircuits[L+1+i].inputWires, 0);
		}
	}

	@Override
	protected void defineOutputWires() {
		outputWires[0] = subCircuits[2*L-2].outputWires[0];

	}

}
