package YaoGC.PSIcomponents;

import YaoGC.ADD_2L_L;
import YaoGC.CompositeCircuit;
import YaoGC.EQUALS_2L_1;
import YaoGC.MUX_2Lplus1_L;
import YaoGC.XOR_L_1;

/**
 * helper circuit for PSI.java
 */

public class WOI extends CompositeCircuit {

	private int k;
	private int ln;
	private int lw;

	public WOI(int k, int ln, int lw) {
		super((k + 1) * (ln + lw), lw, 3*k+2, "WOI");
		this.k = k;
		this.lw = lw;
		this.ln = ln;
	}

	protected void createSubCircuits() throws Exception {
		for (int i = 0; i < k; i++) {
			subCircuits[i] = new EQUALS_2L_1(ln);
			subCircuits[k + i] = new MUX_2Lplus1_L(lw);
			subCircuits[2 * k + i] = new ADD_2L_L(lw);
		}
		subCircuits[3*k] = new XOR_L_1(k);
		subCircuits[3 * k + 1] = new MUX_2Lplus1_L(lw);
		super.createSubCircuits();
	}

	@Override
	protected void connectWires() throws Exception {
		// first we connect n to all EQUALS
		for (int l = 0; l < k; l++) {
			for (int i = 0; i < ln; i++) {
				inputWires[i].connectTo(subCircuits[l].inputWires, i);
			}
		}
		// now connect all m_i to the corresp. EQUALS
		for (int l = 0; l < k; l++) {
			for (int i = 0; i < ln; i++) {
				inputWires[(l + 1) * (ln + lw) + i].connectTo(subCircuits[l].inputWires, i + ln);
			}
		}
		// outputs of the EQUALS feed into the MUXs as C
		for (int i = 0; i < k; i++) {
			subCircuits[i].outputWires[0].connectTo(subCircuits[i + k].inputWires, 2 * lw);
		}
		// one input of all MUX is always 0
		for (int l = 0; l < k; l++) {
			for (int i = 0; i < lw; i++) {
				subCircuits[k + l].inputWires[MUX_2Lplus1_L.X(i)].fixWire(0);
			}
		}
		// and the other input is the corresponding weight
		for (int l = 0; l < k; l++) {
			for (int i = 0; i < lw; i++) {
				inputWires[(l + 1) * (ln + lw) + ln + i].connectTo(subCircuits[k + l].inputWires,
						MUX_2Lplus1_L.Y(i));
			}
		}
		// the outputs of the EQUALS also go into the XOR_L_1
		for(int i=0; i<k; i++){
			subCircuits[i].outputWires[0].connectTo(subCircuits[3*k].inputWires, i);
		}
		subCircuits[3*k].outputWires[0].connectTo(subCircuits[3*k+1].inputWires, 2*lw);
		for(int i=0; i<lw; i++){
			subCircuits[3*k+1].inputWires[MUX_2Lplus1_L.X(i)].fixWire(0);
			inputWires[ln+i].connectTo(subCircuits[3*k+1].inputWires, MUX_2Lplus1_L.Y(i));
		}
		// and finally the row of adders
		for(int l=0; l<k; l++){
			for( int i=0; i<lw; i++){
				subCircuits[k+l].outputWires[i].connectTo(subCircuits[2*k+l].inputWires, lw+i);
			}
		}
		for(int i=0; i<lw; i++){
			subCircuits[3*k+1].outputWires[i].connectTo(subCircuits[2*k].inputWires, i);
		}
		for(int l=0; l<k-1; l++){
			for(int i=0; i<lw; i++){
				subCircuits[2*k+l].outputWires[i].connectTo(subCircuits[2*k+l+1].inputWires, i);
			}
		}
	}

	@Override
	protected void defineOutputWires() {
		outputWires = subCircuits[3*k-1].outputWires;
	}

}
