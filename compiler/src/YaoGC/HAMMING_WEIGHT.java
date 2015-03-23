package YaoGC;

import math.BitMath;

/**
 * Computes the Hamming weight of the input x
 * InputWires:
 * -first l wires: x
 * OutputWires:
 * ceil(log_2(l+1)) wires
 */

public class HAMMING_WEIGHT extends CompositeCircuit {

	private int power;
	private int n;

	public HAMMING_WEIGHT(int l) {
		super(l, BitMath.bitLength(l), 1, "Hammingweight_" + l + "_" + BitMath.bitLength(l));
		int[] pn = BitMath.ceil_log_with_n(l); // we want to know power = min_k(l<=2**k) and n
		// = 2**p
		this.power = pn[0];
		this.n = pn[1];
	}

	protected void createSubCircuits() throws Exception {
		if (inDegree == 2) {
			subCircuits[0] = new HALF_ADD_2_2();
		} else {
			if (inDegree == n) {
				subCircuits[0] = new HW2p(inDegree, outDegree, power);
			} else {
				if (inDegree == n - 1) {
					subCircuits[0] = new HW2pminus1(inDegree, outDegree, power);
				} else {
					if (inDegree == n / 2 + 1) {
						subCircuits[0] = new HW2pPlus1(inDegree, outDegree, power);
					} else {
						subCircuits[0] = new HWelse(inDegree, outDegree, power);
					}

				}
			}
		}

		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {

		Wire[] subInputs = subCircuits[0].inputWires;
		for (int i = 0; i < inDegree; i++) {
			inputWires[i].connectTo(subInputs, i);
		}
	}

	@Override
	protected void defineOutputWires() {
		System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, outDegree);
	}


	class HW2pPlus1 extends CompositeCircuit {
		/*
		 * Hamming Weight circuit for inputs with bitlength l = (2**p) + 1, p in
		 * N
		 */
		private int power;

		public HW2pPlus1(int inDegree, int outDegree, int power) {
			super(inDegree, outDegree, 2, "Hammingweight_" + inDegree);
			this.power = power;
		}

		protected void createSubCircuits() throws Exception {
			subCircuits[0] = new HW2pminus1(inDegree - 2, outDegree - 1, power - 1);
			subCircuits[1] = new ADD_1Lplus1plusCarry_Lplus1(outDegree - 1);

			super.createSubCircuits();
		}

		@Override
		protected void connectWires() throws Exception {
			for (int i = 0; i < inDegree - 2; i++) {
				inputWires[i].connectTo(subCircuits[0].inputWires, i);
			}
			inputWires[inDegree - 2].connectTo(subCircuits[1].inputWires, outDegree - 1);
			inputWires[inDegree - 1].connectTo(subCircuits[1].inputWires, outDegree);
			for (int i = 0; i < outDegree - 1; i++) {
				subCircuits[0].outputWires[i].connectTo(subCircuits[1].inputWires, i);
			}

		}

		@Override
		protected void defineOutputWires() {
			System.arraycopy(subCircuits[1].outputWires, 0, outputWires, 0, outDegree);
		}

	}

	class HW2p extends CompositeCircuit {
		/* Hamming Weight circuit for inputs with bitlength l = 2**p, p in N */

		private int power;

		public HW2p(int inDegree, int outDegree, int power) {
			super(inDegree, outDegree, 2, "Hammingweight_" + inDegree);
			this.power = power;
		}

		protected void createSubCircuits() throws Exception {
			subCircuits[0] = new HW2pminus1(inDegree - 1, outDegree - 1, power);
			subCircuits[1] = new ADD1_Lplus1_Lplus1(outDegree - 1);

			super.createSubCircuits();
		}

		@Override
		protected void connectWires() throws Exception {
			for (int i = 0; i < inDegree - 1; i++) {
				inputWires[i].connectTo(subCircuits[0].inputWires, i);
			}
			inputWires[inDegree - 1].connectTo(subCircuits[1].inputWires, 0);
			for (int i = 0; i < outDegree - 1; i++) {
				subCircuits[0].outputWires[i].connectTo(subCircuits[1].inputWires, i + 1);
			}

		}

		@Override
		protected void defineOutputWires() {
			System.arraycopy(subCircuits[1].outputWires, 0, outputWires, 0, outDegree);
		}

	}

	class HW2pminus1 extends CompositeCircuit {
		/* Hamming Weight circuit for inputs with bitlength l = (2**p)-1, p in N */

		private int power;

		public HW2pminus1(int inDegree, int outDegree, int power) {
			super(inDegree, outDegree, numberOfSubCircuitsHW2pminus1(power), "Hammingweight_"
					+ inDegree);
			this.power = power;
		}

		protected void createSubCircuits() throws Exception {
			if (power == 2) {
				subCircuits[0] = new ADD_3_2();
			} else {
				subCircuits[0] = new HW2pminus1(inDegree / 2, outDegree - 1, power - 1);
				subCircuits[1] = new HW2pminus1(inDegree / 2, outDegree - 1, power - 1);
				subCircuits[2] = new ADD_2LplusCarry_Lplus1(outDegree - 1);
			}

			super.createSubCircuits();
		}

		@Override
		protected void connectWires() throws Exception {
			if (power == 2) { // then we have to hook up the ADD_3_2
				for (int i = 0; i < 3; i++) {
					inputWires[i].connectTo(subCircuits[0].inputWires, i);
				}
			} else {
				int halfInDegree = inDegree / 2;
				for (int i = 0; i < halfInDegree; i++) {
					inputWires[i].connectTo(subCircuits[0].inputWires, i);
					inputWires[i + halfInDegree].connectTo(subCircuits[1].inputWires, i);
				}
				for (int i = 0; i < outDegree - 1; i++) {
					subCircuits[0].outputWires[i].connectTo(subCircuits[2].inputWires, i);
					subCircuits[1].outputWires[i].connectTo(subCircuits[2].inputWires, i
							+ outDegree - 1);
				}
				inputWires[inDegree - 1].connectTo(subCircuits[2].inputWires, 2 * outDegree - 2);
			}

		}

		@Override
		protected void defineOutputWires() {
			if (power == 2) {
				outputWires[0] = subCircuits[0].outputWires[0];
				outputWires[1] = subCircuits[0].outputWires[1];
			} else {
				System.arraycopy(subCircuits[2].outputWires, 0, outputWires, 0, outDegree);
			}

		}

	}

	class HWelse extends CompositeCircuit {
		/*
		 * Hamming Weight circuit for inputs that don't fit to one of the above
		 * circuits
		 */

		private int power;

		public HWelse(int inDegree, int outDegree, int power) {
			super(inDegree, outDegree, 3, "Hammingweight_" + inDegree);
			this.power = power;
		}

		protected void createSubCircuits() throws Exception {
			int firstSize = n / 2 - 1;
			int secondSize = inDegree - firstSize - 1;
			subCircuits[0] = new HW2pminus1(firstSize, outDegree - 1, power - 1);
			subCircuits[1] = new HAMMING_WEIGHT(secondSize);
			//subCircuits[2] = new ADD_2LplusCarry_Lplus1(outDegree - 1);
			subCircuits[2] = new ADD_L_M_Carry_Lplus1(outDegree-1, BitMath.bitLength(secondSize));
			super.createSubCircuits();
		}

		@Override
		protected void connectWires() throws Exception {
			int firstSize = n / 2 - 1;
			int secondSize = inDegree - firstSize - 1;
			for (int i = 0; i < firstSize; i++) {
				inputWires[i].connectTo(subCircuits[0].inputWires, i);
			}
			for (int i = firstSize; i < firstSize + secondSize; i++) {
				inputWires[i].connectTo(subCircuits[1].inputWires, i - firstSize);
			}
			//inputWires[inDegree - 1].connectTo(subCircuits[2].inputWires, 2 * outDegree - 2);
			// now the internal connections:
			int wireIdx = 0;
			for (int i = 0; i < outDegree - 1; i++) {
				subCircuits[0].outputWires[i].connectTo(subCircuits[2].inputWires, wireIdx++);
			}
			for (int i = 0; i < subCircuits[1].outDegree; i++) {
				subCircuits[1].outputWires[i].connectTo(subCircuits[2].inputWires, wireIdx++);
			}
			inputWires[inDegree - 1].connectTo(subCircuits[2].inputWires, wireIdx);
			/*
			for (int i = wireIdx; i < 2 * outDegree - 2; i++) {
				// fill the rest with zeros
				subCircuits[2].inputWires[i].fixWire(0);
			}*/
		}

		@Override
		protected void defineOutputWires() {
			System.arraycopy(subCircuits[2].outputWires, 0, outputWires, 0, outDegree);
		}

	}

	private static int numberOfSubCircuitsHW2pminus1(int power) {
		if (power == 2) {
			return 1;
		} else {
			return 3;
		}
	}

}
