package YaoGC;

public class Karatsuba extends CompositeCircuit {

	private int l,k,m;
	
	public Karatsuba(int l, int k) {
		super(l+k, l+k, 9, "Karatsuba_"+l+"_"+k);
		this.l = l;
		this.k = k;
		this.m = (l+1)/2;
	}
	
	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new FastMUL_L_M(m,m);
		subCircuits[1] = new FastMUL_L_M(l-m,k-m);
		subCircuits[2] = new ADD_2L_Lplus1(m);
		subCircuits[3] = new ADD_2L_Lplus1(m);
		subCircuits[4] = new FastMUL_L_M(m+1, m+1);
		subCircuits[5] = new SUB_2L_L(2*m+1);
		subCircuits[6] = new SUB_2L_L(2*m+1);
		subCircuits[7] = new ADD_2L_Lplus1(2*m+1);
		subCircuits[8] = new ADD_2L_Lplus1(l+k-2*m);
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		// z_0 = x_0 * y_0
		for(int i=0; i<m; i++){
			inputWires[i].connectTo(subCircuits[0].inputWires, i);
			inputWires[l+i].connectTo(subCircuits[0].inputWires, m+i);
		}
		// z_2 = x_1 * y_1
		for(int i=m; i<l; i++){
			inputWires[i].connectTo(subCircuits[1].inputWires, i-m);
			if(i<k){
				inputWires[l+i].connectTo(subCircuits[1].inputWires, l-m+i-m);
			}
		}
		// a_0 = x_1 + x_0
		for(int i=0; i<m; i++){
			inputWires[i].connectTo(subCircuits[2].inputWires, 2*i);
			if(i+m < l){
				inputWires[i+m].connectTo(subCircuits[2].inputWires, 2*i+1);
			}else{
				subCircuits[2].inputWires[2*i+1].fixWire(0);
			}
		}
		// a_1 = y_1 + y_0
		for(int i=0; i<m; i++){
			inputWires[l+i].connectTo(subCircuits[3].inputWires, 2*i);
			if(i+m < k){
				inputWires[l+i+m].connectTo(subCircuits[3].inputWires, 2*i+1);
			}else{
				subCircuits[3].inputWires[2*i+1].fixWire(0);
			}
		}		
		// a_2 = a_1 * a_0
		for(int i=0; i<m+1; i++){
			subCircuits[2].outputWires[i].connectTo(subCircuits[4].inputWires, i);
			subCircuits[3].outputWires[i].connectTo(subCircuits[4].inputWires, i+m+1);
		}
		// a_3 = a_2 - z_0
		for(int i=0; i<2*m; i++){
			subCircuits[4].outputWires[i].connectTo(subCircuits[5].inputWires, i);
			subCircuits[0].outputWires[i].connectTo(subCircuits[5].inputWires, 2*m+1+i);
		}
		subCircuits[4].outputWires[2*m].connectTo(subCircuits[5].inputWires, 2*m);
		subCircuits[5].inputWires[4*m+1].fixWire(0);
		// z_1 = a_3 - z_2
		for(int i=0; i<l+k-2*m; i++){
			subCircuits[5].outputWires[i].connectTo(subCircuits[6].inputWires, i);
			subCircuits[1].outputWires[i].connectTo(subCircuits[6].inputWires, 2*m+1+i);
		}
		for(int i=l+k-2*m; i<2*m+1 ;i++){
			subCircuits[5].outputWires[i].connectTo(subCircuits[6].inputWires, i);
			subCircuits[6].inputWires[2*m+1+i].fixWire(0);
		}
		// a_5 = (z_0 >> m) + z_1
		for(int i=0; i<m; i++){
			subCircuits[0].outputWires[i+m].connectTo(subCircuits[7].inputWires, 2*i);
		}
		for(int i=m; i<2*m+1; i++){
			subCircuits[7].inputWires[2*i].fixWire(0);
		}
		for(int i=0; i<2*m+1; i++){
			subCircuits[6].outputWires[i].connectTo(subCircuits[7].inputWires, 2*i+1);
		}
		// a_6 = (a_5 >> m) + z_2
		for(int i=0; i<l+k-2*m; i++){
			if(i<m+2){
				subCircuits[7].outputWires[i+m].connectTo(subCircuits[8].inputWires, 2*i);
			}else{
				subCircuits[8].inputWires[2*i].fixWire(0);
			}
		}
		for(int i=0; i<l+k-2*m; i++){
			subCircuits[1].outputWires[i].connectTo(subCircuits[8].inputWires, 2*i+1);
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<m; i++){
			outputWires[i] = subCircuits[0].outputWires[i];
		}
		for(int i=0; i<m; i++){
			outputWires[m+i] = subCircuits[7].outputWires[i];
		}
		for(int i=0; i<l+k-2*m; i++){
			outputWires[2*m+i] = subCircuits[8].outputWires[i];
		}
	}

}
