'''
converts a .mec file into a .bmec file
bmec is essentially mec in binary
All entries in a bmec file are 4 byte integers in big-endian byte order.
'''
import struct


def writeRegisterList(rList, bf):
    rList = rList.strip()
    if len(rList)!=0:
        rList = rList.split(',')       
    l = len(rList)
    bf.write(struct.pack("!i", l))
    for i in range(l):
        bf.write(struct.pack("!i", int(rList[i])))

def writeInt(intStr, bf):
    bf.write(struct.pack("!i", int(intStr)))

def convertMECtoBMEC(mecFile, bmecFile):
    print "converting %s to %s ..." % (mecFile, bmecFile)
    mf = open(mecFile, 'r')
    bf = open(bmecFile, 'wb')
    
    #inputsCreator:
    writeRegisterList(mf.readline()[14:], bf)
    #inputsEvaluator:
    writeRegisterList(mf.readline()[16:], bf)
    #outputsCreator:
    writeRegisterList(mf.readline()[15:], bf)
    #outputsEvaluator:
    writeRegisterList(mf.readline()[17:], bf)
    #numberOfRegisters:
    token = mf.readline()[18:].strip()
    writeInt(token, bf)
    #numberOfGates:
    numOfGates = int(mf.readline()[14:].strip())
    bf.write(struct.pack("!i", numOfGates))
    #now the gates
    for i in xrange(numOfGates):
        gate = mf.readline().strip().split(";")
        writeInt(gate[0], bf)
        inps = gate[1].split(',')
        writeInt(inps[0], bf)
        writeInt(inps[1], bf)
        writeInt(gate[2], bf)
        
    mf.close()
    bf.close()
    print "Done."
    
if __name__ == '__main__':
    mecFile = "DBQuery_10_40_10_10_min.mec"
    bmecFile = "DBQuery_10_40_10_10.bmec"
    convertMECtoBMEC(mecFile, bmecFile)