package YaoGC;

/**
 * picks a random number out of the range 1 .. (2^n)-1 These are all possible
 * n-bit integer except 0.
 * 
 * inputs: 
 *  - first l bits: n 
 *  - next 2^l bits: r_1^a, first randomness from party A
 *  - next 2^l bits: r_2^a, second randomness from party A 
 *  - next 2^l bits: r_1^b, first randomness from party B
 *  - last 2^l bits: r_2^b, second randomness form party B 
 * outputs: - 2^l bits: r uniformly at random out of {1, 2, ..., (2^n)-1}
 * 
 */

public class RandomFromRange_1_L extends CompositeCircuit {

	private int bits, bitwidth;
	
	private final int XOR1 = 0;
	private int XOR2, XOR3;
	private final int BITMASK = 1;

	public RandomFromRange_1_L(int l) {
		super(l + 4 * ((1 << l) - 1), (1 << l) - 1, 8 + 3 * ((1 << l) - 1), "RandomFromRange_1_" + l);
		this.bits = l;
		this.bitwidth = (1 << l) - 1;
		XOR2 = 2 + bitwidth;
		XOR3 = 3 + 2 * bitwidth;
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[XOR1] = new XOR_2L_L(bitwidth); // r_1 = r_1^a xor r_1^b
		subCircuits[BITMASK] = new BitMask(bitwidth); // 2^n -1
		for (int i = 0; i < bitwidth; i++) {
			subCircuits[2 + i] = AND_2_1.newInstance();
		}
		subCircuits[XOR2] = new XOR_2L_L(bitwidth); // r_2 = r_2^a xor r_2^b
		for (int i = 0; i < bitwidth; i++) {
			subCircuits[3 + bitwidth + i] = AND_2_1.newInstance();
		}
		subCircuits[XOR3] = new XOR_2L_L(bitwidth); // r_3 = r_1^a xor r_2^a
		for (int i = 0; i < bitwidth; i++) {
			subCircuits[4 + 2 * bitwidth + i] = AND_2_1.newInstance();
		}
		// control structure
		subCircuits[4 + 3 * bitwidth] = new NEQ_ZERO(bitwidth);
		subCircuits[5 + 3 * bitwidth] = new MUX_2Lplus1_L(bitwidth);
		subCircuits[6 + 3 * bitwidth] = new NEQ_ZERO(bitwidth);
		subCircuits[7 + 3 * bitwidth] = new MUX_2Lplus1_L(bitwidth);

		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		// n
		for (int i = 0; i < bits; i++) {
			inputWires[i].connectTo(subCircuits[BITMASK].inputWires, i);
		}
		// all the random goodness
		for( int i=0; i<bitwidth; i++){
			inputWires[bits+i].connectTo(subCircuits[XOR1].inputWires, i); //r_1^a
			inputWires[bits+i].connectTo(subCircuits[XOR3].inputWires, i);//r_1^a
			inputWires[bits+2*bitwidth+i].connectTo(subCircuits[XOR1].inputWires, bitwidth+i); //r_1^b
			inputWires[bits+bitwidth+i].connectTo(subCircuits[XOR2].inputWires, i); //r_2^a
			inputWires[bits+bitwidth+i].connectTo(subCircuits[XOR3].inputWires, bitwidth+i); //r_2^a
			inputWires[bits+3*bitwidth+i].connectTo(subCircuits[XOR2].inputWires, bitwidth+i); //r_2^b
		}
		// r_1 mod 2^n-1, r_2 mod 2^n-1 and r_3 mod 2^n-1
		for(int i=0; i<bitwidth; i++){
			subCircuits[XOR1].outputWires[i].connectTo(subCircuits[2+i].inputWires, 0);
			subCircuits[BITMASK].outputWires[i].connectTo(subCircuits[2+i].inputWires, 1);
			subCircuits[XOR2].outputWires[i].connectTo(subCircuits[3+bitwidth+i].inputWires, 0);
			subCircuits[BITMASK].outputWires[i].connectTo(subCircuits[3+bitwidth+i].inputWires, 1);
			subCircuits[XOR3].outputWires[i].connectTo(subCircuits[4+2*bitwidth+i].inputWires, 0);
			subCircuits[BITMASK].outputWires[i].connectTo(subCircuits[4+2*bitwidth+i].inputWires, 1);
		}
		// hook up the NEQ_ZEROs
		for(int i=0; i<bitwidth; i++){
			subCircuits[2+i].outputWires[0].connectTo(subCircuits[4+3*bitwidth].inputWires, i);
			subCircuits[3+bitwidth+i].outputWires[0].connectTo(subCircuits[6+3*bitwidth].inputWires, i);
		}
		//and finally the MUX
		for(int i=0; i<bitwidth; i++){
			subCircuits[4+2*bitwidth+i].outputWires[0].connectTo(subCircuits[7+3*bitwidth].inputWires, MUX_2Lplus1_L.X(i));
			subCircuits[3+bitwidth+i].outputWires[0].connectTo(subCircuits[7+3*bitwidth].inputWires, MUX_2Lplus1_L.Y(i));
		}
		subCircuits[6+3*bitwidth].outputWires[0].connectTo(subCircuits[7+3*bitwidth].inputWires, 2*bitwidth);
		for(int i=0; i<bitwidth; i++){
			subCircuits[7+3*bitwidth].outputWires[i].connectTo(subCircuits[5+3*bitwidth].inputWires, MUX_2Lplus1_L.X(i));
			subCircuits[2+i].outputWires[0].connectTo(subCircuits[5+3*bitwidth].inputWires, MUX_2Lplus1_L.Y(i));
		}
		subCircuits[4+3*bitwidth].outputWires[0].connectTo(subCircuits[5+3*bitwidth].inputWires, 2*bitwidth);

	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<bitwidth; i++){
			outputWires[i] = subCircuits[5+3*bitwidth].outputWires[i];
		}

	}

}
