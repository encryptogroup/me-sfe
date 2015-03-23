// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;


public class OR_2_1 extends SimpleCircuit_2_1 {
    public OR_2_1() {
	super("OR_2_1");
    }

    public static OR_2_1 newInstance() {
	    return new OR_2_1();
    }
    
    public void execute(){
    	Wire inWireL = inputWires[0];
		Wire inWireR = inputWires[1];
		Wire outWire = outputWires[0];


		if (inWireL.value != Wire.UNKNOWN_SIG && inWireR.value != Wire.UNKNOWN_SIG) {
			compute();
		} else if (inWireL.value != Wire.UNKNOWN_SIG) {
			if (inWireL.value == 0){
				outWire.value = Wire.UNKNOWN_SIG;
				outWire.invd = inWireR.invd;
				outWire.registerNum = inWireR.getRegisterNum();
			}else{
				outWire.invd = false;
				outWire.value = 1;
			}
		} else if (inWireR.value != Wire.UNKNOWN_SIG) {
			if (inWireR.value == 0){
				outWire.invd = inWireL.invd;
				outWire.value = Wire.UNKNOWN_SIG;
				outWire.registerNum = inWireL.getRegisterNum();
			}else{
				outWire.invd = false;
				outWire.value = 1;
			}	
		} else {
			pw.print(outWire.getRegisterNum() + ";");
			pw.print(inWireL.getRegisterNum() + "," + inWireR.getRegisterNum() + ";");
			outWire.invd = inWireL.invd ^ inWireR.invd;
			if(inWireL.invd){
				if(inWireR.invd){
					pw.println("14");
				}else{
					pw.println("13");
				}
			}else{
				if(inWireR.invd){
					pw.println("11");
				}else{
					pw.println("7");
				}
			}
			outWire.value = Wire.UNKNOWN_SIG;
			numberOfGates++;
			nonlinearGatesCount++;
		}
		// fire up the output
		outWire.setReady();
    }

    protected void compute() {
	byte left = inputWires[0].value;
	byte right = inputWires[1].value;

	outputWires[0].value = (byte)(left | right);
    }

	@Override
	public int getNumOfNonLinearGates() {
		// TODO Auto-generated method stub
		return 1;
	}

}
