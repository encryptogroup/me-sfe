// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;


public abstract class SimpleCircuit_2_1 extends Circuit {


	public SimpleCircuit_2_1(String name) {
		super(2, 1, name);
	}

	public void build() throws Exception {
		createInputWires();
		createOutputWires();
	}

	protected void createInputWires() {
		super.createInputWires();

		for (int i = 0; i < inDegree; i++)
			inputWires[i].addObserver(this);
	}

	protected void createOutputWires() {
		outputWires[0] = new Wire();
	}

	
}
