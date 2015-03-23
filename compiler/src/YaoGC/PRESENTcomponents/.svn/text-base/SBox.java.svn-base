package YaoGC.PRESENTcomponents;

import YaoGC.AND_2_1;
import YaoGC.CompositeCircuit;
import YaoGC.OR_2_1;
import YaoGC.XNOR_2_1;
import YaoGC.XOR_2_1;



/* SBox for the PRESENT Cipher.
 * input x (4 bits) with x0 LSB
 * output y (4 bits) with y0 LSB
 
 with:
t1 = x1^x2
t2 = x2&t1
t3 = x3^t2
y0 = x0^t3
t2 = t1&t3
t1 = t1^y0
t2 = t2^X2
t4 = x0|t2
y1 = t1^t4
t2 = t2^(not x0) 
y3 = y1^t2
t2 = t2|t1
y2 = t3^t2
*/

public class SBox extends CompositeCircuit {

	public SBox() {
		super(4, 4, 13, "PRESENT_SBox");
	}
	
	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new XOR_2_1(); 		//T1
		subCircuits[1] = AND_2_1.newInstance();	//T2
		subCircuits[2] = new XOR_2_1();			//T3
		subCircuits[3] = new XOR_2_1();			//Y3
		subCircuits[4] = AND_2_1.newInstance();	//T2
		subCircuits[5] = new XOR_2_1();			//T1
		subCircuits[6] = new XOR_2_1();			//T2
		subCircuits[7] = OR_2_1.newInstance();	//T4
		subCircuits[8] = new XOR_2_1();			//Y2
		subCircuits[9] = new XNOR_2_1();		//T2
		subCircuits[10] = new XOR_2_1();		//Y0
		subCircuits[11] = OR_2_1.newInstance();	//T2
		subCircuits[12] = new XOR_2_1();		//Y1
		
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		inputWires[1].connectTo(subCircuits[0].inputWires, 0);					//t1 = X1^X2
		inputWires[2].connectTo(subCircuits[0].inputWires, 1);
		inputWires[2].connectTo(subCircuits[1].inputWires, 0); 					//t2 = X2&t1
		subCircuits[0].outputWires[0].connectTo(subCircuits[1].inputWires, 1);
		inputWires[3].connectTo(subCircuits[2].inputWires, 0);					//t3 = X3^t2
		subCircuits[1].outputWires[0].connectTo(subCircuits[2].inputWires, 1);
		inputWires[0].connectTo(subCircuits[3].inputWires, 0);					//y0 = X0^t3
		subCircuits[2].outputWires[0].connectTo(subCircuits[3].inputWires, 1);
		subCircuits[0].outputWires[0].connectTo(subCircuits[4].inputWires, 0);	//t2 = t1&t3
		subCircuits[2].outputWires[0].connectTo(subCircuits[4].inputWires, 1);
		subCircuits[0].outputWires[0].connectTo(subCircuits[5].inputWires, 0);	//t1 = t1^y0  
		subCircuits[3].outputWires[0].connectTo(subCircuits[5].inputWires, 1);
		subCircuits[4].outputWires[0].connectTo(subCircuits[6].inputWires, 0);	//t2 = t2^X2
		inputWires[2].connectTo(subCircuits[6].inputWires, 1);
		inputWires[0].connectTo(subCircuits[7].inputWires, 0);					//t4 = X0|t2
		subCircuits[6].outputWires[0].connectTo(subCircuits[7].inputWires, 1);
		subCircuits[5].outputWires[0].connectTo(subCircuits[8].inputWires, 0);	//y1 = t1^t4
		subCircuits[7].outputWires[0].connectTo(subCircuits[8].inputWires, 1);
		subCircuits[6].outputWires[0].connectTo(subCircuits[9].inputWires, 0);	//t2 = t2^(not X0) 
		inputWires[0].connectTo(subCircuits[9].inputWires, 1);
		subCircuits[8].outputWires[0].connectTo(subCircuits[10].inputWires, 0);	//y3 = y1^t2
		subCircuits[9].outputWires[0].connectTo(subCircuits[10].inputWires, 1);
		subCircuits[9].outputWires[0].connectTo(subCircuits[11].inputWires, 0);	//t2 = t2|t1
		subCircuits[5].outputWires[0].connectTo(subCircuits[11].inputWires, 1);
		subCircuits[2].outputWires[0].connectTo(subCircuits[12].inputWires, 0);	//y2 = t3^t2
		subCircuits[11].outputWires[0].connectTo(subCircuits[12].inputWires, 1);
	}

	@Override
	protected void defineOutputWires() {
		outputWires[3] = subCircuits[10].outputWires[0];
		outputWires[2] = subCircuits[12].outputWires[0];
		outputWires[1] = subCircuits[8].outputWires[0];
		outputWires[0] = subCircuits[3].outputWires[0];
	}

}
