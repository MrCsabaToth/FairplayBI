// TimedCommitment.java
//
// class TimedCommitment
//
// A timed commitment that can be gradually made easier to decode.
//
// Created November 20, 2003 by Ori Peleg
//
// Copyright (C) 2012 Csaba Toth, Wei Xie
package SFE.TimedCommitments;

import java.math.BigInteger;

import java.util.BitSet;
import java.util.Vector;


public class TimedCommitment {
	//~ Instance fields --------------------------------------------------------

	// data members
	private final BigInteger    N;
	@SuppressWarnings("unused")
	private final BigInteger    g;
	@SuppressWarnings("unused")
	private final int           n;
	private final int           k;
	@SuppressWarnings("unused")
	private final Vector<BigInteger> W;
	private final BitSet        S;
	private java.util.SortedSet<TimedCommitmentHint> hints = new java.util.TreeSet<TimedCommitmentHint>();

	//~ Constructors -----------------------------------------------------------

	/**
	 * package-visible constructor.
	 *
	 * Sets the initial commitment fields
	 *
	 * @param N the modulo
	 * @param g as per Boneh-Naor's algorithm
	 * @param n security parameter, as per Boneh-Naor's algorithm
	 * @param k security parameter, as per Boneh-Naor's algorithm
	 * @param W as per Boneh-Naor's algorithm: [g^2, g^4, g^16, ..., g^ (2^(2^k))]
	 * @param S the encrypted message, as per Boneh-Naor's algorithm
	 */
	public TimedCommitment(BigInteger N, BigInteger g, int n, int k, Vector<BigInteger> W,
	                       BitSet S) {
		this.N     = N;
		this.g     = g;
		this.n     = n;
		this.k     = k;
		this.W     = W;
		this.S     = S;

		// The one-before-last element in W is the first hint
		final BigInteger hintValue = W.elementAt(W.size() - 2);
		addHint(new TimedCommitmentHint(1, hintValue));
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Learn the new hint, for use when decoding.
	 *
	 * When decoding the best hint available is used.
	 * The timed commitment is created with the first hint already: each
	 * succesive hint, if given according to Pinkas' algorithm's order, cuts
	 * the decoding time in half.
	 */
	public void addHint(TimedCommitmentHint hint) {
		boolean firstHintEntry = hints.add(hint);
		assert firstHintEntry;
	}

	/**
	 * Decode the message based on the best hint available.
	 *
	 * The class instance is initialized with the first hint already,
	 * deduceable from the vector W.
	 *
	 * @return the decoded message
	 */
	public BitSet decodeMessage() {
		final BitSet K = generateKey();

		BitSet       result = (BitSet) S.clone();
		result.xor(K);

		return result;
	}

	/**
	 * Generate the key based on V.
	 *
	 * @return the generated key
	 */
	private BitSet generateKey() {
		final BigInteger V = calculateV();
		@SuppressWarnings("unused")
		final int        B = TimedCommitmentConstants.B;

		// The key, same length as the encrypted message
		BitSet     key = new BitSet(S.size());

		BigInteger currentValue = V;

		for (int i = key.size() - 1; i >= 0; --i) {
			key.set(i, TimedCommitmentHelpers.booleanLSB(currentValue));
			currentValue = currentValue.modPow(TWO, N);
		}

		return key;
	}

	/**
	 * Calculate the starting point (named 'V' by Boneh-Naor's algorithm) for
	 * decoding the message (the BBS generator's seed)
	 *
	 * @return the starting point
	 */
	private BigInteger calculateV() {
		// Start from the best hint available
		final TimedCommitmentHint hint = getBestHint();

		// Determine the number of iterations needed from the hint
		final int        hintIndex     = hint.index.intValue();
		final BigInteger messageSize   = BigInteger.valueOf(S.size());
		final BigInteger numIterations =
			TWO.pow(k - hintIndex).subtract(messageSize);

		// Start from the hint's value. After the iterations we will have the
		// starting point for decoding (the V).
		BigInteger currentValue = hint.value;

		// iterate numIterations times
		for (BigInteger i = ZERO; i.compareTo(numIterations) < 0;
			     i = i.add(ONE)) {
			// Each iteration is a squaring modulo N, as per Pinkas' paper
			currentValue = currentValue.modPow(TWO, N);
		}

		return currentValue;
	}

	/**
	 * @return the best hint in the hints map
	 */
	private TimedCommitmentHint getBestHint() {
		assert ! hints.isEmpty();

		return hints.last();
	}

	//~ Static fields/initializers ---------------------------------------------

	// useful constants
	private static final BigInteger ZERO = BigInteger.ZERO;
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = BigInteger.valueOf(2);
}


// End of file TimedCommitment.java
