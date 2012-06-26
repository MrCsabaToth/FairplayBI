This Readme shows how to use the two-party SFE software
Author: Yaron Sella. Date: 28 January 2004
=======================================================


A. Examples for compiling an SFDL program:
------------------------------------------

1. run_bob -c progs/Billionaires.txt
2. run_alice -c progs/Billionaires.txt

(-c for compile,
 progs/Billionaires.txt - SFDL program to compile)

Both commands produce the same two output files: 
1. progs/Billionaires.txt.Opt.circuit (an SHDL circuit)
2. progs/Billionaires.txt.Opt.fmt


B. Example for running Bob (should be first):
---------------------------------------------
run_bob -r progs/Billionaires.txt S&b~n2#m8_Q 4

(-r for run,
 progs/Billionaires.txt - program to run,
 3rd parameter - crazy string for random seed,
 4th parameter - OT type [1-4], 4 is the best one)


C. Example for running Alice (should be second):
------------------------------------------------

run_alice -r progs/Billionaires.txt 5miQ^0s1 humus.cs.huji.ac.il

(-r for run,
 progs/Billionaires.txt - program to run,
 3rd parameter - crazy string for random seed,
 4th parameter - hostname where Bob is)


D. Some general comments:
-------------------------

1. As with regular programs - first compile, then run.
2. Bob & Alice use fixed port (no. 3496) for TCP/IP communication.
