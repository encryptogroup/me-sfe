package YaoGC;

/*public class XNOR_2_1 extends CompositeCircuit {

	public XNOR_2_1() {
		super(2, 1, 2, "XNOR");
	}

	protected void createSubCircuits() throws Exception{
		subCircuits[0] = new XOR_2_1();
		subCircuits[1] = new XOR_2_1();
		super.createSubCircuits();
	}
	
	@Override
	protected void connectWires() throws Exception {
		inputWires[0].connectTo(subCircuits[0].inputWires, 0);
		inputWires[1].connectTo(subCircuits[0].inputWires, 1);
		subCircuits[0].outputWires[0].connectTo(subCircuits[1].inputWires, 0);
		subCircuits[1].inputWires[1].fixWire(1);
	}

	@Override
	protected void defineOutputWires() {
		outputWires = subCircuits[1].outputWires;
		
	}

}*/
//Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>


public class XNOR_2_1 extends SimpleCircuit_2_1 {
	private static final String name = "XNOR_2_1";
	public XNOR_2_1() {
		super(name);
	}

	protected void compute() {
		byte left = inputWires[0].value;
		byte right = inputWires[1].value;

		outputWires[0].value = (byte) (1-(left ^ right));
	}

	public void execute() {
		Wire inWireL = inputWires[0];
		Wire inWireR = inputWires[1];
		Wire outWire = outputWires[0];


		if (inWireL.value != Wire.UNKNOWN_SIG && inWireR.value != Wire.UNKNOWN_SIG) {
			compute();
		} else if (inWireL.value != Wire.UNKNOWN_SIG) {
			if (inWireL.value == 0)
				outWire.invd = !inWireR.invd;
			else
				outWire.invd = inWireR.invd;
			outWire.value = Wire.UNKNOWN_SIG;
			outWire.registerNum = inWireR.getRegisterNum();
		} else if (inWireR.value != Wire.UNKNOWN_SIG) {
			if (inWireR.value == 0)
				outWire.invd = !inWireL.invd;
			else
				outWire.invd = inWireL.invd;

			outWire.value = Wire.UNKNOWN_SIG;
			outWire.registerNum = inWireL.getRegisterNum();
		} else {
			pw.print(outWire.getRegisterNum() + ";");
			pw.print(inWireL.getRegisterNum() + "," + inWireR.getRegisterNum() + ";");
			outWire.invd = inWireL.invd ^ inWireR.invd;
			if(outWire.invd){
				pw.println("6");
			}else{
				pw.println("9");
			}
			outWire.value = Wire.UNKNOWN_SIG;
			numberOfGates++;
			linearGatesCount++;
		}
		// fire up the output
		outWire.setReady();
	}

	@Override
	public int getNumOfNonLinearGates() {
		return 0;
	}


}

