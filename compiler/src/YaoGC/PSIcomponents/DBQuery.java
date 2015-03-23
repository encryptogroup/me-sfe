package YaoGC.PSIcomponents;

import YaoGC.CompositeCircuit;

/**
 * Implements a circuit to compute the numerator of the weighted Dice coefficient, as described in the paper 
 * 'Privacy-Preserving Fraud Detection Across Multiple Phone Record Databases'.
 */

public class DBQuery extends CompositeCircuit {

	private int k;
	private int ln;
	private int lw;
	private int n;
	
	
	public DBQuery(int k, int ln, int lw, int n){
		super((n+1)*k*(ln+lw),n*lw, n, "DBQuery");
		this.k = k;
		this.ln = ln;
		this.lw = lw;
		this.n = n;
	}
	
	protected void createSubCircuits() throws Exception {
		for(int i=0; i<n; i++){
			subCircuits[i]= new PSI(k,ln,lw);
		}
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<n; i++){
			for(int l=0; l<k*(ln+lw); l++){
				inputWires[l].connectTo(subCircuits[i].inputWires, l);
			}
		}
		for(int i=0; i<n; i++){
			for(int l=0; l<k*(ln+lw); l++){
				inputWires[(i+1)*k*(ln+lw)+l].connectTo(subCircuits[i].inputWires, k*(ln+lw)+l);
			}
		}
	}

	@Override
	protected void defineOutputWires() {
		for(int i=0; i<n; i++){
			for(int l=0; l<lw; l++){
				outputWires[i*lw+l] = subCircuits[i].outputWires[l];
			}
		}

	}

}
