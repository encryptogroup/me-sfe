Warning: This compiler is somewhat awkward to use. You are welcome to contribute improvements.

In general: Having a circuit in the format of the FastGC framework, these tools enable you to 
convert the circuit into the 'mec'-, and 'bmec'-formats used in me-sfe.

There are three major components:
1. Compiler.java
Converts the FastGC circuit into the 'mec' format. But beware that every gate gets a unique ID 
assigned. This is, as stated in the paper, in general not very good. Therefore, move on to 
component two.
2. Optimizer.java
Given a 'mec'-circuit from the compiler, the optimizer identifies the 'working set' (The 
registers needed to run the circuit). 
3. MEC_To_BMEC_Converter.py
Converts a 'mec' file into the binary 'bmec' format. 

Good luck.