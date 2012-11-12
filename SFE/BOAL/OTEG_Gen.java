// OTEG_Gen - Generate OT DL-based public key parameters.
// Copyright (C) 2004 Dahlia Malkhi, Yaron Sella. 
// Copyright (C) 2012 Csaba Toth, Wei Xie
// See full copyright license terms in file ../GPL.txt

/**
 * This class generates OT DL-based public key parameters:
 * p - a 1024 bit prime, q - a 160 bit prime such that q|(p-1)
 * g - a generator of order q in Z_p
 * C - a constant generated by g whose discrete log is unknown
 *
 * @author Dahlia Malkhi and Yaron Sella.
 */

//---------------------------------------------------------------   

package SFE.BOAL;

import java.math.*;

import java.security.*;


public class OTEG_Gen {
    private static final int Psize = 1024;
    private static final int Qsize = 160;
    private static final int certainty = 64;
    private SecureRandom random = new SecureRandom();

    /**
     * Constructor - does all the work
     */
    public OTEG_Gen() {
        BigInteger p;
        BigInteger q;
        BigInteger pdq;
        BigInteger g;
        BigInteger C;

        System.out.print(
            "Finding primes p (1024 bits) and q (160 bits) s.t. q|(p-1)...");
        q = new BigInteger(Qsize, certainty, random);

        do {
            pdq = new BigInteger(Psize - Qsize, random);
            pdq = pdq.clearBit(0); // Make sure it's even
            p = q.multiply(pdq);
            p = p.add(BigInteger.ONE);
        } while (!p.isProbablePrime(certainty));

        System.out.println("done");

        System.out.print("Finding a generator of order q in Z_p...");

        do {
            g = new BigInteger(Psize - 1, random); // Make sure it's < p
        } while ((g.modPow(pdq, p)).equals(BigInteger.ONE) ||
                (g.modPow(q, p)).equals(BigInteger.ONE));

        System.out.println("done");

        g = g.modPow(pdq, p);
        C = new BigInteger(Qsize - 1, random);
        C = g.modPow(C, p);

        System.out.println("p = " + p.toString(16));
        System.out.println("q = " + q.toString(16));
        System.out.println("g = " + g.toString(16));
        System.out.println("C = " + C.toString(16));
    }

    //---------------------------------------------------------------

    /**
     * A main program for activating the ElGamalKeyPairGenerator.
     */
    public static void main(String[] args) {
        @SuppressWarnings("unused")
		OTEG_Gen key = null;

        key = new OTEG_Gen();
    }
}
