package YaoGC.PSIcomponents;

import YaoGC.ADD_2L_L;
import YaoGC.CompositeCircuit;


/**
 * Implements the Boolean circuit shown in Figure 11 in the paper 'Privacy-Preserving Fraud Detection Across Multiple Phone Record Databases'
 * k: number of numbers to compare with
 * ln: number of bits to represent a phone number
 * lw: number of bits to represent a weight
 */
public class PSI extends CompositeCircuit {

	
	
	private int k;
	private int lw;
	private int ln;
	
	public PSI(int k, int ln, int lw){
		super(2*k*(ln+lw),lw,2*k-1,"PSI");
		this.k = k;
		this.lw = lw;
		this.ln = ln;
	}
	
	protected void createSubCircuits() throws Exception {
		for(int i=0; i<k; i++){
			subCircuits[i]= new WOI(k,ln,lw);
		}
		for(int i=0; i<k-1; i++){
			subCircuits[k+i]= new ADD_2L_L(lw);
		}
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		for(int i=0; i<k; i++){
			for( int l=0; l<ln+lw; l++){
				inputWires[i*(ln+lw)+l].connectTo(subCircuits[i].inputWires, l);
			}
		}
		for(int i=0; i<k; i++){
			for(int l=0; l<k*(ln+lw); l++){
				inputWires[k*(ln+lw)+l].connectTo(subCircuits[i].inputWires, ln+lw+l);
			}
		}
		for(int i=0; i<k-1; i++){
			for(int l=0; l<lw; l++){
				subCircuits[i+1].outputWires[l].connectTo(subCircuits[k+i].inputWires, lw+l);
			}
		}
		for(int l=0; l<lw; l++){
			subCircuits[0].outputWires[l].connectTo(subCircuits[k].inputWires, l);
		}
		for(int i=0; i<k-2; i++){
			for(int l=0; l<lw; l++){
				subCircuits[k+i].outputWires[l].connectTo(subCircuits[k+i+1].inputWires, l);
			}
		}

	}

	@Override
	protected void defineOutputWires() {
		outputWires = subCircuits[2*k-2].outputWires;

	}

}
