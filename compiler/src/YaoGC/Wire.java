// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;


public class Wire extends TransitiveObservable {
	public static final byte UNKNOWN_SIG = -1;

	// These four fields are for garbling
	//public static int K = 0;
	//private static Random rnd = new Random();
	public static final int labelBitLength = 80;

	//public static final BigInteger R = new BigInteger(labelBitLength - 1, rnd);

	//public final int serialNum; // every wire gets a unique serial number
	private static int registerCounter = 0;
	protected int registerNum = -1;
	public byte value = UNKNOWN_SIG;
	//public BigInteger lbl;
	public boolean invd = false;
	// we want to be able to tell when the label of this wire is not needed any
	// more. Therefore we introduce this consumedCount which will be increased
	// every time an observer has read the label and doesn't need it any more.
	// since we delete the labels when they are not needed by a gate any more
	// there can be situations where an output label get's deleted. So therefore
	// we introduce this boolean property to protect the label from deletion
	protected boolean isOutputWire = false;

	public Wire() {
		//serialNum = K++;
		// lbl = new BigInteger(labelBitLength, rnd); That's completely
		// unnecessary, because the labels will be generated later on
	}
	
	public int getRegisterNum(){
		if(registerNum == -1){
			registerNum = registerCounter++;
			return registerNum;
		}else{
			return registerNum;
		}
	}
	
	public static int getRegisterCount(){
		return registerCounter;
	}


	public void setReady() {
		setChanged();
		notifyObservers();
	}

	public void connectTo(Wire[] ws, int idx) {
		Wire w = ws[idx];

		for (int i = 0; i < w.observers.size(); i++) {
			Circuit c = (Circuit) w.observers.get(i);
			this.addObserver(c);
			for (int j = 0; j < c.inputWires.length; j++) {
				if (w.equals(c.inputWires[j])) {
					c.inputWires[j] = this;
				}
			}
		}
		w.deleteObservers();
	}

	public void fixWire(int v) {
		this.value = (byte) v;

		for (int i = 0; i < this.observers.size(); i++) {
			Circuit c = (Circuit) this.observers.get(i);
			c.inDegree--;
			if (c.inDegree == 0) {
				c.compute();
				for (int j = 0; j < c.outDegree; j++)
					c.outputWires[j].fixWire(c.outputWires[j].value);
			}
		}
	}

}
