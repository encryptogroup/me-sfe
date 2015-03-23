package YaoGC;

import math.BitMath;

/**
 * A barrel shifter.
 * Left shifts a value x for y positions
 * Output: z = x << y
 * 
 * inputs:
 *  - first l wires: x
 *  - last log(l) wires: y
 */

public class BarrelShifter extends CompositeCircuit {

	private int L, logL;
	public BarrelShifter(int l) {
		super(BitMath.bitLength(l-1)+l, l, BitMath.bitLength(l-1)*l, "BarrelShifter_" + l);
		this.L = l;
		this.logL = BitMath.bitLength(l-1);
	}
	
	protected void createSubCircuits() throws Exception {
		for(int i=0; i<logL*L; i++){
			subCircuits[i] = new MUX_3_1();
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		//the inputs of the value x to shift
		subCircuits[0].inputWires[MUX_3_1.Y].fixWire(0);
		for(int i=0; i<L-1; i++){
			inputWires[i].connectTo(subCircuits[i].inputWires, MUX_3_1.X);
			inputWires[i].connectTo(subCircuits[i+1].inputWires, MUX_3_1.Y);
		}
		inputWires[L-1].connectTo(subCircuits[L-1].inputWires, MUX_3_1.X);
		//internal wires
		for(int i=1; i<logL; i++){
			for(int j=0; j<(1<<i); j++){
				subCircuits[i*L+j].inputWires[MUX_3_1.Y].fixWire(0);
			}
			for(int j=0; j<L; j++){
				subCircuits[(i-1)*L+j].outputWires[0].connectTo(subCircuits[i*L + j].inputWires, MUX_3_1.X);
			}
			for(int j=0; j<L-(1<<i); j++){
				subCircuits[(i-1)*L+j].outputWires[0].connectTo(subCircuits[i*L + j + (1<<i)].inputWires, MUX_3_1.Y);
			}
		}
		//inputs of the shift value y
		for(int i=0; i<logL; i++){
			for(int j=0; j<L; j++){
				inputWires[L+i].connectTo(subCircuits[i*L + j].inputWires, MUX_3_1.C);
			}
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<L; i++){
			outputWires[i] = subCircuits[L*(logL-1) + i].outputWires[0];
		}

	}

}
