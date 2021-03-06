package YaoGC;


public class AES_SBox extends CompositeCircuit {

	public AES_SBox() {
		super(8, 8, 115, "AES_SBox");
	}

	protected void createSubCircuits() throws Exception {
		subCircuits[0] = new XOR_2_1();
		subCircuits[1] = new XOR_2_1();
		subCircuits[2] = new XOR_2_1();
		subCircuits[3] = new XOR_2_1();
		subCircuits[4] = new XOR_2_1();
		subCircuits[5] = new XOR_2_1();
		subCircuits[6] = new XOR_2_1();
		subCircuits[7] = new XOR_2_1();
		subCircuits[8] = new XOR_2_1();
		subCircuits[9] = new XOR_2_1();
		subCircuits[10] = new XOR_2_1();
		subCircuits[11] = new XOR_2_1();
		subCircuits[12] = new XOR_2_1();
		subCircuits[13] = new XOR_2_1();
		subCircuits[14] = new XOR_2_1();
		subCircuits[15] = new XOR_2_1();
		subCircuits[16] = new XOR_2_1();
		subCircuits[17] = new XOR_2_1();
		subCircuits[18] = new XOR_2_1();
		subCircuits[19] = new XOR_2_1();
		subCircuits[20] = new XOR_2_1();
		subCircuits[21] = new XOR_2_1();
		subCircuits[22] = new XOR_2_1();
		subCircuits[23] = AND_2_1.newInstance();
		subCircuits[24] = AND_2_1.newInstance();
		subCircuits[25] = new XOR_2_1();
		subCircuits[26] = AND_2_1.newInstance();
		subCircuits[27] = new XOR_2_1();
		subCircuits[28] = AND_2_1.newInstance();
		subCircuits[29] = AND_2_1.newInstance();
		subCircuits[30] = new XOR_2_1();
		subCircuits[31] = AND_2_1.newInstance();
		subCircuits[32] = new XOR_2_1();
		subCircuits[33] = AND_2_1.newInstance();
		subCircuits[34] = AND_2_1.newInstance();
		subCircuits[35] = new XOR_2_1();
		subCircuits[36] = AND_2_1.newInstance();
		subCircuits[37] = new XOR_2_1();
		subCircuits[38] = new XOR_2_1();
		subCircuits[39] = new XOR_2_1();
		subCircuits[40] = new XOR_2_1();
		subCircuits[41] = new XOR_2_1();
		subCircuits[42] = new XOR_2_1();
		subCircuits[43] = new XOR_2_1();
		subCircuits[44] = new XOR_2_1();
		subCircuits[45] = new XOR_2_1();
		subCircuits[46] = new XOR_2_1();
		subCircuits[47] = AND_2_1.newInstance();
		subCircuits[48] = new XOR_2_1();
		subCircuits[49] = AND_2_1.newInstance();
		subCircuits[50] = new XOR_2_1();
		subCircuits[51] = new XOR_2_1();
		subCircuits[52] = new XOR_2_1();
		subCircuits[53] = AND_2_1.newInstance();
		subCircuits[54] = new XOR_2_1();
		subCircuits[55] = new XOR_2_1();
		subCircuits[56] = new XOR_2_1();
		subCircuits[57] = AND_2_1.newInstance();
		subCircuits[58] = new XOR_2_1();
		subCircuits[59] = new XOR_2_1();
		subCircuits[60] = AND_2_1.newInstance();
		subCircuits[61] = new XOR_2_1();
		subCircuits[62] = new XOR_2_1();
		subCircuits[63] = new XOR_2_1();
		subCircuits[64] = new XOR_2_1();
		subCircuits[65] = new XOR_2_1();
		subCircuits[66] = new XOR_2_1();
		subCircuits[67] = AND_2_1.newInstance();
		subCircuits[68] = AND_2_1.newInstance();
		subCircuits[69] = AND_2_1.newInstance();
		subCircuits[70] = AND_2_1.newInstance();
		subCircuits[71] = AND_2_1.newInstance();
		subCircuits[72] = AND_2_1.newInstance();
		subCircuits[73] = AND_2_1.newInstance();
		subCircuits[74] = AND_2_1.newInstance();
		subCircuits[75] = AND_2_1.newInstance();
		subCircuits[76] = AND_2_1.newInstance();
		subCircuits[77] = AND_2_1.newInstance();
		subCircuits[78] = AND_2_1.newInstance();
		subCircuits[79] = AND_2_1.newInstance();
		subCircuits[80] = AND_2_1.newInstance();
		subCircuits[81] = AND_2_1.newInstance();
		subCircuits[82] = AND_2_1.newInstance();
		subCircuits[83] = AND_2_1.newInstance();
		subCircuits[84] = AND_2_1.newInstance();
		subCircuits[85] = new XOR_2_1();
		subCircuits[86] = new XOR_2_1();
		subCircuits[87] = new XOR_2_1();
		subCircuits[88] = new XOR_2_1();
		subCircuits[89] = new XOR_2_1();
		subCircuits[90] = new XOR_2_1();
		subCircuits[91] = new XOR_2_1();
		subCircuits[92] = new XOR_2_1();
		subCircuits[93] = new XOR_2_1();
		subCircuits[94] = new XOR_2_1();
		subCircuits[95] = new XOR_2_1();
		subCircuits[96] = new XOR_2_1();
		subCircuits[97] = new XOR_2_1();
		subCircuits[98] = new XOR_2_1();
		subCircuits[99] = new XOR_2_1();
		subCircuits[100] = new XOR_2_1();
		subCircuits[101] = new XOR_2_1();
		subCircuits[102] = new XOR_2_1();
		subCircuits[103] = new XOR_2_1();
		subCircuits[104] = new XOR_2_1();
		subCircuits[105] = new XOR_2_1();
		subCircuits[106] = new XOR_2_1();
		subCircuits[107] = new XNOR_2_1();
		subCircuits[108] = new XNOR_2_1();
		subCircuits[109] = new XOR_2_1();
		subCircuits[110] = new XOR_2_1();
		subCircuits[111] = new XOR_2_1();
		subCircuits[112] = new XOR_2_1();
		subCircuits[113] = new XNOR_2_1();
		subCircuits[114] = new XNOR_2_1();
		super.createSubCircuits();
	}

	protected void connectWires() throws Exception {
		inputWires[4].connectTo(subCircuits[0].inputWires, 0);
		inputWires[2].connectTo(subCircuits[0].inputWires, 1);
		inputWires[7].connectTo(subCircuits[1].inputWires, 0);
		inputWires[1].connectTo(subCircuits[1].inputWires, 1);
		inputWires[7].connectTo(subCircuits[2].inputWires, 0);
		inputWires[4].connectTo(subCircuits[2].inputWires, 1);
		inputWires[7].connectTo(subCircuits[3].inputWires, 0);
		inputWires[2].connectTo(subCircuits[3].inputWires, 1);
		inputWires[6].connectTo(subCircuits[4].inputWires, 0);
		inputWires[5].connectTo(subCircuits[4].inputWires, 1);
		subCircuits[4].outputWires[0].connectTo(subCircuits[5].inputWires, 0);
		inputWires[0].connectTo(subCircuits[5].inputWires, 1);
		subCircuits[5].outputWires[0].connectTo(subCircuits[6].inputWires, 0);
		inputWires[4].connectTo(subCircuits[6].inputWires, 1);
		subCircuits[1].outputWires[0].connectTo(subCircuits[7].inputWires, 0);
		subCircuits[0].outputWires[0].connectTo(subCircuits[7].inputWires, 1);
		subCircuits[5].outputWires[0].connectTo(subCircuits[8].inputWires, 0);
		inputWires[7].connectTo(subCircuits[8].inputWires, 1);
		subCircuits[5].outputWires[0].connectTo(subCircuits[9].inputWires, 0);
		inputWires[1].connectTo(subCircuits[9].inputWires, 1);
		subCircuits[9].outputWires[0].connectTo(subCircuits[10].inputWires, 0);
		subCircuits[3].outputWires[0].connectTo(subCircuits[10].inputWires, 1);
		inputWires[3].connectTo(subCircuits[11].inputWires, 0);
		subCircuits[7].outputWires[0].connectTo(subCircuits[11].inputWires, 1);
		subCircuits[11].outputWires[0].connectTo(subCircuits[12].inputWires, 0);
		inputWires[2].connectTo(subCircuits[12].inputWires, 1);
		subCircuits[11].outputWires[0].connectTo(subCircuits[13].inputWires, 0);
		inputWires[6].connectTo(subCircuits[13].inputWires, 1);
		subCircuits[12].outputWires[0].connectTo(subCircuits[14].inputWires, 0);
		inputWires[0].connectTo(subCircuits[14].inputWires, 1);
		subCircuits[12].outputWires[0].connectTo(subCircuits[15].inputWires, 0);
		subCircuits[4].outputWires[0].connectTo(subCircuits[15].inputWires, 1);
		subCircuits[13].outputWires[0].connectTo(subCircuits[16].inputWires, 0);
		subCircuits[2].outputWires[0].connectTo(subCircuits[16].inputWires, 1);
		inputWires[0].connectTo(subCircuits[17].inputWires, 0);
		subCircuits[16].outputWires[0].connectTo(subCircuits[17].inputWires, 1);
		subCircuits[15].outputWires[0].connectTo(subCircuits[18].inputWires, 0);
		subCircuits[16].outputWires[0].connectTo(subCircuits[18].inputWires, 1);
		subCircuits[15].outputWires[0].connectTo(subCircuits[19].inputWires, 0);
		subCircuits[3].outputWires[0].connectTo(subCircuits[19].inputWires, 1);
		subCircuits[4].outputWires[0].connectTo(subCircuits[20].inputWires, 0);
		subCircuits[16].outputWires[0].connectTo(subCircuits[20].inputWires, 1);
		subCircuits[1].outputWires[0].connectTo(subCircuits[21].inputWires, 0);
		subCircuits[20].outputWires[0].connectTo(subCircuits[21].inputWires, 1);
		inputWires[7].connectTo(subCircuits[22].inputWires, 0);
		subCircuits[20].outputWires[0].connectTo(subCircuits[22].inputWires, 1);
		subCircuits[7].outputWires[0].connectTo(subCircuits[23].inputWires, 0);
		subCircuits[12].outputWires[0].connectTo(subCircuits[23].inputWires, 1);
		subCircuits[10].outputWires[0].connectTo(subCircuits[24].inputWires, 0);
		subCircuits[14].outputWires[0].connectTo(subCircuits[24].inputWires, 1);
		subCircuits[24].outputWires[0].connectTo(subCircuits[25].inputWires, 0);
		subCircuits[23].outputWires[0].connectTo(subCircuits[25].inputWires, 1);
		subCircuits[6].outputWires[0].connectTo(subCircuits[26].inputWires, 0);
		inputWires[0].connectTo(subCircuits[26].inputWires, 1);
		subCircuits[26].outputWires[0].connectTo(subCircuits[27].inputWires, 0);
		subCircuits[23].outputWires[0].connectTo(subCircuits[27].inputWires, 1);
		subCircuits[1].outputWires[0].connectTo(subCircuits[28].inputWires, 0);
		subCircuits[20].outputWires[0].connectTo(subCircuits[28].inputWires, 1);
		subCircuits[9].outputWires[0].connectTo(subCircuits[29].inputWires, 0);
		subCircuits[5].outputWires[0].connectTo(subCircuits[29].inputWires, 1);
		subCircuits[29].outputWires[0].connectTo(subCircuits[30].inputWires, 0);
		subCircuits[28].outputWires[0].connectTo(subCircuits[30].inputWires, 1);
		subCircuits[8].outputWires[0].connectTo(subCircuits[31].inputWires, 0);
		subCircuits[17].outputWires[0].connectTo(subCircuits[31].inputWires, 1);
		subCircuits[31].outputWires[0].connectTo(subCircuits[32].inputWires, 0);
		subCircuits[28].outputWires[0].connectTo(subCircuits[32].inputWires, 1);
		subCircuits[2].outputWires[0].connectTo(subCircuits[33].inputWires, 0);
		subCircuits[16].outputWires[0].connectTo(subCircuits[33].inputWires, 1);
		subCircuits[0].outputWires[0].connectTo(subCircuits[34].inputWires, 0);
		subCircuits[18].outputWires[0].connectTo(subCircuits[34].inputWires, 1);
		subCircuits[34].outputWires[0].connectTo(subCircuits[35].inputWires, 0);
		subCircuits[33].outputWires[0].connectTo(subCircuits[35].inputWires, 1);
		subCircuits[3].outputWires[0].connectTo(subCircuits[36].inputWires, 0);
		subCircuits[15].outputWires[0].connectTo(subCircuits[36].inputWires, 1);
		subCircuits[36].outputWires[0].connectTo(subCircuits[37].inputWires, 0);
		subCircuits[33].outputWires[0].connectTo(subCircuits[37].inputWires, 1);
		subCircuits[25].outputWires[0].connectTo(subCircuits[38].inputWires, 0);
		subCircuits[35].outputWires[0].connectTo(subCircuits[38].inputWires, 1);
		subCircuits[27].outputWires[0].connectTo(subCircuits[39].inputWires, 0);
		subCircuits[37].outputWires[0].connectTo(subCircuits[39].inputWires, 1);
		subCircuits[30].outputWires[0].connectTo(subCircuits[40].inputWires, 0);
		subCircuits[35].outputWires[0].connectTo(subCircuits[40].inputWires, 1);
		subCircuits[32].outputWires[0].connectTo(subCircuits[41].inputWires, 0);
		subCircuits[37].outputWires[0].connectTo(subCircuits[41].inputWires, 1);
		subCircuits[38].outputWires[0].connectTo(subCircuits[42].inputWires, 0);
		subCircuits[13].outputWires[0].connectTo(subCircuits[42].inputWires, 1);
		subCircuits[39].outputWires[0].connectTo(subCircuits[43].inputWires, 0);
		subCircuits[19].outputWires[0].connectTo(subCircuits[43].inputWires, 1);
		subCircuits[40].outputWires[0].connectTo(subCircuits[44].inputWires, 0);
		subCircuits[21].outputWires[0].connectTo(subCircuits[44].inputWires, 1);
		subCircuits[41].outputWires[0].connectTo(subCircuits[45].inputWires, 0);
		subCircuits[22].outputWires[0].connectTo(subCircuits[45].inputWires, 1);
		subCircuits[42].outputWires[0].connectTo(subCircuits[46].inputWires, 0);
		subCircuits[43].outputWires[0].connectTo(subCircuits[46].inputWires, 1);
		subCircuits[42].outputWires[0].connectTo(subCircuits[47].inputWires, 0);
		subCircuits[44].outputWires[0].connectTo(subCircuits[47].inputWires, 1);
		subCircuits[45].outputWires[0].connectTo(subCircuits[48].inputWires, 0);
		subCircuits[47].outputWires[0].connectTo(subCircuits[48].inputWires, 1);
		subCircuits[46].outputWires[0].connectTo(subCircuits[49].inputWires, 0);
		subCircuits[48].outputWires[0].connectTo(subCircuits[49].inputWires, 1);
		subCircuits[49].outputWires[0].connectTo(subCircuits[50].inputWires, 0);
		subCircuits[43].outputWires[0].connectTo(subCircuits[50].inputWires, 1);
		subCircuits[44].outputWires[0].connectTo(subCircuits[51].inputWires, 0);
		subCircuits[45].outputWires[0].connectTo(subCircuits[51].inputWires, 1);
		subCircuits[43].outputWires[0].connectTo(subCircuits[52].inputWires, 0);
		subCircuits[47].outputWires[0].connectTo(subCircuits[52].inputWires, 1);
		subCircuits[52].outputWires[0].connectTo(subCircuits[53].inputWires, 0);
		subCircuits[51].outputWires[0].connectTo(subCircuits[53].inputWires, 1);
		subCircuits[53].outputWires[0].connectTo(subCircuits[54].inputWires, 0);
		subCircuits[45].outputWires[0].connectTo(subCircuits[54].inputWires, 1);
		subCircuits[44].outputWires[0].connectTo(subCircuits[55].inputWires, 0);
		subCircuits[54].outputWires[0].connectTo(subCircuits[55].inputWires, 1);
		subCircuits[48].outputWires[0].connectTo(subCircuits[56].inputWires, 0);
		subCircuits[54].outputWires[0].connectTo(subCircuits[56].inputWires, 1);
		subCircuits[45].outputWires[0].connectTo(subCircuits[57].inputWires, 0);
		subCircuits[56].outputWires[0].connectTo(subCircuits[57].inputWires, 1);
		subCircuits[57].outputWires[0].connectTo(subCircuits[58].inputWires, 0);
		subCircuits[55].outputWires[0].connectTo(subCircuits[58].inputWires, 1);
		subCircuits[48].outputWires[0].connectTo(subCircuits[59].inputWires, 0);
		subCircuits[57].outputWires[0].connectTo(subCircuits[59].inputWires, 1);
		subCircuits[50].outputWires[0].connectTo(subCircuits[60].inputWires, 0);
		subCircuits[59].outputWires[0].connectTo(subCircuits[60].inputWires, 1);
		subCircuits[46].outputWires[0].connectTo(subCircuits[61].inputWires, 0);
		subCircuits[60].outputWires[0].connectTo(subCircuits[61].inputWires, 1);
		subCircuits[61].outputWires[0].connectTo(subCircuits[62].inputWires, 0);
		subCircuits[58].outputWires[0].connectTo(subCircuits[62].inputWires, 1);
		subCircuits[50].outputWires[0].connectTo(subCircuits[63].inputWires, 0);
		subCircuits[54].outputWires[0].connectTo(subCircuits[63].inputWires, 1);
		subCircuits[50].outputWires[0].connectTo(subCircuits[64].inputWires, 0);
		subCircuits[61].outputWires[0].connectTo(subCircuits[64].inputWires, 1);
		subCircuits[54].outputWires[0].connectTo(subCircuits[65].inputWires, 0);
		subCircuits[58].outputWires[0].connectTo(subCircuits[65].inputWires, 1);
		subCircuits[63].outputWires[0].connectTo(subCircuits[66].inputWires, 0);
		subCircuits[62].outputWires[0].connectTo(subCircuits[66].inputWires, 1);
		subCircuits[65].outputWires[0].connectTo(subCircuits[67].inputWires, 0);
		subCircuits[12].outputWires[0].connectTo(subCircuits[67].inputWires, 1);
		subCircuits[58].outputWires[0].connectTo(subCircuits[68].inputWires, 0);
		subCircuits[14].outputWires[0].connectTo(subCircuits[68].inputWires, 1);
		subCircuits[54].outputWires[0].connectTo(subCircuits[69].inputWires, 0);
		inputWires[0].connectTo(subCircuits[69].inputWires, 1);
		subCircuits[64].outputWires[0].connectTo(subCircuits[70].inputWires, 0);
		subCircuits[20].outputWires[0].connectTo(subCircuits[70].inputWires, 1);
		subCircuits[61].outputWires[0].connectTo(subCircuits[71].inputWires, 0);
		subCircuits[5].outputWires[0].connectTo(subCircuits[71].inputWires, 1);
		subCircuits[50].outputWires[0].connectTo(subCircuits[72].inputWires, 0);
		subCircuits[17].outputWires[0].connectTo(subCircuits[72].inputWires, 1);
		subCircuits[63].outputWires[0].connectTo(subCircuits[73].inputWires, 0);
		subCircuits[16].outputWires[0].connectTo(subCircuits[73].inputWires, 1);
		subCircuits[66].outputWires[0].connectTo(subCircuits[74].inputWires, 0);
		subCircuits[18].outputWires[0].connectTo(subCircuits[74].inputWires, 1);
		subCircuits[62].outputWires[0].connectTo(subCircuits[75].inputWires, 0);
		subCircuits[15].outputWires[0].connectTo(subCircuits[75].inputWires, 1);
		subCircuits[65].outputWires[0].connectTo(subCircuits[76].inputWires, 0);
		subCircuits[7].outputWires[0].connectTo(subCircuits[76].inputWires, 1);
		subCircuits[58].outputWires[0].connectTo(subCircuits[77].inputWires, 0);
		subCircuits[10].outputWires[0].connectTo(subCircuits[77].inputWires, 1);
		subCircuits[54].outputWires[0].connectTo(subCircuits[78].inputWires, 0);
		subCircuits[6].outputWires[0].connectTo(subCircuits[78].inputWires, 1);
		subCircuits[64].outputWires[0].connectTo(subCircuits[79].inputWires, 0);
		subCircuits[1].outputWires[0].connectTo(subCircuits[79].inputWires, 1);
		subCircuits[61].outputWires[0].connectTo(subCircuits[80].inputWires, 0);
		subCircuits[9].outputWires[0].connectTo(subCircuits[80].inputWires, 1);
		subCircuits[50].outputWires[0].connectTo(subCircuits[81].inputWires, 0);
		subCircuits[8].outputWires[0].connectTo(subCircuits[81].inputWires, 1);
		subCircuits[63].outputWires[0].connectTo(subCircuits[82].inputWires, 0);
		subCircuits[2].outputWires[0].connectTo(subCircuits[82].inputWires, 1);
		subCircuits[66].outputWires[0].connectTo(subCircuits[83].inputWires, 0);
		subCircuits[0].outputWires[0].connectTo(subCircuits[83].inputWires, 1);
		subCircuits[62].outputWires[0].connectTo(subCircuits[84].inputWires, 0);
		subCircuits[3].outputWires[0].connectTo(subCircuits[84].inputWires, 1);
		subCircuits[82].outputWires[0].connectTo(subCircuits[85].inputWires, 0);
		subCircuits[83].outputWires[0].connectTo(subCircuits[85].inputWires, 1);
		subCircuits[77].outputWires[0].connectTo(subCircuits[86].inputWires, 0);
		subCircuits[78].outputWires[0].connectTo(subCircuits[86].inputWires, 1);
		subCircuits[72].outputWires[0].connectTo(subCircuits[87].inputWires, 0);
		subCircuits[80].outputWires[0].connectTo(subCircuits[87].inputWires, 1);
		subCircuits[76].outputWires[0].connectTo(subCircuits[88].inputWires, 0);
		subCircuits[77].outputWires[0].connectTo(subCircuits[88].inputWires, 1);
		subCircuits[69].outputWires[0].connectTo(subCircuits[89].inputWires, 0);
		subCircuits[79].outputWires[0].connectTo(subCircuits[89].inputWires, 1);
		subCircuits[69].outputWires[0].connectTo(subCircuits[90].inputWires, 0);
		subCircuits[72].outputWires[0].connectTo(subCircuits[90].inputWires, 1);
		subCircuits[74].outputWires[0].connectTo(subCircuits[91].inputWires, 0);
		subCircuits[75].outputWires[0].connectTo(subCircuits[91].inputWires, 1);
		subCircuits[67].outputWires[0].connectTo(subCircuits[92].inputWires, 0);
		subCircuits[70].outputWires[0].connectTo(subCircuits[92].inputWires, 1);
		subCircuits[73].outputWires[0].connectTo(subCircuits[93].inputWires, 0);
		subCircuits[74].outputWires[0].connectTo(subCircuits[93].inputWires, 1);
		subCircuits[83].outputWires[0].connectTo(subCircuits[94].inputWires, 0);
		subCircuits[84].outputWires[0].connectTo(subCircuits[94].inputWires, 1);
		subCircuits[79].outputWires[0].connectTo(subCircuits[95].inputWires, 0);
		subCircuits[87].outputWires[0].connectTo(subCircuits[95].inputWires, 1);
		subCircuits[89].outputWires[0].connectTo(subCircuits[96].inputWires, 0);
		subCircuits[92].outputWires[0].connectTo(subCircuits[96].inputWires, 1);
		subCircuits[71].outputWires[0].connectTo(subCircuits[97].inputWires, 0);
		subCircuits[85].outputWires[0].connectTo(subCircuits[97].inputWires, 1);
		subCircuits[70].outputWires[0].connectTo(subCircuits[98].inputWires, 0);
		subCircuits[93].outputWires[0].connectTo(subCircuits[98].inputWires, 1);
		subCircuits[85].outputWires[0].connectTo(subCircuits[99].inputWires, 0);
		subCircuits[96].outputWires[0].connectTo(subCircuits[99].inputWires, 1);
		subCircuits[81].outputWires[0].connectTo(subCircuits[100].inputWires, 0);
		subCircuits[96].outputWires[0].connectTo(subCircuits[100].inputWires, 1);
		subCircuits[91].outputWires[0].connectTo(subCircuits[101].inputWires, 0);
		subCircuits[97].outputWires[0].connectTo(subCircuits[101].inputWires, 1);
		subCircuits[88].outputWires[0].connectTo(subCircuits[102].inputWires, 0);
		subCircuits[97].outputWires[0].connectTo(subCircuits[102].inputWires, 1);
		subCircuits[71].outputWires[0].connectTo(subCircuits[103].inputWires, 0);
		subCircuits[98].outputWires[0].connectTo(subCircuits[103].inputWires, 1);
		subCircuits[100].outputWires[0].connectTo(subCircuits[104].inputWires, 0);
		subCircuits[101].outputWires[0].connectTo(subCircuits[104].inputWires, 1);
		subCircuits[68].outputWires[0].connectTo(subCircuits[105].inputWires, 0);
		subCircuits[102].outputWires[0].connectTo(subCircuits[105].inputWires, 1);
		subCircuits[98].outputWires[0].connectTo(subCircuits[106].inputWires, 0);
		subCircuits[102].outputWires[0].connectTo(subCircuits[106].inputWires, 1);
		subCircuits[95].outputWires[0].connectTo(subCircuits[107].inputWires, 0);
		subCircuits[101].outputWires[0].connectTo(subCircuits[107].inputWires, 1);
		subCircuits[87].outputWires[0].connectTo(subCircuits[108].inputWires, 0);
		subCircuits[99].outputWires[0].connectTo(subCircuits[108].inputWires, 1);
		subCircuits[103].outputWires[0].connectTo(subCircuits[109].inputWires, 0);
		subCircuits[104].outputWires[0].connectTo(subCircuits[109].inputWires, 1);
		subCircuits[92].outputWires[0].connectTo(subCircuits[110].inputWires, 0);
		subCircuits[105].outputWires[0].connectTo(subCircuits[110].inputWires, 1);
		subCircuits[90].outputWires[0].connectTo(subCircuits[111].inputWires, 0);
		subCircuits[105].outputWires[0].connectTo(subCircuits[111].inputWires, 1);
		subCircuits[86].outputWires[0].connectTo(subCircuits[112].inputWires, 0);
		subCircuits[104].outputWires[0].connectTo(subCircuits[112].inputWires, 1);
		subCircuits[103].outputWires[0].connectTo(subCircuits[113].inputWires, 0);
		subCircuits[110].outputWires[0].connectTo(subCircuits[113].inputWires, 1);
		subCircuits[94].outputWires[0].connectTo(subCircuits[114].inputWires, 0);
		subCircuits[109].outputWires[0].connectTo(subCircuits[114].inputWires, 1);
	}

	protected void defineOutputWires() {
		outputWires[7] = subCircuits[106].outputWires[0];
		outputWires[5] = subCircuits[114].outputWires[0];
		outputWires[6] = subCircuits[113].outputWires[0];
		outputWires[0] = subCircuits[108].outputWires[0];
		outputWires[2] = subCircuits[112].outputWires[0];
		outputWires[1] = subCircuits[107].outputWires[0];
		outputWires[4] = subCircuits[110].outputWires[0];
		outputWires[3] = subCircuits[111].outputWires[0];
	}
}