FairplayBI
==========

A version of Fairplay software for 2-party SMC (Secure Multi-party Computation) which is _really_ able to handle
arbitrarily long integers. Related links:
- Secure multi-party computation: http://en.wikipedia.org/wiki/Secure_multi-party_computation
- Original project: http://www.cs.huji.ac.il/project/Fairplay/
- FairplayPF has a patch for Fairplay: http://thomaschneider.de/FairplayPF/

Changelist
---
- FairplayFP patch
- All cointainer classes (Vector, Map, Set, etc) was converted to type-safe generics
- Had to add Compiler/InputFormat.java to be able to fully accomplish the type-safe generics
- Converted the internal representation of Int<*> SMC program types to BigInteger instead of Java int
- Changed the party (Alice/Bob) run-time input parsing so it can take in BigIntegers
- It is possible now to place the run-time input into files instead of a console input
- The software searches for the program script relative to the rundir Java environment variable
(that was already used for finding the log4j configuration file)
- The output is able to display negative integer results (original Fairplay couldn't even do that with int)
- The output is able to display BigInteger (arbitrarily long integer) results (regardless of negative or positive)
- Lots of minor fixes and corrections

Effects of changes
---
- The source code gat modern Java conform, safer
- Really able to handle arbitrarily long integers
- Can integrate with other solutions easier (run-time input from file)
- There can be performance penalty for some BigInteger operations compared to int (simple incerement for example)
- The source code got cleaner (converting from and to bit representations: BigInteger natively supports that while
with integer there were bit manipulation/shift operations)

Known limitations
---
- Cannot specify BigInteger constants in the program script. This is because the Tokenizer is based on Java's
StreamTokenizer, which automatically parses the number (regardless you invoke the parseNumber function or not),
and that stores the number in double floating point variable. This leads to precision loss in some cases,
and prevents BigInteger constants only in the program script. Note however, that you can specify BigIntegers as
and input in the standard input / input files for Alice and Bob. 
- End point of for-loop must be constant values

License
---
Please refer to GPL.txt

Authors
---
- Csaba Toth &lt;csaba.toth (at) vanderbilt.edu>
- Wei Xie &lt;wei.xie (at) vanderbilt.edu>
