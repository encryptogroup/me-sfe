package YaoGC;

public class AES_SBox_4 extends CompositeCircuit {

	public AES_SBox_4() {
		super(32, 32, 4, null);
	}

	protected void createSubCircuits() throws Exception {
		for(int i=0; i<4; i++){
			subCircuits[i] = new AES_SBox();
		}
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<4; i++){
			for(int j=0; j<8; j++){
				inputWires[i*4+j].connectTo(subCircuits[i].inputWires, j);
			}
		}

	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<4; i++){
			System.arraycopy(subCircuits[i].outputWires, 0, outputWires, i*8, 8);
		}
	}

}
