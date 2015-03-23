package YaoGC.PRESENTcomponents;

import YaoGC.CompositeCircuit;

public class SBox16 extends CompositeCircuit {

	public SBox16() {
		super(64, 64, 16, null);
	}
	
	protected void createSubCircuits() throws Exception {
		for(int i=0; i<16; i++){
			subCircuits[i] = new SBox();
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<16; i++){
			for(int j = 0; j<4; j++){
				inputWires[i*4+j].connectTo(subCircuits[i].inputWires, j);
			}
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<16; i++){
			for(int j = 0; j<4; j++){
				outputWires[i*4+j] = subCircuits[i].outputWires[j];
			}
		}

	}

}
