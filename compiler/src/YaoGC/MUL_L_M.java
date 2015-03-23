package YaoGC;
/**
 * A school-method multiplication circuit that computes z = x * y
 * where x is a l-bit and y is a k-bit integer. 
 * k has to be bigger than 1 and l has to be bigger than k!
 * 
 * inputs: 
 *  - first l wires: x
 *  - next k wires: y    with l>=k>1
 * output:
 *  - l*k wires: z
 */

public class MUL_L_M extends CompositeCircuit {

	private int l,k, kl;
	
	public MUL_L_M(int l, int k) {
		super(l+k, l+k, l*k + k-1, "MUL_" + l + "_" + k);
		this.l = l;
		this.k = k;
		this.kl = k*l;
	}
	
	protected void createSubCircuits() throws Exception {
		int kl = k*l;
		for(int i=0; i<kl; i++){
			subCircuits[i] = AND_2_1.newInstance();
		}
		for(int i=0; i<k-1; i++){
			subCircuits[kl+i] = new ADD_2L_Lplus1(l);
		}
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		//first multiply the inputs pair-wise
		for(int i=0; i<k; i++){
			for(int j=0; j<l; j++){
				inputWires[l+i].connectTo(subCircuits[i*l+j].inputWires, 0);
				inputWires[j].connectTo(subCircuits[i*l+j].inputWires, 1);
			}
		}
		//now add them all up
		//first adder is a bit different...
		for(int i=0; i<l; i++){
			if(i == l-1){
				subCircuits[kl].inputWires[2*i].fixWire(0);
			}else{
				subCircuits[i+1].outputWires[0].connectTo(subCircuits[kl].inputWires, 2*i);
			}
			subCircuits[l+i].outputWires[0].connectTo(subCircuits[kl].inputWires, 2*i+1);
		}
		//and the rest
		for(int i=1; i<k-1; i++){
			for(int j=0; j<l; j++){
				subCircuits[(i+1)*l+j].outputWires[0].connectTo(subCircuits[kl+i].inputWires, 2*j); //these are the outputs of a row of ANDs
				subCircuits[kl+i-1].outputWires[j+1].connectTo(subCircuits[kl+i].inputWires, 2*j+1); //these are the outputs of the adder from the row before
			}
		}

	}

	@Override
	protected void defineOutputWires() {
		outputWires[0] = subCircuits[0].outputWires[0];
		for(int i=0; i<k-2; i++){
			outputWires[i+1] = subCircuits[kl+i].outputWires[0];
		}
		System.arraycopy(subCircuits[kl+k-2].outputWires, 0, outputWires,k-1, l+1);
	}

}
