// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.util.Random;


public class AND_2_1 extends SimpleCircuit_2_1 {
	private static final String name = "AND_2_1";
	static final Random rnd = new Random();
    public AND_2_1() {
	super(name);
    }

    public static AND_2_1 newInstance() {
	    return new AND_2_1();
    }

    public void execute(){
    	Wire inWireL = inputWires[0];
		Wire inWireR = inputWires[1];
		Wire outWire = outputWires[0];


		if (inWireL.value != Wire.UNKNOWN_SIG && inWireR.value != Wire.UNKNOWN_SIG) {
			compute();
		} else if (inWireL.value != Wire.UNKNOWN_SIG) {
			if (inWireL.value == 0){
				outWire.value = 0;
				outWire.invd = false;
			}else{
				outWire.invd = inWireR.invd;
				outWire.value = Wire.UNKNOWN_SIG;
				outWire.registerNum = inWireR.getRegisterNum();
			}
		} else if (inWireR.value != Wire.UNKNOWN_SIG) {
			if (inWireR.value == 0){
				outWire.invd = false;
				outWire.value = 0;
			}else{
				outWire.invd = inWireL.invd;
				outWire.value = Wire.UNKNOWN_SIG;
				outWire.registerNum = inWireL.getRegisterNum();
			}	
		} else {
			pw.print(outWire.getRegisterNum() + ";");
			pw.print(inWireL.getRegisterNum() + "," + inWireR.getRegisterNum() + ";");
			//outWire.invd = inWireL.invd ^ inWireR.invd; //shouldn't this always be 'false'???
			outWire.invd = false;
			if(inWireL.invd){
				if(inWireR.invd){
					pw.println("8");
				}else{
					pw.println("4");
				}
			}else{
				if(inWireR.invd){
					pw.println("2");
				}else{
					pw.println("1");
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
	byte left  = inputWires[0].value;
	byte right = inputWires[1].value;

	outputWires[0].value = (byte)(left & right);
    }

	@Override
	public int getNumOfNonLinearGates() {
		// TODO Auto-generated method stub
		return 1;
	}

}
