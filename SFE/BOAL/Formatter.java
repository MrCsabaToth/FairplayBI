// Formatter.java - Format files handling for I/O.
// Copyright (C) 2004 Dahlia Malkhi, Yaron Sella. 
// See full copyright license terms in file ../GPL.txt

package SFE.BOAL;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.apache.log4j.*;


/** Formatter

The format file describes the I/O with Alice and Bob in the following format:

<p>
Input line:
<br>
Alice/Bob input type "prompt-string" [ line-num1 .. line-numk ]
<p>
This line has the following interpretation:
First, it designated this as Alice or Bob's input.
The 'type' specifies the type of input, e.g., integer.
The input will be provided by prompting Alice using "prompt-string".
The input value is internally translated into k bits, LSB to MSB.
These bits are given as input bits to gates in the designated lines.

<p>
Output line:
<br>
Alice/Bob output type "output-prefix" [ line-num1 .. line-numk ]
<p>
This line has the following interpretation:
First, it designated this as Alice or Bob's output.
The 'type' specifies the type of output, e.g., integer.
The output will be gathered from k output gates, LSB to MSB, in the designated
output gate lines.
These output bits are translated into a value of the designated type, and
printed with the designated "output-prefix"
<p>
Example file is:
<br>
<code>
        Alice input integer "Enter # employees (up to 3): " [ 1 2 ]
        Bob input integer "Enter # employees (up to 3): " [ 3 4 ]
        Alice output integer "Total # employees: " [ 100 101 102 ]
        Bob output integer "Total # employees: " [ 100 101 102 ]
</code>
This indicates that Alice should be prompted for an integer no greater than 3,
which appears as input wires 1 and 2. Similarly, Bob should be prompted for his
integer value for input wires 3 and 4. The output for Alice is an integer built
off of output wires 100,101,102; likewise is Bob's output.


@author: Dahlia Malkhi and Yaron Sella
 */
/**
 * Formatter The format file describes the I/O with Alice and Bob in the following format: <p> Input line: <br> Alice/Bob input type "prompt-string" [ line-num1 .. line-numk ] <p> This line has the following interpretation: First, it designated this as Alice or Bob's input. The 'type' specifies the type of input, e.g., integer. The input will be provided by prompting Alice using "prompt-string". The input value is internally translated into k bits, LSB to MSB. These bits are given as input bits to gates in the designated lines. <p> Output line: <br> Alice/Bob output type "output-prefix" [ line-num1 .. line-numk ] <p> This line has the following interpretation: First, it designated this as Alice or Bob's output. The 'type' specifies the type of output, e.g., integer. The output will be gathered from k output gates, LSB to MSB, in the designated output gate lines. These output bits are translated into a value of the designated type, and printed with the designated "output-prefix" <p> Example file is: <br> <code> Alice input integer "Enter # employees (up to 3): " [ 1 2 ] Bob input integer "Enter # employees (up to 3): " [ 3 4 ] Alice output integer "Total # employees: " [ 100 101 102 ] Bob output integer "Total # employees: " [ 100 101 102 ] </code> This indicates that Alice should be prompted for an integer no greater than 3, which appears as input wires 1 and 2. Similarly, Bob should be prompted for his integer value for input wires 3 and 4. The output for Alice is an integer built off of output wires 100,101,102; likewise is Bob's output.
 * @author  : Dahlia Malkhi and Yaron Sella
 */
public class Formatter implements Serializable {
	private static final long serialVersionUID = 7755453784445270903L;

	protected static String newline = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(Formatter.class);
    protected static final int EOF_INDICATOR = -2; // Input file ended
    protected static final int WORD_INDICATOR = -1; // Token read was a WORD
    protected StreamTokenizer st;
    protected String s = null; // Stores WORD tokens
    protected boolean is_alice_line = false; // indicates Alice/Bob line
    protected IO curIO = null; // IO object for current line
    protected Vector<IO> FMT = new Vector<IO>(10, 10); // keeps all IO information

    public Formatter(StreamTokenizer st) {
        this.st = st;

        // The chars '[' appears in input files to improve
        // readability for humans, but the Parser simply ignores it.
        // ']' on the other hand, is parsed: it indicates the end of a list.
        st.whitespaceChars('[', '[');
        st.wordChars(']', ']');

        // parse strings between '"' as one token
        st.quoteChar('"');
    }

    //---------------------------------------------------------------

    /**
     * This routine marks all the input/output gates of a circuit as
     * belonging to either Alice or Bob. 
     *
     * @param c a gate-level circuit
     */
    public void markIO (Circuit c, int[] tsize) {
        tsize[0] = 0;
        tsize[1] = 0;

        // scan through all format object for input objects
        for (int i = 0; i < FMT.size(); i++) {

            IO io = FMT.elementAt(i);

            // mark all input/output wires
            for (int j = 0; j < io.getNLines(); j++) {
                int line_num = io.getLinenum(j);
                Gate g = c.getGate(line_num);
                g.markAliceBob (io.isAlice());
                if (g.isBobInput())  tsize[0] += g.gmeasureInpPayload();
                if (g.isBobOutput()) tsize[1] += g.gmeasureOutPayload();
		    }
		}
    }

    //---------------------------------------------------------------
 
    public void getBobInput(Circuit c, BufferedReader br) {
        getInput(c, false, br);
    }

    public void getAliceInput(Circuit c, BufferedReader br) {
        getInput(c, true, br);
    }

    //---------------------------------------------------------------

    /**
     * This routine prompts for Alice or Bob inputs, and sets the
     * corresponding wires binary values.
     *
     * @param c a gate-level circuit
     * @param is_alice indicates whether this input is for Alice or Bob
     */
    public void getInput(Circuit c, boolean is_alice, BufferedReader br) {
        String inpline = null;

        // Scan through all format objects 
        for (int i = 0; i < FMT.size(); i++) {
        	IO io = FMT.elementAt(i);
            logger.debug("pulled element from FMT");

            // Verify it's an input object of the approriate player (i.e., Alice/Bob).
            if ((!io.isInput()) || (is_alice != io.isAlice())) {
                continue;
            }

            System.out.print(io.getPrefix());
            System.out.flush();

            try {
                inpline = br.readLine();
            } catch (Exception e) {
                logger.error("Formatter: readLine exception " + e.getMessage());

                return;
            }

            StringTokenizer st = new StringTokenizer(inpline);
            BigInteger val = new BigInteger(st.nextToken());

            logger.debug("input is: " + val);

            // set all specified input bits, one by one
            for (int j = 0; j < io.getNLines(); j++) {
                // this is the j'th bit value
                int b = val.testBit(j) ? 1 : 0;

                // this bit should store as input at line_num
                int line_num = io.getLinenum(j);

                // this is the gate of line_num
                Gate g = c.getGate(line_num);

                g.setValue(b); // set the input value

                logger.debug("input bit " + j + " is: " + b);
            }
        }
    }

    //---------------------------------------------------------------
 
    public void getBobOutput(Circuit c, BufferedWriter bw) {
        getOutput(c, false, bw);
    }

    public void getAliceOutput(Circuit c, BufferedWriter bw) {
        getOutput(c, true, bw);
    }

    //---------------------------------------------------------------

    /**
     * This routine collects all output results from the circuit for
     * either Alice or Bob, and prints them out (the circuit must be 
     * already evaluated).
     *
     * @param c a gate-level circuit
     * @param is_alice indicates whether this output is for Alice or Bob
     */
    public void getOutput(Circuit c, boolean is_alice, BufferedWriter bw) {
        // Scan through all format objects 
        for (int i = 0; i < FMT.size(); i++) {
            IO io = FMT.elementAt(i);

            // Verify it's an output object of the approriate player (i.e., Alice/Bob).
            if (io.isInput() || (is_alice != io.isAlice())) {
                continue;
            }

			try {
				if (bw != null)
					bw.write(newline);
				else
					System.out.println();

				BigInteger value = new BigInteger("0");
	            int numBits = io.getNLines();
	            // collect all specified output bits, one by one
	            for (int j = 0; j < numBits; j++) {
	                // this bit belongs to output of j'th line_num
	                int line_num = io.getLinenum(j);
	
	                // this is the value of the gate at line_num
	                int bit = c.getGate(line_num).getValue();
	                logger.debug("output bit " + line_num + " is: " + bit);
	
	                if (bit > 0)
	                	value = value.setBit(j);
	                else
	                	value = value.clearBit(j);
	            }
	
	            BigInteger bref = BigInteger.ONE.shiftLeft(numBits - 1);
	            if (value.compareTo(bref) > 0) {	// Negative number in two's complement representation => correct it
	            	value = value.subtract(bref.shiftLeft(1));
	            }
	        	String outputStr = io.getPrefix() + value;
	            if (bw != null) {
					bw.write(outputStr);
					bw.write(newline);
	            } else {
	            	System.out.println(outputStr);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    //---------------------------------------------------------------

    /**
     * Extract input payload from an encrypted circuit (for the
     * purpose of communicating it in minimal overhead).
     *
     * @param c a gate-level circuit
     * @param total_size total size of the circuit's payload in bytes.
     * @param alice_inputs true for alice, false for bob
     * @return a byte array with the relevant information.
     */
    public byte[] fextractInpPayload (Circuit c, int total_size, boolean alice_inputs) {
        int i, j, j1, k = 0, line_num ;
        byte[] small_byte_arr;
        byte[] big_byte_arr = new byte[total_size];

        for (i = 0; i < FMT.size(); i++) {
            IO io = FMT.elementAt(i);

            // Verify it's a relevant i/o object
            if (!io.isInput()) continue;
            if (io.isAlice() != alice_inputs) continue;

            // Scan all specified input bits, one by one,
            // Extract payload data from relevant gates,
            // and store them in the result array
            for (j = 0; j < io.getNLines(); j++) {
                // This bit belongs to output of j'th line_num
                line_num = io.getLinenum(j);
                Gate g = c.getGate(line_num);
                small_byte_arr = g.gextractInpPayload();
                for (j1 = 0; j1 < small_byte_arr.length ; j1++, k++)
                   big_byte_arr[k] = small_byte_arr[j1];
            }
        }

        return (big_byte_arr);
    }

    //---------------------------------------------------------------

    /**
     * Inject input payload into a circuit.
     *
     * @param c a gate-level circuit
     * @param info byte array with data to inject
     * @param alice_inputs true for alice, false for bob
     */
    public void finjectInpPayload (Circuit c, byte[] info, boolean alice_inputs) {
        int i, j, k, line_num, consumed_bytes ;

        for (i = k = 0; i < FMT.size(); i++) {
            IO io = FMT.elementAt(i);

            // Verify it's a relevant i/o object
            if (!io.isInput()) continue;
            if (io.isAlice() != alice_inputs) continue;

            // Scan all specified input bits, one by one,
            // and inject the payload data
            for (j = 0; j < io.getNLines(); j++) {
                // This bit belongs to output of j'th line_num
                line_num = io.getLinenum(j);
                Gate g = c.getGate(line_num);
                consumed_bytes = g.ginjectInpPayload(info, k);
                k += consumed_bytes;
            }
        }
    }

    //---------------------------------------------------------------

    /**
     * Extract output payload from an encrypted circuit (for the
     * purpose of communicating it in minimal overhead).
     *
     * @param c a gate-level circuit
     * @param total_size total size of the circuit's payload in bytes.
     * @param alice_inputs true for alice, false for bob
     * @return a byte array with the relevant information.
     */
    public byte[] fextractOutPayload (Circuit c, int total_size, boolean alice_inputs) {
        int i, j, j1, k = 0, line_num ;
        byte[] small_byte_arr;
        byte[] big_byte_arr = new byte[total_size];

        for (i = 0; i < FMT.size(); i++) {
            IO io = FMT.elementAt(i);

            // Verify it's a relevant i/o object
            if (io.isInput()) continue;
            if (io.isAlice() != alice_inputs) continue;

            // Scan all specified input bits, one by one,
            // Extract payload data from relevant gates,
            // and store them in the result array
            for (j = 0; j < io.getNLines(); j++) {
                // This bit belongs to output of j'th line_num
                line_num = io.getLinenum(j);
                Gate g = c.getGate(line_num);
                small_byte_arr = g.gextractOutPayload();
                for (j1 = 0; j1 < small_byte_arr.length ; j1++, k++)
                   big_byte_arr[k] = small_byte_arr[j1];
            }
        }

        return (big_byte_arr);
    }

    //---------------------------------------------------------------

    /**
     * Inject output payload into a circuit.
     *
     * @param c a gate-level circuit
     * @param info byte array with data to inject
     * @param alice_outputs true for alice, false for bob
     */
    public void finjectOutPayload (Circuit c, byte[] info, boolean alice_outputs) {
        int i, j, k, line_num, consumed_bytes ;

        for (i = k = 0; i < FMT.size(); i++) {
            IO io = FMT.elementAt(i);

            // Verify it's a relevant i/o object
            if (io.isInput()) continue;
            if (io.isAlice() != alice_outputs) continue;

            // Scan all specified input bits, one by one,
            // and inject the payload data
            for (j = 0; j < io.getNLines(); j++) {
                // This bit belongs to output of j'th line_num
                line_num = io.getLinenum(j);
                Gate g = c.getGate(line_num);
                consumed_bytes = g.ginjectOutPayload(info, k);
                k += consumed_bytes;
            }
        }
    }

    //---------------------------------------------------------------

    /**
     * The parse() method is the parser of the formatter input file.
     * It is called at the beginning of the IO format definition file,
     * and it consumes the entire input file while building an
     * internal representation of the I/O format
     *
     * @exception - FormatterError.
     */
    public void parse() throws FormatterError {
        try {
            while (parseAliceBob())
                parseLineName();

            st = null;
        } catch (Exception e) {
            logger.error("parse: exception " + e.getMessage());

            return;
        }
    }

    //---------------------------------------------------------------

    /**
     * Parse line names. Current options: 'input' or 'output'.
     *
     * @exception - FormatterError (bad line name)
     */
    protected void parseLineName() throws FormatterError {
        int rc = parseToken(false);
        boolean inp_line = s.equals("input");
        boolean out_line = s.equals("output");

        if ((rc != WORD_INDICATOR) || (!inp_line && !out_line)) {
            throw new FormatterError("parseLineName : Bad line name");
        }

        if (inp_line) {
            logger.info("parseLineName: input line ");
            curIO.setInputFlag(inp_line);
        }

        if (out_line) {
            logger.info("parseLineName: output line ");
            curIO.setInputFlag(inp_line);
        }

        parseFormat();
    }

    //---------------------------------------------------------------

    /**
     * Parse an input format line (after the keyword input).
     *
     * @exception - FormatterError.
     */
    private void parseFormat() throws FormatterError {
        parseString("integer"); // currently the only allowed type

        // eat up the prefix string
        int status = parseToken(false);

        if (status != WORD_INDICATOR) {
            throw new FormatterError("parseFormat : expecting IO prefix ");
        }

        curIO.setPrefix(s);
        logger.info("parseFormat: prefix is '" + s + "'");

        parseNumbers(); // each up the list of line number

        FMT.add(curIO); // add this (completed) IO object to list of IOs
        logger.info("parseFormat: line done.");
    }

    //---------------------------------------------------------------

    /**
     * Parse the line-numbers that are associated with this IO
     *
     * @exception - FormatterError (bad input)
     */
    protected void parseNumbers() throws FormatterError {
        while (parseLineNum())
            ;
    }

    //---------------------------------------------------------------

    /**
     * Parse one line-number
     *
     * @return true if end of list not reached yet
     * @exception - FormatterError (bad number)
     */
    private boolean parseLineNum() throws FormatterError {
        int val = parseToken(false);

        if (val == WORD_INDICATOR) {
            if (s.equals("]")) {
                return false;
            } else {
                throw new FormatterError("parseNumbers : bad word " + s);
            }
        } else {
            // Store this line-number in the current IO object
            curIO.addLinenum(val);
            logger.info("parseLineNum: line number is " + val);
        }

        return true;
    }

    //---------------------------------------------------------------

    /**
     * Parse Alice/Bob.
     * The only method that can accept eof.
     *
     * @return true if parsing should continue (a new line encountered).
     * @exception - FormatterError (bad line number)
     */
    private boolean parseAliceBob() throws FormatterError {
        int ln = parseToken(true);

        if (ln == EOF_INDICATOR) {
            return false;
        }

        if (ln != WORD_INDICATOR) {
            throw new FormatterError("parseAliceBob : Bad first word");
        }

        is_alice_line = s.equals("Alice");

        if (!is_alice_line && !s.equals("Bob")) {
            throw new FormatterError("parseAliceBob : Bad first word");
        }

        logger.info("parseAliceBob: " + s);

        // allocate new IO object for Alice/Bob
        curIO = new IO(is_alice_line);

        return (true);
    }

    //---------------------------------------------------------------

    /**
     * Parse a single token. The only method that activates
     * the StreamTokenizer.
     *
     * @param eof_ok - true if OK to see eof now.
     * @return value of numeric token, or indication of WORD token or eof.
     * @exception - FormatterError upon StreamTokenizer/IO problem
     * @side-effect - puts WORD token in string s.
     */
    protected int parseToken(boolean eof_ok) throws FormatterError {
        int status;
        int rc;

        try {
            status = st.nextToken();
        } catch (Exception e) {
            throw new FormatterError("parseToken: st.nextToken failed");
        }

        s = "";

        if (status == StreamTokenizer.TT_EOF) {
            if (!eof_ok) {
                throw new FormatterError("parseToken: unexpected eof");
            }

            rc = EOF_INDICATOR;
        } else if (st.ttype == StreamTokenizer.TT_NUMBER) {
            logger.debug("parseToken: token is number: " + st.nval);
            rc = (int) st.nval;
        } else {
            logger.debug("parseToken: token is string: " + st.sval);
            s = new String(st.sval);
            rc = WORD_INDICATOR;
        }

        logger.debug("parseToken: returning rc = " + rc);

        return (rc);
    }

    //---------------------------------------------------------------

    /**
     * Parse a specific string.
     *
     * @param s1 - string to be parsed.
     * @exception - FormatterError (expected string not found)
     */
    protected void parseString(String s1) throws FormatterError {
        parseToken(false);

        if (!s.equals(s1)) {
            throw new FormatterError("parseString : '" + s1 + "' expected");
        }
    }
}
