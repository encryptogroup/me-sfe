package YaoGC.PRESENTcomponents;

import YaoGC.CompositeCircuit;
import YaoGC.XOR_2L_L;

public class PresentFull extends CompositeCircuit {

	private static final int ROUNDS = 32;
	private static final int[] PBOX = new int[] { 0, 16, 32, 48, 1, 17, 33, 49, 2, 18, 34, 50, 3,
		19, 35, 51, 4, 20, 36, 52, 5, 21, 37, 53, 6, 22, 38, 54, 7, 23, 39, 55, 8, 24, 40, 56,
		9, 25, 41, 57, 10, 26, 42, 58, 11, 27, 43, 59, 12, 28, 44, 60, 13, 29, 45, 61, 14, 30,
		46, 62, 15, 31, 47, 63 };
	
	public PresentFull() {
		super((ROUNDS+1)*64, 64, ROUNDS, "Present_full");
	}
	
	protected void createSubCircuits() throws Exception{
		for(int i=0; i<ROUNDS-1; i++){
			subCircuits[i] = new PresentRound();
		}
		subCircuits[ROUNDS-1] = new XOR_2L_L(64); //AddRoundKey
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<64; i++){ //the message to encrypt
			inputWires[i].connectTo(subCircuits[0].inputWires, i);
		}
		for(int i=0; i<ROUNDS; i++){ //all the round keys
			for(int j=0; j<64; j++){
				inputWires[(i+1)*64 + j].connectTo(subCircuits[i].inputWires, 64+j);
			}
		}
		for(int i=0; i<ROUNDS-1; i++){ //internal connections
			for(int j=0; j<64; j++){
				subCircuits[i].outputWires[j].connectTo(subCircuits[i+1].inputWires, PBOX[j]);
			}
		}

	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[ROUNDS-1].outputWires, 0, outputWires, 0, outputWires.length);

	}

}
