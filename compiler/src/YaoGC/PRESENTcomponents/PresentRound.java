package YaoGC.PRESENTcomponents;

import YaoGC.CompositeCircuit;
import YaoGC.XOR_2L_L;

public class PresentRound extends CompositeCircuit {

	public PresentRound() {
		super(128, 64, 17, "PresentRound");
	}

	protected void createSubCircuits() throws Exception{
		subCircuits[0] = new XOR_2L_L(64);
		for(int i=1; i<17; i++){
			subCircuits[i] = new SBox();
		}
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<inDegree; i++){
			inputWires[i].connectTo(subCircuits[0].inputWires, i);
		}
		//connect the SBoxes
		for(int i=0; i<16; i++){
			for(int j=0; j<4; j++){
				subCircuits[0].outputWires[i*4 + j].connectTo(subCircuits[i+1].inputWires, j);
			}
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<16; i++){
			for(int j=0; j<4; j++){
				outputWires[4*i+j] = subCircuits[i+1].outputWires[j];
			}
		}

	}

}
