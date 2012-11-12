// OTMESS.java - Wrapper for Oblivious Transfer messages.
// Copyright (C) 2004 Dahlia Malkhi, Yaron Sella. 
// Copyright (C) 2012 Csaba Toth, Wei Xie
// See full copyright license terms in file ../GPL.txt

package SFE.BOAL;

import java.io.*;

import java.math.*;


class OTMESS implements Serializable {
	private static final long serialVersionUID = -2614119124762456655L;

	BigInteger[] num;

    public OTMESS() {
    }

    public OTMESS(BigInteger inp_num) {
        num = new BigInteger[1];
        num[0] = inp_num;
    }

    public OTMESS(BigInteger[] inp_nums) {
        num = new BigInteger[inp_nums.length];

        for (int i = 0; i < inp_nums.length; i++)
            num[i] = inp_nums[i];
    }
}
