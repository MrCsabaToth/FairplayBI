// Alice.java - Alice's part of the 2-party SFE protocol. 
// Copyright (C) 2004 Dahlia Malkhi, Yaron Sella.
// Copyright (C) 2012 Csaba Toth, Wei Xie
// See full Copyright license terms in file ../GPL.txt

package SFE.BOAL;

import SFE.Compiler.*;
import SFE.GUI.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.*;

/** Alice
 *  @author: Dahlia Malkhi and Yaron Sella
 */

//---------------------------------------------------------------

/**
 * Alice
 * @author  : Dahlia Malkhi and Yaron Sella
 */
public class Alice {
	private static final Logger logger = Logger.getLogger(Alice.class);
	private static final int num_of_circuits = 10;	// Fairplay: 2, FairplayFP: 10
	Formatter f = null;

	//---------------------------------------------------------------

	/**
	 * Alice Constructor
	 *
	 * @param circuit_filename - circuit filename
	 * @param fmt_filename - format filename
	 * @param hostname - where to find Bob
	 * @param num_iterations - how many iterations to do
	 * @param stats - print run statistics in the end
	 */
	public Alice(String circuit_filename, String fmt_filename, String sseed,
			String hostname, int num_iterations, boolean stats, InputStreamReader isr,
			BufferedWriter bw) throws Exception {
		BufferedReader br = new BufferedReader(isr);
		int i, j;
		int ot_type;
		int cc_num;
		int[] bob_io_size = new int[2];
		Parser p = null;
		OT ot;
		Socket sock = null;
		ObjectInputStream fromBob = null;
		ObjectOutputStream toBob = null;
		long sum1=0, sum2=0, sum3=0, sum4=0;

		// Preparations
		MyUtil.init(sseed);

		// Connect to Bob
		try {
			sock = new Socket(hostname, 3496);
			fromBob = new ObjectInputStream(sock.getInputStream());
			toBob = new ObjectOutputStream(sock.getOutputStream());
		} catch (UnknownHostException e) {
			System.err.println("Alice: Don't know host " + hostname);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println(
					"Alice: Couldn't get I/O for the connection with Bob");
			System.exit(-1);
		}

		MyUtil.sendInt(toBob, (num_iterations << 8) + num_of_circuits, true);
		ot_type = MyUtil.receiveInt(fromBob);
		ot = new OT(ot_type);

		Vector<byte[]> vEncPayload = new Vector<byte[]> (num_of_circuits);
		byte[] EncPayload;
		byte[] SecPayload;
		byte[] InpPayload;
		byte[] OutPayload;
		int EncPayloadSize=0;
		int SecPayloadSize=0;
		int InpPayloadSize;
		int OutPayloadSize;
		Circuit c;

		for (i = 0; i < num_iterations; i++) {

			MyUtil.deltaTime (true);

			logger.info("Iteration no = " + i);

			// Parse the IOformat file and prepare the inputs
			try {
				// Preparations
				FileReader fmt = new FileReader(fmt_filename);
				StreamTokenizer fmtst = new StreamTokenizer(fmt);

				// IO Formatting
				f = new Formatter(fmtst);
				f.parse();

				// Cleanup
				fmt.close();
			} catch (IOException e) {
				logger.error("Alice: cannot open/close " + fmt_filename + " - " +
						e.getMessage());
			} catch (FormatterError e) {
				logger.error("Alice: parsing " + fmt_filename + " failed.");
			} catch (Exception e) {
				logger.error("Alice: exception - " + e.getMessage());
			}

			// Parse the circuit file
			try {
				// Preparations
				FileReader fr = new FileReader(circuit_filename);
				StreamTokenizer st = new StreamTokenizer(fr);

				// Parsing
				p = new Parser(st);
				p.parse();

				// Cleanup
				fr.close();
			} catch (IOException e) {
				logger.error("Alice: cannot open/close " + circuit_filename +
						" - " + e.getMessage());
				System.exit(1);
			} catch (Exception e) {
				logger.error("Alice: exception - " + e.getMessage());
				System.exit(1);
			}

			c = p.getCircuit();        // Obtain a circuit object
			f.markIO (c, bob_io_size); // Mark its inputs & outputs
			InpPayloadSize = bob_io_size[0];
			OutPayloadSize = bob_io_size[1];
			c.generateEncCircuit();    // Encrypt it (dummy)
			EncPayloadSize = c.cmeasureEncPayload();
			SecPayloadSize = c.cmeasureSecPayload();

			sum1 += MyUtil.deltaTime (false);

			// Run the SFE protocol
			// ====================

			// Receive encrypted circuits payload from Bob
			for (j = 0; j < num_of_circuits ; j++) {
				EncPayload = new byte[EncPayloadSize];
				MyUtil.receiveBytes (fromBob, EncPayload, EncPayloadSize);
				vEncPayload.add (EncPayload);
			}

			// Choose a circuit to evaluate and tell Bob
			cc_num = MyUtil.randomByte() ;
			if (cc_num < 0) cc_num += 256 ;
			cc_num = cc_num % num_of_circuits;
			logger.debug("Alice: chose circuit number " + cc_num + " for evaluation");
			MyUtil.sendInt(toBob, cc_num, true);

			// Receive encrypted circuits with secrets
			// (except the chosen one) from Bob
			for (j = 0; j < num_of_circuits ; j++) {
				if (j != cc_num) {
					EncPayload = vEncPayload.elementAt(j);
					c.cinjectEncPayload (EncPayload);
					SecPayload = new byte[SecPayloadSize];
					MyUtil.receiveBytes (fromBob, SecPayload, SecPayloadSize);
					c.cinjectSecPayload (SecPayload);
					if (!c.isCorrect()) {
						logger.error("Alice: caught Bob cheating!");
						System.exit(1);
					}
				}
			}
			
			// UPDATED by Thomas Schneider
			c.cinjectEncPayload(vEncPayload.elementAt(cc_num));
			
			// Receive Bob's inputs for the chosen circuit and place them in it
			InpPayload = new byte[InpPayloadSize];
			MyUtil.receiveBytes (fromBob, InpPayload, InpPayloadSize);
			f.finjectInpPayload (c, InpPayload, false);

			sum2 += MyUtil.deltaTime (false);

			// Read Alice's inputs 
			f.getAliceInput(c, br); 

			// OTs - Alice is the chooser + 
			// place Alice's inputs in the chosen circuit
			OT.ChooserOTs(c, f, ot, toBob, fromBob);

			sum3 += MyUtil.deltaTime (false);

			c.evalGarbledCircuit(true, false);
			logger.info("circuit evaluation completed!");

			// Send Bob his garbled results
			OutPayload = f.fextractOutPayload (c, OutPayloadSize, false);
			MyUtil.sendBytes (toBob, OutPayload, true);

			// print Alice's output
			f.getAliceOutput(c, bw); 

			sum4 += MyUtil.deltaTime (false);
		}

		if (stats) {
			System.out.println("Initial calculations   [sum1] = " + (float)sum1/1000.0);
			System.out.println("Circuits communication [sum2] = " + (float)sum2/1000.0);
			System.out.println("Oblivious Transfers    [sum3] = " + (float)sum3/1000.0);
			System.out.println("Evaluation & output    [sum4] = " + (float)sum4/1000.0);
		}

		// Cleanup
		toBob.close();
		fromBob.close();
		sock.close();
	}

	//---------------------------------------------------------------

	/**
	 * This routine is for debugging socket communication
	 */
	public void pongping(ObjectOutputStream toBob, ObjectInputStream fromBob,
			int a) {
		System.out.println("Sending " + a + " to Bob");
		MyUtil.sendInt(toBob, a, true);
		System.out.println("Attempting to read num from Bob");

		int u = MyUtil.receiveInt(fromBob);
		System.out.println("Got Int from Bob " + u);
	}

	//---------------------------------------------------------------

	public static void aliceUsage(int err_code) {
		System.out.println("Alice activation error code = " + err_code);
		System.out.println("Usage: java SFE.BOAL.Alice -e|-c[n]|-r[n] <filename> <seed> <hostname> <num_iterations>");
		System.out.println(" -e = EDIT, -c = COMPILE, -r = RUN, [n] = NoOpt)");
		System.out.println(" (<seed> <hostname>, <num_iterations> expected only with -r[n])");
		System.out.println(" Examples: 1. java SFE.Alice -c Maximum.txt");
		System.out.println("           2. java SFE.Alice -r Maximum.txt Xb@&5H1m!p sands 100");
		System.exit(1);
	}

	//---------------------------------------------------------------

	/**
	 * Main program for activating Alice
	 *
	 * @param args - command line arguments.
	 *               args[0] should be -e, -c[n] or -r[n]
	 *               args[1] should be filename
	 *               args[2] should be seed for RNG
	 *               args[3] should be hostname (only with -r[n])
	 *               args[4] should be number of iterations (only with -r[n])
	 */
	public static void main(String[] args) throws Exception {
		String filename;
		String circ_fname;
		String fmt_fname;
		String input_fname;
		String output_fname;
		int num_iterations;
		boolean edit = false;
		boolean compile = false;
		boolean run_stats = false;
		boolean run = false;
		boolean opt = false;
		InputStreamReader isr = null;
		BufferedWriter bw = null;

		// Load logging configuration file
		PropertyConfigurator.configure(MyUtil.pathFile("SFE_logcfg.lcf"));

		// Various legality tests on command line parameters

		if ((args.length < 2) || (args.length > 5))
			aliceUsage(1);

		edit = args[0].equals("-e");
		compile = args[0].equals("-c") || args[0].equals("-cn");
		run_stats = args[0].equals("-s") ;
		run = run_stats || args[0].equals("-r") || args[0].equals("-rn");
		opt = args[0].equals("-r") || args[0].equals("-c") || args[0].equals("-s");

		if (!edit && !compile && !run && !run_stats)
			aliceUsage(2);

		if (run && (args.length < 4))
			aliceUsage(3);

		filename = MyUtil.pathFile(new String(args[1]));
		if (opt) {
			circ_fname = new String(filename + ".Opt.circuit");
			fmt_fname = new String(filename + ".Opt.fmt");
		}
		else {
			circ_fname = new String(filename + ".NoOpt.circuit");
			fmt_fname = new String(filename + ".NoOpt.fmt");
		}

		input_fname = new String(filename + ".Alice.input");
		{
			File f = new File(input_fname);

			if (f.exists()) {
				System.out.println("Alice's input will be read from file: " + input_fname + " instead of stdin");
				isr = new FileReader(f);

				if (run) {
					output_fname = new String(filename + ".Alice.output");
					File of = new File(output_fname);
					if (of.exists())
						of.delete();
					of.createNewFile();
					bw = new BufferedWriter(new FileWriter(of));
				}
			} else {
				System.out.println("Alice's input will be read from stdin, no input file found");
				isr = new InputStreamReader(System.in);
			}
		}

		if (compile) {
			File f = new File(filename);

			if (!f.exists()) {
				System.out.println("Input program file " + filename + " not found");
				aliceUsage(4);
			}
		}

		if (run) {
			File f1 = new File(circ_fname);
			File f2 = new File(fmt_fname);

			if (!f1.exists() || !f2.exists()) {
				if (!f1.exists())
					System.out.println("Input circuit file " + circ_fname + " not found");
				if (!f2.exists())
					System.out.println("Input format file " + fmt_fname + " not found");
				aliceUsage(5);
			}
		}

		// Do something (finally...)

		if (edit) {
			GUIMain.guiMain(filename);
		}

		if (compile) {
			SFECompiler.compile(filename, opt);
		}

		if (run) {
			System.out.println("Running Alice...");
			try {
				if (args.length < 5)
					num_iterations = 1 ;
				else
					num_iterations = Integer.parseInt(args[4]);
				/*Alice a = */new Alice(circ_fname, fmt_fname, args[2], args[3], num_iterations, run_stats, isr, bw);
			} catch (Exception e) {
				System.out.println("Alice's main err: " + e.getMessage());
				e.printStackTrace();
			}
			if (isr != null) isr.close();
			if (bw != null) bw.close();
		}
	}
}
