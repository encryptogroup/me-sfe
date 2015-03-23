package YaoGC;

import math.BitMath;

/**
 * Creates a bitmask of max l bit. input: n, a ceil_log(l)-bit number denoting
 * the number of bits set in the bitmask output: x, an l-bit number with x =
 * (2**n)-1
 */

public class BitMask extends CompositeCircuit {

	private int l;
	private int logl;

	public BitMask(int l) {
		super(BitMath.bitLength(l), l, numOfMUX(l), "BitMask_" + l);
		this.l = l;
		this.logl = BitMath.bitLength(l);
	}

	protected void createSubCircuits() throws Exception {
		for (int i = 0; i < numOfMUX(l); i++) {
			subCircuits[i] = new MUX_3_1();
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		// connect the inputs
		int muxIdx = 0;
		for (int i = 0; i < logl-1; i++) {
			for (int j = 0; j < numOfMuxInRow(i); j++) {
				inputWires[i].connectTo(subCircuits[muxIdx++].inputWires, MUX_3_1.C);
			}
		}
		for(int i=0; i<l; i++){ //the last row
			inputWires[logl-1].connectTo(subCircuits[muxIdx++].inputWires, MUX_3_1.C);
		}
		// the constant 1-inputs to the MUXs
		for (int i = 0; i < logl; i++) {
			for (int j = 0; j < powOfTwo(i); j++) {
				subCircuits[numOfMuxInRows(i) + j].inputWires[MUX_3_1.Y].fixWire(1);
			}
		}
		// the constant 0-inputs to the MUXs
		for (int i = 0; i < logl-1; i++) {
			for (int j = numOfMuxInRow(i-1); j < numOfMuxInRow(i); j++) {
				subCircuits[numOfMuxInRows(i) + j].inputWires[MUX_3_1.X].fixWire(0);
			}
		}
		for(int j=numOfMuxInRow(logl-2); j< l; j++){
			subCircuits[numOfMuxInRows(logl-1) + j].inputWires[MUX_3_1.X].fixWire(0);
		}
		// the internal connections
		int nOfMux = numOfMUX(l);
		muxIdx = 0;
		for(int i=0; i<logl-1; i++){
			for(int j = 0; j<numOfMuxInRow(i); j++){
				subCircuits[muxIdx].outputWires[0].connectTo(subCircuits[numOfMuxInRow(i)+muxIdx].inputWires, MUX_3_1.X);
				if(numOfMuxInRow(i+1)+muxIdx < nOfMux){
					subCircuits[muxIdx].outputWires[0].connectTo(subCircuits[numOfMuxInRow(i+1)+muxIdx].inputWires, MUX_3_1.Y);
				}
				muxIdx++;
			}
		}

	}

	@Override
	protected void defineOutputWires() {
		int lastRowIdx = subCircuits.length-l;
		for(int i=0; i<l;i++){
			outputWires[i] = subCircuits[lastRowIdx + i].outputWires[0];
		}
	}

	private static int numOfMuxInRow(int i) {
		return (1 << (i + 1)) - 1;
	}
	
	private static int numOfMuxInRows(int numberOfRows){
		int res = 0;
		for(int i=0; i<numberOfRows; i++){
			res += numOfMuxInRow(i);
		}
		return res;
	}

	private static int numOfMUX(int l) {
		//if l == 2**k - 1, then numOfMux = 2**(k+1)-k-2
		int logl = BitMath.bitLength(l);
		return powOfTwo(logl) - logl - 1 + l;
	}

	/* returns 2 to the power of i */
	private static int powOfTwo(int i) {
		if (i < 0) {
			return 0;
		} else {
			return 1 << i;
		}
	}

	public static void main(String[] args){
		for(int i=1; i<16; i++){
			System.out.println(i + ": " + BitMask.numOfMUX(i) + ", " + BitMask.numOfMuxInRow(i));
		}
	}
}
