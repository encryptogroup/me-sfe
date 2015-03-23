package YaoGC;

/**
 * adds an L-bit number x, an M-bit number y and a carry bit c. 
 * L has to be greater than M!
 * Output (L+1 bits) z = x+y+c 
 * InputWires: 
 *   -first L wires: x 
 *   -next M wires: y 
 *   -last wire: c
 */

public class ADD_L_M_Carry_Lplus1 extends CompositeCircuit {

	private int l, m;
	
	public ADD_L_M_Carry_Lplus1(int L, int M) {
		super(L+M+1, L+1, L, "ADD_" + L + "_" + M + "_Carry_" + (L+1));
		l = L;
		m = M;
	}
	
	protected void createSubCircuits() throws Exception {				
		for(int i=0; i<m; i++){
			subCircuits[i] = new ADD_3_2();
		}
		for(int i=m; i<l; i++){
			subCircuits[i] = new HALF_ADD_2_2();
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<l; i++){  //these are the l bits of x
			inputWires[i].connectTo(subCircuits[i].inputWires, 0);
		}
		for(int i=0; i<m; i++){  //the m bits of y
			inputWires[l+i].connectTo(subCircuits[i].inputWires, ADD_3_2.Y);
		}
		//the carry bit
		inputWires[l+m].connectTo(subCircuits[0].inputWires, ADD_3_2.CIN);
		//the internal connections:
		for(int i=0; i<m-1; i++){
			subCircuits[i].outputWires[ADD_3_2.COUT].connectTo(subCircuits[i+1].inputWires, ADD_3_2.CIN);
		}
		for(int i=m-1; i<l-1; i++){
			subCircuits[i].outputWires[HALF_ADD_2_2.COUT].connectTo(subCircuits[i+1].inputWires, HALF_ADD_2_2.Y);
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<l; i++){
			outputWires[i] = subCircuits[i].outputWires[ADD_3_2.S];
		}
		outputWires[l] = subCircuits[l-1].outputWires[HALF_ADD_2_2.COUT];

	}

}
